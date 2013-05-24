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

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.monitor.nodes.NodeWSCoL;
import it.polimi.monitor.nodes.PARAM_LISTNode;
import it.polimi.recovery.AtomicRecoveryAction;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.RecoveryResult;
import it.polimi.recovery.data.RuleParams;
import it.polimi.recovery.data.ServiceInvocationParams;

import java.util.Vector;

import javax.wsdl.WSDLException;

import antlr.collections.AST;

public class ActionNode extends WSReLNode 
{
	private String actionName = null;
	private Vector<NodeWSCoL> paramList = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 469114698396360791L;

	@Override
	public void doRecovery(RecoveryParams recoveryParams, RecoveryResult recoveryResult)
	{
		// TODO Auto-generated method stub
		RecoveryResult result = null;

		if(this.actionName.equalsIgnoreCase("ignore"))
		{
			result = new RecoveryResult(true, "Found ignore() action.\n");
			recoveryResult.setThereIgnoreAction();
		}
		if(this.actionName.equalsIgnoreCase("halt"))
		{
			result = new RecoveryResult(true, "Found halt() action.\n");
			recoveryResult.setThereHaltAction();
		}
		if(this.actionName.equalsIgnoreCase("notify"))
			try
			{
				result =  AtomicRecoveryAction.Notify((String) this.paramList.get(0).getMonitoringValue(),
						(String) this.paramList.get(1).getMonitoringValue(),
						recoveryParams.getXmlMailConfig());
			}
			catch (WSCoLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//The 'retry' action can be used only in post-condition
		if(this.actionName.equalsIgnoreCase("retry") && !recoveryParams.getProcessParams().isPrecondition())
		{
			result = AtomicRecoveryAction.Retry(recoveryParams.getInvocationServiceData());
			recoveryResult.setServiceReinvocationOutput(result.getServiceReinvocationOutput());
			recoveryResult.setThereInokeActivity();
			recoveryResult.setNeedRemonitoring();
		}
		if(this.actionName.equalsIgnoreCase("rebind"))
		{
			try
			{
				result = AtomicRecoveryAction.Rebind((String) this.paramList.get(0).getMonitoringValue(), 
						recoveryParams.getInvocationServiceData(), 
						!recoveryParams.getProcessParams().isPrecondition());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WSDLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			recoveryResult.setThereRebindAction();
			recoveryResult.setNewServiceEndpoint(result.getNewServiceEndpoint());
			
			if(result.isThereInokeActivity())
			{
				recoveryResult.setThereInokeActivity();
				recoveryResult.setServiceReinvocationOutput(result.getServiceReinvocationOutput());
				recoveryResult.setNeedRemonitoring();
			}
		}
		if(this.actionName.equalsIgnoreCase("change_supervision_rules"))
		{
			if(this.paramList.get(0).equals(""))
			{
				result.addMessage("'monitoring' parameter is required for 'change_supervision_rules' action");
				result.setRecoveryResult(false);
				return;
			}
			
			try
			{
				result = AtomicRecoveryAction.ChangeSupervisionRules((String) this.paramList.get(0).getMonitoringValue(), 
						(String) this.paramList.get(1).getMonitoringValue(), 
						(String) this.paramList.get(2).getMonitoringValue());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recoveryResult.setChangeSupervisionRule(result.getChangeSupervisionRule());
			recoveryResult.setThereChangingSupervisionRules();
			recoveryResult.setNeedRemonitoring();
		}
		if(this.actionName.equalsIgnoreCase("change_supervision_params"))
		{
			int priority = 0;
			
			if(!this.paramList.get(0).equals(""))
			{
				try
				{
					priority = Integer.parseInt((String) this.paramList.get(0).getMonitoringValue());
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
					result.addMessage("Exception in 'change_supervision_params': " + e.getMessage());
					result.setRecoveryResult(false);
					return;
				}
				catch (WSCoLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try
			{
				result = AtomicRecoveryAction.ChangeSupervisionParams(new RuleParams(priority,
																					(String) this.paramList.get(1).getMonitoringValue(),
																					(String) this.paramList.get(2).getMonitoringValue()), 
																		(String) this.paramList.get(3).getMonitoringValue());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recoveryResult.setChangeSupervisionParams(result.getChangeSupervisionParams());
			recoveryResult.setThereChangingSupervisionParams();
			recoveryResult.setNeedRemonitoring();
		}
		if(this.actionName.equalsIgnoreCase("change_process_params"))
		{
			int priority = 0;
			
			if(!this.paramList.get(0).equals(""))
			{
				try
				{
					priority = Integer.parseInt((String) this.paramList.get(0).getMonitoringValue());
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
					result.addMessage("Exception in 'change_process_params': " + e.getMessage());
					result.setRecoveryResult(false);
					return;
				}
				catch (WSCoLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try
			{
				result = AtomicRecoveryAction.ChangeProcessParams(priority, (String) this.paramList.get(1).getMonitoringValue());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recoveryResult.setChangeProcessParams(result.getChangeProcessParams());
			recoveryResult.setThereChangingProcessParams();
			recoveryResult.setNeedRemonitoring();
		}
		if(this.actionName.equalsIgnoreCase("call"))
		{
			//The output of the invocation is not recorded in 'serviceReinvocationOutput' attribute because
			//is never used for remonitoring.
			try
			{
				result = AtomicRecoveryAction.Call(new ServiceInvocationParams((String) this.paramList.get(0).getMonitoringValue(), 
						(String) this.paramList.get(1).getMonitoringValue(), 
						(String) this.paramList.get(2).getMonitoringValue()));
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(this.actionName.equalsIgnoreCase("substitute"))
		{
			try
			{
				result = AtomicRecoveryAction.Substitute(new ServiceInvocationParams((String) this.paramList.get(0).getMonitoringValue(), 
																						(String) this.paramList.get(1).getMonitoringValue(), 
																						(String) this.paramList.get(2).getMonitoringValue()),
															(String) this.paramList.get(3).getMonitoringValue());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recoveryResult.setServiceReinvocationOutput(result.getServiceReinvocationOutput());
			recoveryResult.setThereInokeActivity();

			if(!recoveryParams.getProcessParams().isPrecondition())
				recoveryResult.setNeedRemonitoring();
		}
		if(this.actionName.equalsIgnoreCase("process_callback"))
		{
			try
			{
				result = AtomicRecoveryAction.ProcessCallback(recoveryParams.getProcessParams().getProcessWSDLUrl(), 
						(String) this.paramList.get(0).getMonitoringValue(), 
						(String) this.paramList.get(1).getMonitoringValue());
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Common operation among all the actions
		recoveryResult.setRecoveryResult(result.isRecoveryResult());
		recoveryResult.addMessage(result.getMessage());
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases)
	{ //[luca] cambiato simpleast
		// TODO Auto-generated method stub
		SimpleAST simpleAST = (SimpleAST)this.getFirstChild();
		
		this.actionName = simpleAST.getValue();
		
		
		AST temp = simpleAST.getNextSibling();
		
		if (temp instanceof PARAM_LISTNode)
		{
			PARAM_LISTNode pList = (PARAM_LISTNode) temp;
			
			try
			{
				pList.evaluate(inputMonitor, aliases, tempAliases);
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try
			{
				this.paramList = pList.getMonitoringValue();
				
				for(int i = 0; i < this.paramList.size(); i++)
				{
					this.paramList.get(i).evaluate(inputMonitor, aliases, tempAliases);
//					
//					System.out.println("GetText " + this.paramList.get(i).getMonitoringValue() + " | toString " + this.paramList.get(i).toString());
				}
//				NodeWSCoL t=tempVector.elementAt(2);
//				t.evaluate(inputMonitor, aliases, tempAliases);
//				System.err.println("++++++++++++++++++++");
//				System.err.println(t.getMonitoringValue());
//				System.err.println("++++++++++++++++++++");
			}
			catch (WSCoLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		
//		this.paramList= new Vector<String>();
//		
//		int children = temp.getNumberOfChildren();
//
//		for(int i = 0; i < children; i++)
//		{
//			if(i == 0)
//				temp = temp.getFirstChild();
//			else
//				temp = temp.getNextSibling();
//			
//			this.paramList.add(temp.toString());
//			
//			System.out.println("Node " + i + ": " + temp.toString());
//		}
	}
	public String toString()
	{
		return "Action";
	}
}
