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

public class ChangeSupervisionRule
{
	private String newWSCoLRule;
	private String newWSReLStrategy;
	private String changeType;

	public ChangeSupervisionRule()
	{
		// TODO Auto-generated constructor stub
		this.newWSCoLRule = null;
		this.newWSReLStrategy = null;
		this.changeType = null;
	}

	public ChangeSupervisionRule(String newWSCoLRule, String newWSReLStrategy, String changeType)
	{
		this.newWSCoLRule = newWSCoLRule;
		this.newWSReLStrategy = newWSReLStrategy;
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

	public String getNewWSCoLRule()
	{
		return newWSCoLRule;
	}

	public void setNewWSCoLRule(String newWSCoLRule)
	{
		this.newWSCoLRule = newWSCoLRule;
	}

	public String getNewWSReLStrategy()
	{
		return newWSReLStrategy;
	}

	public void setNewWSReLStrategy(String newWSReLStrategy)
	{
		this.newWSReLStrategy = newWSReLStrategy;
	}
}
