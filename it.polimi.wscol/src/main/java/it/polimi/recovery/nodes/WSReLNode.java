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

package it.polimi.recovery.nodes;

import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.RecoveryResult;

import java.util.logging.Logger;

import antlr.BaseAST;
import antlr.Token;
import antlr.collections.AST;

public abstract class WSReLNode extends BaseAST {
	private static final long serialVersionUID = -6694583642256768802L;
	protected Logger logger = null;
	protected int type;

	public WSReLNode() {
		logger = Logger.getLogger("WSReL Executor");
	}

	@Override
	public void initialize(AST t) {
		this.type = t.getType();
	}

	@Override
	public void initialize(Token t) {
		this.type = t.getType();
	}

	@Override
	public void initialize(int t, String txt) {
		this.type = t;
	}

	public abstract void evaluate(InputMonitor inputMonitor, Aliases aliases, AliasNodes tempAliases);
	public abstract void doRecovery(RecoveryParams recoveryParams, RecoveryResult recoveryResult);
}
