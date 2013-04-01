
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

public class CoupleProcessUser {

	private String process;
	private long idProcess;
	private String user;
	private int priority;
	
	public CoupleProcessUser(){
		process = null;
		idProcess = -1;
		user = null;
		priority = 0;
	}
	
	public void setProcess(String process,long id){
		this.process = process;
		idProcess = id;
	}
	
	public void setUser(String user){
		this.user = user;
	}
	
	public String getProcess(){
		return process;
	}
	
	public long getId(){
		return idProcess;
	}
	
	public String getUser(){
		return user;
	}
	
	public void setPriority(int pr){
		this.priority = pr;
	}
	
	public int getPriority(){
		return priority;
	}
	
}
