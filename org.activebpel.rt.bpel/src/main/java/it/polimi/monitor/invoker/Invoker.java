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

package it.polimi.monitor.invoker;

import it.polimi.monitor.invoker.data.OutXMLVariable;
import it.polimi.monitor.invoker.data.XMLVariable;
import it.polimi.monitor.invoker.exceptions.ServiceWSDLExcpetion;
import it.polimi.monitor.invoker.service.Element;
import it.polimi.monitor.invoker.service.ServiceWSDL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import xsul.wsdl.WsdlMessagePart;

public class Invoker
{
        private XMLVariable inVariable;

        private ServiceWSDL serviceWsdl;

        private SOAPFactory soapFactory;

        public Invoker()
        {

        }

        @SuppressWarnings("unchecked")
        public String Invoke(String wsdlLocation, String operationName, String xmlInVariable)
        {
//		long start = System.currentTimeMillis();

                try
                {
                        String query = "/InvokeServiceParameters";

                        Service service;

                        QName portName;

                        this.inVariable = new XMLVariable(xmlInVariable);

                        this.serviceWsdl = new ServiceWSDL(wsdlLocation);
                        URL url = new URL(wsdlLocation);
                        QName serviceName = new QName(this.serviceWsdl.GetTargetNamespace(), this.serviceWsdl.GetServiceName());
                        portName = new QName(this.serviceWsdl.GetTargetNamespace(), this.serviceWsdl.GetPortName());

                        service = Service.create(url, serviceName);

                        Dispatch dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

                        MessageFactory mf = MessageFactory.newInstance();

                        SOAPMessage request = mf.createMessage();

                        SOAPPart part = request.getSOAPPart();
                        SOAPEnvelope envelope = part.getEnvelope();
                        envelope.setAttribute("xmlns:ser", this.serviceWsdl.GetTargetNamespace());
                        envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

                        SOAPBody body = envelope.getBody();
                        this.soapFactory = SOAPFactory.newInstance();

                        String namespacePrefix = "ser";

//			if(!this.serviceWsdl.isSchemaQualified())
//			{
//				namespacePrefix = "ser";
//			}

                        Name bodyName = this.soapFactory.createName(operationName, namespacePrefix, null);
                        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

                        String messageName = this.serviceWsdl.RetreiveInMessageName(operationName);

                        Iterator<Element> parts = this.serviceWsdl.GetMessageParts(messageName);

                        Element messagePart;

                        if(this.serviceWsdl.getSoapBindingType().equals("document"))
                        {
                                messagePart = parts.next();

                                parts = this.serviceWsdl.GetComplexTypeDefinition(messagePart.GetType()).iterator();

                                query += "/" + messagePart.GetType();
                        }

                        while(parts.hasNext())
                        {
                                messagePart = parts.next();

                                Name child;

                                if(this.serviceWsdl.isSchemaQualified() && this.serviceWsdl.getSoapBindingType().equals("document"))
                                {
                                        child = this.soapFactory.createName(messagePart.GetName(), "ser", this.serviceWsdl.getSchemaTargetNamespace());
                                }
                                else if(this.serviceWsdl.isSchemaQualified() && this.serviceWsdl.getSoapBindingType().equals("rpc"))
                                {
                                        child = this.soapFactory.createName(messagePart.GetName(), null, null);
                                }
                                else
                                {
                                        child = this.soapFactory.createName(messagePart.GetName());
                                }

                                SOAPElement symbol = bodyElement.addChildElement(child);

                                if(this.isComplexType(messagePart.GetType()))
                                {
                                        this.addSOAPElement(symbol, messagePart.GetType(), query + "/" + messagePart.GetName());
                                }
                                else
                                {
                                        String value = this.inVariable.GetValue(query + "/" + messagePart.GetName());

                                        if(value.equals(""))
                                        {
                                                System.out.println("Value of " + query + "/" + messagePart.GetName() + " is null.");
//						symbol.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "nil"), "true");
                                                symbol.setAttribute("xsi:nil", "true");
                                        }
                                        else
                                                symbol.addTextNode(value);
                                }
                        }

//			System.out.println("SOAP:" + request.getSOAPBody());

//			long stop = System.currentTimeMillis();
//			System.out.println("Start: " + start + " | Stop: " + stop + " | time to complete: " + (stop - start));

                        SOAPMessage response = (SOAPMessage) dispatch.invoke(request);

//			long start_post = System.currentTimeMillis();

                        String outVariable = new OutXMLVariable().GetXMLVariable(response.getSOAPBody());

//			long stop_post = System.currentTimeMillis();
//			System.out.println("Start post: " + start_post + " | Stop_post: " + stop_post + " | time to complete without invoke: " + ((stop - start)+(stop_post - start_post)));

                        System.out.println("Response: " + outVariable);

                        if(outVariable != null)
                        {
                                return outVariable;
                        }
                        else
                        {
                                return null;
                        }
                }
                catch (ServiceWSDLExcpetion e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (MalformedURLException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (SOAPException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (Throwable e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                return null;
        }

        private void addSOAPElement(SOAPElement parentElement, String parentType, String queryXPath)
        {
                Element temp;
                String query;
                SOAPElement childElement;

                try
                {
                        Vector <Element> complexTypeElements = this.serviceWsdl.GetComplexTypeDefinition(parentType);

                        for(int i = 0; i < complexTypeElements.size(); i++)
                        {
                                temp = (Element) complexTypeElements.elementAt(i);

//				Name childName = this.soapFactory.createName(temp.GetName());
                                Name childName;

                                if(this.serviceWsdl.isSchemaQualified())
                                {
                                        childName = this.soapFactory.createName(temp.GetName(), "ser", this.serviceWsdl.getSchemaTargetNamespace());
                                }
                                else
                                {
                                        childName = this.soapFactory.createName(temp.GetName());
                                }

                                childElement = parentElement.addChildElement(childName);

                                if(isComplexType(temp.GetType()))
                                {
                                        query = queryXPath + "/" + temp.GetName();
                                        addSOAPElement(childElement, temp.GetType(), query);
                                }
                                else
                                {
                                        query = queryXPath + "/" + temp.GetName();

//					childElement.addTextNode(this.inVariable.GetValue(query));
                                        String value = this.inVariable.GetValue(query);

                                        if(value.equals(""))
                                        {
                                                System.out.println("Value of " + query + " is null.");
//						childElement.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "nil"), "true");
                                                childElement.setAttribute("xsi:nil", "true");
                                        }
                                        else
                                                childElement.addTextNode(value);
                                }
                        }
                }
                catch (SOAPException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (ServiceWSDLExcpetion e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        private boolean isComplexType(WsdlMessagePart mp)
        {
                String partType = mp.getPartType().getLocalPart();

                return !(partType.equals("int") ||
                                partType.equals("string") ||
                                partType.equals("boolean") ||
                                partType.equals("float"));
        }

        private boolean isComplexType(String type)
        {
                return !(type.contains("int") ||
                                type.contains("string") ||
                                type.contains("boolean") ||
                                type.contains("float") ||
                                type.contains("double") ||
                                type.contains("long"));
        }
}
