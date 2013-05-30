
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
 

package it.polimi.monitor.configurationmanager.persistencedata;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "processes")
public class ProcessData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5583840446284498478L;

	@EmbeddedId
	private ProcessDataPK primaryKey;
	
	private int processPriority;
	
	public ProcessData()
	{
		
	}
	
	public ProcessData(String pID, String uID, int pPriority)
	{
		this.primaryKey = new ProcessDataPK(pID, uID);
		this.processPriority = pPriority;
	}
	
	public String GetProcessID()
	{
		return this.primaryKey.getProcessID();
	}
	
	public String GetUserID()
	{
		return this.primaryKey.getUserID();
	}
	
	public int GetProcessPriority()
	{
		return processPriority;
	}
	
	public void SetProcessPriority(int pPriority)
	{
		this.processPriority = pPriority;
	}
	public String toString(){
		return "name: "+GetProcessID()+ " user: "+GetUserID()+" priority:"+ GetProcessPriority();
	}
}
