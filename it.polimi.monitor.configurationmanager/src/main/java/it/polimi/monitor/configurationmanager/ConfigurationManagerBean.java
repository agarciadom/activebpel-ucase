
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
 

package it.polimi.monitor.configurationmanager;

import it.polimi.monitor.configurationmanager.data.SupervisionRuleInfoWrapper;
import it.polimi.monitor.configurationmanager.data.ProcessInfoWrapper;
import it.polimi.monitor.configurationmanager.data.TemporaryRuleChangingInfoWrapper;
import it.polimi.monitor.configurationmanager.persistencedata.SupervisionRule;
import it.polimi.monitor.configurationmanager.persistencedata.SupervisionRulePK;
import it.polimi.monitor.configurationmanager.persistencedata.ProcessData;
import it.polimi.monitor.configurationmanager.persistencedata.ProcessDataPK;
import it.polimi.monitor.configurationmanager.persistencedata.TemporaryProcessDataChanging;
import it.polimi.monitor.configurationmanager.persistencedata.TemporaryProcessDataChangingPK;
import it.polimi.monitor.configurationmanager.persistencedata.TemporarySupervisionRuleChange;
import it.polimi.monitor.configurationmanager.persistencedata.TemporarySupervisionRuleChangePK;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@WebService(serviceName="ConfigurationManagerWS", targetNamespace="http://it.polimi.monitor/configurationmanager", name="ConfigurationManager")
@SOAPBinding(style=SOAPBinding.Style.RPC)
@Stateless
public class ConfigurationManagerBean implements ConfigurationManager
{
	@PersistenceContext(unitName="configuration_manager")
	private EntityManager entityManager;
	
