
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
 

package it.polimi.monitor.monitorlogger.persistencedata;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class RecoveryResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4793514020880760207L;

	@EmbeddedId
	private RecoveryResultPK pk;

	private String completeRecoveryStrategy;
	private String executedRecoveryStrategy;
	private boolean successful;
	private long recoveryTime;
	public RecoveryResult()
	{
		// TODO Auto-generated constructor stub
	}

	public RecoveryResult(RecoveryResultPK pk, String completeRecoveryStrategy, String executedRecoveryStrategy, boolean successful, long recoveryTime)
	{
		this.pk = pk;
		this.completeRecoveryStrategy = completeRecoveryStrategy;
		this.executedRecoveryStrategy = executedRecoveryStrategy;
		this.successful = successful;
		this.recoveryTime = recoveryTime;
	}

	public String getCompleteRecoveryStrategy()
	{
		return completeRecoveryStrategy;
	}

	public void setCompleteRecoveryStrategy(String completeRecoveryStrategy)
	{
		this.completeRecoveryStrategy = completeRecoveryStrategy;
	}

	public String getExecutedRecoveryStrategy()
	{
		return executedRecoveryStrategy;
	}

	public void setExecutedRecoveryStrategy(String executedRecoveryStrategy)
	{
		this.executedRecoveryStrategy = executedRecoveryStrategy;
	}

	public RecoveryResultPK getPk()
	{
		return pk;
	}

	public void setPk(RecoveryResultPK pk)
	{
		this.pk = pk;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((completeRecoveryStrategy == null) ? 0 : completeRecoveryStrategy.hashCode());
		result = PRIME * result + ((executedRecoveryStrategy == null) ? 0 : executedRecoveryStrategy.hashCode());
		result = PRIME * result + ((pk == null) ? 0 : pk.hashCode());
		result = PRIME * result + (successful ? 1231 : 1237);
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
		final RecoveryResult other = (RecoveryResult) obj;
		if (completeRecoveryStrategy == null)
		{
			if (other.completeRecoveryStrategy != null)
				return false;
		}
		else if (!completeRecoveryStrategy.equals(other.completeRecoveryStrategy))
			return false;
		if (executedRecoveryStrategy == null)
		{
			if (other.executedRecoveryStrategy != null)
				return false;
		}
		else if (!executedRecoveryStrategy.equals(other.executedRecoveryStrategy))
			return false;
		if (pk == null)
		{
			if (other.pk != null)
				return false;
		}
		else if (!pk.equals(other.pk))
			return false;
		if (successful != other.successful)
			return false;
		if (recoveryTime != other.recoveryTime)
			return false;
		return true;
		
	}

	public long getRecoveryTime() {
		return recoveryTime;
	}

	public void setRecoveryTime(long recoveryTime) {
		this.recoveryTime = recoveryTime;
	}
}
