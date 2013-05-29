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

public class NumberOperatorNode extends BinaryNode {
	private static final Logger LOGGER = Logger.getLogger(NumberOperatorNode.class.getCanonicalName());
	private static final long serialVersionUID = -759269138524555803L;

	private final String PLUS = "+";
	private final String MINUS = "-";
	private final String STAR = "*";
	private final String SLASH = "/";
	private final String PERCENTAGE = "%";

	private String op;
	private int operator;

	private final int PLUS_OPERATOR = 0;
	private final int MINUS_OPERATOR = 1;
	private final int STAR_OPERATOR = 2;
	private final int SLASH_OPERATOR = 3;
	private final int PERCENTAGE_OPERATOR = 4;

	/**
         *
         */
	public NumberOperatorNode(Token tok) {
		op = tok.getText();
		operator = operatorType(op);
		switch (operator) {
		case PLUS_OPERATOR:
			serializeTag = "plus";
			break;
		case MINUS_OPERATOR:
			serializeTag = "minus";
			break;
		case STAR_OPERATOR:
			serializeTag = "star";
			break;
		case SLASH_OPERATOR:
			serializeTag = "slash";
			break;
		case PERCENTAGE_OPERATOR:
			serializeTag = "percentage";
			break;
		}
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		Object res;
		LOGGER.info("Start getMonitoringValue " + serializeTag);
		switch (operator) {
		case PLUS_OPERATOR:
			res = plusOperation();
			break;
		case MINUS_OPERATOR:
			res = minusOperation();
			break;
		case STAR_OPERATOR:
			res = starOperation();
			break;
		case SLASH_OPERATOR:
			res = slashOperation();
			break;
		case PERCENTAGE_OPERATOR:
			res = percentageOperation();
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
		if (s.equals(PLUS))
			return PLUS_OPERATOR;
		else if (s.equals(MINUS))
			return MINUS_OPERATOR;
		else if (s.equals(STAR))
			return STAR_OPERATOR;
		else if (s.equals(SLASH))
			return SLASH_OPERATOR;
		else if (s.equals(PERCENTAGE))
			return PERCENTAGE_OPERATOR;

		else
			return -1;
	}

	private Object plusOperation() throws WSCoLCastException {
		switch (valueType) {
		case NUMBER:
			return new Double(dLeft + dRight);
		case STRING:
			return new String(sLeft + sRight);
		case BOOLEAN: // non si può fare gt di booleani
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define plus operation beetwen boolean");
		default:
			// caso inverosimile
			return null;
		}

	}

	private Double minusOperation() throws WSCoLCastException {
		switch (valueType) {
		case NUMBER:
			return new Double(dLeft - dRight);
		case STRING:
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define substraction beetwen string");
		case BOOLEAN: // non si può fare gt di booleani
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define substraction beetwen boolean");
		default:
			// caso inverosimile
			return null;
		}
	}

	private Double starOperation() throws WSCoLCastException {
		switch (valueType) {
		case NUMBER:
			return new Double(dLeft * dRight);
		case STRING:
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define multiplication beetwen string");
		case BOOLEAN: // non si può fare gt di booleani
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define multiplication beetwen boolean");
		default:
			// caso inverosimile
			return null;
		}
	}

	private Double slashOperation() throws WSCoLCastException {
		switch (valueType) {
		case NUMBER:
			return new Double(dLeft / dRight);
		case STRING:
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException("Can't define division beetwen string");
		case BOOLEAN: // non si può fare gt di booleani
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define division beetwen boolean");
		default:
			// caso inverosimile
			return null;
		}
	}

	private Double percentageOperation() throws WSCoLCastException {
		switch (valueType) {
		case NUMBER:
			return new Double(dLeft % dRight);
		case STRING:
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define percentage beetwen string");
		case BOOLEAN: // non si può fare gt di booleani
			LOGGER.severe("WSCoLCastException");
			throw new WSCoLCastException(
					"Can't define percentage beetwen boolean");
		default:
			// caso inverosimile
			return null;
		}
	}

	@Override
	public String toString() {
		return op;
	}
}
