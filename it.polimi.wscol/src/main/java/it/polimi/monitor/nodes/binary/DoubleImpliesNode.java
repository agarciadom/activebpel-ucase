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

import java.util.logging.Logger;

import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
/**
 * Class for analyze a doble implication ("<==>") of boolen logic between two terms. The analyze return true if 
 * the terms are both true or false. Otherwise return false.
 * 
 * The serialize tag of this node is doubleImplies.
 * 
 * @author Luca Galluppi
 *
 */
public class DoubleImpliesNode extends BinaryNode {
	private static final Logger LOGGER = Logger.getLogger(DoubleImpliesNode.class.getCanonicalName());
	private static final long serialVersionUID = 7945820925585599011L;
	
	public DoubleImpliesNode() {
		serializeTag="doubleImplies";
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		Boolean res;
		LOGGER.info("Start getMonitoringValue "+serializeTag);
		switch(valueType){
			case BOOLEAN:
				if (bLeft.booleanValue() == false && bRight.booleanValue()== false) {
					res= new Boolean(true);
					break;
				}else if (bLeft.booleanValue() == true && bRight.booleanValue()== true) { 
					res= new Boolean(true);
					break;
				} else {
					res= new Boolean(false);
					break;
				}
			case NUMBER:
				LOGGER.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define DoubleImplies operation beetwen number");
			case STRING:
				LOGGER.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define DoubleImplies operation beetwen string");
			default:
				LOGGER.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define instace of terms");
		}
		LOGGER.info("Finish getMonitoringValue "+serializeTag+" result: "+res);
		return res;
	}
	@Override
	public String toString(){
		return "<==>";
	}

}
