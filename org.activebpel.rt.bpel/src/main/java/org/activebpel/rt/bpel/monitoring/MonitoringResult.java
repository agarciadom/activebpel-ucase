
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
 
 
 
package org.activebpel.rt.bpel.impl.monitoring;

import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;

public class MonitoringResult
{
	private boolean result;
	private String monitoringData;
	private Aliases aliases = null; 
	private AliasNodes tempAliases = null;

	public MonitoringResult(boolean result, String monitoringData, Aliases aliases, AliasNodes tempAliases)
	{
		this.result = result;
		this.monitoringData = monitoringData;
		this.aliases = aliases;
		this.tempAliases = tempAliases;
	}

	public String getMonitoringData()
	{
		return monitoringData;
	}

	public boolean isResult()
	{
		return result;
	}

	public Aliases getAliases()
	{
		return aliases;
	}

	public AliasNodes getTempAliases()
	{
		return tempAliases;
	}
}
