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

package it.polimi.monitor;

import java.util.Vector;
/**
 * This class is use to costruct an object that has information about a monitoring of
 * WSCoL rules with a set of data.
 *  
 * @author Luca Galluppi
 *
 */
public class ResultMonitor {
	
	private Boolean valueMonitor=null;
	private Vector<String> messages=null;
	private Vector<Boolean> values=null;
	/**
	 * Costruct a new object that will collect informations and calculate the global
	 * monitoring value. It also can give the informations collected.
	 *
	 */
	public ResultMonitor(){
		valueMonitor=new Boolean(true);
		this.messages=new Vector<String>();
		this.values=new Vector<Boolean>();
	}
	/**
	 * Add a new information to this.
	 * @param mes The message that want to add.
	 */
	public void addMessage(String mes) {
		messages.add(mes);
	}
	/**
	 * Add a new partial value of a single rule to this.
	 * @param value The value to add.
	 */
	public void addValue(Boolean value) {
		values.add(value);
	}

	/**
	 * Get all informations stored on this.
	 * @return the messages all message added.
	 */
	public Vector<String> getMessages() {
		return messages;
	}

	/**
	 * Make the "and" logical opertation form all the values added.  If there is no values true is return.
	 * @return the valueMonitor And of all value. If there is no values true is return.
	 */
	public Boolean getValueMonitor() {
		calculateMonitor();
		return valueMonitor;
	}
	
	private void calculateMonitor(){
		for (Boolean i:values)
			if (i.booleanValue()==false) 
				valueMonitor=new Boolean(false);
			else
				if(valueMonitor.booleanValue()==true && i.booleanValue()==true)
					valueMonitor=i;
				else 
					valueMonitor=new Boolean(false);
	}

	@Override
	public String toString() {
		String mes=new String();
		for(String i:messages)
			mes=mes+i+"\n";
		return "Boolean: "+ getValueMonitor() +"\nMessaggi: " + mes; 
	}
}
