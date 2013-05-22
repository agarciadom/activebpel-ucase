
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
 

package it.polimi.monitor.monitorlogger;

import it.polimi.monitor.monitorlogger.data.MonitoringResultInfoWrapper;
import it.polimi.monitor.monitorlogger.data.RecoveryResultInfoWrapper;
import it.polimi.monitor.monitorlogger.persistencedata.MonitoringResult;
import it.polimi.monitor.monitorlogger.persistencedata.MonitoringResultPK;
import it.polimi.monitor.monitorlogger.persistencedata.RecoveryResult;
import it.polimi.monitor.monitorlogger.persistencedata.RecoveryResultPK;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@WebService(serviceName="MonitorLoggerWS", targetNamespace="http://it.polimi.monitor/monitorlogger", name="MonitorLogger")
@SOAPBinding(style=SOAPBinding.Style.RPC)
@Stateless
public class MonitorLoggerBean implements MonitorLogger
{
	private static final Logger LOGGER = Logger.getLogger(MonitorLoggerBean.class.getCanonicalName());

	@PersistenceContext(unitName="monitor_logger")
	private EntityManager entityManager;

	@WebMethod
	public boolean insertNewMonitoringResult(@WebParam(name="monitoringResultInfoWrapper") MonitoringResultInfoWrapper monitoringResultInfoWrapper)
	{
		if(this.entityManager != null)
		{
			if(monitoringResultInfoWrapper.getDate() == null)
			{
				monitoringResultInfoWrapper.setDate(new Date(System.currentTimeMillis()));
			}
			
			MonitoringResultPK pk = new MonitoringResultPK(monitoringResultInfoWrapper.getDate(),
															monitoringResultInfoWrapper.getProcessID(),
															monitoringResultInfoWrapper.getUserID(),
															monitoringResultInfoWrapper.getLocation(),
															monitoringResultInfoWrapper.isPrecondition());
			
			MonitoringResult monitoringResult = new MonitoringResult(pk,
																		monitoringResultInfoWrapper.getWscolRule(),
																		monitoringResultInfoWrapper.getWscolPriority(),
																		monitoringResultInfoWrapper.getProcessPriority(),
																		monitoringResultInfoWrapper.getTimeFrame(),
																		monitoringResultInfoWrapper.getProviders(),
																		monitoringResultInfoWrapper.isMonitoringResult(),
																		monitoringResultInfoWrapper.getMonitoringData(),
																		monitoringResultInfoWrapper.getMonitoringTime());
			
			this.entityManager.persist(monitoringResult);
			
			return true;
		}
		else
		{
			System.out.println("\n\nEntityManager is null!\n\n");
		}
		
		return false;
	}

	@WebMethod
	public MonitoringResultInfoWrapper[] getMonitoringResults(@WebParam(name="monitoringResultInfoWrapper") MonitoringResultInfoWrapper monitoringResultInfoWrapper)
	{
		MonitoringResultInfoWrapper[] results = null;
		                            
		if(this.entityManager != null)
		{
			List<MonitoringResult> resultSet = getMonitoringResultsList(monitoringResultInfoWrapper);
			
			if(resultSet.size() > 0)
			{
				results = new MonitoringResultInfoWrapper[resultSet.size()];
				
				for(int i = 0; i < resultSet.size(); i++)
				{
					MonitoringResult temp = resultSet.get(i);
					MonitoringResultInfoWrapper result = new MonitoringResultInfoWrapper();
					
					result.setDate(temp.getPk().getDate());
					result.setLocation(temp.getPk().getLocation());
					result.setMonitoringData(temp.getMonitoringData());
					result.setMonitoringResult(temp.isMonitoringResult());
					result.setPrecondition(temp.getPk().isPrecondition());
					result.setProcessID(temp.getPk().getProcessID());
					result.setProcessPriority(temp.getProcessPriority());
					result.setProviders(temp.getProviders());
					result.setTimeFrame(temp.getTimeFrame());
					result.setUserID(temp.getPk().getUserID());
					result.setWscolPriority(temp.getWscolPriority());
					result.setWscolRule(temp.getWscolRule());
					result.setMonitoringTime(temp.getMonitoringTime());
					
					results[i] = result;
				}
			}
			
			return results;
		}
		else
		{
			System.out.println("\n\nEntityManager is null!\n\n");
		}
		
		return results;
	}

	@WebMethod
	public boolean removeMonitoringResults(@WebParam(name="monitoringResultInfoWrapper") MonitoringResultInfoWrapper monitoringResultInfoWrapper) {
		if (entityManager == null) {
			LOGGER.severe("The entity manager is unavailable");
			return false;
		}
	
		for (MonitoringResult r : getMonitoringResultsList(monitoringResultInfoWrapper)) {
			entityManager.remove(r);
		}
		return true;
	}

