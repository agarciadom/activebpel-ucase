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

public class SLASHNode extends NodeWSCoL {
	private String tree=null;
  
	private static final long serialVersionUID = -4864006265851756450L;
    
	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases) throws WSCoLException {
		//Guardo il numero di livelli
		if((this.getFirstChild().getClass()).equals(SLASHNode.class)){
			//	ho piu livelli
			SLASHNode node = (SLASHNode)this.getFirstChild();
			node.evaluate(inputMonitor,  aliases ,  tempAliases);
			tree=node.getMonitoringValue();
			// ho uno slash e un figlio simpleAST
			if (node.getNextSibling() != null) {
				SimpleAST brother=(SimpleAST)node.getNextSibling();
				brother.evaluate(inputMonitor,  aliases ,  tempAliases);
				tree=tree + "/" + brother.getMonitoringValue();
			}
		} else {
			// ho un solo livello
			SimpleAST brother = (SimpleAST)this.getFirstChild();
			brother.evaluate(inputMonitor,  aliases ,  tempAliases);
			tree="/"+brother.getMonitoringValue();
			// ho 2 elementi
			if (brother.getNextSibling() != null) {
				brother=(SimpleAST)brother.getNextSibling();
				brother.evaluate(inputMonitor,  aliases ,  tempAliases);
				tree=tree + "/" + brother.getMonitoringValue();
			}
		}		
	}

	@Override
	public String getMonitoringValue() throws WSCoLException {
		return tree;
	}
	@Override
	public String toString() {
        return "/";
    }
}
