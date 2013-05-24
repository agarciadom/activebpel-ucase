/*
 Copyright 2007 Politecnico di Milano
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

import it.polimi.monitor.invoker.exceptions.ServiceWSDLExcpetion;

import java.util.Collection;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

public class ServiceWSDLJaxWS {
	private String wsdlLocation;

	private Definition wsdlDef;

	public ServiceWSDLJaxWS(String wsdlLoc) throws WSDLException {
		this.wsdlLocation = wsdlLoc;
		this.wsdlDef = WSDLFactory.newInstance().newWSDLReader()
				.readWSDL(wsdlLoc);
	}

	public String GetTargetNamespace() {
		String targetNS = wsdlDef.getTargetNamespace();

		if (targetNS != null) {
			return targetNS;
		}

		return getFirstService().getQName().getNamespaceURI();
	}

	private Service getFirstService() {
		final Service firstService = (Service) wsdlDef.getServices().values()
				.iterator().next();
		return firstService;
	}

	public QName GetServiceQName() throws ServiceWSDLExcpetion {
		if (!isThereOnlyOneServiceName()) {
			throw new ServiceWSDLExcpetion(
					"More than one Service present in the wsdl:"
							+ this.wsdlLocation);
		}

		return getFirstService().getQName();
	}

	public String GetPortName() {
		final Port port = (Port) getFirstService().getPorts().values()
				.iterator().next();
		return port.getName();
	}

	public boolean isThereOnlyOneServiceName() {
		return wsdlDef.getServices().size() == 1;
	}

	public String RetreiveInMessageName(String operation, QName service,
			QName port) {
		Binding serviceBinding = wsdlDef.getService(service)
				.getPort(port.getLocalPart()).getBinding();
		QName portTypeQName = serviceBinding.getPortType().getQName();
		PortType portType = wsdlDef.getPortType(portTypeQName);

		final Operation op = portType.getOperation(operation, null, null);
		if (op != null) {
			return op.getInput().getName();
		}
		return null;
	}

	public Collection<Part> GetMessageParts(QName messageName) {
		return wsdlDef.getMessage(messageName).getParts().values();
	}

	public Vector<Element> GetComplexTypeDefinition(String operation,
			QName service, String port, Collection<Part> messageParts) {
		final Binding serviceBinding = wsdlDef.getService(service)
				.getPort(port).getBinding();
		final BindingOperation bindingOperation = serviceBinding
				.getBindingOperation(operation, null, null);

		for (Part part : messageParts) {
			System.out.println(String.format("Part %s: type %s, element %s",
					part.getName(), part.getTypeName(), part.getElementName()));
		}

		return null;
	}
}