	@WebMethod
	public boolean insertNewRecoveryResult(@WebParam(name="recoveryResultInfoWrapper") RecoveryResultInfoWrapper recoveryResultInfoWrapper)
	{
		if(this.entityManager != null)
		{
			if(recoveryResultInfoWrapper.getDate() == null)
			{
				recoveryResultInfoWrapper.setDate(new Date(System.currentTimeMillis()));
			}
			
			RecoveryResultPK pk = new RecoveryResultPK(recoveryResultInfoWrapper.getDate(),
															recoveryResultInfoWrapper.getProcessID(),
															recoveryResultInfoWrapper.getUserID(),
															recoveryResultInfoWrapper.getLocation(),
															recoveryResultInfoWrapper.isPrecondition());
			
			RecoveryResult recoveryResult = new RecoveryResult(pk,
																recoveryResultInfoWrapper.getCompleteRecoveryStrategy(),
																recoveryResultInfoWrapper.getExecutedRecoveryStrategy(),
																recoveryResultInfoWrapper.isSuccessful(),
																recoveryResultInfoWrapper.getRecoveryTime());
			
			this.entityManager.persist(recoveryResult);
			
			return true;
		}
		else
		{
			System.out.println("\n\nEntityManager is null!\n\n");
		}
		
		return false;
	}

	@WebMethod
	public RecoveryResultInfoWrapper[] getRecoveryResults(@WebParam(name="recoveryResultInfoWrapper") RecoveryResultInfoWrapper recoveryResultInfoWrapper)
	{
		RecoveryResultInfoWrapper[] results = null;
		                            
		if(this.entityManager != null)
		{
			List<RecoveryResult> resultSet = getRecoveryResultsList(recoveryResultInfoWrapper);
			
			if(resultSet.size() > 0)
			{
				results = new RecoveryResultInfoWrapper[resultSet.size()];
				
				for(int i = 0; i < resultSet.size(); i++)
				{
					RecoveryResult temp = resultSet.get(i);
					RecoveryResultInfoWrapper result = new RecoveryResultInfoWrapper();
					
					result.setDate(temp.getPk().getDate());
					result.setLocation(temp.getPk().getLocation());
					result.setPrecondition(temp.getPk().isPrecondition());
					result.setProcessID(temp.getPk().getProcessID());
					result.setUserID(temp.getPk().getUserID());
					result.setCompleteRecoveryStrategy(temp.getCompleteRecoveryStrategy());
					result.setExecutedRecoveryStrategy(temp.getExecutedRecoveryStrategy());
					result.setSuccessful(temp.isSuccessful());
					result.setRecoveryTime(temp.getRecoveryTime());
					
					results[i] = result;
				}
			}
			
			return results;
		}
		else
		{
			System.out.println("\n\nEntityManager is null!\n\n");
		}
		
		return results;
	}

	@WebMethod
	public boolean removeRecoveryResults(@WebParam(name="recoveryResultInfoWrapper") RecoveryResultInfoWrapper recoveryResultInfoWrapper) {
		if (entityManager == null) {
			LOGGER.severe("The entity manager is unavailable");
			return false;
		}

		for (RecoveryResult r : getRecoveryResultsList(recoveryResultInfoWrapper)) {
			entityManager.remove(r);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<MonitoringResult> getMonitoringResultsList(
			MonitoringResultInfoWrapper monitoringResultInfoWrapper) {
		return entityManager.createQuery("from MonitoringResult a where a.pk.processID=:pid and a.pk.userID=:uid and a.pk.location=:location and a.pk.isPrecondition=:precondition")
														.setParameter("pid", monitoringResultInfoWrapper.getProcessID())
														.setParameter("uid", monitoringResultInfoWrapper.getUserID())
														.setParameter("location", monitoringResultInfoWrapper.getLocation())
														.setParameter("precondition", monitoringResultInfoWrapper.isPrecondition())
														.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<RecoveryResult> getRecoveryResultsList(RecoveryResultInfoWrapper recoveryResultInfoWrapper) {
		return entityManager.createQuery("from RecoveryResult a where a.pk.processID=:pid and a.pk.userID=:uid and a.pk.location=:location and a.pk.isPrecondition=:precondition")
															.setParameter("pid", recoveryResultInfoWrapper.getProcessID())
															.setParameter("uid", recoveryResultInfoWrapper.getUserID())
															.setParameter("location", recoveryResultInfoWrapper.getLocation())
															.setParameter("precondition", recoveryResultInfoWrapper.isPrecondition())
															.getResultList();
	}
}
