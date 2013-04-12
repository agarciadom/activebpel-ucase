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

public class AliasInfo {
	private String xpath=null;
	private XmlCursor cursorData=null;
	private String extraPath="";
	/**
	 * @param xpath
	 * @param cursorData
	 */
	public AliasInfo(String xpath, XmlCursor cursorData) {
		this.xpath = xpath;
		this.cursorData = cursorData;
	}
	/**
	 * @return the extraPath
	 */
	public String getExtraPath() {
		return extraPath;
	}
	/**
	 * @param extraPath the extraPath to set
	 */
	public void setExtraPath(String extraPath) {
		this.extraPath = extraPath;
	}
	/**
	 * @return the cursorData
	 */
	public XmlCursor getCursorData() {
		return cursorData;
	}
	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}
	
}
