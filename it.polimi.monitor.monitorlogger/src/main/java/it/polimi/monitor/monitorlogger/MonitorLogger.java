
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

import javax.ejb.Remote;
import javax.jws.WebParam;

@Remote
public interface MonitorLogger {

	RecoveryResultInfoWrapper[] GetRecoveryResults(@WebParam(name="monitoringResultInfoWrapper") RecoveryResultInfoWrapper recoveryResultInfoWrapper);

	boolean InsertNewRecoveryResult(@WebParam(name="recoveryResultInfoWrapper") RecoveryResultInfoWrapper recoveryResultInfoWrapper);

	MonitoringResultInfoWrapper[] GetMonitoringResults(@WebParam(name="monitoringResultInfoWrapper") MonitoringResultInfoWrapper monitoringResultInfoWrapper);

	boolean InsertNewMonitoringResult(@WebParam(name="monitoringResultInfoWrapper") MonitoringResultInfoWrapper monitoringResultInfoWrapper);

}
