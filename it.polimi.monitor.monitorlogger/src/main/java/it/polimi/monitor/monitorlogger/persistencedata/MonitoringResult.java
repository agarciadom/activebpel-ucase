
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
 

package it.polimi.monitor.monitorlogger.persistencedata;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class MonitoringResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1617323396986078548L;

	@EmbeddedId
	private MonitoringResultPK pk;
	
	private String wscolRule;
	private int wscolPriority;
	private int processPriority;
	private String timeFrame;
	private String providers;
	private boolean monitoringResult;
	private String monitoringData;
	private long monitoringTime;

	public MonitoringResult()
	{
	}

	public MonitoringResult(MonitoringResultPK pk, String wscolRule, int wscolPriority, int processPriority, String timeFrame, String providers, boolean monitoringResult, String monitoringData, long monitoringTime)
	{
		this.pk = pk;
		this.wscolRule = wscolRule;
		this.wscolPriority = wscolPriority;
		this.processPriority = processPriority;
		this.timeFrame = timeFrame;
		this.providers = providers;
		this.monitoringResult = monitoringResult;
		this.monitoringData = monitoringData;
		this.monitoringTime = monitoringTime;
	}

	public String getMonitoringData()
	{
		return monitoringData;
	}

	public void setMonitoringData(String monitoringData)
	{
		this.monitoringData = monitoringData;
	}

	public boolean isMonitoringResult()
	{
		return monitoringResult;
	}

	public void setMonitoringResult(boolean monitoringResult)
	{
		this.monitoringResult = monitoringResult;
	}

	public MonitoringResultPK getPk()
	{
		return pk;
	}

	public void setPk(MonitoringResultPK pk)
	{
		this.pk = pk;
	}

	public int getProcessPriority()
	{
		return processPriority;
	}

	public void setProcessPriority(int processPriority)
	{
		this.processPriority = processPriority;
	}

	public String getProviders()
	{
		return providers;
	}

	public void setProviders(String providers)
	{
		this.providers = providers;
	}

	public String getTimeFrame()
	{
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame)
	{
		this.timeFrame = timeFrame;
	}

	public int getWscolPriority()
	{
		return wscolPriority;
	}

	public void setWscolPriority(int wscolPriority)
	{
		this.wscolPriority = wscolPriority;
	}

	public String getWscolRule()
	{
		return wscolRule;
	}

	public void setWscolRule(String wscolRule)
	{
		this.wscolRule = wscolRule;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((monitoringData == null) ? 0 : monitoringData.hashCode());
		result = PRIME * result + (monitoringResult ? 1231 : 1237);
		result = PRIME * result + ((pk == null) ? 0 : pk.hashCode());
		result = PRIME * result + processPriority;
		result = PRIME * result + ((providers == null) ? 0 : providers.hashCode());
		result = PRIME * result + ((timeFrame == null) ? 0 : timeFrame.hashCode());
		result = PRIME * result + wscolPriority;
		result = PRIME * result + ((wscolRule == null) ? 0 : wscolRule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MonitoringResult other = (MonitoringResult) obj;
		if (monitoringData == null)
		{
			if (other.monitoringData != null)
				return false;
		}
		else if (!monitoringData.equals(other.monitoringData))
			return false;
		if (monitoringResult != other.monitoringResult)
			return false;
		if (pk == null)
		{
			if (other.pk != null)
				return false;
		}
		else if (!pk.equals(other.pk))
			return false;
		if (processPriority != other.processPriority)
			return false;
		if (providers == null)
		{
			if (other.providers != null)
				return false;
		}
		else if (!providers.equals(other.providers))
			return false;
		if (timeFrame == null)
		{
			if (other.timeFrame != null)
				return false;
		}
		else if (!timeFrame.equals(other.timeFrame))
			return false;
		if (wscolPriority != other.wscolPriority)
			return false;
		if (monitoringTime != other.monitoringTime)
			return false; 
		if (wscolRule == null)
		{
			if (other.wscolRule != null)
				return false;
		}
		else if (!wscolRule.equals(other.wscolRule))
			return false;
		return true;
	}

	public long getMonitoringTime() {
		return monitoringTime;
	}

	public void setMonitoringTime(long monitorTime) {
		this.monitoringTime = monitorTime;
	}
}
