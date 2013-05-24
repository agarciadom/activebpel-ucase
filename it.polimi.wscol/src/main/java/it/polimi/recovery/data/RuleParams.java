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

public class RuleParams
{
	private Integer priority;
	private String timeFrame;
	private String providers;
	
	public RuleParams()
	{
		// TODO Auto-generated constructor stub
	}
	public RuleParams(Integer priority, String timeFrame, String providers)
	{
		this.priority = priority;
		this.timeFrame = timeFrame;
		this.providers = providers;
	}
	public Integer getPriority()
	{
		return priority;
	}
	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}
	public String getTimeFrame()
	{
		return timeFrame;
	}
	public void setTimeFrame(String timeFrame)
	{
		this.timeFrame = timeFrame;
	}
	public String getProviders()
	{
		return providers;
	}
	public void setProviders(String providers)
	{
		this.providers = providers;
	}
}
