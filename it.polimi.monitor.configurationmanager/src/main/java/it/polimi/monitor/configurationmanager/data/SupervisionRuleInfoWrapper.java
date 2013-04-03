
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
 

package it.polimi.monitor.configurationmanager.data;


public class SupervisionRuleInfoWrapper 
{
	private String processID;
	private String userID;
	private String location;
	private boolean isPrecondition;
	private String wscolRule;
	private int priority;
	private String recoveryStrategy;
	private String timeFrame;
	private String providers;

	public boolean isPrecondition()
	{
		return isPrecondition;
	}
	public void setPrecondition(boolean isPrecondition)
	{
		this.isPrecondition = isPrecondition;
	}
	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public int getPriority()
	{
		return priority;
	}
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	public String getProcessID()
	{
		return processID;
	}
	public void setProcessID(String processID)
	{
		this.processID = processID;
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
	public String getUserID()
	{
		return userID;
	}
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	public String getWscolRule()
	{
		return wscolRule;
	}
	public void setWscolRule(String wscolRule)
	{
		this.wscolRule = wscolRule;
	}
}
