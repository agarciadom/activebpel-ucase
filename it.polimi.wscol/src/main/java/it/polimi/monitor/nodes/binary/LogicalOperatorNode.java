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

import it.polimi.exception.WSCoLException;
import it.polimi.exception.WSCoLCastException;
import antlr.Token;

/**
 * 
 * @author Luca Galluppi
 * 
 */
public class LogicalOperatorNode extends BinaryNode {
	private static final Logger LOGGER = Logger.getLogger(LogicalOperatorNode.class.getCanonicalName());

	private final String AND = "&&";
	private final String OR = "||";
	private int operator;
	private String op;
	private final int AND_OPERATOR = 0;
	private final int OR_OPERATOR = 1;
	private static final long serialVersionUID = 8548551642234774770L;

	public LogicalOperatorNode(Token tok) {
		op = tok.getText();
		operator = operatorType(op);
		switch (operator) {
		case AND_OPERATOR:
			serializeTag = "and";
			break;
		case OR_OPERATOR:
			serializeTag = "or";
			break;
		}
	}

	private Boolean andEvaluate() throws WSCoLCastException {
		switch (valueType) {
		case BOOLEAN:
			if (bLeft.booleanValue() && bRight.booleanValue())
				return new Boolean(true);
			else
				return new Boolean(false);
		case NUMBER:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException(
					"Can't define AND operation beetwen number");
		case STRING:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException(
					"Can't define AND operation beetwen string");
		default:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException("Can't define instace of terms");
		}
	}

	private Boolean orEvaluate() throws WSCoLCastException {
		switch (valueType) {
		case BOOLEAN:
			if (bLeft.booleanValue() || bRight.booleanValue())
				return new Boolean(true);
			else
				return new Boolean(false);
		case NUMBER:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException(
					"Can't define OR operation beetwen number");
		case STRING:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException(
					"Can't define OR operation beetwen string");
		default:
			LOGGER.severe("WSCoLCastExceprion");
			throw new WSCoLCastException("Can't define instace of terms");
		}
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		Boolean res;
		LOGGER.info("Start getMonitoringValue " + serializeTag);
		switch (operator) {
		case AND_OPERATOR:
			res = andEvaluate();
			break;
		case OR_OPERATOR:
			res = orEvaluate();
			break;
		default:
			LOGGER.severe("WSCoLException");
			throw new WSCoLException("Can't define logical operation");
		}
		LOGGER.info("Finish getMonitoringValue " + serializeTag + " result: "
				+ res);
		return res;

	}

	private int operatorType(String s) {
		if (s.equals(AND))
			return AND_OPERATOR;
		else if (s.equals(OR))
			return OR_OPERATOR;
		else
			return -1;
	}

	@Override
	public String toString() {
		return op;
	}
}
