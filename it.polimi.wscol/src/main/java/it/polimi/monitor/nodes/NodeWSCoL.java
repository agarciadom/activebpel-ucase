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
import it.polimi.monitor.ResultMonitor;

import java.util.logging.Logger;

import antlr.BaseAST;
import antlr.Token;
import antlr.collections.AST;

/**
 * This abstract class represents a NodeWSCoL that is use for analyze a rule
 * WSCoL define with WSCoL grammar. This node has two abstract methods that will
 * be use for evaluate and have the monitor value of a single node.
 * 
 * 
 * @author Luca Galluppi
 */
public abstract class NodeWSCoL extends BaseAST {
	private static final long serialVersionUID = -7610762948410522700L;

	protected InputMonitor inputMonitor = null;
	protected Logger logger = null;
	protected ResultMonitor resultMonitor = null;
	protected String serializeTag;

	/**
	 * Create a new NodeWSCoL and set the logger for have more information.
	 * 
	 */
	public NodeWSCoL() {
		logger = Logger.getLogger("Monitor WSCoL");
	}

	/**
	 * This method is use for costruct a NodeWSCoL with his child derived from
	 * the WSCoL grammar.
	 * 
	 * If some errors occur during the evaluation a WSCoLException is throw.
	 * 
	 * @param inputMonitor
	 *            The inputMonitor information for evaluate the Node
	 * @param aliases
	 *            The aliases known by the node
	 * @param tempAliases
	 *            The aliasnodes known by the node
	 * @throws WSCoLException
	 *             If an error occur a WSCoLException is throw.
	 */
	public abstract void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes aliasNodes) throws WSCoLException;

	/**
	 * This method is use for calculate the monitoring value of the node. If
	 * some errors occur during the calculation a WSCoLException is throw.
	 * 
	 * @return The value of monitoring of the node.
	 * @throws WSCoLException
	 *             If an error occur a WSCoLException is throw.
	 */
	public abstract Object getMonitoringValue() throws WSCoLException;

	// richiesto da baseast
	public void initialize(int t, String txt) {
	}

	// richiesto da baseast
	public void initialize(AST t) {
	}

	// richiesto da baseast
	public void initialize(Token tok) {
	}

	/**
	 * This method is use for add to the ResultMonitor the value and
	 * informations of monitoring the node.
	 */
	public void calculateResultMonitor() {
		try {
			Object res = getMonitoringValue();
			if (res instanceof Boolean) {
				resultMonitor.addValue((Boolean) res);
				resultMonitor.addMessage("<" + serializeTag + ">" + res + "</"
						+ serializeTag + ">");
			} else if (res instanceof String)
				resultMonitor.addMessage((String) res);
			else if (res instanceof Number)
				resultMonitor.addMessage(((Number) res).toString());
		} catch (WSCoLException e) {
			resultMonitor.addMessage(e.getMessage());
		}

	}

	/**
	 * This method is use for set in the ResultMonitor so the node can save
	 * information about the monitoring.
	 * 
	 * @param resultMonitor
	 *            The resultMonitor to set.
	 */
	public void setResultMonitor(ResultMonitor resultMonitor) {
		this.resultMonitor = resultMonitor;
	}

}
