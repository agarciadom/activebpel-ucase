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

import java.util.Vector;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

public class PARAM_LISTNode extends NodeWSCoL{

	private Vector<NodeWSCoL> parametres;
	private static final long serialVersionUID = 9197523087897922080L;

	/**
	 * 
	 */
	public PARAM_LISTNode() {
		parametres= new Vector<NodeWSCoL>();
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases ) throws WSCoLException {
    	NodeWSCoL temp=null;
    	for(int i=0; i < this.getNumberOfChildren(); i++) {
    		if(i==0) {
    			parametres.add(temp=(NodeWSCoL)this.getFirstChild());
    		}
    		else {
    			parametres.add(temp=(NodeWSCoL)temp.getNextSibling());
    		}
    	}
		
	}

	@Override
	public Vector<NodeWSCoL> getMonitoringValue() throws WSCoLException {
		return parametres;
	}

}
