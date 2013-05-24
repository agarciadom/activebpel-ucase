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

import java.util.Enumeration;
import java.util.Hashtable;
/**
 * List of {@link AliasNode} defined in the quantification rules like forall, avg.
 * 
 * @author Luca Galluppi
 *  
 */
public class AliasNodes {
	private Hashtable<String,AliasNode> list;
 	
 	/**
 	 * Create a new List of {@link AliasNode}
 	 *
 	 */
    public AliasNodes() {
        list = new Hashtable<String,AliasNode>();
    }
    
    /**
     * Add a new AliasNode in the list.
     * @param a the AliasNode.
     */
    public void addAliasNode(AliasNode a) throws WSCoLException {
    	list.put(a.getIdentifier(), a);
    }
    
    /**
     * Remove the AliasNode with the specified identifier, if it is in the list.
     * @param identifier the identifier of the AliasNode.
     */
    public void removeAliasNode(String identifier) {
      	if (list.containsKey(identifier))
      		list.remove(identifier);
    }
    /**
     * Extract the AliasNode with the specified identifier, if it isn't in the list return null.
     * @param identifier the identifier of the Alias.
     * @return the alias or null if it isn't in the list.
     */
    public AliasNode getAliasNode(String identifier) {
        if (list.containsKey(identifier))
        	return list.get(identifier);
        return null;
    }
    /**
     * Extract all the AliasNode in the list.
     * @return an array of all the AliasNode in the list.
     */
    public AliasNode[] getAllAliasNodes() {
        AliasNode[] a=new AliasNode[list.size()];
        Enumeration<AliasNode> i = list.elements();
        for (int j=0; j<list.size(); j++) {
            a[j]=i.nextElement();
        }
        return a;    
    }
    /**
     * Check if the identifier is present in the list. If the id is in the list return true 
     * otherwise return false.
     * @param identifier the identifier of the AliasNode.
     * @return true if present otherwise false
     */
    public boolean isKnowAliasNode(String identifier) {
		if(list.containsKey(identifier)) 
			return true;
		else 
			return false;
	}
}
