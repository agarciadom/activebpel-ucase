
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
 

package it.polimi.persistence;

import java.io.Serializable;
import java.util.Date;

public class HistoricalVariableInfo implements Serializable{
	private String aliasName; //not null
	private int assertionType; //not null
	private String processID; //not null
	private String location; //not null
	
	private String userID;

	private long instanceID;

	private Date timeValue;
	
	private String value;
	private static final long serialVersionUID = 23131L;

	/**
	 * 
	 */
	public HistoricalVariableInfo() {
		super();
	}
	/**
	 * @param aliasName
	 * @param assertionType
	 * @param processID
	 * @param location
	 * @param userID
	 * @param instanceID
	 * @param timeValue
	 * @param value
	 */
	public HistoricalVariableInfo(String aliasName, int assertionType, String processID, String location, String userId, Long instanceId, Date timeValue, String value) {
		super();
		this.aliasName = aliasName;
		this.assertionType = assertionType;
		this.processID = processID;
		this.location = location;
		this.userID = userId;
		this.instanceID = instanceId;
		this.timeValue = timeValue;
		this.value = value;
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
	 * @return the instanceID
	 */
	public long getInstanceID() {
		return instanceID;
	}

	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(long instanceId) {
		this.instanceID = instanceId;
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
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userId) {
		this.userID = userId;
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
		return "HistoricalVariableInfo with aliasName " + aliasName 
		+ ", assertionType " + assertionType + " , processID " + processID 
		+ ", location " + location+ ", userID " + userID + ", instanceID " 
		+ instanceID + ", timeValue " + timeValue.getTime() + ", value " + value;
	}

	
}
