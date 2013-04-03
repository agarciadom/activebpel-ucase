
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
public class TemporaryProcessDataChangingPK implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2371695392878656738L;
	
	private String processID;
	private String userID;
	private long processInstanceID;

	public TemporaryProcessDataChangingPK()
	{
		// TODO Auto-generated constructor stub
	}

	public TemporaryProcessDataChangingPK(String processID, String userID, long processInstanceID)
	{
		this.processID = processID;
		this.userID = userID;
		this.processInstanceID = processInstanceID;
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

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((processID == null) ? 0 : processID.hashCode());
		result = PRIME * result + (int) (processInstanceID ^ (processInstanceID >>> 32));
		result = PRIME * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TemporaryProcessDataChangingPK other = (TemporaryProcessDataChangingPK) obj;
		if (processID == null)
		{
			if (other.processID != null)
				return false;
		}
		else if (!processID.equals(other.processID))
			return false;
		if (processInstanceID != other.processInstanceID)
			return false;
		if (userID == null)
		{
			if (other.userID != null)
				return false;
		}
		else if (!userID.equals(other.userID))
			return false;
		return true;
	}
}
