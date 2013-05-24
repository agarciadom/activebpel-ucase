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

package it.polimi.recovery.data;

public class RecoveryResult {
	private boolean recoveryResult;
	private boolean thereHaltAction;
	private boolean thereIgnoreAction;
	private boolean thereInokeActivity;
	private boolean thereChangingSupervisionRules;	
	private boolean thereChangingSupervisionParams;
	private boolean thereChangingProcessParams;
	private boolean thereRebindAction;
	private boolean needRemonitoring;

	private String serviceReinvocationOutput;
	private String message;
	private String newServiceEndpoint;
	
	private ChangeProcessParams changeProcessParams;
	private ChangeSupervisionRule changeSupervisionRule;
	private ChangeSupervisionParams changeSupervisionParams;

	public RecoveryResult(boolean recoveryResult, String message)
	{
		this.recoveryResult = recoveryResult;
		this.message = message;
		
		this.thereHaltAction = false;
		this.thereIgnoreAction = false;
		this.thereInokeActivity = false;
		this.thereChangingSupervisionRules = false;	
		this.thereChangingSupervisionParams = false;
		this.thereChangingProcessParams = false;
		this.thereRebindAction = false;
		this.needRemonitoring = false;

		this.serviceReinvocationOutput = null;
		this.newServiceEndpoint = null;
		
		this.changeProcessParams = null;
		this.changeSupervisionRule = null;
		this.changeSupervisionParams = null;
	}
	public RecoveryResult()
	{
		// TODO Auto-generated constructor stub
		this.recoveryResult = false;
		this.thereHaltAction = false;
		this.thereIgnoreAction = false;
		this.thereInokeActivity = false;
		this.thereChangingSupervisionRules = false;	
		this.thereChangingSupervisionParams = false;
		this.thereChangingProcessParams = false;
		this.thereRebindAction = false;
		this.needRemonitoring = false;
		
		this.serviceReinvocationOutput = null;
		this.message = null;
		this.newServiceEndpoint = null;
		
		this.changeProcessParams = null;
		this.changeSupervisionRule = null;
		this.changeSupervisionParams = null;
	}
	public boolean isRecoveryResult()
	{
		return recoveryResult;
	}
	public void setRecoveryResult(boolean recoveryResult)
	{
		this.recoveryResult = recoveryResult;
	}
	public String getServiceReinvocationOutput()
	{
		return serviceReinvocationOutput;
	}
	public void setServiceReinvocationOutput(String serviceReinvocationOutput)
	{
		this.serviceReinvocationOutput = serviceReinvocationOutput;
	}
	public boolean isThereHaltAction()
	{
		return thereHaltAction;
	}
	public void setThereHaltAction()
	{
		this.thereHaltAction = true;
	}
	public boolean isThereIgnoreAction()
	{
		return thereIgnoreAction;
	}
	public void setThereIgnoreAction()
	{
		this.thereIgnoreAction = true;
	}
	public ChangeProcessParams getChangeProcessParams()
	{
		return changeProcessParams;
	}
	public void setChangeProcessParams(ChangeProcessParams changeProcessParams)
	{
		this.changeProcessParams = changeProcessParams;
	}
	public ChangeSupervisionParams getChangeSupervisionParams()
	{
		return changeSupervisionParams;
	}
	public void setChangeSupervisionParams(
			ChangeSupervisionParams changeSupervisionParams)
	{
		this.changeSupervisionParams = changeSupervisionParams;
	}
	public ChangeSupervisionRule getChangeSupervisionRule()
	{
		return changeSupervisionRule;
	}
	public void setChangeSupervisionRule(ChangeSupervisionRule changeSupervisionRule)
	{
		this.changeSupervisionRule = changeSupervisionRule;
	}
	public boolean isThereChangingProcessParams()
	{
		return thereChangingProcessParams;
	}
	public void setThereChangingProcessParams()
	{
		this.thereChangingProcessParams = true;
	}
	public boolean isThereChangingSupervisionParams()
	{
		return thereChangingSupervisionParams;
	}
	public void setThereChangingSupervisionParams()
	{
		this.thereChangingSupervisionParams = true;
	}
	public boolean isThereChangingSupervisionRules()
	{
		return thereChangingSupervisionRules;
	}
	public void setThereChangingSupervisionRules()
	{
		this.thereChangingSupervisionRules = true;
	}
	public boolean isThereInokeActivity()
	{
		return thereInokeActivity;
	}
	public void setThereInokeActivity()
	{
		this.thereInokeActivity = true;
	}
	public boolean isThereRebindAction()
	{
		return thereRebindAction;
	}
	public void setThereRebindAction()
	{
		this.thereRebindAction = true;
	}
	public String getNewServiceEndpoint()
	{
		return newServiceEndpoint;
	}
	public void setNewServiceEndpoint(String newServiceEndpoint)
	{
		this.newServiceEndpoint = newServiceEndpoint;
	}
	public boolean isNeedRemonitoring()
	{
		return needRemonitoring;
	}
	public void setNeedRemonitoring()
	{
		this.needRemonitoring = true;
	}
	public String getMessage()
	{
		return message;
	}
	public void addMessage(String message)
	{
		if(this.message == null)
			this.message = message;
		else
			this.message = this.message.concat(message);	
	}
	public void reset()
	{
		this.recoveryResult = false;
		this.thereHaltAction = false;
		this.thereIgnoreAction = false;
		this.thereInokeActivity = false;
		this.thereChangingSupervisionRules = false;	
		this.thereChangingSupervisionParams = false;
		this.thereChangingProcessParams = false;
		this.thereRebindAction = false;
		this.needRemonitoring = false;
		
		this.serviceReinvocationOutput = null;
		this.message = null;
		this.newServiceEndpoint = null;
		
		this.changeProcessParams = null;
		this.changeSupervisionRule = null;
		this.changeSupervisionParams = null;
	}
}
