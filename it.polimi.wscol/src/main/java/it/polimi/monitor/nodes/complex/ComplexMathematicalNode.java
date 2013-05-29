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

import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.nodes.AliasNode;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.monitor.nodes.NodeWSCoL;
import it.polimi.monitor.nodes.binary.BinaryNode;

import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Luca Galluppi
 * 
 */
public abstract class ComplexMathematicalNode extends NodeWSCoL {
	private static final long serialVersionUID = 8186306156464203358L;
	private static final Logger LOGGER = Logger.getLogger(ComplexMathematicalNode.class.getCanonicalName());
	
	protected AliasNode aliasNode = null;
	protected NodeWSCoL condition = null;
	protected Double[] numbers = null;

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes tempAliases) throws WSCoLException {
		LOGGER.info("Start evaluate " + serializeTag);
		this.inputMonitor = inputMonitor;
		Vector<Object> results = new Vector<Object>();
		aliasNode = (AliasNode) this.getFirstChild();
		aliasNode.setTypeOfExtraction(AliasNode.EXTRACTALLTOGETHER);
		aliasNode.evaluate(inputMonitor, aliases, tempAliases);
		condition = (NodeWSCoL) aliasNode.getNextSibling();
		for (int i = 0; i < aliasNode.getNumberOfChildren(); i++) {
			condition.evaluate(inputMonitor, aliases, tempAliases);
			Object res = condition.getMonitoringValue();
			if ((condition instanceof BinaryNode) && (res instanceof Boolean)
					&& ((Boolean) res).booleanValue() == true) {
				Object temp = ((BinaryNode) condition)
						.getValueComplexOp(aliasNode.getIdentifier());
				if (temp != null)
					results.add(temp);
				else
					throw new WSCoLException("Error null value");
			} else if (res instanceof Boolean
					&& ((Boolean) res).booleanValue() == false) {
				// non aggiungo
			} else {
				results.add(res);
			}
			aliasNode.nextChild();
		}
		tempAliases.removeAliasNode(aliasNode.getIdentifier());
		// determino i numeri
		setNumbers(results);
		LOGGER.info("Finish evaluate " + serializeTag);
	}

	protected void setNumbers(Vector<Object> term) throws WSCoLCastException {
		// determino se gli oggetti sono numeri
		Vector<Double> num = new Vector<Double>();
		for (Object i : term) {
			if (i instanceof Boolean) {
				LOGGER.severe("Can't use complex mathematical expression with "
						+ i.getClass());
				throw new WSCoLCastException(
						"Can't use complex mathematical expression with booleans");
			} else if (i instanceof String) {
				try {
					num.add(Double.parseDouble((String) i));
				} catch (NumberFormatException e) {
					LOGGER.severe("Can't use complex mathematical expression with "
							+ i.getClass());
					throw new WSCoLCastException(
							"Can't use complex mathematical expression with strings");
				}
			} else if (i instanceof Double) {
				num.add((Double) i);
			} else {
				LOGGER.severe("Can't use complex mathematical expression with "
						+ i.getClass());
				throw new WSCoLCastException(
						"Can use complex mathematical expression only with numbers");
			}
		}
		if (num.size() != term.size()) {
			LOGGER.severe("Can use complex mathematical expression only with numbers");
			throw new WSCoLCastException(
					"Can use complex mathematical expression only with numbers");
		}
		numbers = new Double[num.size()];
		num.toArray(numbers);
		Arrays.sort(numbers);
	}
}
