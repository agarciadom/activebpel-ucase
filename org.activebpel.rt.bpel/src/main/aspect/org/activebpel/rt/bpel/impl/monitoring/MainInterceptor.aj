
/* Copyright 2007, 2008 , DEEP SE group, Dipartimento di Elettronica e Informazione (DEI), Politecnico di Milano */


/*  
 *  Licence: 
 *
 *
 *  This file is part of  DYNAMO .
 *
 *
 *	DYNAMO is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DYNAMO is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DYNAMO.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package org.activebpel.rt.bpel.impl.monitoring;

import it.polimi.WSCoL.WSCoLLexer;
import it.polimi.WSCoL.WSCoLParser;
import it.polimi.monitor.Monitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.monitor.stubs.configurationmanager.ConfigurationManager;
import it.polimi.monitor.stubs.configurationmanager.ConfigurationManagerWS;
import it.polimi.monitor.stubs.configurationmanager.ConfigurationManagerWSLocator;
import it.polimi.monitor.stubs.configurationmanager.ProcessInfoWrapper;
import it.polimi.monitor.stubs.configurationmanager.SupervisionRuleInfoWrapper;
import it.polimi.monitor.stubs.configurationmanager.TemporaryRuleChangingInfoWrapper;
import it.polimi.monitor.stubs.monitorlogger.MonitorLogger;
import it.polimi.monitor.stubs.monitorlogger.MonitorLoggerWS;
import it.polimi.monitor.stubs.monitorlogger.MonitorLoggerWSLocator;
import it.polimi.monitor.stubs.monitorlogger.MonitoringResultInfoWrapper;
import it.polimi.monitor.stubs.monitorlogger.RecoveryResultInfoWrapper;
import it.polimi.monitor.util.Configuration;
import it.polimi.recovery.Recovery;
import it.polimi.recovery.XMLParser;
import it.polimi.recovery.data.ProcessParams;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.ServiceInvocationParams;
import it.polimi.recovery.data.SupervisionParams;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeExpressionBaseDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeBusinessProcessEngine;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.activity.AeActivityImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityPickImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.monitoring.wscolinterpreter.WSCoLBPEL_VAR;
import org.activebpel.rt.bpel.impl.monitoring.wscolinterpreter.WSCoLFinder;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.message.AeMessageData;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public privileged aspect MainInterceptor {
	private long respTime = 0;
	private long timerRespTime = 0;
	private long monitoringTime = 0;
	private long monitoringTimePost = 0;
	private long monitoringTimePre = 0;
	
	private long timerMonitoringTime = 0;
	private long dynamoPostTime = 0;
	private long dynamoPreTime = 0;
	private long timer=0;
	private long timerGlobal = 0;
	private long recoveryTime = 0;
	private long recoveryTimePre = 0;
	private long recoveryTimePost = 0;
	private long timerRecoveryTime = 0;
	//protected ProcessTimeAnalysisBean procPt;
	
	private static Configuration conf = new Configuration();
	private String configHVarExample = "<webservice>"
			+ "<wsdl>"+conf.getString("HistoricalVariable")+"</wsdl>"
			+ "<store_wm>createHistoricalVariable</store_wm>"
			+ "<retrieve_wm>findHistoricalVariable</retrieve_wm>";/*
																	 * + "<processID>process</processID>" + "<assertionType>0</assertionType>" + "<location>/pippo</location>" + "<userID>luca</userID>" + "<instanceID>1233</instanceID>" + "</webservice>";
																	 */
	private WSCoLFinder finder = new WSCoLFinder();
	private ProcessUsersList list;
	private HashMap<String, String> processWSDLList;
	private ConfigurationManager cm;
	private MonitorLogger ml;

	pointcut processExecution(AeBusinessProcess process):
		execution(public void AeBusinessProcess.execute()) && target(process);

	after(AeBusinessProcess process): processExecution(process){
		synchronized (this) {
			String processName;
			String processWSDLAddress;
			long id;
			String user;
			String priority;
			int pr = 0;

			processName = (process.getName()).getLocalPart();
			id = process.getProcessId();
			AeInboundReceive startingMessage = process.getCreateMessage();

			AeMessageData message = (AeMessageData) startingMessage
					.getMessageData();

			// System.out.println("questa e' il nome di chi usa il processo " +
			// (String) message.getData("name"));
			user = (String) message.getData("username");
			priority = (String) message.getData("priority");

			System.out.println("Username: " + user + " | process: "
					+ processName);

			if (user != null) {
				if (priority != null) {
					if (priority.matches("1")) {
						pr = 1;
					} else if (priority.matches("2")) {
						pr = 2;
					} else if (priority.matches("3")) {
						pr = 3;
					} else if (priority.matches("4")) {
						pr = 4;
					} else if (priority.matches("5")) {
						pr = 5;
					}
					list.addProcessUser(processName, id, user, pr);
				} else {
					list.addProcessUser(processName, id, user, 0);
				}

				// List of WSDL of the instantiated process
				if (!this.processWSDLList.containsKey(processName)) {
					processWSDLAddress = startingMessage.getContext()
							.getMyEndpointReference().getAddress()
							+ ".wsdl";
					this.processWSDLList.put(processName, processWSDLAddress);
					// System.out.println("PartnerEndpointReference address: " +
					// processWSDLAddress);
				}
			}
		}
	}

	pointcut processTermination(AeBusinessProcess process):
