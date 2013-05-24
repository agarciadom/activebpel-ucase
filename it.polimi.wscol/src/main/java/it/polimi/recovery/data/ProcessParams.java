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

public class ProcessParams
{
	private String processID;
	private long processInstanceID;
	private String userID;
	private String location;
	private String processWSDLUrl;
	private boolean isPrecondition;
	private int processPriority;

	public ProcessParams(String processID, long processInstanceID, String userID, String location, String processWSDLUrl, boolean isPrecondition, int processPriority)
	{
		this.processID = processID;
		this.processInstanceID = processInstanceID;
		this.userID = userID;
		this.location = location;
		this.processWSDLUrl = processWSDLUrl;
		this.isPrecondition = isPrecondition;
		this.processPriority = processPriority;
	}

	public ProcessParams()
	{
		// TODO Auto-generated constructor stub
		this.processID = null;
		this.processInstanceID = 0;
		this.userID = null;
		this.location = null;
		this.processWSDLUrl = null;
		this.isPrecondition = false;
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

	public long getProcessInstanceID()
	{
		return processInstanceID;
	}

	public void setProcessInstanceID(long processInstanceID)
	{
		this.processInstanceID = processInstanceID;
	}

	public String getProcessWSDLUrl()
	{
		return processWSDLUrl;
	}

	public void setProcessWSDLUrl(String processWSDLUrl)
	{
		this.processWSDLUrl = processWSDLUrl;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public int getProcessPriority()
	{
		return processPriority;
	}

	public void setProcessPriority(int processPriority)
	{
		this.processPriority = processPriority;
	}

	public ProcessParams clone()
	{
		return new ProcessParams(this.processID,
									this.processInstanceID,
									this.userID,
									this.location,
									this.processWSDLUrl,
									this.isPrecondition,
									this.processPriority);
	}
}
