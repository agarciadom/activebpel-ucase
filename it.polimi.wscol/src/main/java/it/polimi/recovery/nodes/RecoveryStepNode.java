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

package it.polimi.recovery.nodes;

import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.Monitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.recovery.XMLParser;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.RecoveryResult;

import java.util.Vector;

public class RecoveryStepNode extends WSReLNode 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8047409716689856628L;
	//It can be an 'and' node or aa 'action' node
	private WSReLNode first = null;
//	//It can be only a 'recovery step' node
//	private WSReLNode second = null;
	
	@Override
	public void doRecovery(RecoveryParams recoveryParams, RecoveryResult recoveryResult)
	{
		RecoveryResult tempRecoveryResult = new RecoveryResult();
		this.first.doRecovery(recoveryParams, tempRecoveryResult);
		
		if(tempRecoveryResult.isThereIgnoreAction())
		{
			recoveryResult.setThereIgnoreAction();
			recoveryResult.setRecoveryResult(true);
			recoveryResult.addMessage(tempRecoveryResult.getMessage());
			return;
		}
		if(tempRecoveryResult.isThereHaltAction())
		{
			recoveryResult.setThereHaltAction();
		}
		if(tempRecoveryResult.isThereRebindAction())
		{
			recoveryResult.setThereRebindAction();
			recoveryResult.setNewServiceEndpoint(tempRecoveryResult.getNewServiceEndpoint());
		}
		//For 'substitute' or 'rebind' action used in preconditions
		if(tempRecoveryResult.isThereInokeActivity())
		{
			recoveryResult.setServiceReinvocationOutput(tempRecoveryResult.getServiceReinvocationOutput());
		}

		recoveryResult.setRecoveryResult(tempRecoveryResult.isRecoveryResult());
		recoveryResult.addMessage(tempRecoveryResult.getMessage());
		
		//If there weren't actions needing remonitoring, the result is returned.
		if(!tempRecoveryResult.isNeedRemonitoring())
		{
			return;
		}
		
		//Doing re-monitoring after the recovery-step
		String wscolRule = null;
		String monitoringData = recoveryParams.getSupervisionParams().getMonitoringData();;
		String recovery = null;
		String configHvar = recoveryParams.getSupervisionParams().getConfigHvar();
		int processPriority = 0;
		int wscolRulePriority = 0;
		
		if(tempRecoveryResult.isThereChangingSupervisionRules())
		{
			wscolRule = tempRecoveryResult.getChangeSupervisionRule().getNewWSCoLRule();
			recovery = tempRecoveryResult.getChangeSupervisionRule().getNewWSReLStrategy();
		}
		else
		{
			wscolRule = recoveryParams.getSupervisionParams().getWscolRule();
		}
		
		if(tempRecoveryResult.isThereChangingSupervisionParams())
		{
			wscolRulePriority = tempRecoveryResult.getChangeSupervisionParams().getNewPriority();
		}
		else
		{
			wscolRulePriority = recoveryParams.getSupervisionParams().getWscolRulePriority();
		}
		
		if(tempRecoveryResult.isThereChangingProcessParams())
		{
			processPriority = tempRecoveryResult.getChangeProcessParams().getNewPriority();
		}
		else
		{
			processPriority = recoveryParams.getProcessParams().getProcessPriority();
		}
		
		if(tempRecoveryResult.isThereInokeActivity())
		{
			//Substitute in monitoringData the new values with invoke result.
			monitoringData = this.updateMonitoringData(monitoringData, tempRecoveryResult.getServiceReinvocationOutput());
		}
		
		
		if(tempRecoveryResult.isThereHaltAction())
			recoveryResult.setThereHaltAction();
		
		if(tempRecoveryResult.isThereInokeActivity())
		{
			recoveryResult.setThereInokeActivity();
			recoveryResult.setServiceReinvocationOutput(tempRecoveryResult.getServiceReinvocationOutput());
		}
		if(tempRecoveryResult.isThereChangingProcessParams())
		{
			recoveryResult.setThereChangingProcessParams();
			recoveryResult.setChangeProcessParams(tempRecoveryResult.getChangeProcessParams());
		}
		if(tempRecoveryResult.isThereChangingSupervisionParams())
		{
			recoveryResult.setThereChangingSupervisionParams();
			recoveryResult.setChangeSupervisionParams(tempRecoveryResult.getChangeSupervisionParams());
		}
		if(tempRecoveryResult.isThereChangingSupervisionRules())
		{
			recoveryResult.setThereChangingSupervisionRules();
			recoveryResult.setChangeSupervisionRule(tempRecoveryResult.getChangeSupervisionRule());
		}

		if(wscolRulePriority < processPriority)
		{
			recoveryResult.setRecoveryResult(true);
			recoveryResult.addMessage("During recovery, priorities were changed and the monitoring rule is switched off now.\n");
			return;
		}
		
		String rule = this.removeNamespaceFromRule(wscolRule);
		
		Monitor monitor = new Monitor();
		boolean result = monitor.evaluateMonitoring(rule, monitoringData, configHvar).getValueMonitor().booleanValue();
		
		if(result)
		{
			recoveryResult.setRecoveryResult(true);
		}
//		else if(!result && tempRecoveryResult.isThereChangingSupervisionRules())
//			if((recovery != null) && (!recovery.equals("")))
//			{
//				//Do the new recovery??
//			}
		else
		{
			recoveryResult.setRecoveryResult(false);
		}
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases, AliasNodes tempAliases)
	{
		// TODO Auto-generated method stub
		//We can have only one child
		this.first = (WSReLNode) this.getFirstChild();
		this.first.evaluate(inputMonitor, aliases, tempAliases);
		
//		this.second = (WSReLNode) this.first.getNextSibling();
//		if(this.second != null)
//			this.second.evaluate();
	}
	
	public String toString()
	{
		return "RecoveryStep";
	}
	
	private String updateMonitoringData(String oldMonitoringData, String serviceInvocationResult)
	{
		String newMonitoringData = null;

		Vector<String> variables = null;
		Vector<String> xpathList = null;
		
		XMLParser parser = new XMLParser();
		XMLParser parserBPELVariables = new XMLParser();
		
		parserBPELVariables.SetXML(oldMonitoringData);
		variables = parserBPELVariables.GetXPath("monitor_data/data");
		
		int lengthBeginXPath = "/monitor_data/data/".length();
		
		
		//Invoker was modified to remove all namespaces and relative prefixes, before returning the result
		
//		int positionOpenBracket = 0;
//		int positionCloseBracket = 0;
//		int positionColon = 0;
//		
//		//Delete namespaces in the response SOAP message
//		while((positionColon != -1) && (positionCloseBracket < serviceInvocationResult.length()))
//		{
//			positionOpenBracket = serviceInvocationResult.indexOf("<", positionCloseBracket);
//			positionCloseBracket = serviceInvocationResult.indexOf(">", positionOpenBracket);
//			positionColon = serviceInvocationResult.indexOf(":", positionOpenBracket);
//			
//			if((positionColon < positionCloseBracket) && (positionColon != -1))
//			{
//				String temp = serviceInvocationResult.substring(positionOpenBracket + 1, positionColon + 1);
//				
//				serviceInvocationResult = serviceInvocationResult.replace(temp, "");
//			}
//		}
		
		System.out.println("monitoringData:" + oldMonitoringData);
		
		parser.SetXML(serviceInvocationResult);
		
		int lengthBeginResponse = "/Response/".length();
		
		//Retreives xpath list of parts from the Response SOAP message and finds out matches with the list of variables
		//used for the monitoring. The result is a list of xpath to be replaced in the monitoringData evrey loop.
		xpathList = parser.GetXPath("Response");
		
		//Replaces new values from the invocation into the monitoringData set
		int loops = 0;
		
		if(variables.size() >= xpathList.size())
		{
			loops = variables.size();
			
			for(int i = 0; i < loops; i++)
			{
				String bpelVariable = variables.get(i);
				
				for(int l = 0; l < variables.size(); l++)
				{
					String xpath = xpathList.get(l);
					
					if(bpelVariable.substring(bpelVariable.indexOf("/", lengthBeginXPath) + 1).equals(xpath.substring(lengthBeginResponse)))
					{
						String newValue = parser.GetValue(xpath);
						parserBPELVariables.SetValue(bpelVariable, newValue);
					}
				}
			}
		}
		else
		{
			loops = xpathList.size();
			
			for(int i = 0; i < loops; i++)
			{
				String xpath = xpathList.get(i);
				
				for(int l = 0; l < variables.size(); l++)
				{
					String bpelVariable = variables.get(l);
					
					if(bpelVariable.substring(bpelVariable.indexOf("/", lengthBeginXPath) + 1).equals(xpath.substring(lengthBeginResponse)))
					{
						String newValue = parser.GetValue(xpath);
						parserBPELVariables.SetValue(bpelVariable, newValue);
					}
				}
			}
		}
		
		newMonitoringData = parserBPELVariables.GetDocument().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		System.out.println("monitoringData updated: " + newMonitoringData);

		return newMonitoringData;
	}
	
	private String removeNamespaceFromRule(String rule)
	{
		if(!rule.contains(":") || !rule.contains("$"))
			return rule;
		
		String result = rule;
		
		int dollarPosition = result.indexOf("$");
		int blankPosition = result.indexOf(" ", dollarPosition);
		int colonPosition = 0;
		int slashPosition = 0;
		
		while(dollarPosition >= 0)
		{
			String bpelVar = result.substring(dollarPosition, blankPosition);
			
			if(bpelVar.contains(":"))
			{
				colonPosition = bpelVar.indexOf(":");
				
				while(colonPosition >= 0)
				{
					slashPosition = bpelVar.lastIndexOf("/", colonPosition);
					
					String prefix = bpelVar.substring(slashPosition + 1, ++colonPosition);
					
					result = result.replace(prefix, "");
					
					colonPosition = bpelVar.indexOf(":", colonPosition);
				}
			}
			
			dollarPosition = result.indexOf("$", blankPosition);
			blankPosition = result.indexOf(" ", dollarPosition);
		}
		
		System.out.println("Rule without namespaces: " + result);
		
		return result;
	}
}