// (execution(public void AeBusinessProcess.terminate()) || execution(protected
// void AeBusinessProcess.processEnded(IAeFault))) && target(process);
		execution(protected void AeBusinessProcess.processEnded(IAeFault)) && target(process);

	before(AeBusinessProcess process): processTermination(process){
		synchronized (this) {
			String processName;
			long id;
			String user;
			processName = (process.getName()).getLocalPart();
			id = process.getProcessId();
			user = list.findUser(processName, id);
			list.deleteCouple(processName, id, user);

			System.out
					.println("Releasing temporary changes in supervision strategies for process '"
							+ processName
							+ "' (instance: "
							+ id
							+ ") and user '" + user + "'");
			try {
				this.cm.releaseTemporaryProcessChanges(new ProcessInfoWrapper(
						null, processName, id, user));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out
					.println("Released temporary changes in supervision strategies for process '"
							+ processName
							+ "' (instance: "
							+ id
							+ ") and user '" + user + "'");
		}
	}

	pointcut startEngine(AeBusinessProcessEngine processEngine):
		execution(public void AeBusinessProcessEngine.start()) && target(processEngine);

	after(AeBusinessProcessEngine processEngine): startEngine(processEngine){
//da eliminare
		/*if (procPt==null){
			ProcessTimeAnalysisBeanService procLocator=new ProcessTimeAnalysisBeanServiceLocator();
		try {
			this.procPt=procLocator.getProcessTimeAnalysisBeanPort();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}*/
		
		if (this.cm == null) {
			try {
				ConfigurationManagerWS cmLocator = new ConfigurationManagerWSLocator();
				this.cm = cmLocator.getConfigurationManagerPort();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (this.ml == null) {
			try {
				MonitorLoggerWS mlLocator = new MonitorLoggerWSLocator();
				this.ml = mlLocator.getMonitorLoggerPort();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (list == null) {
			list = new ProcessUsersList();
		}
		if (this.processWSDLList == null) {
			this.processWSDLList = new HashMap<String, String>();
		}
	}

	pointcut stopEngine(AeBusinessProcessEngine processEngine):
		execution(public void AeBusinessProcessEngine.stop()) && target(processEngine);

	after(AeBusinessProcessEngine processEngine): stopEngine(processEngine){
		if (this.cm != null) {
			this.cm = null;
		}
		list = null;
	}

	pointcut receiveCallObj(AeActivityReceiveImpl obj):
		execution(void AeActivityReceiveImpl.execute()) && target(obj);

	before(AeActivityReceiveImpl obj): receiveCallObj(obj){
		synchronized (this) {
			timerGlobal=timer=System.currentTimeMillis();
			MonitoringResult monitoringResult = null;
			Vector<Vector> allVariables = new Vector<Vector>();
			int i, j;
			int priority = 0;

			String XPath = obj.getLocationPath();
			String processName = ((obj.getProcess()).getName()).getLocalPart();
			long id = (obj.getProcess()).getProcessId();
			String userName = list.findUser(processName, id);

			String configHVar = this.configHVarExample + "<processID>"
					+ processName + "</processID>"
					+ "<assertionType>1</assertionType>" + "<location>" + XPath
					+ "</location>" + "<userID>" + userName + "</userID>"
					+ "<instanceID>" + id + "</instanceID>" + "</webservice>";

			if (!userName.equalsIgnoreCase("")) {

				ProcessInfoWrapper processInfoWrapper = this.getProcessParams(
						processName, userName, id);

				if (processInfoWrapper == null){
					dynamoPreTime=System.currentTimeMillis()-timer;
					return;
				}
				

				priority = processInfoWrapper.getPriority();

				Vector<String> espressioni = new Vector<String>();

				SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this
						.getSupervisionRule(processName, userName, XPath, true,
								id);

				if (supervisionRuleInfoWrapper != null)
					if (priority <= supervisionRuleInfoWrapper.getPriority()) 
						espressioni.add(supervisionRuleInfoWrapper
								.getWscolRule());
				for (i = 0; i < espressioni.size(); i++) {
					if (espressioni.get(i).contains("&amp;"))
						espressioni.add(i, espressioni.get(i).replaceAll(
								"&amp;", "&&"));
				}
				for (i = 0; i < espressioni.size(); i++) {
					System.out.println("Rule: " + (String) espressioni.get(i));
					WSCoLLexer lexer = new WSCoLLexer(new StringReader(
							(String) espressioni.get(i)));
					WSCoLParser parser = new WSCoLParser(lexer);

					try {
						parser.analyzer();
					} catch (RecognitionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TokenStreamException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					AST parsetree = parser.getAST();
					Vector<WSCoLBPEL_VAR> variables = finder
							.findAllBPELVar(parsetree);
					for (j = 0; j < variables.size(); j++) {
						WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables
								.get(j);
						// devo cercare la variabile nell'ambiente che le e'
						// visibile
						
						
						IAeVariable var = (obj.findEnclosingScope())
								.getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
											if (o instanceof Document) {
												Document doc = (Document) o;
												
												try {
													XmlObject xmlObj = XmlObject.Factory
															.parse(doc
																	.getFirstChild());
													String val = xmlObj.xmlText();
													if (currentVar.getValue() == null){
														currentVar.setValue(val);
														ite.next();
													} else {
														currentVar
																.setValue(val
																		+ currentVar
																				.getValue());
														ite.next();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												//Passo esclusivamente al prossimo Part
												//currentVar.setValue("not initialized");
												ite.next();
												}
										}
										/*
										 * currentVar.setValue("" +
										 * message.getData(currentVar
										 * .getXPath()));
										 */

									} else if (message.getData(currentVar
											.getXPath()) != null) {
										// se l'xpath e' semplice
										currentVar.setValue(""
												+ message.getData(currentVar
														.getXPath()));
									} else {
										// se l'xpath e' del tipo:
										// order/ns2:orderElement/OrderHeader/name
										// devo gestire il caso in cui ho un
										// xpath
										// order/ciccio
										// devo elaborare l'xpath per dividerlo
										// in
										// part
										// e query

										try {
											int f = 0;
											String part = "";
											String xpathrule = currentVar
													.getXPath();
											String queryrule;
											xpathrule.charAt(f);
											while (xpathrule.charAt(f) != '/') {
												part = part
														+ xpathrule.charAt(f);
												f++;
											}
											// questa condizione per distinguere
											// il caso
											// order/ns2:brand da order/brand e'
											// da verificare
											// ulteriormente con altri esempi
											if (xpathrule.contains(":")) {
												queryrule = xpathrule
														.substring(f);
											} else {
												queryrule = "/" + xpathrule;
											}
											Object data = getCopyVariableData(
													obj, var, part, queryrule);
											if (data == null) {
												queryrule = "/" + xpathrule;
												data = getCopyVariableData(obj,
														var, part, queryrule);
											}
											String value = ((Node) data)
													.getFirstChild()
													.getNodeValue();
											currentVar.setValue("" + value);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (AeBusinessProcessException e) {
									e.printStackTrace();
								}
							} else {
								currentVar.setValue("not initialized");
							}
						} else {
							System.out.println("la variabile "
									+ currentVar.getVar() + " non e' visibile");
						}

					}
					allVariables.add(variables);
				}

				if (espressioni.size() > 0) {
					System.out.println("Precondition -------------------------------------");
					System.out.print("Rules: ");
					for(String h : espressioni)
						System.out.println(h);
					
					monitoringResult = callMonitor(allVariables, espressioni,
							configHVar);
					// Logging
					MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

					monitoringResultInfoWrapper.setProcessID(processName);
					monitoringResultInfoWrapper.setUserID(userName);
					monitoringResultInfoWrapper.setLocation(XPath);
					monitoringResultInfoWrapper.setPrecondition(true);
					monitoringResultInfoWrapper.setProcessPriority(priority);
					monitoringResultInfoWrapper
							.setWscolRule(supervisionRuleInfoWrapper
									.getWscolRule());
					monitoringResultInfoWrapper
							.setWscolPriority(supervisionRuleInfoWrapper
									.getPriority());
					monitoringResultInfoWrapper
							.setProviders(supervisionRuleInfoWrapper
									.getProviders());
					monitoringResultInfoWrapper
							.setTimeFrame(supervisionRuleInfoWrapper
									.getTimeFrame());
					monitoringResultInfoWrapper
							.setMonitoringResult(monitoringResult.isResult());
					monitoringResultInfoWrapper
							.setMonitoringData(monitoringResult
									.getMonitoringData());
					monitoringResultInfoWrapper
							.setMonitoringTime(monitoringTime);
					monitoringTimePre=monitoringTime;
					monitoringTime=0;
					try {
						this.ml
								.insertNewMonitoringResult(monitoringResultInfoWrapper);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Recovery
					if (!monitoringResult.isResult()) {
						String recovery = supervisionRuleInfoWrapper
								.getRecoveryStrategy();

						if ((recovery == null) || (recovery.equals("")))
							recovery="{ignore()}";

						RecoveryResultWrapper recoveryResultWrapper = this
								.callRecovery(supervisionRuleInfoWrapper, null,
										monitoringResult.getMonitoringData(),
										this.processWSDLList.get(processName),
										id, priority, configHVar,
										monitoringResult.getAliases(),
										monitoringResult.getTempAliases());

						// Logging Recovery
						RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(
								supervisionRuleInfoWrapper
										.getRecoveryStrategy(), null,
								recoveryResultWrapper.getRecoveryMessage(),
								XPath, false, processName, recoveryTime,
								recoveryResultWrapper.isRecoveryResult(),
								userName);
						recoveryTimePre=recoveryTime;
						recoveryTime=0;
						try {
							this.ml
									.insertNewRecoveryResult(recoveryResultInfoWrapper);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						if (recoveryResultWrapper.isRecoveryResult()) {
							if (recoveryResultWrapper.isIgnore()) {
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							if (recoveryResultWrapper.isTerminateProcess()) {
								this.terminateProcess((obj.getProcess()),
										userName);
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							// In Receive/Reply/Pick actions it's obviously not
							// logical a rebind.
							// if(recoveryResultWrapper.isRebindService())
							// {
							// this.rebindService(partnerLinkName, process,
							// newServiceEndpoint);
							// }

							// It's not an invoke action
							// if(recoveryResultWrapper.isServiceInvocation())
							// {
							// //Substitute in output variable
							// }
						}
					}
				}
			}
			dynamoPreTime=System.currentTimeMillis()-timer;
			}
	}

	pointcut invokeCallObj(AeActivityInvokeImpl obj):
		execution(void AeActivityInvokeImpl.execute()) && target(obj);

	/*pointcut invokeObjectCompleted(AeActivityInvokeImpl obj):
		execution(void AeActivityInvokeImpl.objectCompleted()) && target(obj);
	*/
	pointcut activityObjectCompleted(AeActivityImpl obj):
		execution(void AeActivityImpl.objectCompleted()) && target(obj);
	
	void around(AeActivityImpl obj): activityObjectCompleted(obj){
		if (obj instanceof AeActivityInvokeImpl) {
			respTime = System.currentTimeMillis() - timerRespTime;
			timer=System.currentTimeMillis();
			AeActivityInvokeImpl invoke = (AeActivityInvokeImpl) obj;
			monitorInvokePostCondition(invoke);
			dynamoPostTime=System.currentTimeMillis()-timer;
			monitoringTimePost=monitoringTime;
			recoveryTimePost=recoveryTime;
			monitoringTime=0;
			recoveryTime=0;
			addProcessAnalysis(obj.getLocationPath());
		} if (obj instanceof AeActivityReceiveImpl) {
			timer=System.currentTimeMillis();
			AeActivityReceiveImpl receive = (AeActivityReceiveImpl) obj;
			monitorReceivePostCondition(receive);
			monitoringTimePost=monitoringTime;
			recoveryTimePost=recoveryTime;
			monitoringTime=0;
			recoveryTime=0;
			dynamoPostTime=System.currentTimeMillis()-timer;
			addProcessAnalysis(obj.getLocationPath());
		}
		proceed(obj);
	}
	
	
	private void monitorInvokePostCondition(AeActivityInvokeImpl obj) {
		//System.err.println("Search Supervisione Rules");
		MonitoringResult monitoringResult = null;
		Vector<Vector> allVariables = new Vector<Vector>();
		int i, j;
		int priority = 0;

		String XPath = obj.getLocationPath();
		String processName = ((obj.getProcess()).getName()).getLocalPart();
		long id = (obj.getProcess()).getProcessId();
		String userName = list.findUser(processName, id);

		String configHVar = this.configHVarExample + "<processID>"+ processName + "</processID>"+ "<assertionType>0</assertionType>" + 
			"<location>" + XPath + "</location>" + "<userID>" + userName + "</userID>" + "<instanceID>" + id + "</instanceID>" + "</webservice>";

		// --------------------------------------------------------------------------------------
		// AREA DI TEST

		// --------------------------------------------------------------------------------------
		if (!userName.equalsIgnoreCase("")) {
			ProcessInfoWrapper processInfoWrapper = this.getProcessParams(processName, userName, id);
			if (processInfoWrapper == null)
				return;
						
			priority = processInfoWrapper.getPriority();
			Vector<String> espressioni = new Vector<String>();

			SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this.getSupervisionRule(processName, userName, XPath, false, id);
			if (supervisionRuleInfoWrapper != null) {
				if (priority <= supervisionRuleInfoWrapper.getPriority()) {
					espressioni.add(supervisionRuleInfoWrapper.getWscolRule());
					//System.err.println("Find Supervisione Rules");
				}
			}
			
			
			for (i = 0; i < espressioni.size(); i++) {

				if (espressioni.get(i).contains("&amp;"))
					espressioni.add(i, espressioni.get(i).replaceAll("&amp;","&&"));
			}
			for (i = 0; i < espressioni.size(); i++) {
				System.out.println("Rule: " + (String) espressioni.get(i));

				WSCoLLexer lexer = new WSCoLLexer(new StringReader((String) espressioni.get(i)));
				WSCoLParser parser = new WSCoLParser(lexer);

				try {
					parser.analyzer();
				} catch (RecognitionException e) {
					e.printStackTrace();
				} catch (TokenStreamException e) {
					e.printStackTrace();
				}
				AST parsetree = parser.getAST();
				Vector<WSCoLBPEL_VAR> variables = finder.findAllBPELVar(parsetree);
				for (j = 0; j < variables.size(); j++) {
					WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables.get(j);
					
					// devo cercare la variabile nell'ambiente che le e'
					// visibile
					
					IAeVariable var = (obj.findEnclosingScope()).getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							//System.out.println("Nome della variabile:" + var.getName());
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
										if (o instanceof Document) {
											Document doc = (Document) o;

											try {
												XmlObject xmlObj = XmlObject.Factory.parse(doc.getFirstChild());
												String val = xmlObj.xmlText();
												if (currentVar.getValue() == null){
													currentVar.setValue(val);
													ite.next();
												} else {
													currentVar.setValue(val	+ currentVar.getValue());
													ite.next();
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										} else {
											//Passo esclusivamente al prossimo Part
											//currentVar.setValue("not initialized");
											ite.next();
										}
									}
									/*
									 * currentVar.setValue("" +
									 * message.getData(currentVar .getXPath()));
									 */

								} else if (message.getData(currentVar
										.getXPath()) != null) {
									// se l'xpath e' semplice
									currentVar.setValue(""+ message.getData(currentVar.getXPath()));
								} else {
									// se l'xpath e' del tipo: order/ns2:orderElement/OrderHeader/name
									// devo gestire il caso in cui ho un xpath order/ciccio devo elaborare l'xpath per dividerlo
									// in part e query

									try {
										int f = 0;
										String part = "";
										String xpathrule = currentVar
												.getXPath();
										String queryrule;

										xpathrule.charAt(f);
										while (xpathrule.charAt(f) != '/') {
											part = part + xpathrule.charAt(f);
											f++;
										}
										// questa condizione per distinguere il caso order/ns2:brand da order/brand e'
										// da verificare ulteriormente con altri esempi
										if (xpathrule.contains(":")) {
											queryrule = xpathrule.substring(f);
										} else {
											queryrule = "/" + xpathrule;
										}
										Object data = getCopyVariableData(obj,var, part, queryrule);
										if (data == null) {
											queryrule = "/" + xpathrule;
											data = getCopyVariableData(obj,var, part, queryrule);
										}

										String value = ((Node) data).getFirstChild().getNodeValue();
										currentVar.setValue("" + value);

									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (AeBusinessProcessException e) {
								e.printStackTrace();
							}
						} else {
							currentVar.setValue("not initialized");
						}
					} else {
						System.out.println("la variabile "+ currentVar.getVar() + " non e' visibile");
					}

					System.out.println("currentVar: " + currentVar.getValue());

				}
				allVariables.add(variables);
			}

			if (espressioni.size() > 0) {
				System.out.println("Postcondition -------------------------------------");
				System.out.print("Rules: ");
				for(String h : espressioni)
					System.out.println(h);
				monitoringResult = callMonitor(allVariables, espressioni,configHVar);

				System.out.println("Monitoring result: "+ monitoringResult.isResult());

				// Logging
				MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

				monitoringResultInfoWrapper.setProcessID(processName);
				monitoringResultInfoWrapper.setUserID(userName);
				monitoringResultInfoWrapper.setLocation(XPath);
				monitoringResultInfoWrapper.setPrecondition(false);
				monitoringResultInfoWrapper.setProcessPriority(priority);
				monitoringResultInfoWrapper.setWscolRule(supervisionRuleInfoWrapper.getWscolRule());
				monitoringResultInfoWrapper.setWscolPriority(supervisionRuleInfoWrapper.getPriority());
				monitoringResultInfoWrapper.setProviders(supervisionRuleInfoWrapper.getProviders());
				monitoringResultInfoWrapper.setTimeFrame(supervisionRuleInfoWrapper.getTimeFrame());
				monitoringResultInfoWrapper.setMonitoringResult(monitoringResult.isResult());
				monitoringResultInfoWrapper.setMonitoringData(monitoringResult.getMonitoringData());
				monitoringResultInfoWrapper.setMonitoringTime(monitoringTime);

				try {
					this.ml.insertNewMonitoringResult(monitoringResultInfoWrapper);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				// Recovery
				if (!monitoringResult.isResult()) {
					String recovery = supervisionRuleInfoWrapper.getRecoveryStrategy();

					if (((recovery == null) || (recovery.equals(""))))
						recovery="{ignore()}";

					final String inputVariable = ((AeActivityInvokeDef)obj.getDefinition()).getInputVariable();
					AeVariable variable = (AeVariable)obj.getProcess().getVariable(obj.getLocationPath(), inputVariable);
					AeBusinessProcess process = (AeBusinessProcess) obj.getProcess();

					// Need WSDL of invoke and operation ...
					final AeBPELExtendedWSDLDef aeBPELExtendedWSDLDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(process.getProcessPlan(), variable.getMessageType());
					String serviceWSDL = aeBPELExtendedWSDLDef.getWSDLDef().getDocumentBaseURI();

					// System.out.println("WSDL url?? " +
					// aeBPELExtendedWSDLDef.getWSDLDef().getDocumentBaseURI());

					String operation = obj.getDef().getOperation();
					// System.out.println("Operation: " + operation);

					String serviceInvocationInput = null;
					try {
						Document document = process.serializeVariable(variable);

						serviceInvocationInput = "<InvokeServiceParameters>"+ this.variableToString(document)+ "</InvokeServiceParameters>";
					} catch (AeBusinessProcessException e) {
						e.printStackTrace();
					}

					ServiceInvocationParams serviceInvocationParam = new ServiceInvocationParams(serviceWSDL, operation, serviceInvocationInput);

					RecoveryResultWrapper recoveryResultWrapper = this.callRecovery(supervisionRuleInfoWrapper, serviceInvocationParam, 
															monitoringResult.getMonitoringData(),this.processWSDLList.get(processName), id,
															priority, configHVar, monitoringResult.getAliases(), monitoringResult.getTempAliases());
					// Logging Recovery
					RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(supervisionRuleInfoWrapper.getRecoveryStrategy(),
												null, recoveryResultWrapper.getRecoveryMessage(),XPath, false, processName, recoveryTime,
												recoveryResultWrapper.isRecoveryResult(), userName);
					try {
						this.ml.insertNewRecoveryResult(recoveryResultInfoWrapper);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

					if (recoveryResultWrapper.isRecoveryResult()) {
						if (recoveryResultWrapper.isIgnore()) {
							return;
						}

						if (recoveryResultWrapper.isTerminateProcess()) {
							this.terminateProcess((obj.getProcess()), userName);
							return;
						}

						// Rebind the service to another endpoint, but only for the current instance of bpel
						if (recoveryResultWrapper.isRebindService()) {
							System.out.println("Binding service: " +recoveryResultWrapper.getNewServiceEndpoint());
							this.rebindService(obj.getDef().getPartnerLink(), process, recoveryResultWrapper.getNewServiceEndpoint());
						}
						
						if (recoveryResultWrapper.isServiceInvocation()) {
							// Substitute in output variable
							XMLParser parser = new XMLParser();

							parser.SetXML(recoveryResultWrapper.getServiceInvocationResult());

							AeVariable outputVar = (AeVariable) obj.getOutputVariable();

							AeMessageData messageData = null;
							try {
								messageData = (AeMessageData) outputVar.getMessageData();
							} catch (AeBusinessProcessException e) {
								e.printStackTrace();
							}

							Iterator partNames = messageData.getPartNames();

							while (partNames.hasNext()) {
								String partName = (String) partNames.next();
								Object data = messageData.getData(partName);

								if (data instanceof DocumentImpl) {
									DocumentImpl newData = (DocumentImpl) data;

									System.out.println("newData pre-update: "+ this.variableToString(newData));

									NodeList children = newData.getChildNodes();

									for (int l = 0; l < children.getLength(); l++) {
										Node child = children.item(l);

										this.substituteValuesInVariables(child,	parser, "Response");
									}

									System.out.println("newData: "+ this.variableToString(newData));

									messageData.setData(partName, newData);
								} else {
									System.out.println("Variable part (" + outputVar.getName() + "/" + partName	+ ") is a "	+ data.getClass()
											+ " --> Substitute the value: "	+ parser.GetValue("Response/" + partName));
									messageData.setData(partName, parser.GetValue("Response/" + partName));
								}
							}
						}
					}
				}
			}
		}
	}
	private void monitorReceivePostCondition(AeActivityReceiveImpl obj) {
		synchronized (this) {
			MonitoringResult monitoringResult = null;
			String XPath = obj.getLocationPath();
			Vector<Vector> allVariables = new Vector<Vector>();
			int i, j;
			int priority = 0;
			String processName = ((obj.getProcess()).getName()).getLocalPart();
			long id = (obj.getProcess()).getProcessId();
			String userName = list.findUser(processName, id);

			String configHVar = this.configHVarExample + "<processID>"
					+ processName + "</processID>"	+ "<assertionType>0</assertionType>" + "<location>" + XPath
					+ "</location>" + "<userID>" + userName + "</userID>"+ "<instanceID>" + id + "</instanceID>" + "</webservice>";

			if (!userName.equalsIgnoreCase("")) {

				ProcessInfoWrapper processInfoWrapper = this.getProcessParams(processName, userName, id);

				if (processInfoWrapper == null)
					return;

				priority = processInfoWrapper.getPriority();

				Vector<String> espressioni = new Vector<String>();

				SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this.getSupervisionRule(processName, userName, XPath,false, id);

				if (supervisionRuleInfoWrapper != null){
					if (priority <= supervisionRuleInfoWrapper.getPriority()) 
						espressioni.add(supervisionRuleInfoWrapper.getWscolRule());
				//System.err.println("Find Supervisione Rules "+supervisionRuleInfoWrapper.getWscolRule()+" "+supervisionRuleInfoWrapper.getUserID());
				}
				
				for (i = 0; i < espressioni.size(); i++) {
					if (espressioni.get(i).contains("&amp;"))
						espressioni.add(i, espressioni.get(i).replaceAll("&amp;", "&&"));
				}
				for (i = 0; i < espressioni.size(); i++) {
					System.out.println("Rule: " + (String) espressioni.get(i));
					WSCoLLexer lexer = new WSCoLLexer(new StringReader((String) espressioni.get(i)));
					WSCoLParser parser = new WSCoLParser(lexer);

					try {
						parser.analyzer();
					} catch (RecognitionException e) {
						e.printStackTrace();
					} catch (TokenStreamException e) {
						e.printStackTrace();
					}
					AST parsetree = parser.getAST();
					Vector<WSCoLBPEL_VAR> variables = finder.findAllBPELVar(parsetree);
					for (j = 0; j < variables.size(); j++) {
						WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables.get(j);
						// devo cercare la variabile nell'ambiente che le e' visibile
						
						IAeVariable var = (obj.findEnclosingScope()).getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
											if (o instanceof Document) {
												Document doc = (Document) o;

												try {
													XmlObject xmlObj = XmlObject.Factory.parse(doc.getFirstChild());
													String val = xmlObj.xmlText();
													if (currentVar.getValue() == null){
														currentVar.setValue(val);
														ite.next();
													} else {
														currentVar.setValue(val	+ currentVar.getValue());
														ite.next();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												//Passo esclusivamente al prossimo Part
												//currentVar.setValue("not initialized");
												ite.next();
												}
										}
										/*
										 * currentVar.setValue("" +
										 * message.getData(currentVar .getXPath()));
										 */

									} else if (message.getData(currentVar.getXPath()) != null) {
									//	System.out.println("Simple var");
										// se l'xpath e' semplice
										currentVar.setValue(""+ message.getData(currentVar.getXPath()));
									} else {

										// se l'xpath e' del tipo: order/ns2:orderElement/OrderHeader/name
										// devo gestire il caso in cui ho un xpath order/ciccio
										// devo elaborare l'xpath per dividerlo in part e query

										try {
											int f = 0;
											String part = "";
											String xpathrule = currentVar.getXPath();
											String queryrule;

											xpathrule.charAt(f);
											while (xpathrule.charAt(f) != '/') {
												part = part + xpathrule.charAt(f);
												f++;
											}
											// questa condizione per distinguere il caso order/ns2:brand da order/brand e'
											// da verificare ulteriormente con altri esempi
											if (xpathrule.contains(":")) {
												queryrule = xpathrule.substring(f);
											} else {
												queryrule = "/" + xpathrule;
											}
											Object data = getCopyVariableData(obj, var, part, queryrule);
											if (data == null) {
												queryrule = "/" + xpathrule;
												data = getCopyVariableData(obj,	var, part, queryrule);
											}

											String value = ((Node) data).getFirstChild().getNodeValue();
											currentVar.setValue("" + value);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (AeBusinessProcessException e) {
									e.printStackTrace();
								}
							} else {
								currentVar.setValue("not initialized");
							}
						} else {
							System.out.println("la variabile "+ currentVar.getVar() + " non e' visibile");
						}

						System.out.println("currentVar: " + currentVar.getValue());

					}
					allVariables.add(variables);
				}

				if (espressioni.size() > 0) {
					System.out.println("Postcondition -------------------------------------");
					System.out.print("Rules: ");
					for(String h : espressioni)
						System.out.println(h);
					monitoringResult = callMonitor(allVariables, espressioni, configHVar);

					// Logging
					MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

					monitoringResultInfoWrapper.setProcessID(processName);
					monitoringResultInfoWrapper.setUserID(userName);
					monitoringResultInfoWrapper.setLocation(XPath);
					monitoringResultInfoWrapper.setPrecondition(false);
					monitoringResultInfoWrapper.setProcessPriority(priority);
					monitoringResultInfoWrapper.setWscolRule(supervisionRuleInfoWrapper.getWscolRule());
					monitoringResultInfoWrapper.setWscolPriority(supervisionRuleInfoWrapper.getPriority());
					monitoringResultInfoWrapper.setProviders(supervisionRuleInfoWrapper.getProviders());
					monitoringResultInfoWrapper.setTimeFrame(supervisionRuleInfoWrapper.getTimeFrame());
					monitoringResultInfoWrapper.setMonitoringResult(monitoringResult.isResult());
					monitoringResultInfoWrapper.setMonitoringData(monitoringResult.getMonitoringData());
					monitoringResultInfoWrapper.setMonitoringTime(monitoringTime);

					try {
						this.ml.insertNewMonitoringResult(monitoringResultInfoWrapper);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (!monitoringResult.isResult()) {
						String recovery = supervisionRuleInfoWrapper.getRecoveryStrategy();

						if ((recovery == null) || (recovery.equals("")))
							recovery="{ignore()};";
							
						RecoveryResultWrapper recoveryResultWrapper = this.callRecovery(supervisionRuleInfoWrapper, null,monitoringResult.getMonitoringData(),
										this.processWSDLList.get(processName), id, priority, configHVar, monitoringResult.getAliases(),	monitoringResult.getTempAliases());

						// Logging Recovery
						RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(supervisionRuleInfoWrapper.getRecoveryStrategy(), null,
								recoveryResultWrapper.getRecoveryMessage(),	XPath, false, processName, recoveryTime, recoveryResultWrapper.isRecoveryResult(),
								userName);
						try {
							this.ml.insertNewRecoveryResult(recoveryResultInfoWrapper);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}

						if (recoveryResultWrapper.isRecoveryResult()) {
							if (recoveryResultWrapper.isIgnore()) {
								return;
							}

							if (recoveryResultWrapper.isTerminateProcess()) {
								this.terminateProcess((obj.getProcess()),userName);
								return;
							}

							// In Receive/Reply/Pick actions it's obviously not
							// logical a rebind.
							// if(recoveryResultWrapper.isRebindService())
							// {
							// this.rebindService(partnerLinkName, process, newServiceEndpoint);
							// }

							// It's not an invoke action
							// if(recoveryResultWrapper.isServiceInvocation())
							// {
							// //Substitute in output variable
							// }
						}
					}
				}
			}
			
		}
	}
	
		
	before(AeActivityInvokeImpl obj): invokeCallObj(obj){
		synchronized (this) {
			timerGlobal=timer=System.currentTimeMillis();
			MonitoringResult monitoringResult = null;
			Vector<Vector> allVariables = new Vector<Vector>();
			int i, j;
			int priority = 0;

			String XPath = obj.getLocationPath();
			String processName = ((obj.getProcess()).getName()).getLocalPart();
			long id = (obj.getProcess()).getProcessId();
			String userName = list.findUser(processName, id);

			String configHVar = this.configHVarExample + "<processID>"	+ processName + "</processID>" + "<assertionType>1</assertionType>" + "<location>" + XPath
					+ "</location>" + "<userID>" + userName + "</userID>" + "<instanceID>" + id + "</instanceID>" + "</webservice>";

			if (!userName.equalsIgnoreCase("")) {

				ProcessInfoWrapper processInfoWrapper = this.getProcessParams(processName, userName, id);

				if (processInfoWrapper == null){
					dynamoPreTime=System.currentTimeMillis()-timer;
					return;
				}
				priority = processInfoWrapper.getPriority();

				Vector<String> espressioni = new Vector<String>();

				SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this.getSupervisionRule(processName, userName, XPath, true,	id);

				if (supervisionRuleInfoWrapper != null)
					if (priority <= supervisionRuleInfoWrapper.getPriority()) 
						espressioni.add(supervisionRuleInfoWrapper.getWscolRule());
				for (i = 0; i < espressioni.size(); i++) {
					if (espressioni.get(i).contains("&amp;"))
						espressioni.add(i, espressioni.get(i).replaceAll("&amp;", "&&"));
				}

				for (i = 0; i < espressioni.size(); i++) {
					System.out.println((String) espressioni.get(i));

					WSCoLLexer lexer = new WSCoLLexer(new StringReader((String) espressioni.get(i)));
					WSCoLParser parser = new WSCoLParser(lexer);

					try {
						parser.analyzer();
					} catch (RecognitionException e) {
						e.printStackTrace();
					} catch (TokenStreamException e) {
						e.printStackTrace();
					}

					AST parsetree = parser.getAST();
					Vector<WSCoLBPEL_VAR> variables = finder.findAllBPELVar(parsetree);
					
					for (j = 0; j < variables.size(); j++) {
						WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables.get(j);
						// devo cercare la variabile nell'ambiente che le e' visibile
						
						
						IAeVariable var = (obj.findEnclosingScope()).getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
											if (o instanceof Document) {
												Document doc = (Document) o;

												try {
													XmlObject xmlObj = XmlObject.Factory.parse(doc.getFirstChild());
													String val = xmlObj.xmlText();
													if (currentVar.getValue() == null){
														currentVar.setValue(val);
														ite.next();
													} else {
														currentVar.setValue(val	+ currentVar.getValue());
														ite.next();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												//Passo esclusivamente al prossimo Part
												//currentVar.setValue("not initialized");
												ite.next();
												}
										}
										/*
										 * currentVar.setValue("" +	 * message.getData(currentVar * .getXPath()));
										 */

									} else if (message.getData(currentVar.getXPath()) != null) {
										// se l'xpath e' semplice
										currentVar.setValue("" + message.getData(currentVar.getXPath()));
									} else {
										// se l'xpath e' del tipo: order/ns2:orderElement/OrderHeader/name
										// devo gestire il caso in cui ho un xpath order/ciccio
										// devo elaborare l'xpath per dividerlo in part e query

										try {
											int f = 0;
											String part = "";
											String xpathrule = currentVar.getXPath();
											String queryrule;
											xpathrule.charAt(f);
											while (xpathrule.charAt(f) != '/') {
												part = part	+ xpathrule.charAt(f);
												f++;
											}
											// questa condizione per distinguere il caso
											// order/ns2:brand da order/brand e' da verificare ulteriormente con altri esempi
											if (xpathrule.contains(":")) {
												queryrule = xpathrule.substring(f);
											} else {
												queryrule = "/" + xpathrule;
											}
											Object data = getCopyVariableData(obj, var, part, queryrule);
											if (data == null) {
												queryrule = "/" + xpathrule;
												data = getCopyVariableData(obj,	var, part, queryrule);
											}
											String value = ((Node) data).getFirstChild().getNodeValue();
											currentVar.setValue("" + value);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (AeBusinessProcessException e) {
									e.printStackTrace();
								}
							} else {
								currentVar.setValue("not initialized");
							}
						} else {
							System.out.println("la variabile "+ currentVar.getVar() + " non e' visibile");
						}

					}
					allVariables.add(variables);
				}

				if (espressioni.size() > 0) {
					System.out.println("Precondition -------------------------------------");
					System.out.print("Rules: ");
					for(String h : espressioni)
						System.out.println(h);
					
					monitoringResult = callMonitor(allVariables, espressioni, configHVar);

					// Logging
					MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

					monitoringResultInfoWrapper.setProcessID(processName);
					monitoringResultInfoWrapper.setUserID(userName);
					monitoringResultInfoWrapper.setLocation(XPath);
					monitoringResultInfoWrapper.setPrecondition(true);
					monitoringResultInfoWrapper.setProcessPriority(priority);
					monitoringResultInfoWrapper.setWscolRule(supervisionRuleInfoWrapper.getWscolRule());
					monitoringResultInfoWrapper.setWscolPriority(supervisionRuleInfoWrapper.getPriority());
					monitoringResultInfoWrapper.setProviders(supervisionRuleInfoWrapper.getProviders());
					monitoringResultInfoWrapper.setTimeFrame(supervisionRuleInfoWrapper.getTimeFrame());
					monitoringResultInfoWrapper.setMonitoringResult(monitoringResult.isResult());
					monitoringResultInfoWrapper.setMonitoringData(monitoringResult.getMonitoringData());
					monitoringResultInfoWrapper.setMonitoringTime(monitoringTime);
					monitoringTimePre=monitoringTime;
					monitoringTime=0;
					
					try {
						this.ml.insertNewMonitoringResult(monitoringResultInfoWrapper);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					// Recovery
					if (!monitoringResult.isResult()) {
						String recovery = supervisionRuleInfoWrapper.getRecoveryStrategy();

						if ((recovery == null) || (recovery.equals("")))
							recovery="{ignore()}";

						final String inputVariable = ((AeActivityInvokeDef)obj.getDefinition()).getInputVariable();
						AeVariable variable = (AeVariable)obj.getProcess().getVariable(obj.getLocationPath(), inputVariable);
						AeBusinessProcess process = (AeBusinessProcess) obj.getProcess();

						// Need WSDL of invoke and operation ...
						final AeBPELExtendedWSDLDef aeBPELExtendedWSDLDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(process.getProcessPlan(), variable.getMessageType());
						String serviceWSDL = aeBPELExtendedWSDLDef.getWSDLDef().getDocumentBaseURI();
						// System.out.println("WSDL url?? " + aeBPELExtendedWSDLDef.getWSDLDef().getDocumentBaseURI());

						String operation = obj.getDef().getOperation();
						// System.out.println("Operation: " + operation);

						String serviceInvocationInput = null;
						try {
							Document document = process.serializeVariable(variable);

							serviceInvocationInput = "<InvokeServiceParameters>" + this.variableToString(document) + "</InvokeServiceParameters>";
						} catch (AeBusinessProcessException e) {
							e.printStackTrace();
						}

						ServiceInvocationParams serviceInvocationParam = new ServiceInvocationParams(serviceWSDL, operation, serviceInvocationInput);

						RecoveryResultWrapper recoveryResultWrapper = this.callRecovery(supervisionRuleInfoWrapper,	serviceInvocationParam,
								monitoringResult.getMonitoringData(), this.processWSDLList.get(processName), id, priority, configHVar,
										monitoringResult.getAliases(),monitoringResult.getTempAliases());
						// Logging Recovery
						RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(supervisionRuleInfoWrapper.getRecoveryStrategy(), null,
								recoveryResultWrapper.getRecoveryMessage(),	XPath, false, processName, recoveryTime, recoveryResultWrapper.isRecoveryResult(),
								userName);
						recoveryTimePre=recoveryTime;
						recoveryTime=0;
						try {
							this.ml.insertNewRecoveryResult(recoveryResultInfoWrapper);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}

						if (recoveryResultWrapper.isRecoveryResult()) {
							if (recoveryResultWrapper.isIgnore()) {
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							if (recoveryResultWrapper.isTerminateProcess()) {
								this.terminateProcess((obj.getProcess()),userName);
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							// Rebind the service to another endpoint, but only
							// for the current instance of bpel
							if (recoveryResultWrapper.isRebindService()) {
								System.out.println("Binding service: " + recoveryResultWrapper.getNewServiceEndpoint());
								this.rebindService(obj.getDef().getPartnerLink(), process,recoveryResultWrapper.getNewServiceEndpoint());
							}

							// Not logical to substitute the output of the service invocation, before the invoke executed by
							// the engine
							// if(recoveryResultWrapper.isServiceInvocation())
							// {
							//
							// }
						}
					}
				}
			}
			dynamoPreTime=System.currentTimeMillis()-timer;
			// Timer for resptime start
			timerRespTime = System.currentTimeMillis();
		}
	}

	pointcut pickCallObj(AeActivityPickImpl obj):
		execution(void AeActivityPickImpl.execute()) && target(obj);

	pointcut pickOnMessage(AeOnMessage.AeMessageDispatcher dispatcher):
		execution(void onMessage(IAeMessageData)) && target(dispatcher);

	after(AeOnMessage.AeMessageDispatcher dispatcher): pickOnMessage(dispatcher) {
		synchronized (this) {
			final AeOnMessage obj = (AeOnMessage)dispatcher.getTarget();
			
			timer=System.currentTimeMillis();
			// da vedere se le regole post sulla pick vanno messe col xpath
			// della
			// pick stessa o dell'onMessage figlio. Per ora uso quello del
			// figlio. per usare il pick basta fare obj=obj.getParent();
			MonitoringResult monitoringResult = null;
			Vector<Vector> allVariables = new Vector<Vector>();
			int i, j;
			int priority = 0;

			String XPath = obj.getLocationPath();
			String processName = ((obj.getProcess()).getName()).getLocalPart();
			long id = (obj.getProcess()).getProcessId();
			String userName = list.findUser(processName, id);

			String configHVar = this.configHVarExample + "<processID>"	+ processName + "</processID>"	+ "<assertionType>0</assertionType>" + "<location>" + XPath
					+ "</location>" + "<userID>" + userName + "</userID>" + "<instanceID>" + id + "</instanceID>" + "</webservice>";

			if (!userName.equalsIgnoreCase("")) {

				ProcessInfoWrapper processInfoWrapper = this.getProcessParams(processName, userName, id);

				if (processInfoWrapper == null){
					dynamoPostTime=System.currentTimeMillis()-timer;
					addProcessAnalysis(obj.getLocationPath());
					return;
				}

				priority = processInfoWrapper.getPriority();

				Vector<String> espressioni = new Vector<String>();

				SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this.getSupervisionRule(processName, userName, XPath,false, id);

				if (supervisionRuleInfoWrapper != null)
					if (priority <= supervisionRuleInfoWrapper.getPriority())
						espressioni.add(supervisionRuleInfoWrapper.getWscolRule());
				for (i = 0; i < espressioni.size(); i++) {
					if (espressioni.get(i).contains("&amp;"))
						espressioni.add(i, espressioni.get(i).replaceAll("&amp;", "&&"));
				}

				for (i = 0; i < espressioni.size(); i++) {
					System.out.println((String) espressioni.get(i));

					WSCoLLexer lexer = new WSCoLLexer(new StringReader((String) espressioni.get(i)));
					WSCoLParser parser = new WSCoLParser(lexer);

					try {
						parser.analyzer();
					} catch (RecognitionException e) {
						e.printStackTrace();
					} catch (TokenStreamException e) {
						e.printStackTrace();
					}

					AST parsetree = parser.getAST();
					Vector<WSCoLBPEL_VAR> variables = finder.findAllBPELVar(parsetree);
					for (j = 0; j < variables.size(); j++) {
						WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables.get(j);
						System.out.println("\n\n\nMappa delle variabili: ");
						// devo cercare la variabile nell'ambiente che le e' visibile
						
						IAeVariable var = (obj.findEnclosingScope()).getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
											if (o instanceof Document) {
												Document doc = (Document) o;

												try {
													XmlObject xmlObj = XmlObject.Factory.parse(doc.getFirstChild());
													String val = xmlObj.xmlText();
													if (currentVar.getValue() == null){
														currentVar.setValue(val);
														ite.next();
													} else {
														currentVar.setValue(val	+ currentVar.getValue());
														ite.next();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												//Passo esclusivamente al prossimo Part
												//currentVar.setValue("not initialized");
												ite.next();
												}
										}
										/*
										 * currentVar.setValue("" +	 * message.getData(currentVar * .getXPath()));
										 */

									} else if (message.getData(currentVar.getXPath()) != null) {
										// se l'xpath e' semplice
										currentVar.setValue(""	+ message.getData(currentVar.getXPath()));
									} else {
										// se l'xpath e' del tipo: order/ns2:orderElement/OrderHeader/name
										// devo gestire il caso in cui ho un xpath order/ciccio
										// devo elaborare l'xpath per dividerlo in part e query

										try {
											int f = 0;
											String part = "";
											String xpathrule = currentVar.getXPath();
											String queryrule;
											xpathrule.charAt(f);
											while (xpathrule.charAt(f) != '/') {
												part = part	+ xpathrule.charAt(f);
												f++;
											}
											// questa condizione per distinguere il caso order/ns2:brand da order/brand e'
											// da verificare ulteriormente con altri esempi
											if (xpathrule.contains(":")) {
												queryrule = xpathrule.substring(f);
											} else {
												queryrule = "/" + xpathrule;
											}
											Object data = getCopyVariableData(obj, var, part, queryrule);
											if (data == null) {
												queryrule = "/" + xpathrule;
												data = getCopyVariableData(obj,	var, part, queryrule);
											}
											String value = ((Node) data).getFirstChild().getNodeValue();
											currentVar.setValue("" + value);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (AeBusinessProcessException e) {
									e.printStackTrace();
								}
							} else {
								currentVar.setValue("not initialized");
							}
						} else {
							System.out.println("la variabile " + currentVar.getVar() + " non e' visibile");
						}

					}
					allVariables.add(variables);
				}

				if (espressioni.size() > 0) {
					System.out.println("Postcondition -------------------------------------");
					System.out.print("Rules: ");
					for(String h : espressioni)
						System.out.println(h);
					
					monitoringResult = callMonitor(allVariables, espressioni, configHVar);

					// Logging
					MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

					monitoringResultInfoWrapper.setProcessID(processName);
					monitoringResultInfoWrapper.setUserID(userName);
					monitoringResultInfoWrapper.setLocation(XPath);
					monitoringResultInfoWrapper.setPrecondition(false);
					monitoringResultInfoWrapper.setProcessPriority(priority);
					monitoringResultInfoWrapper.setWscolRule(supervisionRuleInfoWrapper.getWscolRule());
					monitoringResultInfoWrapper.setWscolPriority(supervisionRuleInfoWrapper.getPriority());
					monitoringResultInfoWrapper.setProviders(supervisionRuleInfoWrapper.getProviders());
					monitoringResultInfoWrapper.setTimeFrame(supervisionRuleInfoWrapper.getTimeFrame());
					monitoringResultInfoWrapper.setMonitoringResult(monitoringResult.isResult());
					monitoringResultInfoWrapper.setMonitoringData(monitoringResult.getMonitoringData());
					monitoringResultInfoWrapper.setMonitoringTime(monitoringTime);
					monitoringTimePost=monitoringTime;
					monitoringTime=0;
					try {
						this.ml.insertNewMonitoringResult(monitoringResultInfoWrapper);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					// Recovery
					if (!monitoringResult.isResult()) {
						String recovery = supervisionRuleInfoWrapper.getRecoveryStrategy();

						if ((recovery == null) || (recovery.equals("")))
							recovery="{ignore()}";

						RecoveryResultWrapper recoveryResultWrapper = this.callRecovery(supervisionRuleInfoWrapper, null,monitoringResult.getMonitoringData(),
										this.processWSDLList.get(processName),id, priority, configHVar,	monitoringResult.getAliases(), monitoringResult.getTempAliases());

						recoveryTimePost=recoveryTime;
						recoveryTime=0;
						// Logging Recovery
						RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(supervisionRuleInfoWrapper.getRecoveryStrategy(), null,
								recoveryResultWrapper.getRecoveryMessage(),	XPath, false, processName, recoveryTime, recoveryResultWrapper.isRecoveryResult(),
								userName);
						try {
							this.ml.insertNewRecoveryResult(recoveryResultInfoWrapper);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}

						if (recoveryResultWrapper.isRecoveryResult()) {
							if (recoveryResultWrapper.isIgnore()) {
								dynamoPostTime=System.currentTimeMillis()-timer;
								addProcessAnalysis(obj.getLocationPath());
								return;
							}

							if (recoveryResultWrapper.isTerminateProcess()) {
								this.terminateProcess((obj.getProcess()),userName);
								dynamoPostTime=System.currentTimeMillis()-timer;
								addProcessAnalysis(obj.getLocationPath());
								return;
							}

							// In Receive/Reply/Pick actions it's obviously not
							// logical a rebind.
							// if(recoveryResultWrapper.isRebindService())
							// {
							// this.rebindService(partnerLinkName, process,
							// newServiceEndpoint);
							// }

							// It's not an invoke action
							// if(recoveryResultWrapper.isServiceInvocation())
							// {
							// //Substitute in output variable
							// }
						}
					}
				}
			}
			dynamoPostTime=System.currentTimeMillis()-timer;
			
			addProcessAnalysis(obj.getLocationPath());
		}
	}

	before(AeActivityPickImpl obj): pickCallObj(obj){
		synchronized (this) {
			timerGlobal=timer=System.currentTimeMillis();
			MonitoringResult monitoringResult = null;
			Vector<Vector> allVariables = new Vector<Vector>();
			int i, j;
			int priority = 0;

			String XPath = obj.getLocationPath();
			String processName = ((obj.getProcess()).getName()).getLocalPart();
			long id = (obj.getProcess()).getProcessId();
			String userName = list.findUser(processName, id);

			String configHVar = this.configHVarExample + "<processID>"
					+ processName + "</processID>"
					+ "<assertionType>1</assertionType>" + "<location>" + XPath
					+ "</location>" + "<userID>" + userName + "</userID>"
					+ "<instanceID>" + id + "</instanceID>" + "</webservice>";

			if (!userName.equalsIgnoreCase("")) {

				ProcessInfoWrapper processInfoWrapper = this.getProcessParams(
						processName, userName, id);

				if (processInfoWrapper == null){
					dynamoPreTime=System.currentTimeMillis()-timer;
					return;
				}
				priority = processInfoWrapper.getPriority();

				Vector<String> espressioni = new Vector<String>();

				SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = this
						.getSupervisionRule(processName, userName, XPath, true,
								id);

				if (supervisionRuleInfoWrapper != null)
					if (priority <= supervisionRuleInfoWrapper.getPriority()) 
						espressioni.add(supervisionRuleInfoWrapper
								.getWscolRule());
				for (i = 0; i < espressioni.size(); i++) {
					if (espressioni.get(i).contains("&amp;"))
						espressioni.add(i, espressioni.get(i).replaceAll(
								"&amp;", "&&"));
				}
				for (i = 0; i < espressioni.size(); i++) {
					System.out.println((String) espressioni.get(i));

					WSCoLLexer lexer = new WSCoLLexer(new StringReader(
							(String) espressioni.get(i)));
					WSCoLParser parser = new WSCoLParser(lexer);

					try {
						parser.analyzer();
					} catch (RecognitionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TokenStreamException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					AST parsetree = parser.getAST();
					Vector<WSCoLBPEL_VAR> variables = finder
							.findAllBPELVar(parsetree);
					for (j = 0; j < variables.size(); j++) {
						WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) variables
								.get(j);
						// devo cercare la variabile nell'ambiente che le e'
						// visibile
						
						
						
						IAeVariable var = (obj.findEnclosingScope())
								.getVariable(currentVar.getVar());
						if (var != null) {
							System.out.println("Var diversa da null");
							if (var.hasMessageData()) {
								try {
									System.out.println("var ha message data");
									AeMessageData message = (AeMessageData) var
											.getMessageData();
									if (currentVar.isComplexOp()) {
										System.out.println("Current var è un oggetto complesso");
										Iterator ite = message.getPartNames();
										while (ite.hasNext()) {
											String namePart = (String) ite.next();
											Object o = message.getData(namePart);
											System.out.println("Parte: "+namePart+" Valore: "+((Document)o).getTextContent());
											if (o instanceof Document) {
												Document doc = (Document) o;

												try {
													XmlObject xmlObj = XmlObject.Factory
															.parse(doc
																	.getFirstChild());
													String val = xmlObj.xmlText();
													if (currentVar.getValue() == null){
														currentVar.setValue(val);
														ite.next();
													} else {
														currentVar
																.setValue(val
																		+ currentVar
																				.getValue());
														ite.next();
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												//Passo esclusivamente al prossimo Part
												//currentVar.setValue("not initialized");
												ite.next();
												}
										}
										/*
										 * currentVar.setValue("" +
										 * message.getData(currentVar
										 * .getXPath()));
										 */

									} else if (message.getData(currentVar
											.getXPath()) != null) {
										// se l'xpath e' semplice
										currentVar.setValue(""
												+ message.getData(currentVar
														.getXPath()));
									} else {
										// se l'xpath e' del tipo:
										// order/ns2:orderElement/OrderHeader/name
										// devo gestire il caso in cui ho un
										// xpath
										// order/ciccio
										// devo elaborare l'xpath per dividerlo
										// in
										// part
										// e query

										try {
											int f = 0;
											String part = "";
											String xpathrule = currentVar
													.getXPath();
											String queryrule;
											xpathrule.charAt(f);
											while (xpathrule.charAt(f) != '/') {
												part = part
														+ xpathrule.charAt(f);
												f++;
											}
											// questa condizione per distinguere
											// il caso
											// order/ns2:brand da order/brand e'
											// da verificare
											// ulteriormente con altri esempi
											if (xpathrule.contains(":")) {
												queryrule = xpathrule
														.substring(f);
											} else {
												queryrule = "/" + xpathrule;
											}
											Object data = getCopyVariableData(
													obj, var, part, queryrule);
											if (data == null) {
												queryrule = "/" + xpathrule;
												data = getCopyVariableData(obj,
														var, part, queryrule);
											}
											String value = ((Node) data)
													.getFirstChild()
													.getNodeValue();
											currentVar.setValue("" + value);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (AeBusinessProcessException e) {
									e.printStackTrace();
								}
							} else {
								currentVar.setValue("not initialized");
							}
						} else {
							System.out.println("la variabile "
									+ currentVar.getVar() + " non e' visibile");
						}

					}
					allVariables.add(variables);
				}

				if (espressioni.size() > 0) {
					System.out.println("Precondition -------------------------------------");
					System.out.print("Rules: ");
					for(String h : espressioni)
						System.out.println(h);
					
					monitoringResult = callMonitor(allVariables, espressioni,
							configHVar);

					// Logging
					MonitoringResultInfoWrapper monitoringResultInfoWrapper = new MonitoringResultInfoWrapper();

					monitoringResultInfoWrapper.setProcessID(processName);
					monitoringResultInfoWrapper.setUserID(userName);
					monitoringResultInfoWrapper.setLocation(XPath);
					monitoringResultInfoWrapper.setPrecondition(true);
					monitoringResultInfoWrapper.setProcessPriority(priority);
					monitoringResultInfoWrapper
							.setWscolRule(supervisionRuleInfoWrapper
									.getWscolRule());
					monitoringResultInfoWrapper
							.setWscolPriority(supervisionRuleInfoWrapper
									.getPriority());
					monitoringResultInfoWrapper
							.setProviders(supervisionRuleInfoWrapper
									.getProviders());
					monitoringResultInfoWrapper
							.setTimeFrame(supervisionRuleInfoWrapper
									.getTimeFrame());
					monitoringResultInfoWrapper
							.setMonitoringResult(monitoringResult.isResult());
					monitoringResultInfoWrapper
							.setMonitoringData(monitoringResult
									.getMonitoringData());
					monitoringResultInfoWrapper
							.setMonitoringTime(monitoringTime);
					monitoringTimePre=monitoringTime;
					monitoringTime=0;

					try {
						this.ml
								.insertNewMonitoringResult(monitoringResultInfoWrapper);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					// Recovery
					if (!monitoringResult.isResult()) {
						String recovery = supervisionRuleInfoWrapper
								.getRecoveryStrategy();

						if ((recovery == null) || (recovery.equals("")))
							recovery="{ignore()}";

						RecoveryResultWrapper recoveryResultWrapper = this
								.callRecovery(supervisionRuleInfoWrapper, null,
										monitoringResult.getMonitoringData(),
										this.processWSDLList.get(processName),
										id, priority, configHVar,
										monitoringResult.getAliases(),
										monitoringResult.getTempAliases());

						// Logging Recovery
						RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper(
								supervisionRuleInfoWrapper
										.getRecoveryStrategy(), null,
								recoveryResultWrapper.getRecoveryMessage(),
								XPath, false, processName, recoveryTime,
								recoveryResultWrapper.isRecoveryResult(),
								userName);
						recoveryTimePre=recoveryTime;
						recoveryTime=0;
						try {
							this.ml
									.insertNewRecoveryResult(recoveryResultInfoWrapper);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}

						if (recoveryResultWrapper.isRecoveryResult()) {
							if (recoveryResultWrapper.isIgnore()) {
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							if (recoveryResultWrapper.isTerminateProcess()) {
								this.terminateProcess((obj.getProcess()),
										userName);
								dynamoPreTime=System.currentTimeMillis()-timer;
								return;
							}

							// In Receive/Reply/Pick actions it's obviously not
							// logical a rebind.
							// if(recoveryResultWrapper.isRebindService())
							// {
							// this.rebindService(partnerLinkName, process,
							// newServiceEndpoint);
							// }

							// It's not an invoke action
							// if(recoveryResultWrapper.isServiceInvocation())
							// {
							// //Substitute in output variable
							// }
						}
					}
				}
			}
			dynamoPreTime=System.currentTimeMillis()-timer;
			
			}

	}

	// ----------------------------------------------------------------------------

	private MonitoringResult callMonitor(Vector allVars, Vector espressioni,
			String configHVar) {
		int k, h;
		MonitoringResult monitoringResult = null;

		for (k = 0; k < espressioni.size(); k++) {
			String WsColRule = (String) espressioni.get(k);
			Vector currentVars = (Vector) allVars.get(k);

			// ---------------------------------------------------------------
			// modifico la WSColRule togliento i namespace, questa parte di
			// codice serve
			// perche' il monitor non li gestisce per ora quindi deve essere
			// tolta una volta
			// corretto il monitor
			int f = 0;
			String tempWsCol = WsColRule + '#';
			String var = "";
			WsColRule = "";
			while (tempWsCol.charAt(f) != '#') {
				if (tempWsCol.charAt(f) == '(') {
					if (tempWsCol.charAt(f + 1) == '$') {
						var = "";
						while (tempWsCol.charAt(f) != ')') {
							var = var + tempWsCol.charAt(f);
							f++;
						}
						var = var + ')';
						f++;

						String newvar = "";
						String[] parti = var.split("/");
						int n;
						for (n = 0; n < parti.length; n++) {
							if (parti[n].contains(":")) {
								String[] sottoparti = parti[n].split(":");
								if (n == 0) {
									newvar = newvar + sottoparti[1];
								} else {
									newvar = newvar + "/" + sottoparti[1];
								}

							} else {
								if (n == 0) {
									newvar = newvar + parti[n];
								} else {
									newvar = newvar + "/" + parti[n];
								}
							}
						}

						WsColRule = WsColRule + newvar;
					} else {
						WsColRule = WsColRule + tempWsCol.charAt(f);
						f++;
					}
				} else {
					WsColRule = WsColRule + tempWsCol.charAt(f);
					f++;
				}
			}
			// ---------------------------------------------------------------

			String xmlData = "";
			String preXmlData = "<monitor_data><data";

			for (h = 0; h < currentVars.size(); h++) {

				WSCoLBPEL_VAR currentVar = (WSCoLBPEL_VAR) currentVars.get(h);
				if (currentVar.isComplexOp()) {
					String nomeVar = currentVar.getVar();
					String value = currentVar.getValue();
					xmlData = xmlData + "<" + nomeVar + ">" + value + "</"
							+ nomeVar + ">";
				} else {
					String nomeVar = currentVar.getVar();
					System.out.println("\n\n\nNome var: "+nomeVar);
					String xpath = currentVar.getXPath();
					System.out.println("Xpath: "+xpath);
					String value = currentVar.getValue();
					System.out.println("Value: "+value);
					String[] parts = xpath.split("/");
					int n, m;

					System.out.println(currentVar.toString());

					xmlData = xmlData + "<" + nomeVar + ">";

					for (n = 0; n < parts.length; n++) {
						if (!parts[n].contains("[")) {
							if (!parts[n].contains(":")) {
								xmlData = xmlData + "<" + parts[n] + ">";
							} else {
								String part = parts[n];
								part = part.substring(part.indexOf(":") + 1);
								// String[] tempSubParts = parts[n].split(":");
								// preXmlData = preXmlData + " xmlns:"
								// + tempSubParts[0] + "=\"http://polimi\"";
								// xmlData.replace("<input","<input xmlns:" +
								// tempSubParts[0] + "=\"http://polimi\"");
								// xmlData = xmlData + "<" + parts[n] + ">";
								xmlData = xmlData + "<" + part + ">";
							}
						} else {
							if (!parts[n].contains(":")) {
								String tempPart = parts[n];
								tempPart.replace("[@", " ");
								tempPart.replace("'", "\\" + '"');
								tempPart.replace("]", "");
								xmlData = xmlData + "<" + tempPart + ">";
							} else {
								String[] tempSubParts = parts[n].split(":");
								preXmlData = preXmlData + " xmlns:"
										+ tempSubParts[0]
										+ "=\"http://polimi\"";
								// xmlData.replace("<input","<input xmlns:" +
								// tempSubParts[0] + "=\"http://polimi\"");
								String tempPart = parts[n];
								tempPart.replace("[@", " ");
								tempPart.replace("'", "\\" + '"');
								tempPart.replace("]", "");
								xmlData = xmlData + "<" + tempPart + ">";
							}
						}
					}

					xmlData = xmlData + value;

					for (m = parts.length - 1; m >= 0; m--) {
						if (!parts[m].contains("[")) {
							String part = parts[m];

							if (part.contains(":"))
								part = part.substring(part.indexOf(":") + 1);

							xmlData = xmlData + "</" + part + ">";
						} else {
							String[] tempParts = parts[m].split("[");
							xmlData = xmlData + "</" + tempParts[0] + ">";
						}
					}

					xmlData = xmlData + "</" + nomeVar + ">";
				}
			}
			preXmlData = preXmlData + ">";
			xmlData = preXmlData + xmlData + "</data>";
			xmlData=xmlData+"<resp_time>"+respTime+"</resp_time></monitor_data>";
			System.out.println("Monitoring data : " + xmlData);

			Monitor monitor = new Monitor();
			timerMonitoringTime = System.currentTimeMillis();
			boolean result = monitor.evaluateMonitoring(WsColRule, xmlData,
					configHVar).getValueMonitor().booleanValue();
			monitoringTime = System.currentTimeMillis() - timerMonitoringTime;
			System.out.println("\n\nResult: " + result + " evaluate in time  "+monitoringTime+ "\n\n");
			System.out.println("---------------------------------------------");

			monitoringResult = new MonitoringResult(result, xmlData, monitor
					.getAliases(), monitor.getAliasNodes());
		}

		return monitoringResult;
	}

	/**
	 * Calls the recovery strategy executor
	 */
	private RecoveryResultWrapper callRecovery(
			SupervisionRuleInfoWrapper supervisionRuleInfoWrapper,
			ServiceInvocationParams serviceInvocationParams,
			String monitoringData, String processWSDL, long processInstanceID,
			int processPriority, String configHVar, Aliases aliases,
			AliasNodes tempAliases) {
		String xmlMailConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
				+ "<properties>" + "<entry key=\"mail.smtp.auth\">false</entry>"
				+ "<entry key=\"mail.smtp.host\">localhost</entry>"
				+ "<entry key=\"from\">dynamo</entry>" + "</properties>";

		RecoveryResultWrapper recoveryResultWrapper = new RecoveryResultWrapper();

		ProcessParams processParams = new ProcessParams(
				supervisionRuleInfoWrapper.getProcessID(), processInstanceID,
				supervisionRuleInfoWrapper.getUserID(),
				supervisionRuleInfoWrapper.getLocation(), processWSDL,
				supervisionRuleInfoWrapper.isPrecondition(), processPriority);

		SupervisionParams supervisionParams = new SupervisionParams(
				supervisionRuleInfoWrapper.getWscolRule(),
				supervisionRuleInfoWrapper.getRecoveryStrategy(),
				monitoringData, supervisionRuleInfoWrapper.getPriority(),
				configHVar);

		RecoveryParams recoveryParams = new RecoveryParams(processParams,
				supervisionParams, serviceInvocationParams);

		recoveryParams.setXmlMailConfig(xmlMailConfig);

		Recovery recovery = new Recovery(recoveryParams, aliases, tempAliases);
		timerRecoveryTime = System.currentTimeMillis();
		boolean recoveryResult = recovery.DoRecovery();
		recoveryTime = System.currentTimeMillis() - timerRecoveryTime;
		recoveryResultWrapper.setRecoveryResult(recoveryResult);
		recoveryResultWrapper.setTerminateProcess(recovery
				.isProcessToBeTerminated());

		if (recovery.isServiceToBeRebinded()) {
			recoveryResultWrapper.setRebindService(true);
			recoveryResultWrapper.setNewServiceEndpoint(recovery
					.getNewServiceEndopoint());
		}

		if (recovery.isServiceInvoked()) {
			recoveryResultWrapper.setServiceInvocation(true);
			recoveryResultWrapper.setServiceInvocationResult(recovery
					.GetServiceReinvocationResult());
		}

		System.out
				.println("Recovery message: " + recovery.GetRecoveryMessage());

		recoveryResultWrapper.setRecoveryMessage(recovery.GetRecoveryMessage());

		return recoveryResultWrapper;
	}

	/**
	 * Retreive the value from the BPEL var 'variable'
	 */
	private Object getCopyVariableData(AeAbstractBpelObject obj,
			IAeVariable variable, String part, String query) {
		Object data = null;
		try {
			// Make sure we can find the variable for our variable processing.
			IAeVariable var = variable;
			// Set the data based upon the type which has been set, if any
			if (var.getElementData() != null)
				data = var.getElementData();
			else if (var.getTypeData() != null)
				data = var.getTypeData();
			else if (var.getMessageData() != null) // this throws unitialized
			// variable if it is null
			{
				if (!AeUtil.isNullOrEmpty(part)) {
					// Get the data for the given message part, and process
					// query if necessary
					data = var.getMessageData().getData(part);
				}
			}

			// execute the query against the data if not empty
			if (!AeUtil.isNullOrEmpty(query)) {
				// If data is a document set the context at the root element
				if (data instanceof Document)
					data = ((Document) data).getDocumentElement();
				// Execute query against context data, this MUST return exactly
				// one node
				AeExpressionBaseDef expr = new AeConditionDef();
				expr.setExpression(query);			
				data = obj.executeExpression(expr, data);

				final AeXPathHelper helper = AeXPathHelper.getInstance(obj.getProcess().getBPELNamespace());
				data = helper.unwrapXPathValue(data, obj.getProcess().isDisableSelectionFailure());
			}
		} catch (AeBusinessProcessException e) {
			data = null;
		}

		return data;
	}

	/**
	 * Terminates the process passed as argument
	 */
	private void terminateProcess(IAeBusinessProcess process, String username) {
		try {
			process.terminate();
			System.out.println("Terminating instance " + process.getProcessId()
					+ " of process " + process.getName().getLocalPart()
					+ " for user " + username);
		} catch (AeBusinessProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Modifies the partnerLinkName's address to 'newServiceEndpoint'
	 */
	private void rebindService(String partnerLinkName,
			IAeBusinessProcess iProcess, String newServiceEndpoint) {
		if (iProcess instanceof AeBusinessProcess) {
			final AeBusinessProcess process = (AeBusinessProcess)iProcess;
			AePartnerLink object = process.getPartnerLink(partnerLinkName);
			AeEndpointReference endpoint = (AeEndpointReference) object.getPartnerReference();
			endpoint.setAddress(newServiceEndpoint);
		}
	}

	/**
	 * Obtaining the String representation of the DOM Document representing a
	 * process variable
	 */
	private String variableToString(Document document) {
		String xslt = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
				+ "<xsl:output method=\"xml\" indent=\"no\"/>"
				+

				"<xsl:template match=\"/|comment()|processing-instruction()\">"
				+ "<xsl:copy>"
				+ "<xsl:apply-templates/>"
				+ "</xsl:copy>"
				+ "</xsl:template>"
				+

				"<xsl:template match=\"*\">"
				+ "<xsl:element name=\"{local-name()}\">"
				+ "<xsl:apply-templates select=\"node()\"/>"
				+ "</xsl:element>"
				+ "</xsl:template>"
				+

				"<xsl:template match=\"@*\">"
				+ "<xsl:attribute name=\"{local-name()}\">"
				+ "<xsl:value-of select=\".\"/>"
				+ "</xsl:attribute>"
				+ "</xsl:template>" + "</xsl:stylesheet>";

		Transformer transformer;
		try {
			Source source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			StreamSource xsltSource = new StreamSource(new ByteArrayInputStream(xslt.getBytes()));
			TransformerFactory factory = TransformerFactory.newInstance();
			transformer = factory.newTransformer(xsltSource);
			transformer.transform(source, result);

			String stringResult = stringWriter.getBuffer().toString();

			stringResult = stringResult
					.replace(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?><variable><part>",
							"");
			stringResult = stringResult.replace("</part></variable>", "");
			System.out.println("2 "+stringResult);
			// System.out.println("stringResult: " + stringResult);

			if (stringResult.charAt(0) != '<') {
				Node part = document.getFirstChild().getFirstChild();

				NamedNodeMap attributes = part.getAttributes();

				String partName = attributes.getNamedItem("name")
						.getNodeValue();

				 System.out.println("partName: " + partName);

				stringResult = "<" + partName + ">" + stringResult + "</"
						+ partName + ">";
			}
			
			return stringResult;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private SupervisionRuleInfoWrapper getSupervisionRule(String processName,
			String userName, String location, boolean isPrecondition,
			long processInstanceID) {
		SupervisionRuleInfoWrapper result = null;

		try {
			result = this.cm.getSupervisionRule(processName, userName,
					location, isPrecondition);

			TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper = this.cm
					.getTemporaryChangingRule(new TemporaryRuleChangingInfoWrapper(
							location, null, null, null, null, null,
							isPrecondition, processName, processInstanceID,
							userName));

			if (temporaryRuleChangingInfoWrapper != null) {
				if (temporaryRuleChangingInfoWrapper.getNewCondition() != null)
					result.setWscolRule(temporaryRuleChangingInfoWrapper
							.getNewCondition());
				if (temporaryRuleChangingInfoWrapper.getNewConditionPriority() != null)
					result.setPriority(temporaryRuleChangingInfoWrapper
							.getNewConditionPriority().intValue());
				if (temporaryRuleChangingInfoWrapper.getNewConditionRecovery() != null)
					result.setRecoveryStrategy(temporaryRuleChangingInfoWrapper
							.getNewConditionRecovery());
				if (temporaryRuleChangingInfoWrapper.getNewProviderList() != null)
					result.setProviders(temporaryRuleChangingInfoWrapper
							.getNewProviderList());
				if (temporaryRuleChangingInfoWrapper.getNewTimeFrame() != null)
					result.setTimeFrame(temporaryRuleChangingInfoWrapper
							.getNewTimeFrame());
			}
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return result;
	}

	private ProcessInfoWrapper getProcessParams(String processName,
			String userName, long processInstanceID) {
		ProcessInfoWrapper result = new ProcessInfoWrapper(null, processName,
				processInstanceID, userName);

		try {
			result = this.cm.getTemporaryProcessDataChanging(result);

			if (result == null) {
				result = this.cm.getProcessInfo(processName, userName);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Recursively it visits the structure of the BPEL variable and substitutes
	 * the values
	 */
	private void substituteValuesInVariables(Node parent, XMLParser parser,
			String xPath) {
		// System.out.println("parent name: " + parent.getLocalName());

		if (parent.hasChildNodes() && (parent.getLocalName() != null)) {
			xPath += "/" + parent.getLocalName();
			NodeList children = parent.getChildNodes();

			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				this.substituteValuesInVariables(child, parser, xPath);
			}
		} else {
			Node parentNode = parent.getParentNode();
			Node newNode = parent.cloneNode(true);
			newNode.setTextContent(parser.GetValue(xPath));
			parentNode.removeChild(parent);
			parentNode.appendChild(newNode);

			System.out.println("xPath: " + xPath + " | new value: "
					+ newNode.getTextContent());
		}
	}

	
	
	private void addProcessAnalysis(String x) {
		/*try {
			procPt.addResult(x.replaceAll("@", ""), dynamoPreTime, dynamoPostTime, (System.currentTimeMillis()-timerGlobal), monitoringTimePre, recoveryTimePre,monitoringTimePost, recoveryTimePost);
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/
	}
}