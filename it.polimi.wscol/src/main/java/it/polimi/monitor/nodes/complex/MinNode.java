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

import java.util.logging.Logger;

import it.polimi.exception.WSCoLException;

public class MinNode extends ComplexMathematicalNode {

	private static final Logger LOGGER = Logger.getLogger(MinNode.class.getCanonicalName());
	private static final long serialVersionUID = -3438938603468752436L;

	public MinNode() {
		serializeTag = "min";
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		LOGGER.fine("Start getMonitoringValue " + serializeTag);
		double res = numbers[0];
		LOGGER.fine("Finish getMonitoringValue " + serializeTag + " result: " + res);
		return new Double(res);
	}

	public String toString() {
		return "Min";
	}
}
