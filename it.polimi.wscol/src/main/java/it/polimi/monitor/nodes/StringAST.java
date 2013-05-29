/*
 Copyright 2007 Politecnico di Milano
 Copyright 2013 Antonio García-Domínguez (UCA)
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

package it.polimi.monitor.nodes;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

import java.util.logging.Logger;

import antlr.Token;

public class StringAST extends NodeWSCoL {
	private static final Logger LOGGER = Logger.getLogger(StringAST.class.getCanonicalName());

	private static final long serialVersionUID = 1516605011013830582L;
	private String value;

	public StringAST(Token tok) {
		this.value = tok.getText();
		serializeTag = calculateSerialize();
		LOGGER.info("Set value " + value);
	}

	public void evaluate(InputMonitor inputMonitor, Aliases aliases, AliasNodes tempAliases) throws WSCoLException {
		LOGGER.info("Evaluate " + value);
	}

	@Override
	public String getMonitoringValue() throws WSCoLException {
		return value;
	}

	/**
	 * Get value of the StringAST
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set value of the StringAST
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	private String calculateSerialize() {
		if (value.equals("false") || value.equals("true"))
			return "boolean";
		else
			try {
				Double.parseDouble(value);
				return "number";
			} catch (NumberFormatException e) {
				return "string";
			}
	}

}
