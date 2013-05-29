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
import antlr.Token;

public class OperatorNode extends BinaryNode {
	private static final Logger LOGGER = Logger.getLogger(OperatorNode.class.getCanonicalName());

	private final String DOUBLE_EQUALS ="==";	
	private final String NOT_EQUALS ="!=";
	private int operator;
	private String op;
	
	private final int DOUBLE_EQUALS_OPERATOR=0;
	private final int NOT_EQUALS_OPERATOR=1;
	
	private static final long serialVersionUID = 3560944800413547320L;


	/**
	 * 
	 */
	public OperatorNode(Token tok) {
		op=tok.getText();
		operator=operatorType(op);
		switch (operator) {
			case DOUBLE_EQUALS_OPERATOR:
				serializeTag="double_equals";
				break;
			case NOT_EQUALS_OPERATOR:
				serializeTag="not_equals";
				break;
		}
	
	}


	@Override
	public Object getMonitoringValue() throws WSCoLException {
		Boolean res;
		LOGGER.info("Start getMonitoringValue "+serializeTag);
		switch(operator){
			case DOUBLE_EQUALS_OPERATOR:
				res= doubleEqualsEvaluate();
				break;
			case NOT_EQUALS_OPERATOR:
				res= notEqualsEvaluate();
				break;
			default:
				LOGGER.severe("WSCoLException");
				throw new WSCoLException("Can't define logical operation");
		}
		LOGGER.info("Finish getMonitoringValue "+serializeTag+" result: "+res);
		return res;
	}
	
	private Boolean doubleEqualsEvaluate() throws WSCoLException {
		switch(valueType){
			case BOOLEAN:
				if (bLeft.equals(bRight)) 
					return new Boolean(true);
				 else 
					 return new Boolean(false);
				
			case NUMBER:
				if (dLeft.equals(dRight)) 
					return new Boolean(true);
				 else 
					return new Boolean(false);
				
			case STRING:
				if (sLeft.equals(sRight)) 
					return new Boolean(true);
				 else 
					 return new Boolean(false);
			default:
				LOGGER.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define instace of terms");
		}
	}
	
	private Boolean notEqualsEvaluate() throws WSCoLException {
		switch(valueType){
			case BOOLEAN:
				if (! bLeft.equals(bRight)) 
					return new Boolean(true);
				else 
					return new Boolean(false);
				
			case NUMBER:
				if (! dLeft.equals(dRight)) 
					return new Boolean(true);
				else 
					 return new Boolean(false);
			case STRING:
				if (! sLeft.equals(sRight)) 
					return new Boolean(true);
				else 
					return new Boolean(false);
			default:
				LOGGER.severe("WSCoLCastExceprion");
				throw new WSCoLCastException("Can't define instace of terms");
		}
	}
	
	private int operatorType(String s){
		if (s.equals(DOUBLE_EQUALS))
			return DOUBLE_EQUALS_OPERATOR;
		else if (s.equals(NOT_EQUALS))
			return NOT_EQUALS_OPERATOR;
		else 
			return -1; 
	}
	@Override
	public String toString(){
		return op;
	}
}
