/*
 Copyright 2007 Politecnico di Milano
 Copyright 2013 Antonio García-Domínguez (rewrite on top of WSDL4J and XMLBeans)

 This file is part of Dynamo.

 Dynamo is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 Dynamo is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.polimi.monitor.invoker;

import it.polimi.monitor.invoker.data.OutXMLVariable;
import it.polimi.monitor.invoker.data.XMLVariable;
import it.polimi.monitor.invoker.exceptions.ServiceWSDLException;
import it.polimi.monitor.invoker.service.ServiceWSDL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import javax.wsdl.Part;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaType;

/**
 * Dynamic invoker used by Dynamo to invoke external services at runtime, without having
 * to generate Java code.
 */
public class Invoker {
	private static final Logger LOGGER = Logger.getLogger(Invoker.class.getCanonicalName());

	public String invoke(String wsdlLocation, String operationName, String xmlInVariable) {
		// FIXME: Dynamo only works with doc/literal (one part with the 'element' attribute). It does not work with rpc/lit.
		try {
			final ServiceWSDL serviceWsdl = new ServiceWSDL(wsdlLocation);

			final SOAPMessage request = MessageFactory.newInstance().createMessage();
			final SOAPElement bodyElement = createSOAPBody(operationName, serviceWsdl, request);
			final QName serviceName = populateBody(operationName, xmlInVariable, serviceWsdl, bodyElement);
			final QName portName = new QName(serviceName.getNamespaceURI(), serviceWsdl.getPortName());

			final String outVariable = invokeService(wsdlLocation, serviceName, portName, request);
			if (outVariable != null) {
				return outVariable;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
		}
		return null;
	}

	private SOAPElement createSOAPBody(String operationName,
			final ServiceWSDL serviceWsdl, final SOAPMessage request)
			throws SOAPException {
		final SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
		final SOAPBody body = envelope.getBody();
		envelope.addNamespaceDeclaration("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
	
		if (serviceWsdl.isDocumentStyle()) {
			// Document style does not need a wrapper
			return body;
		} else {
			// RPC style needs a wrapper, according to the WS-I spec
			final QName bodyQName = new QName(serviceWsdl.getTargetNamespace(), operationName, "ser");
			return body.addBodyElement(bodyQName);
		}
	}

	private QName populateBody(String operationName, String xmlInVariable,
			final ServiceWSDL serviceWsdl, final SOAPElement bodyElement)
			throws ServiceWSDLException, SOAPException {
		final QName serviceName = serviceWsdl.getServiceName();
		final QName messageName = serviceWsdl.getInMessageName(operationName);
		final Map<String, Part> parts = serviceWsdl.getMessageParts(messageName);
		final XMLVariable inVariable = new XMLVariable(xmlInVariable);
		for (Map.Entry<String, Part> partEntry : parts.entrySet()) {
			populatePart(serviceWsdl, bodyElement, serviceName, inVariable,
					partEntry.getKey(), partEntry.getValue());
		}
		return serviceName;
	}

	private void populatePart(final ServiceWSDL serviceWsdl,
			final SOAPElement bodyElement, final QName serviceName,
			final XMLVariable inVariable, final String partName,
			final Part messagePart) throws ServiceWSDLException, SOAPException {
		final QName nameElement = getPartElementName(serviceWsdl, serviceName, messagePart);
		final SchemaType type = getPartType(serviceWsdl, messagePart);

		final SOAPElement symbol = bodyElement.addChildElement(nameElement);
		final String partQuery = "/InvokeServiceParameters/" + partName;
		if (isComplex(type)) {
			addSOAPElement(symbol, type, partQuery, inVariable);
		} else {
			final String value = inVariable.GetValue(partQuery);
			if (value.equals("")) {
				LOGGER.warning("Value of " + partQuery + " is null.");
				symbol.setAttribute("xsi:nil", "true");
			} else {
				symbol.addTextNode(value);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String invokeService(String wsdlLocation, final QName serviceName,
			final QName portName, final SOAPMessage request)
			throws MalformedURLException, SOAPException {
		final Service service = Service.create(new URL(wsdlLocation), serviceName);
		final Dispatch dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
		final SOAPMessage response = (SOAPMessage) dispatch.invoke(request);
		final String outVariable = new OutXMLVariable().GetXMLVariable(response.getSOAPBody());
		LOGGER.info("Response: " + outVariable);
		return outVariable;
	}

	private QName getPartElementName(ServiceWSDL serviceWsdl,
			final QName serviceName, final Part messagePart) {
		// We assume that the service is WS-I compliant: doc/lit uses 'element', rpc/lit uses 'type'
		if (serviceWsdl.isDocumentStyle()) {
			return messagePart.getElementName();
		} else {
			// The RPC wrapper element was already created elsewhere
			return new QName("", messagePart.getName());
		}
	}

	private SchemaType getPartType(final ServiceWSDL serviceWsdl,
			final Part messagePart) throws ServiceWSDLException {
		SchemaType type;
		if (serviceWsdl.isDocumentStyle()) {
			type = serviceWsdl.getElement(messagePart.getElementName()).getType();
		} else {
			type = serviceWsdl.getType(messagePart.getTypeName());
		}
		return type;
	}

	private void addSOAPElement(SOAPElement parentElement, SchemaType type,
			String queryXPath, XMLVariable inVariable) {
		try {
			processParticle(parentElement, queryXPath, type.getContentModel(), inVariable);
		} catch (Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	private void processParticle(SOAPElement parentElement, String queryXPath,
			SchemaParticle particle, XMLVariable inVariable)
			throws SOAPException {
		switch (particle.getParticleType()) {
		case SchemaParticle.ELEMENT:
			processElement(parentElement, queryXPath, particle, inVariable);
			break;
		case SchemaParticle.ALL:
		case SchemaParticle.SEQUENCE:
			processSequence(particle, parentElement, queryXPath, inVariable);
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot understand particles of type "
							+ particle.getParticleType());
		}
	}

	private void processElement(SOAPElement parentElement, String queryXPath,
			SchemaParticle particle, XMLVariable inVariable)
			throws SOAPException {
		final SchemaLocalElement element = (SchemaLocalElement) particle;
		final QName elementQName = new QName(element.getName().getNamespaceURI(), element.getName().getLocalPart());

		final SOAPElement childElement = parentElement.addChildElement(elementQName);
		final String query = queryXPath + "/*[local-name(.)='" + elementQName.getLocalPart() + "']";

		if (element.getType().getContentType() != SchemaType.SIMPLE_CONTENT) {
			// FIXME: this doesn't consider the fact that some .wsdl files may
			// have elements with attributes and so on.
			addSOAPElement(childElement, element.getType(), query, inVariable);
		} else {
			String value = inVariable.GetValue(query);
			if (value.equals("")) {
				LOGGER.warning("Value of " + query + " is null.");
				childElement.setAttribute("xsi:nil", "true");
			} else {
				childElement.addTextNode(value);
			}
		}
	}

	private void processSequence(SchemaParticle particle,
			SOAPElement parentElement, String queryXPath, XMLVariable inVariable)
			throws SOAPException {
		for (SchemaParticle child : particle.getParticleChildren()) {
			processParticle(parentElement, queryXPath, child, inVariable);
		}
	}

	private boolean isComplex(final SchemaType type) {
		return type.getContentType() != SchemaType.NOT_COMPLEX_TYPE;
	}
}
