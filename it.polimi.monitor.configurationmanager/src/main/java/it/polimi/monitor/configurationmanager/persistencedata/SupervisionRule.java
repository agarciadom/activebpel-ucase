
/* Copyright 2007, 2008 , DEEP SE group, Dipartimento di Elettronica e Informazione (DEI), Politecnico di Milano */


/*  
 *  License: 
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
 

package it.polimi.monitor.configurationmanager.persistencedata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "supervision_rules")
public class SupervisionRule implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2240795304135401085L;

	@EmbeddedId
	private SupervisionRulePK pk;
	
	private String wscolRule;
	private Integer priority;
	@Column(nullable=true)
	private String recoveryStrategy;
	@Column(nullable=true)
	private String timeFrame;
	@Column(nullable=true)
	private String providers;
	
	public SupervisionRule()
	{
		
	}

	public SupervisionRule(SupervisionRulePK pk, String wscolRule, Integer priority, String recoveryStrategy, String timeFrame, String providers)
	{
		this.pk = pk;
		this.wscolRule = wscolRule;
		this.priority = priority;
		this.recoveryStrategy = recoveryStrategy;
		this.timeFrame = timeFrame;
		this.providers = providers;
	}

	public SupervisionRulePK getPk()
	{
		return pk;
	}

	public void setPk(SupervisionRulePK pk)
	{
		this.pk = pk;
	}

	public Integer getPriority()
	{
		return priority;
	}

	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}

	public String getProviders()
	{
		return providers;
	}

	public void setProviders(String providers)
	{
		this.providers = providers;
	}

	public String getRecoveryStrategy()
	{
		return recoveryStrategy;
	}

	public void setRecoveryStrategy(String recoveryStrategy)
	{
		this.recoveryStrategy = recoveryStrategy;
	}

	public String getTimeFrame()
	{
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame)
	{
		this.timeFrame = timeFrame;
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
		result = PRIME * result + ((pk == null) ? 0 : pk.hashCode());
		result = PRIME * result + priority;
		result = PRIME * result + ((providers == null) ? 0 : providers.hashCode());
		result = PRIME * result + ((recoveryStrategy == null) ? 0 : recoveryStrategy.hashCode());
		result = PRIME * result + ((timeFrame == null) ? 0 : timeFrame.hashCode());
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
		final SupervisionRule other = (SupervisionRule) obj;
		if (pk == null)
		{
			if (other.pk != null)
				return false;
		}
		else if (!pk.equals(other.pk))
			return false;
		if (priority != other.priority)
			return false;
		if (providers == null)
		{
			if (other.providers != null)
				return false;
		}
		else if (!providers.equals(other.providers))
			return false;
		if (recoveryStrategy == null)
		{
			if (other.recoveryStrategy != null)
				return false;
		}
		else if (!recoveryStrategy.equals(other.recoveryStrategy))
			return false;
		if (timeFrame == null)
		{
			if (other.timeFrame != null)
				return false;
		}
		else if (!timeFrame.equals(other.timeFrame))
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
	public String toString(){
		if (providers == null)
			providers="null";
		if (wscolRule == null)
			wscolRule="null";
		if (recoveryStrategy == null)
			recoveryStrategy="null";
		if (timeFrame == null)
			timeFrame="null";
		return "processID: "+pk.getProcessID()+ " user: "+pk.getUserID()+" precondition:" +pk.isPrecondition()+" location:"
		+ pk.getLocation() +" wscolRule: "+this.wscolRule+" recoveryStrategy: " +this.recoveryStrategy+ " timeFrame: "+timeFrame+ " providers: "+providers;
	}
}