	@WebMethod
	public boolean insertNewProcess(@WebParam(name="initProcessInfo") ProcessInfoWrapper initProcessInfo)
	{
		ProcessData pd;
		
		try
		{
			if(entityManager!=null)
			{
				ProcessDataPK pk = new ProcessDataPK(initProcessInfo.getProcessId(), initProcessInfo.getUserId());
				pd = (ProcessData) this.entityManager.find(ProcessData.class, pk);
				
				if(pd != null)
				{
					System.out.println("Process already registered!");
					return false;
				}
				
				pd = new ProcessData(initProcessInfo.getProcessId(), 
												initProcessInfo.getUserId(), 
												initProcessInfo.getPriority());
				
				this.entityManager.persist(pd);
				System.out.println("Add new Process -> "+pd);
				return true;
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@WebMethod
	public boolean insertNewSupervisionRule(@WebParam(name="initInvokeInfo") SupervisionRuleInfoWrapper initInvokeInfo)
	{
		ProcessDataPK pk;
		SupervisionRule ad;
		ProcessData pd;
		
		try
		{
			if(entityManager!=null)
			{
				pk = new ProcessDataPK(initInvokeInfo.getProcessID(), initInvokeInfo.getUserID());
				pd = (ProcessData) this.entityManager.find(ProcessData.class, pk);
				
				if(pd == null)
				{
					System.out.println("Process unkown!"); 
					return false;
				}

				SupervisionRulePK apk = new SupervisionRulePK(pk.getProcessID(), pk.getUserID(), initInvokeInfo.getLocation(), initInvokeInfo.isPrecondition());
				ad = (SupervisionRule) this.entityManager.find(SupervisionRule.class, apk);

				if(ad != null)
				{
					System.out.println("Assertion for process ["+ initInvokeInfo.getProcessID() +"] and user [" + initInvokeInfo.getUserID() + "] already registered!");
					return false;
				}

				ad = new SupervisionRule(apk, 
										 initInvokeInfo.getWscolRule(), 
										 initInvokeInfo.getPriority(),
										 initInvokeInfo.getRecoveryStrategy(),
										 initInvokeInfo.getTimeFrame(),
										 initInvokeInfo.getProviders());
				
				this.entityManager.persist(ad);
				System.out.println("Add new supervisionRule -> "+ad);
				return true;
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@WebMethod
	public boolean releaseProcess(@WebParam(name="processInfo") ProcessInfoWrapper processInfo)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@WebMethod
	public Integer getProcessPriority(@WebParam(name="processInfo") ProcessInfoWrapper processInfo)
	{
		ProcessData pd = null;
		
		try
		{
			if(entityManager!=null)
			{
				ProcessDataPK pk = new ProcessDataPK(processInfo.getProcessId(), processInfo.getUserId());
				pd = (ProcessData) this.entityManager.find(ProcessData.class, pk);
				
				if(pd == null)
				{
					System.out.println("No data recorded for process '" + processInfo.getProcessId() + "' and user '" + processInfo.getUserId() + "'"); 
					return null;
				}

				return pd.GetProcessPriority();
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@WebMethod
	public SupervisionRuleInfoWrapper getSupervisionRule(@WebParam(name="processID") String processID, 
															@WebParam(name="userID") String userID, 
															@WebParam(name="location") String location,
															@WebParam(name="isPrecondition") boolean isPrecondition)
	{
		SupervisionRule ad = null;
		
		try
		{
			if(entityManager!=null)
			{
				SupervisionRulePK pk = new SupervisionRulePK(processID, userID, location, isPrecondition);
				ad = (SupervisionRule) this.entityManager.find(SupervisionRule.class, pk);
	
				if(ad!=null)
				{
					SupervisionRuleInfoWrapper response = new SupervisionRuleInfoWrapper();
					
					response.setProcessID(processID);
					response.setUserID(userID);
					response.setLocation(location);
					response.setPrecondition(isPrecondition);
					response.setWscolRule(ad.getWscolRule());
					response.setPriority(ad.getPriority());
					response.setRecoveryStrategy(ad.getRecoveryStrategy());
					response.setTimeFrame(ad.getTimeFrame());
					response.setProviders(ad.getProviders());
					
					return response;
				}
				else
				{
					System.out.println("No assertions recorded for: " + location); 
				}
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@WebMethod
	public ProcessInfoWrapper[] getMonitoredProcesses()
	{
		ProcessInfoWrapper[] result = null;
		ProcessData p = null;
		ProcessInfoWrapper subResult = null;
		int i = 0;
		
		try
		{
			if(entityManager!=null)
			{
				Collection <ProcessData> pd = this.entityManager.createQuery("from ProcessData p").getResultList();
				
				if(pd != null)
				{
					Iterator <ProcessData> iterator = pd.iterator();
					
					result = new ProcessInfoWrapper[pd.size()];
					
					while(iterator.hasNext())
					{
						p = iterator.next();
						
						subResult = new ProcessInfoWrapper();
						
						subResult.setProcessId(p.GetProcessID());
						subResult.setUserId(p.GetUserID());
						subResult.setPriority(p.GetProcessPriority());
						
						result[i++] = subResult;
					}
					
					return result;
				}
				else
				{
					System.out.println("No process registered in database!"); 
				}
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return result;
	}

	@WebMethod
	public ProcessInfoWrapper getProcessInfo(@WebParam(name="processID") String processID, 
												@WebParam(name="userID") String userID)
	{
		ProcessInfoWrapper result = null;

		System.out.println("Process ID : " + processID);
		System.out.println("userID : " + userID);
		
		try
		{
			if(entityManager!=null)
			{
				ProcessDataPK pk = new ProcessDataPK(processID, userID);
				
				ProcessData pd = (ProcessData) this.entityManager.find(ProcessData.class, pk );
				
				if(pd != null)
				{
					result = new ProcessInfoWrapper();
						
					result.setProcessId(pd.GetProcessID());
					result.setUserId(pd.GetUserID());
					result.setPriority(pd.GetProcessPriority());
					
					return result;
				}
				else
				{
					System.out.println("No process registered in database!"); 
				}
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	@WebMethod
	public SupervisionRuleInfoWrapper[] getProcessSupervisionRules(@WebParam(name="processID") String processID, 
																	@WebParam(name="userID") String userID)
	{
		List<SupervisionRule> set = this.entityManager.createQuery("from SupervisionRule a where a.pk.processID=:pid and a.pk.userID=:uid")
															.setParameter("pid", processID)
															.setParameter("uid", userID)
															.getResultList();
		
		if(set != null)
		{
			SupervisionRuleInfoWrapper[] result = new SupervisionRuleInfoWrapper[set.size()];
			
			SupervisionRuleInfoWrapper subResult;
			
			SupervisionRule supervisionRule;
			
			for(int i = 0; i < set.size(); i++)
			{
				supervisionRule = set.get(i);
				
				subResult = new SupervisionRuleInfoWrapper();
				
				subResult.setProcessID(supervisionRule.getPk().getProcessID());
				subResult.setUserID(supervisionRule.getPk().getUserID());
				subResult.setLocation(supervisionRule.getPk().getLocation());
				subResult.setPrecondition(supervisionRule.getPk().isPrecondition());
				
				subResult.setWscolRule(supervisionRule.getWscolRule());
				subResult.setPriority(supervisionRule.getPriority());
				subResult.setRecoveryStrategy(supervisionRule.getRecoveryStrategy());
				subResult.setTimeFrame(supervisionRule.getTimeFrame());
				subResult.setProviders(supervisionRule.getProviders());
			}
			
			return result;
		}
		
		return null;
	}

	@WebMethod
	public boolean setNewProcessPriority(@WebParam(name="newProcessInfo") ProcessInfoWrapper newProcessInfo)
	{
		ProcessData pd = null;
		
		try
		{
			if(entityManager!=null)
			{
				ProcessDataPK pk = new ProcessDataPK(newProcessInfo.getProcessId(), newProcessInfo.getUserId());
				pd = (ProcessData) this.entityManager.find(ProcessData.class, pk);
				
				if(pd == null)
				{
					System.out.println("Process '" + newProcessInfo.getProcessId() + "' or User '" + newProcessInfo.getUserId() + "' unkown!"); 
					return false;
				}

				pd.SetProcessPriority(newProcessInfo.getPriority());
				
				return true;
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	@WebMethod
	public boolean changeSupervisionRuleParams(@WebParam(name="newInvokeInfo") SupervisionRuleInfoWrapper newInvokeInfo)
	{
		SupervisionRule ad = null;
		
		try
		{
			if(entityManager!=null)
			{
				SupervisionRulePK pk = new SupervisionRulePK(newInvokeInfo.getProcessID(), 
																newInvokeInfo.getUserID(), 
																newInvokeInfo.getLocation(), 
																newInvokeInfo.isPrecondition());
				ad = (SupervisionRule) this.entityManager.find(SupervisionRule.class, pk);
	
				if(ad!=null)
				{
					ad.setWscolRule(newInvokeInfo.getWscolRule());
					ad.setPriority(newInvokeInfo.getPriority());
					ad.setRecoveryStrategy(newInvokeInfo.getRecoveryStrategy());
					ad.setTimeFrame(newInvokeInfo.getTimeFrame());
					ad.setProviders(newInvokeInfo.getProviders());
					
					return true;
				}
				else
				{
					System.out.println("Rules for Invoke '" + newInvokeInfo.getLocation() + "' for process '" + newInvokeInfo.getProcessID() + "' not existing!");
					return false;
				}
			}
			else
			{
				System.out.println("EntityManager -> " + entityManager);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	@WebMethod
	public boolean setTemporaryProcessDataChanging(@WebParam(name="processInfoWrapper") ProcessInfoWrapper processInfoWrapper)
	{
		TemporaryProcessDataChangingPK pk = new TemporaryProcessDataChangingPK(processInfoWrapper.getProcessId(), 
																				processInfoWrapper.getUserId(), 
																				processInfoWrapper.getProcessInstanceId());
		TemporaryProcessDataChanging result = this.entityManager.find(TemporaryProcessDataChanging.class, pk);
		
		if(result == null)
		{
			System.out.println("Inserting new temporary changes for process' info --> Process: " + processInfoWrapper.getProcessId() + 
																					" | User: " + processInfoWrapper.getUserId() + 
																					" | Process Instance: " + processInfoWrapper.getProcessInstanceId());
			TemporaryProcessDataChanging newProcessInfo = new TemporaryProcessDataChanging(pk, processInfoWrapper.getPriority());
			
			this.entityManager.persist(newProcessInfo);
			return true;
		}
		else
		{
			System.out.println("Updating new temporary changes for process' info --> Process: " + processInfoWrapper.getProcessId() + 
																					" | User: " + processInfoWrapper.getUserId() + 
																					" | Process Instance: " + processInfoWrapper.getProcessInstanceId());
			result.setNewPriority(processInfoWrapper.getPriority());
			return true;
		}
	}
	
	@WebMethod
	public boolean setTemporaryChangingRule(@WebParam(name="temporaryRuleChangingInfoWrapper") TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper)
	{
		TemporarySupervisionRuleChangePK pk = new TemporarySupervisionRuleChangePK(temporaryRuleChangingInfoWrapper.getProcessID(),
																	temporaryRuleChangingInfoWrapper.getUserID(),
																	temporaryRuleChangingInfoWrapper.getLocation(),
																	temporaryRuleChangingInfoWrapper.getProcessInstanceID(),
																	temporaryRuleChangingInfoWrapper.isPrecondition());
		
		TemporarySupervisionRuleChange result = this.entityManager.find(TemporarySupervisionRuleChange.class, pk);
		
		if(result == null)
		{
			System.out.println("Inserting new temporary changes for rule's info --> Process: " + temporaryRuleChangingInfoWrapper.getProcessID() + 
																			" | User: " + temporaryRuleChangingInfoWrapper.getUserID() + 
																			" | Process Instance: " + temporaryRuleChangingInfoWrapper.getProcessInstanceID() +
																			" | BPEL Action: " + temporaryRuleChangingInfoWrapper.getLocation() +
																			" | Precondition: " + temporaryRuleChangingInfoWrapper.isPrecondition());

			TemporarySupervisionRuleChange newRule = new TemporarySupervisionRuleChange(pk, 
																			temporaryRuleChangingInfoWrapper.getNewCondition(), 
																			temporaryRuleChangingInfoWrapper.getNewConditionPriority(), 
																			temporaryRuleChangingInfoWrapper.getNewConditionRecovery(),
																			temporaryRuleChangingInfoWrapper.getNewTimeFrame(),
																			temporaryRuleChangingInfoWrapper.getNewProviderList());
			
			this.entityManager.persist(newRule);
			return true;
		}
		else
		{
			System.out.println("Updating new temporary changes for rule's info --> Process: " + temporaryRuleChangingInfoWrapper.getProcessID() + 
					" | User: " + temporaryRuleChangingInfoWrapper.getUserID() + 
					" | Process Instance: " + temporaryRuleChangingInfoWrapper.getProcessInstanceID() +
					" | BPEL Action: " + temporaryRuleChangingInfoWrapper.getLocation() +
					" | Precondition: " + temporaryRuleChangingInfoWrapper.isPrecondition());
			
			if(temporaryRuleChangingInfoWrapper.getNewCondition() != null)
				result.setNewCondition(temporaryRuleChangingInfoWrapper.getNewCondition());
			if(temporaryRuleChangingInfoWrapper.getNewConditionRecovery() != null)
				result.setNewConditionRecovery(temporaryRuleChangingInfoWrapper.getNewConditionRecovery());
			if(temporaryRuleChangingInfoWrapper.getNewConditionPriority() != null)
				result.setNewConditionPriority(temporaryRuleChangingInfoWrapper.getNewConditionPriority());
			if(temporaryRuleChangingInfoWrapper.getNewTimeFrame() != null)
				result.setNewTimeFrame(temporaryRuleChangingInfoWrapper.getNewTimeFrame());
			if(temporaryRuleChangingInfoWrapper.getNewProviderList() != null)
				result.setNewProviderList(temporaryRuleChangingInfoWrapper.getNewProviderList());
			return true;
		}
	}
	
	@WebMethod
	public ProcessInfoWrapper getTemporaryProcessDataChanging(@WebParam(name="processInfoWrapper") ProcessInfoWrapper processInfoWrapper)
	{
		TemporaryProcessDataChangingPK pk = new TemporaryProcessDataChangingPK(processInfoWrapper.getProcessId(), 
				processInfoWrapper.getUserId(), 
				processInfoWrapper.getProcessInstanceId());
		
		TemporaryProcessDataChanging queryResult = this.entityManager.find(TemporaryProcessDataChanging.class, pk);

		if(queryResult == null)
		{
			System.out.println("No temporary changes for process' info --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId());

			return null;
		}
		else
		{
			System.out.println("Found temporary changes for process --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId());

			processInfoWrapper.setPriority(queryResult.getNewPriority());
			
			return processInfoWrapper;
		}
	}
	
	@WebMethod
	public TemporaryRuleChangingInfoWrapper getTemporaryChangingRule(@WebParam(name="temporaryRuleChangingInfoWrapper") TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper)
	{
		TemporarySupervisionRuleChangePK pk = new TemporarySupervisionRuleChangePK(temporaryRuleChangingInfoWrapper.getProcessID(),
																	temporaryRuleChangingInfoWrapper.getUserID(),
																	temporaryRuleChangingInfoWrapper.getLocation(),
																	temporaryRuleChangingInfoWrapper.getProcessInstanceID(),
																	temporaryRuleChangingInfoWrapper.isPrecondition());
		
		TemporarySupervisionRuleChange queryResult = this.entityManager.find(TemporarySupervisionRuleChange.class, pk);
		
		if(queryResult == null)
		{
			System.out.println("No temporary changes for rule --> Process: " + temporaryRuleChangingInfoWrapper.getProcessID() + 
																" | User: " + temporaryRuleChangingInfoWrapper.getUserID() + 
																" | Process Instance: " + temporaryRuleChangingInfoWrapper.getLocation() +
																" | BPEL Action: " + temporaryRuleChangingInfoWrapper.getProcessInstanceID() +
																" | Precondition: " + temporaryRuleChangingInfoWrapper.isPrecondition());

			return null;
		}
		else
		{
			System.out.println("Found temporary changes for rule --> Process: " + temporaryRuleChangingInfoWrapper.getProcessID() + 
																	" | User: " + temporaryRuleChangingInfoWrapper.getUserID() + 
																	" | Process Instance: " + temporaryRuleChangingInfoWrapper.getLocation() +
																	" | BPEL Action: " + temporaryRuleChangingInfoWrapper.getProcessInstanceID() +
																	" | Precondition: " + temporaryRuleChangingInfoWrapper.isPrecondition());
			
			temporaryRuleChangingInfoWrapper.setNewCondition(queryResult.getNewCondition());
			temporaryRuleChangingInfoWrapper.setNewConditionRecovery(queryResult.getNewConditionRecovery());
			temporaryRuleChangingInfoWrapper.setNewConditionPriority(queryResult.getNewConditionPriority());
			temporaryRuleChangingInfoWrapper.setNewTimeFrame(queryResult.getNewTimeFrame());
			temporaryRuleChangingInfoWrapper.setNewProviderList(queryResult.getNewProviderList());

			return temporaryRuleChangingInfoWrapper;
		}
	}
	
	@WebMethod
	public void realeseTemporaryProcessChanges(@WebParam(name="processInfoWrapper") ProcessInfoWrapper processInfoWrapper)
	{
		TemporaryProcessDataChangingPK pk = new TemporaryProcessDataChangingPK(processInfoWrapper.getProcessId(), 
				processInfoWrapper.getUserId(), 
				processInfoWrapper.getProcessInstanceId());
		
		TemporaryProcessDataChanging queryResult = this.entityManager.find(TemporaryProcessDataChanging.class, pk);

		if(queryResult == null)
		{
			System.out.println("No temporary changes for process' info --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId());
		}
		else
		{
			this.entityManager.remove(queryResult);

			System.out.println("Succesfully deleted temporary changes for process' info --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId());
		}
		
		List<TemporarySupervisionRuleChange> queryResult2 = this.entityManager.createQuery("from TemporarySupervisionRuleChange t where t.pk.processID=:pid and t.pk.userID=:uid and t.pk.processInstanceID=:iid")
																			.setParameter("pid", processInfoWrapper.getProcessId())
																			.setParameter("uid", processInfoWrapper.getUserId())
																			.setParameter("iid", processInfoWrapper.getProcessInstanceId())
																			.getResultList();
		
		if(queryResult2 == null)
		{
			System.out.println("No temporary changes for process' rules --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId());
		}
		else
		{
			for(int i = 0; i < queryResult2.size(); i++)
			{
				this.entityManager.remove(queryResult2.get(i));

				System.out.println("Succesfully deleted temporary changes for process' rule --> Process: " + processInfoWrapper.getProcessId() + 
																		" | User: " + processInfoWrapper.getUserId() + 
																		" | Process Instance: " + processInfoWrapper.getProcessInstanceId() +
																		" | location: " + queryResult2.get(i).getPk().getLocation() + 
																		" | isPrecondition: " + queryResult2.get(i).getPk().isPrecondition());
			}
		}
	}

//	public boolean eraseDatabase()
//	{
//		// TODO Auto-generated method stub
//		
//		ProcessInfoWrapper[] processes = null;
//
//		processes = getMonitoredProcess();
//		
//		for (int i = 0; i < processes.length; i++) {
//			
//			ProcessInfoWrapper pInfo = processes[i];
//			String processID = pInfo.getProcessId(); 
//			String userID = pInfo.getUserId();
//			
//			InvokeInfoWrapper[] invokes = getProcessAssertion(processID, userID);
//			
//			for (int j= 0; j< invokes.length; j++) {
//				
//				entityManager.remove(invokes[j]);
//				
//			}
//			
//			entityManager.remove(processes[i]);
//			
//		}
//		
//		
//		return false;
//	}
}