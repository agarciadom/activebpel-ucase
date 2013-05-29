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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class Recovery
{
	private static final Logger LOGGER = Logger.getLogger(Recovery.class.getCanonicalName());
	private static final Level DEFAULT_LEVEL = Level.INFO;

	private RecoveryParams recoveryParams;
	private RecoveryResult recoveryResult;
	private Aliases aliases;
	private AliasNodes tempAliases;

	private ConfigurationManager cm;
	private MonitorLogger ml;

	static {
		LOGGER.setLevel(DEFAULT_LEVEL);
	}

	public Recovery(RecoveryParams recoveryParams, Aliases aliases, AliasNodes tempAliases)
	{
		this.recoveryParams = recoveryParams;
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
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	public static void setLevelLogger(Level l)
	{
		LOGGER.setLevel(l);
	}
	
	private void parseRecoveryStrategy() throws RecognitionException, TokenStreamException
	{
		final WSCoLLexer lexer = new WSCoLLexer(new StringReader(recoveryParams.getSupervisionParams().getRecoveryStrategies()));
		final WSCoLParser parser = new WSCoLParser(lexer);
		parser.recovery();

		LOGGER.info("Start to evaluate recovery strategies");
		final List<WSReLNode> wsrelNodes = evaluateRecoveryNodes(parser.getAST());
		final ProcessParams processParams = this.recoveryParams.getProcessParams();
		for (final WSReLNode node : wsrelNodes) {
			node.doRecovery(this.recoveryParams, this.recoveryResult);
			LOGGER.fine("Rebind?? " + this.recoveryResult.isThereRebindAction() + " | " + this.recoveryResult.getMessage());

			if(this.recoveryResult.isRecoveryResult())
			{
				if(!this.recoveryResult.isThereIgnoreAction())
				{
					/*
					 * Modification in supervision parameters are taken all
					 * toghether. If one of the parameters remains the same, it
					 * must be copied as it is at the time. So, if the
					 * 'recovery' parameter of the 'change_supervision_rules'
					 * action is has omitted (i.e
					 * change_supervision_rules('newRule', '', 'permanent')),
					 * the recovery will be switched off. Differently, if the
					 * changing parameter is a priority, omitting will lead to
					 * set the priority to 0.
					 */
					
					if(this.recoveryResult.isThereChangingProcessParams())
					{
						final ChangeProcessParams tempCPP = this.recoveryResult.getChangeProcessParams();
						try {
							final ProcessInfoWrapper processInfoWrapper = createProcessInfoWrapper(tempCPP, processParams);

							if (tempCPP.getChangeType().equals("permanent")) {
								processInfoWrapper.setProcessInstanceId(null);
								this.cm.setNewProcessPriority(processInfoWrapper);
							}
							else if (tempCPP.getChangeType().equals("bpel_instance")) {
								this.cm.setTemporaryProcessDataChanging(processInfoWrapper);
							}
						} catch (Exception e) {
							LOGGER.severe(e.getLocalizedMessage());
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
						try	{
							supervisionRuleInfoWrapper = this.cm.getSupervisionRule(processParams.getProcessID(), 
																					processParams.getUserID(), 
																					processParams.getLocation(), 
																					processParams.isPrecondition());
						}
						catch (Exception e1)
						{
							LOGGER.severe(e1.getLocalizedMessage());
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
						catch (Exception e)
						{
							LOGGER.severe(e.getLocalizedMessage());
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
	}

	private List<WSReLNode> evaluateRecoveryNodes(final AST recovery) {
		final List<WSReLNode> wsrelNodes = new ArrayList<WSReLNode>();
		WSReLNode temp = (WSReLNode)recovery.getFirstChild();
		for(int i = 0; i < recovery.getNumberOfChildren(); i++) {
			try {
				temp.evaluate(new InputMonitor(this.recoveryParams.getSupervisionParams().getMonitoringData(), this.recoveryParams.getSupervisionParams().getConfigHvar()), 
												this.aliases, 
												this.tempAliases);
			}
			catch (InvalidInputMonitor e) {
				LOGGER.severe(e.getLocalizedMessage());
			}

			wsrelNodes.add(temp);
			temp = (WSReLNode) temp.getNextSibling();  
		}
		return wsrelNodes;
	}

	private ProcessInfoWrapper createProcessInfoWrapper(
			ChangeProcessParams changeParams, ProcessParams processParams) {
		final ProcessInfoWrapper processInfoWrapper = new ProcessInfoWrapper();
		processInfoWrapper.setPriority(changeParams.getNewPriority());
		processInfoWrapper.setProcessId(processParams.getProcessID());
		processInfoWrapper.setUserId(processParams.getUserID());
		processInfoWrapper.setProcessInstanceId(processParams.getProcessInstanceID());
		return processInfoWrapper;
	}
	
	public boolean DoRecovery()
	{
		try {
			this.parseRecoveryStrategy();
		} catch (Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
		}
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
