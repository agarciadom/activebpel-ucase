
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
 

package it.polimi.monitor.monitorlogger.data;

import java.util.Date;

public class RecoveryResultInfoWrapper
{
	private Date date;
	private String processID;
	private String userID;
	private String location;
	private boolean isPrecondition;
	private String completeRecoveryStrategy;
	private String executedRecoveryStrategy;
	private Boolean successful;
	private long recoveryTime;
	
	public String getCompleteRecoveryStrategy()
	{
		return completeRecoveryStrategy;
	}
	public void setCompleteRecoveryStrategy(String completeRecoveryStrategy)
	{
		this.completeRecoveryStrategy = completeRecoveryStrategy;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public String getExecutedRecoveryStrategy()
	{
		return executedRecoveryStrategy;
	}
	public void setExecutedRecoveryStrategy(String executedRecoveryStrategy)
	{
		this.executedRecoveryStrategy = executedRecoveryStrategy;
	}
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
	public String getProcessID()
	{
		return processID;
	}
	public void setProcessID(String processID)
	{
		this.processID = processID;
	}
	public Boolean isSuccessful()
	{
		return successful;
	}
	public void setSuccessful(Boolean successful)
	{
		this.successful = successful;
	}
	public String getUserID()
	{
		return userID;
	}
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	public long getRecoveryTime() {
		return recoveryTime;
	}
	public void setRecoveryTime(long recoveryTime) {
		this.recoveryTime = recoveryTime;
	}
}
