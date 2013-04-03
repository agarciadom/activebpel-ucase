
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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TEMPORARY_PROCESS_DATA_CHANGES")
public class TemporaryProcessDataChanging implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1088101401893012397L;
	
	@EmbeddedId
	private TemporaryProcessDataChangingPK pk;
	private int newPriority;

	public TemporaryProcessDataChanging()
	{
		// TODO Auto-generated constructor stub
	}

	public TemporaryProcessDataChanging(TemporaryProcessDataChangingPK pk, int newPriority)
	{
		this.pk = pk;
		this.newPriority = newPriority;
	}

	public int getNewPriority()
	{
		return newPriority;
	}

	public void setNewPriority(int newPriority)
	{
		this.newPriority = newPriority;
	}

	public TemporaryProcessDataChangingPK getPk()
	{
		return pk;
	}

	public void setPk(TemporaryProcessDataChangingPK pk)
	{
		this.pk = pk;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + newPriority;
		result = PRIME * result + ((pk == null) ? 0 : pk.hashCode());
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
		final TemporaryProcessDataChanging other = (TemporaryProcessDataChanging) obj;
		if (newPriority != other.newPriority)
			return false;
		if (pk == null)
		{
			if (other.pk != null)
				return false;
		}
		else if (!pk.equals(other.pk))
			return false;
		return true;
	}
}
