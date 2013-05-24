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

import it.polimi.WSCoL.WSCoLLexer;
import it.polimi.WSCoL.WSCoLParser;
import it.polimi.exception.InvalidInputMonitor;
import it.polimi.monitor.InputMonitor;
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
import it.polimi.monitor.stubs.monitorlogger.RecoveryResultInfoWrapper;
import it.polimi.recovery.data.ChangeProcessParams;
import it.polimi.recovery.data.ChangeSupervisionParams;
import it.polimi.recovery.data.ChangeSupervisionRule;
import it.polimi.recovery.data.ProcessParams;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.RecoveryResult;
import it.polimi.recovery.nodes.WSReLNode;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class Recovery
{
	private RecoveryParams recoveryParams = null;
	private Logger logger=Logger.getLogger("WSReL Executor");
	private Level defaultLoggerLivel=Level.INFO;
	private RecoveryResult recoveryResult = null;
	private Aliases aliases = null;
	private AliasNodes tempAliases = null;
	
	private ConfigurationManager cm;
	private MonitorLogger ml;
	
	public Recovery(RecoveryParams recoveryParams, Aliases aliases, AliasNodes tempAliases)
	{
		this.recoveryParams = recoveryParams;
		this.logger.setLevel(defaultLoggerLivel);
		this.recoveryResult = new RecoveryResult();
		
		this.aliases = aliases;
		this.tempAliases = tempAliases;

		try 
		{
			if(this.cm == null)
			{
				ConfigurationManagerWS cmLocator = new ConfigurationManagerWSLocator();
				this.cm = cmLocator.getConfigurationManagerPort();
			}
			
			if(this.ml == null)
			{
				MonitorLoggerWS mlLocator = new MonitorLoggerWSLocator();
				this.ml = mlLocator.getMonitorLoggerPort();
			}
		} 
		catch (ServiceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setLevelLogger(Level l)
	{
		this.logger.setLevel(l);
	}
	
	private void parseRecoveryStrategy()
	{
		WSCoLLexer lexer = new WSCoLLexer(new StringReader(this.recoveryParams.getSupervisionParams().getRecoveryStrategies()));
		WSCoLParser parser = new WSCoLParser(lexer);
		
		try
		{
			parser.recovery();
		}
		catch (RecognitionException e)
		{
			// TODO Auto-generated catch block
			this.recoveryResult.setRecoveryResult(false);
			e.printStackTrace();
		}
		catch (TokenStreamException e)
		{
			// TODO Auto-generated catch block
			this.recoveryResult.setRecoveryResult(false);
			e.printStackTrace();
		}
		
//		ASTFrame frame = new ASTFrame("The tree",parser.getAST() );
//		frame.setVisible(true);

		AST recovery = parser.getAST();

		Vector<WSReLNode> wsrelNodes=new Vector<WSReLNode>();
		WSReLNode temp=null;
		
//		this.logger.info("Creating a list of recovery strategies");
		
		for(int i = 0; i < recovery.getNumberOfChildren(); i++)
		{
			if(i == 0)
			{
				temp = (WSReLNode) recovery.getFirstChild();
			}
			else
			{
				temp = (WSReLNode) temp.getNextSibling();
			}
			
			try
			{
				temp.evaluate(new InputMonitor(this.recoveryParams.getSupervisionParams().getMonitoringData(), this.recoveryParams.getSupervisionParams().getConfigHvar()), 
												this.aliases, 
												this.tempAliases);
			}
			catch (InvalidInputMonitor e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			wsrelNodes.add(temp);
		}
		
		this.logger.info("Start to evaluate recovery strategies");
		
		WSReLNode node = null;
		
		ProcessParams processParams = this.recoveryParams.getProcessParams();					

		for(int i = 0; i < wsrelNodes.size(); i++)
		{
			node = wsrelNodes.elementAt(i);
			node.doRecovery(this.recoveryParams, this.recoveryResult);

//			System.out.println("Rebind?? " + this.recoveryResult.isThereRebindAction() + " | " + this.recoveryResult.getMessage());
			
			if(this.recoveryResult.isRecoveryResult())
			{
				if(!this.recoveryResult.isThereIgnoreAction())
				{
					//Modification in supervision parameters are taken all toghether. If one of the 
					//parameters remains the same, it must be copied as it is at the time.
					//So, if the 'recovery' parameter of the 'change_supervision_rules' action is has omitted
					//(i.e change_supervision_rules('newRule', '', 'permanent')), the recovery will be switched 
					//off.
					//Differently, if the changing parameter is a priority, omitting will lead to set
					//the priority to 0.
					
					if(this.recoveryResult.isThereChangingProcessParams())
					{
						//Update process params
						
						ChangeProcessParams tempCPP = this.recoveryResult.getChangeProcessParams();
						
						if(tempCPP.getChangeType().equals("permanent"))
						{
							//In main db tables
							ProcessInfoWrapper processInfoWrapper = new ProcessInfoWrapper(tempCPP.getNewPriority(),
																							processParams.getProcessID(),
																							null,
																							processParams.getUserID());
							
							try
							{
								this.cm.setNewProcessPriority(processInfoWrapper);
							}
							catch (RemoteException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(tempCPP.getChangeType().equals("bpel_instance"))
						{
							//In temporary db tables
							ProcessInfoWrapper processInfoWrapper = new ProcessInfoWrapper(tempCPP.getNewPriority(),
																							processParams.getProcessID(),
																							processParams.getProcessInstanceID(),
																							processParams.getUserID());

							try
							{
								this.cm.setTemporaryProcessDataChanging(processInfoWrapper);
							}
							catch (RemoteException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					if(this.recoveryResult.isThereChangingSupervisionParams() || this.recoveryResult.isThereChangingSupervisionRules())
					{
						//Update supervision params and rules
						
						boolean permanentSupervisionChange = false;
						boolean temporarySupervisionChange = false;
						
						ChangeSupervisionParams tempCSP = this.recoveryResult.getChangeSupervisionParams();
						ChangeSupervisionRule tempCPR = this.recoveryResult.getChangeSupervisionRule();
						
						SupervisionRuleInfoWrapper supervisionRuleInfoWrapper = null;
						try
						{
							supervisionRuleInfoWrapper = this.cm.getSupervisionRule(processParams.getProcessID(), 
																					processParams.getUserID(), 
																					processParams.getLocation(), 
																					processParams.isPrecondition());
						}
						catch (RemoteException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						if(supervisionRuleInfoWrapper == null)
						{
							recoveryResult.addMessage("Trying to permanently or temporarely changing not being supervision rules!");
							recoveryResult.setRecoveryResult(false);
							return;
						}

						TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper = new TemporaryRuleChangingInfoWrapper();
						
						temporaryRuleChangingInfoWrapper.setProcessID(processParams.getProcessID());
						temporaryRuleChangingInfoWrapper.setUserID(processParams.getUserID());
						temporaryRuleChangingInfoWrapper.setLocation(processParams.getLocation());
						temporaryRuleChangingInfoWrapper.setPrecondition(processParams.isPrecondition());
						temporaryRuleChangingInfoWrapper.setProcessInstanceID(processParams.getProcessInstanceID());

						if(tempCSP != null)
						{
							if(tempCSP.getChangeType().equals("permanent"))
							{
								//In main db tables
								supervisionRuleInfoWrapper.setPriority(tempCSP.getNewPriority());
								supervisionRuleInfoWrapper.setTimeFrame(tempCSP.getNewTimeFrame());
								supervisionRuleInfoWrapper.setProviders(tempCSP.getNewProviderList());
								
								permanentSupervisionChange = true;
							}
							else if(tempCSP.getChangeType().equals("bpel_instance"))
							{
								//In temporary db tables
								temporaryRuleChangingInfoWrapper.setNewConditionPriority(tempCSP.getNewPriority());
								temporaryRuleChangingInfoWrapper.setNewProviderList(tempCSP.getNewProviderList());
								temporaryRuleChangingInfoWrapper.setNewTimeFrame(tempCSP.getNewTimeFrame());
								
								temporarySupervisionChange = true;
							}
						}

						if(tempCPR != null)
						{
							if(tempCPR.getChangeType().equals("permanent"))
							{
								//In main db tables
								supervisionRuleInfoWrapper.setWscolRule(tempCPR.getNewWSCoLRule());
								supervisionRuleInfoWrapper.setRecoveryStrategy(tempCPR.getNewWSReLStrategy());
								
								permanentSupervisionChange = true;
							}
							else if(tempCPR.getChangeType().equals("bpel_instance"))
							{
								//In temporary db tables
								temporaryRuleChangingInfoWrapper.setNewCondition(tempCPR.getNewWSCoLRule());
								temporaryRuleChangingInfoWrapper.setNewConditionRecovery(tempCPR.getNewWSReLStrategy());
								
								temporarySupervisionChange = true;
							}
						}

						try
						{
							if(permanentSupervisionChange)
								this.cm.changeSupervisionRuleParams(supervisionRuleInfoWrapper);
							
							if(temporarySupervisionChange)
								this.cm.setTemporaryChangingRule(temporaryRuleChangingInfoWrapper);
						}
						catch (RemoteException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				break;
			}
		}
		
		//Logging
		RecoveryResultInfoWrapper recoveryResultInfoWrapper = new RecoveryResultInfoWrapper();
		
		recoveryResultInfoWrapper.setProcessID(processParams.getProcessID());
		recoveryResultInfoWrapper.setUserID(processParams.getUserID());
		recoveryResultInfoWrapper.setLocation(processParams.getLocation());
		recoveryResultInfoWrapper.setPrecondition(processParams.isPrecondition());
		
		recoveryResultInfoWrapper.setSuccessful(this.recoveryResult.isRecoveryResult());
		recoveryResultInfoWrapper.setCompleteRecoveryStrategy(this.recoveryParams.getSupervisionParams().getRecoveryStrategies());
		recoveryResultInfoWrapper.setExecutedRecoveryStrategy(this.recoveryResult.getMessage());
		//codice doppio
		/*try
		{
			this.ml.insertNewRecoveryResult(recoveryResultInfoWrapper);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public boolean DoRecovery()
	{
		this.parseRecoveryStrategy();
		return this.recoveryResult.isRecoveryResult();
	}
	
	public boolean isThereIgnoreAction()
	{
		return recoveryResult.isThereIgnoreAction();
	}
	
	public boolean isServiceInvoked()
	{
		return this.recoveryResult.isThereInokeActivity();
	}
	
	public String GetServiceReinvocationResult()
	{
		return this.recoveryResult.getServiceReinvocationOutput();
	}
	
	public String GetRecoveryMessage()
	{
		return this.recoveryResult.getMessage();
	}
	
	public boolean isProcessToBeTerminated()
	{
		if(!this.recoveryResult.isThereIgnoreAction())
			return this.recoveryResult.isThereHaltAction();
		else
			return false;
	}
	
	public boolean isServiceToBeRebinded()
	{
		if(!this.recoveryResult.isThereIgnoreAction())
			return this.recoveryResult.isThereRebindAction();
		else
			return false;
	}
	
	public String getNewServiceEndopoint()
	{
		return this.recoveryResult.getNewServiceEndpoint();
	}
}
