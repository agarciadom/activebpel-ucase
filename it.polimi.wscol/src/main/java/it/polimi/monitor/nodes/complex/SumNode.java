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

public class SumNode extends ComplexMathematicalNode {

	private static final Logger LOGGER = Logger.getLogger(SumNode.class.getCanonicalName());
	private static final long serialVersionUID = 3668233340017759784L;

	public SumNode() {
		serializeTag = "sum";
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		LOGGER.fine("Start getMonitoringValue " + serializeTag);

		double sum = 0;
		for (double i : numbers) {
			sum = sum + i;
		}

		LOGGER.fine("Finish getMonitoringValue " + serializeTag + " result: " + sum);
		return sum;
	}

	public String toString() {
		return "Sum";
	}
}
