
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

public class ProcessInfoWrapper
{
	private String processId;
	private String userId;
	private Long processInstanceId;
	private Integer priority;
	
	public Long getProcessInstanceId()
	{
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId)
	{
		this.processInstanceId = processInstanceId;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public Integer getPriority() 
	{
		return priority;
	}
	public void setPriority(Integer priority) 
	{
		this.priority = priority;
	}
	public String getProcessId() 
	{
		return processId;
	}
	public void setProcessId(String processId) 
	{
		this.processId = processId;
	}
}
