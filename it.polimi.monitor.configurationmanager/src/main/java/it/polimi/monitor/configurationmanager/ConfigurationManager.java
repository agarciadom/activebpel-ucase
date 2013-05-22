
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

import it.polimi.monitor.configurationmanager.data.ProcessInfoWrapper;
import it.polimi.monitor.configurationmanager.data.SupervisionRuleInfoWrapper;
import it.polimi.monitor.configurationmanager.data.TemporaryRuleChangingInfoWrapper;

public interface ConfigurationManager
{
	/**
	 * Inserts a process instance id into DynamoDatabase
	 * @param initProcessInfo
	 * @return true if operation finishes successfully, false otherwise
	 * @throws ConfigurationManagerException
	 */
	boolean insertNewProcess(ProcessInfoWrapper initProcessInfo);

	/**
	 * Inserts a service invocation data into DynamoDatabase
	 * @param initInvokeInfo
	 * @return true if operation finishes successfully, false otherwise
	 * @throws ConfigurationManagerException
	 */
	boolean insertNewSupervisionRule(SupervisionRuleInfoWrapper initInvokeInfo);

	/**
	 * Removes a process from the DB
	 * @param processID
	 * @param userID
	 * @return
	 * @throws ConfigurationManagerException
	 */
	boolean releaseProcess(ProcessInfoWrapper processInfo);

	/**
	 * Returns process priority by process-user ID
	 * @param processID
	 * @param userID
	 * @return
	 * @throws ConfigurationManagerException
	 */
	Integer getProcessPriority(ProcessInfoWrapper processInfo);

	/**
	 * Returns condition (pre or post) priority by process-user-invoke-pre/post ID
	 * @param processID
	 * @param userID
	 * @param invokeID
	 * @return
	 * @throws ConfigurationManagerException
	 */	
	SupervisionRuleInfoWrapper getSupervisionRule(String processID, String userID, String location, boolean isPrecondition);

	/**
	 * Removes condition by process-user-invoke-pre/post ID
	 */
	boolean releaseSupervisionRule(String processID, String userID, String location, boolean isPrecondition);

	/**
	 * Retrieve a list of monitored processes
	 * @return A String that contains all monitored process separated by character "|".
	 * @throws ConfigurationManagerException 
	 * 
	 */
	ProcessInfoWrapper[] getMonitoredProcesses();
	
	/**
	 * Retrieve the list of invoke assertions of the given process-user couple ID
	 * @param processID
	 * @param userID
	 * @return
	 */
	SupervisionRuleInfoWrapper[] getProcessSupervisionRules(String processID, String userID);

	/**
	 * Modify process priority
	 * @param newProcessInfo
	 * @return 
	 * @throws ConfigurationManagerException 
	 */
	boolean setNewProcessPriority(ProcessInfoWrapper newProcessInfo);

	/**
	 * Modify invoke priority
	 * @param newInvokeInfo
	 * @return
	 * @throws ConfigurationManagerException 
	 */
	boolean changeSupervisionRuleParams(SupervisionRuleInfoWrapper newInvokeInfo);
	
	boolean setTemporaryProcessDataChanging(ProcessInfoWrapper processInfoWrapper);
	
	boolean setTemporaryChangingRule(TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper);
	
	ProcessInfoWrapper getTemporaryProcessDataChanging(ProcessInfoWrapper processInfoWrapper);
	
	TemporaryRuleChangingInfoWrapper getTemporaryChangingRule(TemporaryRuleChangingInfoWrapper temporaryRuleChangingInfoWrapper);
	
	void releaseTemporaryProcessChanges(ProcessInfoWrapper processInfoWrapper);
}
