
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
 

package it.polimi.monitor.configurationmanager.data;

public class TemporaryRuleChangingInfoWrapper
{
	private String processID;
	private String userID;
	private String location;
	private long processInstanceID;
	private boolean isPrecondition;
	private String newCondition;
	private Integer newConditionPriority;
	private String newConditionRecovery;
	private String newTimeFrame;
	private String newProviderList;

	public boolean isPrecondition()
	{
		return isPrecondition;
	}

	public void setPrecondition(boolean isPrecondition)
	{
		this.isPrecondition = isPrecondition;
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

	public String getProcessID()
	{
		return processID;
	}

	public void setProcessID(String processID)
	{
		this.processID = processID;
	}

	public long getProcessInstanceID()
	{
		return processInstanceID;
	}

	public void setProcessInstanceID(long processInstanceID)
	{
		this.processInstanceID = processInstanceID;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
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
}
