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

package it.polimi.monitor.nodes.complex;

import it.polimi.exception.WSCoLException;

public class MaxNode extends ComplexMathematicalNode {

	private static final long serialVersionUID = -1986024938174339410L;

	/**
	 * 
	 */
	public MaxNode() {
		serializeTag="max";
	}
	@Override
	public Object getMonitoringValue() throws WSCoLException {
		logger.info("Start getMonitoringValue "+serializeTag);
		double res= numbers[numbers.length-1];
		logger.info("Finish getMonitoringValue "+serializeTag+" result: "+res);
		return new Double(res);
	}
	public String toString(){
		return "Max";
	}
}
