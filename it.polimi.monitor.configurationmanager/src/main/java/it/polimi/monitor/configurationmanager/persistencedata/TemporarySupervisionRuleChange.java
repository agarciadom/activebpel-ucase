
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
 

package it.polimi.monitor.configurationmanager.persistencedata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TEMPORAY_CHANGED_RULES")
public class TemporarySupervisionRuleChange implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4800232138879028025L;

	@EmbeddedId
	private TemporarySupervisionRuleChangePK pk;

	@Column(nullable=true)
	private String newCondition;
	@Column(nullable=true)
	private Integer newConditionPriority;
	@Column(nullable=true)
	private String newConditionRecovery;
	@Column(nullable=true)
	private String newTimeFrame;
	@Column(nullable=true)
	private String newProviderList;

	public TemporarySupervisionRuleChange()
	{
		// TODO Auto-generated constructor stub
	}
	
	public TemporarySupervisionRuleChange(TemporarySupervisionRuleChangePK pk, String newCondition, Integer newConditionPriority, String newConditionRecovery, String newTimeFrame, String newProviderList)
	{
		this.pk = pk;
		this.newCondition = newCondition;
		this.newConditionPriority = newConditionPriority;
		this.newConditionRecovery = newConditionRecovery;
		this.newTimeFrame = newTimeFrame;
		this.newProviderList = newProviderList;
	}

	public String getNewCondition()
	{
		return newCondition;
	}
	
	public void setNewCondition(String newCondition)
	{
		this.newCondition = newCondition;
	}
	
	public Integer getNewConditionPriority()
	{
		return newConditionPriority;
	}
	
	public void setNewConditionPriority(Integer newConditionPriority)
	{
		this.newConditionPriority = newConditionPriority;
	}
	
	public String getNewConditionRecovery()
	{
		return newConditionRecovery;
	}
	
	public void setNewConditionRecovery(String newConditionRecovery)
	{
		this.newConditionRecovery = newConditionRecovery;
	}
	
	public TemporarySupervisionRuleChangePK getPk()
	{
		return pk;
	}
	
	public void setPk(TemporarySupervisionRuleChangePK pk)
	{
		this.pk = pk;
	}

	public String getNewProviderList()
	{
		return newProviderList;
	}

	public void setNewProviderList(String newProviderList)
	{
		this.newProviderList = newProviderList;
	}

	public String getNewTimeFrame()
	{
		return newTimeFrame;
	}

	public void setNewTimeFrame(String newTimeFrame)
	{
		this.newTimeFrame = newTimeFrame;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((newCondition == null) ? 0 : newCondition.hashCode());
		result = PRIME * result + newConditionPriority;
		result = PRIME * result + ((newConditionRecovery == null) ? 0 : newConditionRecovery.hashCode());
		result = PRIME * result + ((pk == null) ? 0 : pk.hashCode());
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
		final TemporarySupervisionRuleChange other = (TemporarySupervisionRuleChange) obj;
		if (newCondition == null)
		{
			if (other.newCondition != null)
				return false;
		}
		else if (!newCondition.equals(other.newCondition))
			return false;
		if (newConditionPriority != other.newConditionPriority)
			return false;
		if (newConditionRecovery == null)
		{
			if (other.newConditionRecovery != null)
				return false;
		}
		else if (!newConditionRecovery.equals(other.newConditionRecovery))
			return false;
		if (pk == null)
		{
			if (other.pk != null)
				return false;
		}
		else if (!pk.equals(other.pk))
			return false;
		return true;
	}
}
