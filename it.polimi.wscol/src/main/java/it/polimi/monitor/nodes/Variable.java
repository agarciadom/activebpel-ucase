/*
 Copyright 2007 Politecnico di Milano
 Copyright 2013 Antonio García-Domínguez (UCA)
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

import it.polimi.exception.DataException;
import it.polimi.exception.WSCoLException;

import java.util.logging.Logger;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

public abstract class Variable extends NodeWSCoL {
	private static final long serialVersionUID = -1719121047038666593L;

	private static final Logger LOGGER = Logger.getLogger(Variable.class.getCanonicalName());

	private String prexpath = "for $e in $this/";
	private String postxpath = " return $e";
	protected XmlCursor data = null;

	/**
	 * 
	 * @return
	 * @throws WSCoLException
	 */
	public abstract AliasInfo getAliasValue() throws WSCoLException;

	/**
	 * Get method that return a {@link String} with actual xpath of the
	 * variable. If an error occur with during evaluate of the method.
	 * 
	 * @return Actual xpath of the variable.
	 * @throws WSCoLException
	 *             If an error occur with during evaluate of the method.
	 */
	public abstract String getXpath() throws WSCoLException;

	/**
	 * Get method that return a {@link XmlCursor} with actual data of the
	 * variable.
	 * 
	 * @return Actual data of the variable.
	 */
	public abstract XmlCursor getData();

	/**
	 * Costruct the serialization of data of the variable. The serialization can
	 * be a xml without a root if the data has multiple node with the same
	 * xpath. If an error occur with during evaluate of the method.
	 * 
	 * @return The serialization of the variable.
	 * @throws WSCoLException
	 *             If an error occur with during evaluate of the method.
	 */
	public abstract String serializeVar() throws WSCoLException;

	protected XmlCursor getXmlCursor(String xpath) {
		if (xpath.startsWith("/"))
			xpath = xpath.substring(1);
		String queryExpression = prexpath + xpath + postxpath;
		return data.execQuery(queryExpression);
	}

	protected void parseXml(String xmlFile) throws WSCoLException {
		try {
			data = XmlObject.Factory.parse(xmlFile).newCursor();
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WSCoLException("Error parse file");
		}
	}

	protected String extractValue(String xpath) throws DataException {
		if (xpath.equals("")) {
			XmlCursor cursor = data.newCursor();
			cursor.toFirstContentToken();
			return cursor.getTextValue();
		}
		XmlObject xmlObj = data.getObject();
		if (xpath.startsWith("/"))
			xpath = xpath.substring(1);
		String queryExpression = prexpath + xpath + postxpath;
		XmlObject[] results = xmlObj.execQuery(queryExpression);
		if (results.length > 0) {
			XmlCursor resultCursor = results[0].newCursor();
			LOGGER.info("il valore estratto è " + resultCursor.getTextValue());
			return resultCursor.getTextValue();
		}
		throw new DataException("No element found", data.xmlText(), xpath);
	}

	protected int numberOfNode() throws DataException {
		XmlCursor cursor = data.newCursor();
		int i = 1;
		cursor.toFirstChild();
		while (cursor.toNextSibling()) {
			i++;
		}
		return i;
	}

}
