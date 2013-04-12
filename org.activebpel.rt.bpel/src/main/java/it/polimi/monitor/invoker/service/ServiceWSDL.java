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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.xmlpull.v1.builder.XmlElement;

import xsul.wsdl.WsdlBinding;
import xsul.wsdl.WsdlBindingOperation;
import xsul.wsdl.WsdlDefinitions;
import xsul.wsdl.WsdlException;
import xsul.wsdl.WsdlMessage;
import xsul.wsdl.WsdlMessagePart;
import xsul.wsdl.WsdlPortType;
import xsul.wsdl.WsdlResolver;
import xsul.wsif.WSIFException;
import xsul.wsif.WSIFMessage;
import xsul.wsif.WSIFOperation;
import xsul.wsif.WSIFPort;
import xsul.wsif.WSIFService;
import xsul.wsif.WSIFServiceFactory;
import xsul.wsif.spi.WSIFProviderManager;

public class ServiceWSDL
{
	private String wsdlLocation;

    private WSIFPort port;
    private WSIFService service;
    
    private WsdlDefinitions definitions;
    private WsdlBinding binding;
    
    private String soapBindingType;
    private String schemaTargetNamespace;
    
    private HashMap<String, Vector<Element>> types;
    private HashMap<String, Vector<Element>> messages;
    
    private boolean schemaQualified = false;   

