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

package it.polimi.recovery;

import it.polimi.monitor.invoker.Invoker;
import it.polimi.recovery.data.ChangeProcessParams;
import it.polimi.recovery.data.ChangeSupervisionParams;
import it.polimi.recovery.data.ChangeSupervisionRule;
import it.polimi.recovery.data.RecoveryResult;
import it.polimi.recovery.data.RuleParams;
import it.polimi.recovery.data.ServiceInvocationParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import com.sun.xml.ws.server.DocInfo;
import com.sun.xml.ws.transport.http.server.EndpointEntityResolver;
import com.sun.xml.ws.wsdl.WSDLContext;

public class AtomicRecoveryAction
{
	public static RecoveryResult Notify(String message, String mail, String xmlProperties)
	{
		try
		{
			Properties fMailServerConfig = new Properties();
			
			fMailServerConfig.loadFromXML(new ByteArrayInputStream(xmlProperties.getBytes()));
			
			Session session;

			if(fMailServerConfig.get("mail.smtp.auth").equals("true"))
			{
				session = Session.getDefaultInstance( fMailServerConfig, 
														new SMTPAuthenticator((String) fMailServerConfig.get("mail.smtp.user"),
																(String) fMailServerConfig.get("mail.smtp.password")) );
			}
			else
			{
				session = Session.getDefaultInstance( fMailServerConfig, null );
			}
			
			MimeMessage msg = new MimeMessage( session );

			msg.setFrom(new InternetAddress((String) fMailServerConfig.get("from")));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
			msg.setSubject("Reporting recovery event");
			msg.setContent(message, "text/plain");
			
			Transport.send(msg);
		}
		catch (AddressException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (MessagingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new RecoveryResult(true, "Succesfully executed: notify(" + message + ", " + mail + ")\n");
	}
	
	//No more parametric
	public static RecoveryResult Retry(ServiceInvocationParams serviceParams)
	{
		Invoker invoker = new Invoker();
		String serviceInvokeResponse = invoker.Invoke(serviceParams.getWsdlURL(), serviceParams.getOperation(), serviceParams.getInput());

		if((serviceInvokeResponse != null) && (!serviceInvokeResponse.equals("")))
		{
			RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: retry(" + serviceParams.getWsdlURL() + "," +
																										serviceParams.getOperation() + "," +
																										serviceParams.getInput() +
																										")\n");
			recoveryResult.setServiceReinvocationOutput(serviceInvokeResponse);
			return recoveryResult;
		}
		
		return new RecoveryResult(false, "Retry: invoke activity return an empty or null result!!");
	}
	
	public static RecoveryResult Rebind(String newServiceURI, ServiceInvocationParams serviceParams, boolean toInvoke)
	{
		String serviceInvokeResponse = null;
		RecoveryResult recoveryResult = new RecoveryResult();
		
//		System.out.println("Data input: " + serviceParams.getInput());
		
		if(toInvoke)
		{
			Invoker invoker = new Invoker();
			serviceInvokeResponse = invoker.Invoke(newServiceURI, serviceParams.getOperation(), serviceParams.getInput());
			recoveryResult.setThereInokeActivity();
			recoveryResult.setServiceReinvocationOutput(serviceInvokeResponse);
		}

		recoveryResult.setRecoveryResult(true);
		recoveryResult.addMessage("Succesfully executed: rebind(" + newServiceURI + ")\n");
		recoveryResult.setThereRebindAction();
		recoveryResult.setNewServiceEndpoint(AtomicRecoveryAction.RetrieveServiceEndpoint(newServiceURI));
		
		return recoveryResult;
	}
	
	public static RecoveryResult ChangeSupervisionRules(String analysis, String recovery, /*RecoveryParams recoveryParams, */String changingType)
	{
		ChangeSupervisionRule changeSupervisionRule = new ChangeSupervisionRule(analysis, recovery, changingType);

		RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: change_supervision_rules(" + analysis + "," +
																														recovery + "," +
																														changingType + ")\n");
		
		recoveryResult.setChangeSupervisionRule(changeSupervisionRule);

		return recoveryResult;
	}
	
	public static RecoveryResult ChangeSupervisionParams(RuleParams newParams, /*RecoveryParams recoveryParams, */String changingType)
	{
		ChangeSupervisionParams changeSupervisionParams = new ChangeSupervisionParams(newParams.getPriority(),
																						newParams.getTimeFrame(),
																						newParams.getProviders(),
																						changingType);
		
		RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: change_supervision_params(" + newParams.getPriority() + "," +
																														newParams.getTimeFrame() + "," + 
																														newParams.getProviders() + "," +
																														changingType + ")\n");
		recoveryResult.setChangeSupervisionParams(changeSupervisionParams);

		return recoveryResult;
	}
	
	public static RecoveryResult ChangeProcessParams(/*RecoveryParams recoveryParams, */int processPriority, String changingType)
	{
		ChangeProcessParams changeProcessParams = new ChangeProcessParams(processPriority, changingType);

		RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: change_process_params(" + processPriority + "," +
																													changingType + ")\n");
		
		recoveryResult.setChangeProcessParams(changeProcessParams);
		
		return recoveryResult;
 	}
	
	public static RecoveryResult Call(ServiceInvocationParams serviceParams)
	{
		Invoker invoker = new Invoker();
		
		String serviceInvokeResponse = invoker.Invoke(serviceParams.getWsdlURL(), serviceParams.getOperation(), serviceParams.getInput());
		
		if((serviceInvokeResponse != null) && (!serviceInvokeResponse.equals("")))
		{
			RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: call(" + serviceParams.getWsdlURL() + "," +
																										serviceParams.getOperation() + "," +
																										serviceParams.getInput() +
																										")\n -->Call result: " + 
																										serviceInvokeResponse + "\n");
			return recoveryResult;
		}

		return new RecoveryResult(false, null);
	}
	
	public static RecoveryResult Substitute(ServiceInvocationParams serviceParams, String xslt)
	{
		Invoker invoker = new Invoker();
		
		String serviceInvokeResponse = invoker.Invoke(serviceParams.getWsdlURL(), serviceParams.getOperation(), serviceParams.getInput());
		
		try
		{
			ByteArrayOutputStream resultOutput = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(resultOutput);
			StreamSource xsltSource = new StreamSource(new ByteArrayInputStream(xslt.getBytes()));
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(serviceInvokeResponse.getBytes()));
			DOMSource responseSource = new DOMSource(document);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
			
			transformer.transform(responseSource, result);
			
			System.out.println("XSL Transformation Result: " + resultOutput.toString());
			serviceInvokeResponse = resultOutput.toString();

			RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: substitute(" + serviceParams.getWsdlURL() + "," +
																											serviceParams.getOperation() + "," +
																											serviceParams.getInput() +
																											xslt + ")\n");
			recoveryResult.setServiceReinvocationOutput(serviceInvokeResponse);
			
			return recoveryResult;
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RecoveryResult(false, e.getMessage());
		}
	}
	
	public static RecoveryResult ProcessCallback(String bpelWSDL, String eventHandlerName, String params)
	{
		Invoker invoker = new Invoker();
		
		String serviceInvocationResponse = invoker.Invoke(bpelWSDL, eventHandlerName, params);

		RecoveryResult recoveryResult = new RecoveryResult(true, "Succesfully executed: call(" + bpelWSDL + "," +
																									eventHandlerName + "," +
																									params +
																									")\n -->Process call result: " + 
																									serviceInvocationResponse + "\n");
		return recoveryResult;
	}
	
	private static String RetrieveServiceEndpoint(String wsdlUrl)
	{
		Map<String, DocInfo> map = new HashMap<String, DocInfo>();
		
		EntityResolver entityResolver = new EndpointEntityResolver(map);
		
		WSDLContext wsdlContext = null;
		try
		{
			wsdlContext = new WSDLContext(new URL(wsdlUrl), entityResolver);
		}
		catch (WebServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(wsdlContext.getEndpoint(wsdlContext.getServiceQName()));
		
		return wsdlContext.getEndpoint(wsdlContext.getServiceQName());
	}
}
