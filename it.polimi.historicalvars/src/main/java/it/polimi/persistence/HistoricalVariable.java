
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
 

package it.polimi.persistence;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@NamedQueries({
	@NamedQuery(
		name="Get_HVar",
		query="select value from HistoricalVariable h where h.processID = :processID " +
				"and h.location = :location and h.assertionType = :assertionType and " +
				"h.aliasName = :aliasName and  h.userID = :userID and " +
				" h.instanceID = :instanceID order by h.timeValue desc"
	),
	@NamedQuery(
			name="Get_HVarNoIstance",
			query="select value from HistoricalVariable h where h.processID = :processID " +
					"and h.location = :location and h.assertionType = :assertionType and " +
					"h.aliasName = :aliasName and (:userID is null or h.userID = :userID)" +
					"order by h.timeValue desc"
		),
		@NamedQuery(
		name="Get_HVarNoUser",
		query="select value from HistoricalVariable h where h.processID = :processID " +
				"and h.location = :location and h.assertionType = :assertionType and " +
				"h.aliasName = :aliasName and h.instanceID = :instanceID order by h.timeValue desc"
	),
	@NamedQuery(
			name="Get_HVarNoIstaNoUser",
			query="select value from HistoricalVariable h where h.processID = :processID " +
					"and h.location = :location and h.assertionType = :assertionType and " +
					"h.aliasName = :aliasName order by h.timeValue desc"
		),
	//setParameter("time", new Date(), TemporalType.TIME)
	@NamedQuery(name="Get_Resp_time",
		query="select timeValue from HistoricalVariable h where h.aliasName = :aliasName and h.processID = :processID and h.assertionType = :assertionType and h.location = :location"
	)
})
@Entity
public class HistoricalVariable implements Serializable {
	
	
	//key
	private int id;
	
	private String aliasName; //not null
	private int assertionType; //not null
	private String processID; //not null
	private String location; //not null
	
	private String userID;

	private Long instanceID;

	private Date timeValue;
	
	private String value;
	
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public HistoricalVariable() {
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * @return the assertionType
	 */
	public int getAssertionType() {
		return assertionType;
	}

	/**
	 * @param assertionType the assertionType to set
	 */
	public void setAssertionType(int assertionType) {
		this.assertionType = assertionType;
	}

	/**
	 * @return the aliasName
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * @param aliasName the aliasName to set
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	@Id 
	@GeneratedValue //auto incrementata
	 /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the processID
	 */
	public String getProcessID() {
		return processID;
	}

	/**
	 * @param processID the processID to set
	 */
	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public String getUserID() {
		return this.userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public long getInstanceID() {
		return this.instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	@Temporal(TemporalType.TIMESTAMP)
	/**
	 * @return the timeValue
	 */
	public Date getTimeValue() {
		return timeValue;
	}
	

	/**
	 * @param timeValue the timeValue to set
	 */
	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	@Override
	public String toString() {
		return "Historical variable with ID " + id  + ", aliasName " + aliasName 
		+ ", assertionType " + assertionType + " , processID " + processID 
		+ ", location " + location+ ", userID " + userID + ", instanceID " 
		+ instanceID + ", timeValue " + timeValue + ", value " + value;
	}
	
	public HistoricalVariableInfo historicalVariableInfo(){
		return new HistoricalVariableInfo(getAliasName(), getAssertionType(), getProcessID(), getLocation(), getUserID(), getInstanceID(), getTimeValue(), getValue());
	}
	
}
