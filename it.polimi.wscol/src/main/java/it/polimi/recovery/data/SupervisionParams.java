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

public class SupervisionParams
{
	private String wscolRule;
	private String recoveryStrategies;
	private String monitoringData;
	private int wscolRulePriority;
	private String configHvar;
	
	public SupervisionParams(String wscolRule, String recoveryStrategies, String monitoringData, int wscolRulePriority, String configHvar)
	{
		this.wscolRule = wscolRule;
		this.recoveryStrategies = recoveryStrategies;
		this.monitoringData = monitoringData;
		this.wscolRulePriority = wscolRulePriority;
		this.configHvar = configHvar;
	}

	public SupervisionParams()
	{
		// TODO Auto-generated constructor stub
		this.wscolRule = null;
		this.recoveryStrategies = null;
		this.monitoringData = null;
	}

	public String getMonitoringData()
	{
		return monitoringData;
	}

	public void setMonitoringData(String monitoringData)
	{
		this.monitoringData = monitoringData;
	}

	public String getRecoveryStrategies()
	{
		return recoveryStrategies;
	}

	public void setRecoveryStrategies(String recoveryStrategies)
	{
		this.recoveryStrategies = recoveryStrategies;
	}

	public String getWscolRule()
	{
		return wscolRule;
	}

	public void setWscolRule(String wscolRule)
	{
		this.wscolRule = wscolRule;
	}
	

	public int getWscolRulePriority()
	{
		return wscolRulePriority;
	}

	public void setWscolRulePriority(int wscolRulePriority)
	{
		this.wscolRulePriority = wscolRulePriority;
	}

	public String getConfigHvar()
	{
		return configHvar;
	}

	public void setConfigHvar(String configHvar)
	{
		this.configHvar = configHvar;
	}

	public SupervisionParams clone()
	{
		return new SupervisionParams(this.wscolRule,
										this.recoveryStrategies,
										this.monitoringData,
										this.wscolRulePriority,
										this.configHvar);
	}
}
