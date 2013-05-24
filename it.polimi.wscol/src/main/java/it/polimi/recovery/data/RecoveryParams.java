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

public class RecoveryParams
{
	private ProcessParams processParams;
	private SupervisionParams supervisionParams;
	private ServiceInvocationParams invocationServiceData;
	private String xmlMailConfig;
	
	public RecoveryParams(ProcessParams processParams, SupervisionParams supervisionParams, ServiceInvocationParams invocationServiceData)
	{
		this.processParams = processParams;
		this.supervisionParams = supervisionParams;
		this.invocationServiceData = invocationServiceData;
	}

	public ServiceInvocationParams getInvocationServiceData()
	{
		return invocationServiceData;
	}

	public void setInvocationServiceData(ServiceInvocationParams invocationServiceData)
	{
		this.invocationServiceData = invocationServiceData;
	}

	public ProcessParams getProcessParams()
	{
		return processParams;
	}

	public void setProcessParams(ProcessParams processParams)
	{
		this.processParams = processParams;
	}

	public SupervisionParams getSupervisionParams()
	{
		return supervisionParams;
	}

	public void setSupervisionParams(SupervisionParams supervisionParams)
	{
		this.supervisionParams = supervisionParams;
	}

	public RecoveryParams clone()
	{
		return new RecoveryParams(this.processParams,
									this.supervisionParams,
									this.invocationServiceData);
	}

	public String getXmlMailConfig()
	{
		return xmlMailConfig;
	}

	public void setXmlMailConfig(String xmlMailConfig)
	{
		this.xmlMailConfig = xmlMailConfig;
	}
}
