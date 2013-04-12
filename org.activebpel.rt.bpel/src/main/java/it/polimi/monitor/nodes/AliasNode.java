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

import org.apache.xmlbeans.XmlCursor;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

public class AliasNode extends NodeWSCoL {
	
	private static final long serialVersionUID = 345501908200925896L;
	private NodeWSCoL identifier;
	private NodeWSCoL variable=null;
	protected XmlCursor data=null;
	private int numberOfCurrentChildren = 0;
	private int numberOfChildren = -1;
    private int typeOfExtraction = -1;
    private String rootXml=null;
    
    public static final int EXTRACTSTEPBYSTEP = 0;
    public static final int EXTRACTALLTOGETHER = 1;
	
	
	
	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases) throws WSCoLException {
		//The first child represents the identifier of the Alias
		identifier = (SimpleAST)getFirstChild();
		identifier.evaluate(inputMonitor,  aliases, tempAliases);
		variable=(NodeWSCoL)identifier.getNextSibling();
		variable.evaluate(inputMonitor, aliases, tempAliases);
		if (variable instanceof Variable) {
			//controllare se è una temp alias
			Variable var=(Variable)variable;
			numberOfChildren=var.numberOfNode();
			data=var.getData();
			if (! data.xmlText().contains("<xml-fragment>"))
				rootXml=var.getXpath();				
			else
				rootXml=var.serializeTag;
			tempAliases.addAliasNode(this);
			
		}  else {
			throw new WSCoLException("Error in determine variable");
		}
			
		
	}
	
	@Override
	public XmlCursor getMonitoringValue() throws WSCoLException {
		switch (typeOfExtraction) {
			case EXTRACTALLTOGETHER:
				return data;
			case EXTRACTSTEPBYSTEP:
				return extractCursor(numberOfCurrentChildren).newCursor();
			default:
				return extractCursor(numberOfCurrentChildren).newCursor();
		}
	}
	@Override
	public String toString(){
		return "AliasNode";
	}
	

	/**
	 * @return the identifier
	 */
	public String getIdentifier() throws WSCoLException {
		return (String)identifier.getMonitoringValue();
	}
	
	public void nextChild() {
		logger.severe("Figlio attuale "+numberOfCurrentChildren+ " prossimo figlio " + numberOfCurrentChildren +1 );
		if( numberOfCurrentChildren +1 < numberOfChildren){
			numberOfCurrentChildren++;		}
	}
	
	/**
	 * @return the numberOfChildren
	 */
	public int getNumberOfChildren() {
		return numberOfChildren;
	}
	
	public void setTypeOfExtraction(int type){
		this.typeOfExtraction=type;
	}

	/**
	 * @return the typeOfExtraction
	 */
	public int getTypeOfExtraction() {
		return typeOfExtraction;
	}

	/**
	 * @return the numberOfCurrentChildren
	 */
	public int getNumberOfCurrentChildren() {
		return numberOfCurrentChildren;
	}

	private XmlCursor extractCursor(int i) throws WSCoLException{
		XmlCursor cursor=data.newCursor();
		if (cursor.toChild(i))
			return cursor;
		else
			throw new WSCoLException("pippo");
	}

	/**
	 * @return the rootXml
	 */
	public String getRootXml() {
		return rootXml;
	}   
}
