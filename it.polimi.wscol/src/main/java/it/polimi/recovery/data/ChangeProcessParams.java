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

public class ChangeProcessParams
{
	private int newPriority;
	private String changeType;

	public ChangeProcessParams(int newPriority, String changeType)
	{
		this.newPriority = newPriority;
		this.changeType = changeType;
	}
	
	public ChangeProcessParams()
	{
		// TODO Auto-generated constructor stub
		this.newPriority = 0;
		this.changeType = null;
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
}
