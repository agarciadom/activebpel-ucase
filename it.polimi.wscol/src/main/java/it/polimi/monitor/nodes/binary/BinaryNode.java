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

import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.monitor.nodes.InternalVarNode;
import it.polimi.monitor.nodes.NodeWSCoL;

import java.util.logging.Logger;

/**
 * Abstract class for make me look the ast tree like a heterogeneous tree.
 * 
 * @author Luca Galluppi
 * 
 */
public abstract class BinaryNode extends NodeWSCoL {
	public static final int NUMBER = 0;
	public static final int STRING = 1;
	public static final int BOOLEAN = 2;

	private static final long serialVersionUID = -541369151144616283L;
	private static final Logger LOGGER = Logger.getLogger(BinaryNode.class.getCanonicalName());

	protected int valueType = -1;
	protected Double dLeft = null;
	protected Double dRight = null;
	protected String sLeft = null;
	protected String sRight = null;
	protected Boolean bLeft = null;
	protected Boolean bRight = null;

	/**
	 * Get the left child of the tree.
	 * 
	 * @return the left child of the tree.
	 */
	public NodeWSCoL left() {
		return (NodeWSCoL) getFirstChild();
	}

	/**
	 * Get the right child of the tree. Or null if there isn't the left child.
	 * 
	 * @return the right child of the tree or null if there isn't the left
	 *         child.
	 */
	public NodeWSCoL right() {
		NodeWSCoL t = left();
		if (t == null)
			return null;
		return (NodeWSCoL) t.getNextSibling();
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes tempAliases) throws WSCoLException {
		LOGGER.info("Start evaluate " + serializeTag);
		this.inputMonitor = inputMonitor;
		left().evaluate(inputMonitor, aliases, tempAliases);
		right().evaluate(inputMonitor, aliases, tempAliases);
		getInstace();
		LOGGER.info("Finish evaluate " + serializeTag);
	}

	@Override
	public abstract Object getMonitoringValue() throws WSCoLException;

	protected void getInstace() throws WSCoLException {
		// determino se gli oggetti sono entrambi booleani o stringhe
		String l = left().getMonitoringValue().toString();
		String r = right().getMonitoringValue().toString();

		if ((l.equals("false") || l.equals("true"))
				&& (r.equals("false") || r.equals("true"))) {
			// entrambi boolean
			valueType = BOOLEAN;
			bLeft = Boolean.parseBoolean(l);
			bRight = Boolean.parseBoolean(r);
			LOGGER.fine("entrambi boolean");
			return;
		}
		try {
			// se sono entrambi numeri
			dLeft = Double.parseDouble(l);
			dRight = Double.parseDouble(r);
			valueType = NUMBER;
			LOGGER.fine("entrambi number");
		} catch (NumberFormatException e) {
			// altrimenti sono stringhe
			valueType = STRING;
			sLeft = l;
			sRight = r;
			LOGGER.fine("entrambi stringhe");
		}

		// se sono tutti nulli sollevo eccezione WSCoLCastException()
		if ((bLeft == null && bRight == null)
				&& (dLeft == null && dRight == null)
				&& (sLeft == null && sRight == null))
			throw new WSCoLCastException(
					"Can't make operation beetwen different type");
	}

	/**
	 * Get the value of the Variable with the given id that was used for
	 * monitoring a {@link BinaryNode}.
	 * 
	 * @param id
	 *            The d of the Variable.
	 * @return The value of the Variable.
	 * @throws WSCoLException
	 *             If not complex value found with given id.
	 */
	public String getValueComplexOp(String id) throws WSCoLException {
		if (left() instanceof InternalVarNode
				&& ((InternalVarNode) left()).getIdentifier().equals(id)) {
			return (String) ((InternalVarNode) left()).getCurrentValue();
		} else if (right() instanceof InternalVarNode
				&& ((InternalVarNode) right()).getIdentifier().equals(id))
			return (String) ((InternalVarNode) right()).getCurrentValue();
		else
			throw new WSCoLException("Not complex value found with id: " + id);
	}
}
