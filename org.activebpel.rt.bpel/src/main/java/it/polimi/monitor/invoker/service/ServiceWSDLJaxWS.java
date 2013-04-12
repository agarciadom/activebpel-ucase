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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import com.sun.xml.ws.model.Mode;

import com.sun.xml.ws.server.DocInfo;
import com.sun.xml.ws.transport.http.server.EndpointEntityResolver;
import com.sun.xml.ws.wsdl.WSDLContext;
import com.sun.xml.ws.wsdl.parser.Binding;
import com.sun.xml.ws.wsdl.parser.BindingOperation;
import com.sun.xml.ws.wsdl.parser.Message;
import com.sun.xml.ws.wsdl.parser.PortType;
import com.sun.xml.ws.wsdl.parser.PortTypeOperation;
import com.sun.xml.ws.wsdl.parser.WSDLDocument;

public class ServiceWSDLJaxWS
{
	private String wsdlLocation;
	
	private WSDLContext wsdlContext;
	
	private WSDLDocument wsdlDocument;

	public ServiceWSDLJaxWS(String wsdlLoc) throws MalformedURLException, IOException, XMLStreamException, SAXException
	{
		this.wsdlLocation = wsdlLoc;
		
		Map<String, DocInfo> map = new HashMap<String, DocInfo>();
		
		EntityResolver entityResolver = new EndpointEntityResolver(map);
		
		this.wsdlContext = new WSDLContext(new URL(this.wsdlLocation), entityResolver);
		
		this.wsdlDocument = this.wsdlContext.getWsdlDocument();
	}
	
	public String GetTargetNamespace()
	{
		String targetNS = this.wsdlContext.getTargetNamespace();
		
		if(targetNS != null)
		{
			return targetNS;
		}
		
		return this.wsdlContext.getServiceQName().getNamespaceURI();
	}
	
	public QName GetServiceQName() throws ServiceWSDLExcpetion
	{
		if(!this.isThereOnlyOneServiceName())
		{
			throw new ServiceWSDLExcpetion("More than one Service present in the wsdl:" + this.wsdlLocation);
		}
		
		return this.wsdlContext.getServiceQName();
	}
	
	public QName GetPortQName()
	{
		return this.wsdlContext.getPortName();
	}

	public boolean isThereOnlyOneServiceName()
	{
		Set<QName> serviceNameList = this.wsdlContext.getAllServiceNames();
		
		if(serviceNameList.size() == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

    public QName RetreiveInMessageName(String operation, QName service, QName port)
    {
    	Binding serviceBinding = this.wsdlContext.getWsdlBinding(service, port);
    	
    	QName portTypeQName = serviceBinding.getPortTypeName();
    	PortType portType = this.wsdlDocument.getPortType(portTypeQName);
    	
    	Set<Entry<String, PortTypeOperation>> ptoSet = portType.entrySet();
    	
    	Iterator iterator = ptoSet.iterator();
    	
    	Entry entry;
    	
    	while(iterator.hasNext())
    	{
    		entry = (Entry) iterator.next();
    		
    		if(((String)entry.getKey()).equals(operation))
    		{
    			PortTypeOperation result = (PortTypeOperation) entry.getValue();
    			
    			return result.getInputMessage();
    		}
    	}
    	
    	return null;
    }

    public Iterator GetMessageParts(QName messageName)
    {
		//Retreive message definition from the WSDL
		Message message = this.wsdlDocument.getMessage(messageName);

		return message.iterator();
    }
    
    public Vector<Element> GetComplexTypeDefinition(String operation, QName service, QName port, Iterator messageParts)
    {
    	Binding serviceBinding = this.wsdlContext.getWsdlBinding(service, port);

    	BindingOperation bindingOperation = serviceBinding.get(operation);
    	
    	Element element;
    	String temp;
    	
    	while(messageParts.hasNext())
    	{
    		element = new Element();
    		temp = (String)messageParts.next();
    		
    		System.out.println("Part: " + temp + " - MimeType: " + bindingOperation.getPart(temp, Mode.IN));
    	}
    	
    	return null;
    }
}