    public ServiceWSDL(String wsdl) throws ServiceWSDLExcpetion
	{
		this.wsdlLocation = wsdl;

        WSIFProviderManager.getInstance().addProvider(new xsul.wsif_xsul_soap_http.Provider());

        System.err.println("Loading Service WSDL from " + this.wsdlLocation);

        try 
        {        	
			this.definitions = WsdlResolver.getInstance().loadWsdl(new URI(this.wsdlLocation));

			WSIFServiceFactory wsf = WSIFServiceFactory.newInstance();
			this.service = wsf.getService(definitions);

			/*if only one port type. If there is more than one specify the one desired. not supported yet*/
			this.port = this.service.getPort(); 
			
			QName bindingQN = this.port.getWsdlServicePort().getBinding();
			this.binding = this.definitions.getBinding(bindingQN.getLocalPart());
			XmlElement subBinding = binding.findElementByName("binding");
			
			this.soapBindingType = subBinding.attribute("style").getValue();
//			System.out.println("SOAP Binding Style: " + this.soapBindingType);
			
			this.types = new HashMap<String, Vector<Element>>();
			this.LoadWsdlTypes();
			
			this.messages = new HashMap<String, Vector<Element>>();
			this.LoadWsdlMessages();
        }
        catch (WsdlException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (WSIFException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (URISyntaxException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public String GetServiceName()
    {
    	return this.port.getWsdlServicePort().getService().attribute("name").getValue();
    }
    
    public String GetTargetNamespace()
    {
    	return this.definitions.attribute("targetNamespace").getValue();
    }
    
    public String GetTargetNamespacePrefix()
    {
    	return this.definitions.attribute("targetNamespace").getValue();
    }

    public String GetPortName()
    {
    	return this.port.getWsdlServicePort().getPortName();
    }

    /**
     * 
     * @param messageName
     * @return Iterator<Element>
     * @throws ServiceWSDLExcpetion
     */
    public Iterator<Element> GetMessageParts(String messageName) throws ServiceWSDLExcpetion
    {
    	if(!this.messages.containsKey(messageName))
    	{
    		throw new ServiceWSDLExcpetion("No '" + messageName + "' message found.");
    	}
    	
    	return this.messages.get(messageName).iterator();
    }
    
    public String RetreiveInMessageName(String operation)
    {
    	if(this.binding.getOperation(operation).lookupOperation().getInput().getInputName() != null)
    	{
        	return this.binding.getOperation(operation).lookupOperation().getInput().getInputName();
    	}
    	
    	WsdlPortType portType = this.definitions.getPortType(this.binding.getPortType().getLocalPart());

    	if(portType.getOperation(operation).getInput().getInputName() != null)
    	{
    		return portType.getOperation(operation).getInput().getInputName();
    	}
    	
    	return portType.getOperation(operation).getInput().getMessage().getLocalPart();
    }
    
    public String RetreiveOutMessageName(String operation)
    {
    	if(this.binding.getOperation(operation).lookupOperation().getOutput().getOutputName() != null)
    	{
        	return this.binding.getOperation(operation).lookupOperation().getOutput().getOutputName();
    	}

    	WsdlPortType portType = this.definitions.getPortType(this.binding.getPortType().getLocalPart());

    	if(portType.getOperation(operation).getOutput().getOutputName() != null)
    	{
    		return portType.getOperation(operation).getOutput().getOutputName();
    	}
    	
    	return portType.getOperation(operation).getOutput().getMessage().getLocalPart();
    }
    
    public Vector<Element> GetComplexTypeDefinition(String name) throws ServiceWSDLExcpetion
    {
    	if(name.equals("") || (name == null))
    	{
    		throw new ServiceWSDLExcpetion("Input param 'name' null or ''.");
    	}
    	
    	if(!this.types.containsKey(name))
    	{
    		throw new ServiceWSDLExcpetion("No '" + name + "' type found.");
    	}
    	
    	return this.types.get(name);
    }
    
    private void LoadWsdlTypes() throws ServiceWSDLExcpetion
    {
    	Vector<Element> typeElementList;
    	String typeName = null;
    	
		//Retreiving 'types' defined in the WSDL
		XmlElement types = definitions.getTypes();
		
		//if(!types.hasChildren())
		if(types == null)
		{
			this.types = null;
			
			return;
		}
		
		Iterator schemas = types.children();
		Iterator elements;
		Iterator sequences;
		XmlElement schema;
		XmlElement element;
		XmlElement sequence;
		XmlElement sequenceElement;
		Object temp;
		int i = 1;
		
		while(schemas.hasNext())
		{
			temp = schemas.next();
			
			if(temp instanceof XmlElement)
			{
//				System.out.println("Schema: " + i++);
				
				schema = (XmlElement) temp;

				if(schema.attribute("elementFormDefault") != null)
				{
					if(schema.attribute("elementFormDefault").getValue().equals("qualified"))
					{
						this.schemaQualified = true;
						this.schemaTargetNamespace = schema.attribute("targetNamespace").getValue();
					}
					
//					System.out.println("schema TN: " + this.schemaTargetNamespace);
				}
				
				elements = schema.children();
				
				while(elements.hasNext())
				{
					temp = elements.next();
					
					if(temp instanceof XmlElement)
					{
						element = (XmlElement) temp;
						
						if(element.getName().equals("element"))
						{
							typeName = element.attribute("name").getValue();
							
							Iterator iterator = element.children();
							
							while(iterator.hasNext())
							{
								temp = iterator.next();
								
								if(temp instanceof XmlElement)
								{
									element = (XmlElement) temp;
									
									if(element.getName().equals("complexType"))
									{
										break;
									}
								}
							}
						}
						if(element.getName().equals("complexType"))
						{
							if(element.attribute("name") != null)
							{
								typeName = element.attribute("name").getValue();
							}
							
							typeElementList = new Vector<Element>();
							sequences = element.children();
							
							Element typeElement;
							
							while(sequences.hasNext())
							{
								temp = sequences.next();
								
								if(temp instanceof XmlElement)
								{
									sequence = (XmlElement) temp;
									
									Iterator iterator = sequence.children();
									
									while(iterator.hasNext())
									{
										temp = iterator.next();
										
										if(temp instanceof XmlElement)
										{
											sequenceElement = (XmlElement) temp;
											
											typeElement = new Element();
											
											typeElement.SetName(sequenceElement.attribute("name").getValue());
											typeElement.SetType(sequenceElement.attribute("type").getValue());
											
											typeElementList.add(typeElement);
										}
									}
								}
								
								this.types.put(typeName, typeElementList);
							}
						}
					}
				}
			}
		}
    }
    
    private void LoadWsdlMessages()
    {
    	Object object;
    	WsdlMessage message;
    	WsdlMessagePart messagePart;
    	Iterator messageParts;
    	Vector<Element> wsdlMessageParts;
    	Element wsdlMessagePart;

    	Iterator messages = this.definitions.getMessages().iterator();
    	
    	while(messages.hasNext())
    	{
    		object = messages.next();
    		
    		if(object instanceof WsdlMessage)
    		{
    			message = (WsdlMessage) object;
    			
    			wsdlMessageParts = new Vector<Element>();
    			
//    			System.out.println(message.getMessageName());
    			messageParts = message.getParts().iterator();
    			
    			while(messageParts.hasNext())
    			{
    				object = messageParts.next();
    				
    				if(object instanceof WsdlMessagePart)
    				{
    					messagePart = (WsdlMessagePart) object;
    					
    					wsdlMessagePart = new Element();
    					
    					if(this.soapBindingType.equals("rpc"))
    					{
    						wsdlMessagePart.SetName(messagePart.getPartName());
//    						System.out.println(messagePart.getPartName());
    						wsdlMessagePart.SetType(messagePart.getPartType().getLocalPart());
    						
    						wsdlMessageParts.add(wsdlMessagePart);
    					}
    					if(this.soapBindingType.equals("document"))
    					{
    						wsdlMessagePart.SetName(messagePart.getPartName());
    						wsdlMessagePart.SetType(messagePart.getPartElement().getLocalPart());
    						
    						wsdlMessageParts.add(wsdlMessagePart);
    					}
    				}
    			}
        		
        		this.messages.put(message.getMessageName(), wsdlMessageParts);
    		}
    	}
    }

	public String getSoapBindingType()
	{
		return soapBindingType;
	}

	public boolean isSchemaQualified()
	{
		return schemaQualified;
	}

	public String getSchemaTargetNamespace()
	{
		return schemaTargetNamespace;
	}
}
