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

/**
 * 
 */
package it.polimi.monitor.nodes.complex;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.nodes.AliasNode;
import it.polimi.monitor.nodes.NodeWSCoL;

/**
 * @author Luca Galluppi
 *
 */
public abstract class ComplexQuantificationNode extends NodeWSCoL {
	protected AliasNode aliasNode=null;
	protected NodeWSCoL condition=null;
	protected Boolean value;
		
	
	@Override
	public Boolean getMonitoringValue() throws WSCoLException {
		logger.info("Start getMonitoringValue "+serializeTag);
		logger.info("Finish getMonitoringValue "+serializeTag+" result: "+value);
		return value;
	}

}
