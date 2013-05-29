/*
 Copyright 2007 Politecnico di Milano
 Copyright 2013 Antonio García-Domínguez (University of Cádiz)

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

package it.polimi.monitor.invoker.service;

import it.polimi.monitor.invoker.exceptions.ServiceWSDLException;

import java.util.Map;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

public class ServiceWSDL {
	private static final String DOC_STYLE = "document";
	private static final Logger LOGGER = Logger.getLogger(ServiceWSDL.class.getCanonicalName());

	private String wsdlLocation, soapBindingType;

	private Port port;
	private Service service;
	private Definition definitions;
	private Binding binding;

	private SchemaTypeSystem schemaTypeSystem;

	public ServiceWSDL(String wsdl) throws ServiceWSDLException {
		wsdlLocation = wsdl;

		LOGGER.fine("Loading Service WSDL from " + this.wsdlLocation);

		try {
			definitions = WSDLFactory.newInstance().newWSDLReader()
					.readWSDL(wsdlLocation);

			// FIXME: why does Dynamo assume there is always exactly one <service> in a WSDL doc?
			service = (Service) definitions.getAllServices().values().iterator().next();

			// FIXME: Dynamo assumes the service has only one port
			port = (Port) this.service.getPorts().values().iterator().next();
			binding = port.getBinding();

			for (Object ext : binding.getExtensibilityElements()) {
				if (ext instanceof SOAP12Binding) {
					soapBindingType = ((SOAP12Binding) ext).getStyle();
				} else if (ext instanceof SOAPBinding) {
					soapBindingType = ((SOAPBinding) ext).getStyle();
				}
			}
			LOGGER.finer("SOAP binding style: " + soapBindingType);

			// Parse the embedded XML Schema with XMLBeans (if any exists)
			// FIXME: handle WSDL imports (perhaps using WSDL2XSDTree)
			if (getSchema() != null) {
				final org.w3c.dom.Element schemaElement = getSchema().getElement();
				XmlObject schemaXSB = XmlObject.Factory.parse(schemaElement);
				schemaTypeSystem = XmlBeans.compileXsd(
					new XmlObject[] { schemaXSB },
					XmlBeans.getBuiltinTypeSystem(),
					new XmlOptions().setCompileDownloadUrls());
			} else {
				schemaTypeSystem = XmlBeans.getBuiltinTypeSystem();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	public Port getPort() {
		return port;
	}

	public Service getService() {
		return service;
	}

	public Definition getDefinitions() {
		return definitions;
	}

	public Binding getBinding() {
		return binding;
	}

	public QName getServiceName() {
		return service.getQName();
	}

	public String getTargetNamespace() {
		return definitions.getTargetNamespace();
	}

	public String getPortName() {
		return port.getName();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Part> getMessageParts(QName messageName)
			throws ServiceWSDLException {
		return (Map<String, Part>) definitions.getMessage(messageName).getParts();
	}

	public QName getInMessageName(String operation) {
		final Operation op = binding.getPortType().getOperation(operation, null, null);
		if (op != null && op.getInput() != null) {
			return op.getInput().getMessage().getQName();
		}
		return null;
	}

	public QName getOutMessageName(String operation) {
		final Operation op = binding.getPortType().getOperation(operation, null, null);
		if (op != null && op.getOutput() != null) {
			return op.getOutput().getMessage().getQName();
		}
		return null;
	}

	public SchemaGlobalElement getElement(QName name) throws ServiceWSDLException {
		if (name.equals("") || (name == null)) {
			throw new ServiceWSDLException("Input param 'name' null or ''.");
		}
		return schemaTypeSystem.findElement(name);
	}

	public SchemaType getType(QName name) throws ServiceWSDLException {
		if (name.equals("") || (name == null)) {
			throw new ServiceWSDLException("Input param 'name' null or ''.");
		}
		return schemaTypeSystem.findType(name);
	}

	public String getSoapBindingType() {
		return soapBindingType;
	}

	public boolean isDocumentStyle() {
		return DOC_STYLE.equals(getSoapBindingType());
	}

	private Schema getSchema() {
		final Types types = definitions.getTypes();
		for (Object ext : types.getExtensibilityElements()) {
			if (ext instanceof Schema) {
				return ((Schema) ext);
			}
		}
		return null;
	}
}
