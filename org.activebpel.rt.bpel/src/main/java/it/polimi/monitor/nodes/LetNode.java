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

public class LetNode extends NodeWSCoL {
	
	private NodeWSCoL left=null;
	private	NodeWSCoL right=null;
	
	/**
	 * 
	 */
	public LetNode() {
		serializeTag="let";
	}

	private static final long serialVersionUID = 6309956910734802320L;

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases ) throws WSCoLException {
		left=(NodeWSCoL)this.getFirstChild();
		left.evaluate(inputMonitor,  aliases ,  tempAliases);
		right=(NodeWSCoL)left.getNextSibling();
		right.evaluate(inputMonitor,  aliases ,  tempAliases);
		//distinguo tra variabili o alias normali
		
		if (right instanceof Variable) {
			aliases.addAlias(new Alias((String)left.getMonitoringValue(),((Variable)right).getAliasValue(),((Variable)right).serializeTag));
		} else	{
			//aggiungere l'aliasNode.
			aliases.addAlias(new Alias((String)left.getMonitoringValue(),right.getMonitoringValue(),right.serializeTag));
		}
	}

	@Override
	public Object getMonitoringValue()  throws WSCoLException {
		//non server restituire niente restituisco una stringa
		return "Let: " + left.getMonitoringValue() + " = " + right.getMonitoringValue();
	}
	
	public String toString(){
		return "Let";
	}


}
