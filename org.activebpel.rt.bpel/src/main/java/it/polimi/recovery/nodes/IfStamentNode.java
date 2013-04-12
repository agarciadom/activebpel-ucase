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

import antlr.collections.AST;
import it.polimi.WSCoL.WSCoLLexerTokenTypes;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.Monitor;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.recovery.data.RecoveryParams;
import it.polimi.recovery.data.RecoveryResult;

public class IfStamentNode extends WSReLNode
{
	private AST condition = null;
//	private StrategyNode strategy = null;
	//It could be a StrategyNode or another IfStatement
	private WSReLNode child = null;
	private Aliases aliases = null;
	private AliasNodes tempAliases = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3186625382299721001L;

	@Override
	public void doRecovery(RecoveryParams recoveryParams, RecoveryResult recoveryResult)
	{
		// TODO Auto-generated method stub
		if((this.type == WSCoLLexerTokenTypes.IF) || (this.type == WSCoLLexerTokenTypes.ELSEIF))
		{
			Monitor monitor = new Monitor(this.aliases, this.tempAliases);
			boolean result = monitor.evaluateRulesTree(this.condition.getFirstChild(), recoveryParams.getSupervisionParams().getMonitoringData(), recoveryParams.getSupervisionParams().getConfigHvar()).getValueMonitor().booleanValue();
			
			if(!result)
			{
				recoveryResult.setRecoveryResult(false);
				return;
			}
		}
		
		this.child.doRecovery(recoveryParams, recoveryResult);
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases, AliasNodes tempAliases)
	{
		// TODO Auto-generated method stub
		this.aliases = aliases;
		this.tempAliases = tempAliases;
		
		AST temp = this.getFirstChild();
		
		if((this.type == WSCoLLexerTokenTypes.IF) || (this.type == WSCoLLexerTokenTypes.ELSEIF))
		{
			this.condition = temp;
//			this.strategy = (StrategyNode) this.condition.getNextSibling();
			this.child = (WSReLNode) this.condition.getNextSibling();
		}
		else if(this.type == WSCoLLexerTokenTypes.ELSE)
		{
			this.condition = null;
//			this.strategy = (StrategyNode) temp;
			this.child = (WSReLNode) temp;
		}

		this.child.evaluate(inputMonitor, this.aliases, this.tempAliases);
	}
	
	public String toString()
	{
		if(this.type == WSCoLLexerTokenTypes.IF)
			return "If";
		else if(this.type == WSCoLLexerTokenTypes.ELSEIF)
			return "ElseIf";
		else if(this.type == WSCoLLexerTokenTypes.ELSE)
			return "Else";
		
		return "";
	}
}
