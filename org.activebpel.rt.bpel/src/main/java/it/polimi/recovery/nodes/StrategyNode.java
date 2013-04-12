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

public class StrategyNode extends WSReLNode 
{	
	//It can be an 'or' node or a 'recovery step' node
	private WSReLNode first = null;
//	//It can be only a 'recovery step' node
//	private WSReLNode second = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8379839875438740841L;

	@Override
	public void doRecovery(RecoveryParams recoveryParams, RecoveryResult recoveryResult)
	{
		this.first.doRecovery(recoveryParams, recoveryResult);
		
//		//If the first branch of recovery strategy is successful or a blocking action
//		//then the result is returned back.
//		if(recoveryResult.isRecoveryResult() ||
//			recoveryResult.isThereHaltAction() || 
//			recoveryResult.isThereIgnoreAction())
//		{
//			return;
//		}
//		
//		if(this.second != null)
//			this.second.doRecovery(recoveryParams, recoveryResult);
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases)
	{
		//We can have only one child
		this.first = (WSReLNode) this.getFirstChild();
		this.first.evaluate(inputMonitor, aliases, tempAliases);
		
//		this.second = (WSReLNode) this.first.getNextSibling();
//		if(this.second != null)
//			this.second.evaluate();
	}
	public String toString()
	{
		return "Strategy";
	}
}
