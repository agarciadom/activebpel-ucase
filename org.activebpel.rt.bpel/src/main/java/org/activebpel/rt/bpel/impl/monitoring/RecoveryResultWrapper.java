
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

public class RecoveryResultWrapper
{
	private boolean recoveryResult = false;
	private boolean terminateProcess = false;
	private boolean rebindService = false;
	private boolean serviceInvocation = false;
	private boolean ignore = false;
	private String newServiceEndpoint = null;
	private String serviceInvocationResult = null;
	private String recoveryMessage = null;

	public RecoveryResultWrapper()
	{
		// TODO Auto-generated constructor stub
	}

	public String getNewServiceEndpoint()
	{
		return newServiceEndpoint;
	}

	public void setNewServiceEndpoint(String newServiceEndpoint)
	{
		this.newServiceEndpoint = newServiceEndpoint;
	}

	public boolean isRebindService()
	{
		return rebindService;
	}

	public boolean isRecoveryResult()
	{
		return recoveryResult;
	}

	public boolean isServiceInvocation()
	{
		return serviceInvocation;
	}

	public boolean isTerminateProcess()
	{
		return terminateProcess;
	}

	public String getServiceInvocationResult()
	{
		return serviceInvocationResult;
	}

	public void setServiceInvocationResult(String serviceInvocationResult)
	{
		this.serviceInvocationResult = serviceInvocationResult;
	}

	public void setRebindService(boolean rebindService)
	{
		this.rebindService = rebindService;
	}

	public void setRecoveryResult(boolean recoveryResult)
	{
		this.recoveryResult = recoveryResult;
	}

	public void setServiceInvocation(boolean serviceInvocation)
	{
		this.serviceInvocation = serviceInvocation;
	}

	public void setTerminateProcess(boolean terminateProcess)
	{
		this.terminateProcess = terminateProcess;
	}
	
	public void setIgnore()
	{
		this.ignore = true;
	}
	
	public boolean isIgnore()
	{
		return this.ignore;
	}

	public String getRecoveryMessage()
	{
		return recoveryMessage;
	}

	public void setRecoveryMessage(String recoveryMessage)
	{
		this.recoveryMessage = recoveryMessage;
	}
}
