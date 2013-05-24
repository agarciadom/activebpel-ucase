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

public class ChangeSupervisionParams
{
	private int newPriority;
	private String newTimeFrame;
	private String newProviderList;
	private String changeType;

	public ChangeSupervisionParams()
	{
		// TODO Auto-generated constructor stub
		this.newPriority = 0;
		this.newTimeFrame = null;
		this.newProviderList = null;
		this.changeType = null;
	}

	public ChangeSupervisionParams(int newPriority, String newTimeFrame, String newProviderList, String changeType)
	{
		this.newPriority = newPriority;
		this.newTimeFrame = newTimeFrame;
		this.newProviderList = newProviderList;
		this.changeType = changeType;
	}

	public String getChangeType()
	{
		return changeType;
	}

	public void setChangeType(String changeType)
	{
		this.changeType = changeType;
	}

	public int getNewPriority()
	{
		return newPriority;
	}

	public void setNewPriority(int newPriority)
	{
		this.newPriority = newPriority;
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
