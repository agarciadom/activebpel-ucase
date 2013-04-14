
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

import java.util.*;

public class ProcessUsersList {

	private Vector list = new Vector();
	
	public synchronized void addProcess(String process,long id){
		CoupleProcessUser couple = new CoupleProcessUser();
		couple.setProcess(process,id);
		list.add(couple);
	}
	
	public synchronized void addProcessUser(String process,long id,String user,int priority){
		CoupleProcessUser couple = new CoupleProcessUser();
		couple.setProcess(process,id);
		couple.setUser(user);
		couple.setPriority(priority);
		list.add(couple);
		
	}
	
	public synchronized String findUser(String process, long id) {
		int i;
		for (i = 0; i < list.size(); i++) {
			CoupleProcessUser currentCouple = (CoupleProcessUser) list.get(i);
			if (process.equals(currentCouple.getProcess())) {
				if (id == currentCouple.getId()) {
					return currentCouple.getUser();
				}
			}
		}
		return "";
	}
	
	public synchronized int findPriority(String process, long id){
		int i;
		for (i = 0; i < list.size(); i++) {
			CoupleProcessUser currentCouple = (CoupleProcessUser) list.get(i);
			if (process.equals(currentCouple.getProcess())) {
				if (id == currentCouple.getId()) {
					return currentCouple.getPriority();
				}
			}
		}
		return 5;
	}
	
	public synchronized void deleteCouple(String process, long id, String user) {
		int i;
		for (i = 0; i < list.size(); i++) {
			CoupleProcessUser currentCouple = (CoupleProcessUser) list.get(i);
			if (process.equals(currentCouple.getProcess())) {
				if (id == currentCouple.getId()) {
					if (user.equals(currentCouple.getUser())) {
						list.remove(i);
					}
				}
			}
		}
	}
	
}
