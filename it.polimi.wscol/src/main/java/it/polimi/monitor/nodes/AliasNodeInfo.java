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

public class AliasNodeInfo {
	private String xpath=null;
	private XmlCursor cursorData=null;
	private int numNode=-1;
	private int typeOfExtraction = -1;
	/**
	 * @param xpath
	 * @param stringData
	 * @param typeOfExtraction
	 */
	public AliasNodeInfo(int typeOfExtraction,String xpath, XmlCursor cursorData, int numNode) {
		this.typeOfExtraction=typeOfExtraction;
		this.xpath = xpath;
		this.cursorData= cursorData;
		this.numNode=numNode;
	}
	
	
	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}
	/**
	 * @return the stringData
	 */
	public XmlCursor getCursorData() {
		return cursorData;
	}
	/**
	 * @return the numNode
	 */
	public int getNumNode() {
		return numNode;
	}
	
	
	/**
	 * @return the typeOfExtraction
	 */
	public int getTypeOfExtraction() {
		return typeOfExtraction;
	}
}
