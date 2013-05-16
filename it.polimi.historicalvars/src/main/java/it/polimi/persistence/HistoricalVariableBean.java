
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
 

/**
 * 
 */
package it.polimi.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Luca Galluppi
 *
 */

@Stateless

@WebService
public class HistoricalVariableBean implements HistoricalVariableRemote {
	@PersistenceContext(unitName="historicalVariables") 
	EntityManager manager;

	
	/**
	 * 
	 */
	@WebMethod
	public String[] findHistoricalVariable(@WebParam(name="processID") String processID, @WebParam(name="userID") String userID, @WebParam(name="instanceID") Long instanceID, @WebParam(name="location") String location, @WebParam(name="assertionType") int assertionType,@WebParam(name="aliasName") String aliasName, @WebParam(name="numberOfResults")int numberOfResults ) throws SearchHistoricalVariableException{
		System.out.println("Find Historical Variable");
		//Se non ho i parametri minimi sollevo l'eccezione SearchHistoricalVariableException
		if (processID == null &&  aliasName == null && location == null) 
			throw new SearchHistoricalVariableException("A few parameter specified for search Historical Variable \n" +
					"you must provide a value for  aliasname, assertion type, \n" +
					"process id, location and value.");
		System.out.println("Parameters:");
		System.out.println("processID: "+processID);
		System.out.println("userID: "+userID);
		System.out.println("instanceID: "+instanceID);
		System.out.println("location: "+location);
		System.out.println("assertionType: "+assertionType);
		System.out.println("aliasName: "+aliasName);
		System.out.println("numerOfResults: "+numberOfResults);
		Query q;
		if ((instanceID==-1)&&(userID==null)){
			q=manager.createNamedQuery("Get_HVarNoIstaNoUser").setParameter("processID", processID)
			.setParameter("location", location).setParameter("assertionType", assertionType).setParameter("aliasName", aliasName);
		System.out.println(1);
		}else if(instanceID==-1){
			q=manager.createNamedQuery("Get_HVarNoIstance").setParameter("processID", processID)
			.setParameter("userID", userID).setParameter("location", location)
			.setParameter("assertionType", assertionType).setParameter("aliasName", aliasName);
			System.out.println(2);
		}else if (userID==null){
			 q=manager.createNamedQuery("Get_HVarNoUser").setParameter("processID", processID)
			.setParameter("instanceID", instanceID).setParameter("location", location)
			.setParameter("assertionType", assertionType).setParameter("aliasName", aliasName);
			 System.out.println(3);
		}else{
			q=manager.createNamedQuery("Get_HVar").setParameter("processID", processID)
			.setParameter("userID", userID).setParameter("instanceID", instanceID).setParameter("location", location)
			.setParameter("assertionType", assertionType).setParameter("aliasName", aliasName);
			System.out.println(4);
		}//eseguo la query	
		List l=q.setMaxResults(numberOfResults).getResultList();
		System.out.println("Number of query results "+l.size());
		Vector<String> result=new Vector<String>();
		//colleziono i risultati
		for (Object object : l) {
			String h = (String)object;
			result.add(h);
		}
		//restituisco i risultati
		System.out.println("Return results");
		return result.toArray(new String[result.size()]);
	}
	
	@WebMethod
	public Calendar getRespTime(@WebParam(name="processID") String processID, @WebParam(name="userID") String userID, @WebParam(name="instanceID") Long instanceID, @WebParam(name="location") String location, @WebParam(name="assertionType") int assertionType,@WebParam(name="aliasName") String aliasName ) {
		if (processID == null &&  aliasName == null && location == null) 
			return null;
		Object tmp =manager.createNamedQuery("Get_Resp_time").setParameter("processID", processID)
		.setParameter("location", location).setParameter("assertionType", assertionType).setParameter("aliasName", aliasName).getSingleResult();
		Calendar c=new GregorianCalendar();
		c.setTime((Date)tmp);
		return c; 
	}

	@WebMethod
	public boolean createHistoricalVariable(@WebParam(name="processID") String processID, @WebParam(name="userID") String userID, @WebParam(name="instanceID") Long instanceID, @WebParam(name="location") String location, @WebParam(name="assertionType") int assertionType,@WebParam(name="aliasName") String aliasName, @WebParam(name="value")String value) throws CreateHistoricalVariableException {
		System.out.println("Create HistoricalVariable");
		
		if (processID == null && aliasName == null && location == null && value == null) 
			throw new CreateHistoricalVariableException("A few parameter specified for create Historical Variable \n" +
					"you must provide a value for  aliasname, assertion type, \n" +
					"process id, location and value.");
		
		HistoricalVariable hvar=new HistoricalVariable();
		
		System.out.println("Parameters:");
		System.out.println("processID: "+processID);
		System.out.println("userID: "+userID);
		System.out.println("instanceID: "+instanceID);
		System.out.println("location: "+location);
		System.out.println("assertionType: "+assertionType);
		System.out.println("aliasName: "+aliasName);
		System.out.println("value: "+value);
		
		System.out.println("Set parameters");
		
		hvar.setAliasName(aliasName);
		hvar.setProcessID(processID);
		hvar.setAssertionType(assertionType);
		hvar.setLocation(location);
		hvar.setInstanceID(instanceID);
		hvar.setUserID(userID);
		hvar.setValue(value);
		
		hvar.setTimeValue((Date) new GregorianCalendar().getTime());
		
		System.out.println("Save hvar");
		
		
		this.manager.persist(hvar);
		
		System.out.println("Hvar saved");
		return true;
	}

	@WebMethod
	public HistoricalVariableInfo[] getAllHistoricalVariables(){
		System.out.println("Get all variable");
		Vector<HistoricalVariableInfo> result=new Vector<HistoricalVariableInfo>();
		Query q = manager.createQuery("from HistoricalVariable");
		System.out.println(q);
		List l = q.getResultList();
		System.out.println("numero risultati query "+l.size());
		for (Object object : l) {
			HistoricalVariable h = (HistoricalVariable)object;
			result.add(h.historicalVariableInfo());
			System.out.println(h.toString());
		}
		System.out.println("restituisco risultati");
		return result.toArray(new HistoricalVariableInfo[result.size()]);
	}
	
	
}
