
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
 

package it.polimi.monitor.configurationmanager.persistencedata;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ProcessDataPK implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5006323906321075947L;

	private String processID;
	private String userID;

	public ProcessDataPK()
	{
		// TODO Auto-generated constructor stub
	}
	public ProcessDataPK(String processID, String userID)
	{
		// TODO Auto-generated constructor stub
		this.processID = processID;
		this.userID = userID;
	}
	public String getProcessID()
	{
		return processID;
	}
//	public void setProcessID(String processID)
//	{
//		this.processID = processID;
//	}
	public String getUserID()
	{
		return userID;
	}
//	public void setUserID(String userID)
//	{
//		this.userID = userID;
//	}
}
