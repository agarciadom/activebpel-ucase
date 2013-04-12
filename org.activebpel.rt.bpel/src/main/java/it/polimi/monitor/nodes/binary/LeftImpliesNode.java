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

package it.polimi.monitor.nodes.binary;

import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
/**
 * Class for analyze a left implication ("==>") of boolen logic between two terms. The analyze return false
 * the left term is true and the right is false. Otherwise return true.
 * 
 * The serialize tag of this node is leftImplies.
 * 
 * @author Luca Galluppi
 *
 */
public class LeftImpliesNode extends BinaryNode{

	private static final long serialVersionUID = -1467691068700035490L;

	/**
	 * 
	 *
	 */
	public LeftImpliesNode() {
		serializeTag="leftImplies";
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		Boolean res;
		logger.info("Start getMonitoringValue "+serializeTag);
		switch(instace){
			case BOOLEAN:
				if (bLeft.booleanValue() == true && bRight.booleanValue()== false) {
					res=new Boolean(false);
					break;
				} else {
					res= new Boolean(true);
					break;
				}
			case NUMBER:
				logger.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define LeftImplies operation beetwen number");
			case STRING:
				logger.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define LeftImplies operation beetwen string");
			default:
				logger.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define instace of terms");
		}
		logger.info("Finish getMonitoringValue "+serializeTag+" result: "+res);
		return res;
	}
	@Override
	public String toString(){
		return "==>";
	}
}
