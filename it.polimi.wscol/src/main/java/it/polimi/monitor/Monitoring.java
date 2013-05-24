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

package it.polimi.monitor;

import antlr.collections.AST;

/**
 * Interfarce that can be use for calculate a monitoring value based on the data. 
 * 
 * @author Luca Galluppi
 *
 */
public interface Monitoring {
	/**
	 * Calculate monitoring value based on the parameters. If rules are more than one 
	 * than the AND logical operation is calculate.
	 *  
	 * @param rules A {@link AST} tree of the rules.
	 * @param data The data of the process.
	 * @param configHvar The data for interact with historical variable.
	 * @return calculated AND logical operation. 
	 */
	public ResultMonitor evaluateRulesTree(AST rules, String data, String configHvar);
}
