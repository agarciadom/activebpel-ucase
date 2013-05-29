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

package it.polimi.monitor.nodes;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

/**
 * This class is use for create a alias. A alias can be costruct from a variable
 * or from a Object value. If the alias is a variable it will have an xpath and
 * data like an internal variable.
 * 
 * @author Luca Galluppi
 */
public class Alias extends NodeWSCoL {
	private static final long serialVersionUID = -8220542313481056659L;

	private String identifier = null;
	private Object value = null;
	private int aliasType = 0;

	/**
	 * Constant for define a Alias of a Variable
	 */
	public static final int ALIAS_VAR = 1;

	/**
	 * Constant for define a Alias of a Object
	 */
	public static final int ALIAS_COSTANT = 0;

	/**
	 * Construct a new Alias from a Object value with an identifier, the value
	 * and the serialize tag that is evaluated based on the origin of value.
	 * 
	 * @param identifier
	 *            the alias id.
	 * @param value
	 *            the value of the alias.
	 * @param serializeTag
	 *            serialiaze tag based on the origin of value.
	 */
	public Alias(String identifier, Object value, String serializeTag) {
		aliasType = ALIAS_COSTANT;
		this.identifier = identifier;
		this.value = value;
		this.serializeTag = serializeTag;
	}

	/**
	 * Construct a new Alias from a {@link Variable} with an identifier, the
	 * variable saved in a {@link AliasInfo} and the serialize tag that is
	 * evaluated based on {@link AliasInfo}.
	 * 
	 * @param identifier
	 *            the alias id.
	 * @param value
	 *            Information of the variable for the alias.
	 * @param serializeTag
	 *            serialiaze tag based on the variable.
	 */
	public Alias(String identifier, AliasInfo value, String serializeTag) {
		aliasType = ALIAS_VAR;
		this.identifier = identifier;
		this.value = value;
		this.serializeTag = serializeTag;
	}

	/**
	 * Get the identifier of the alias.
	 * 
	 * @return the identifier of the alias.
	 */
	protected String getIdentifier() {
		return identifier;
	}

	/**
	 * Set the identifier of the alias.
	 * 
	 * @param identifier
	 *            the identifier to set
	 */
	protected void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the value
	 */
	protected Object getValue() {
		return value;
	}

	// Nothing to be done.
	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes tempAliases) throws WSCoLException {

	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		return value;
	}

	/**
	 * Return the type of the Alias: costant or variable.
	 * 
	 * @return the aliasType
	 */
	public int getAliasType() {
		return aliasType;
	}

	/**
	 * Restrict the xpath of the alias.
	 * 
	 * @param xpath
	 *            restricted xpath.
	 */
	public void setExtraPath(String xpath) {
		if (value instanceof AliasInfo)
			((AliasInfo) value).setExtraPath(xpath);
	}

}
