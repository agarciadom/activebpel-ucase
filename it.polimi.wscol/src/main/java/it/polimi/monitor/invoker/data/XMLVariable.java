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

package it.polimi.monitor.invoker.data;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLVariable {
	private static final Logger LOGGER = Logger.getLogger(XMLVariable.class.getCanonicalName());
	private static final XPath XPATH;
	private Document document;

	static {
		XPATH = XPathFactory.newInstance().newXPath();
	}

	public XMLVariable(String value) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.document = builder.parse(new ByteArrayInputStream(value.getBytes()));
		} catch (Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	public String GetValue(String XPath) {
		if (XPath.equals("") || (XPath == null)) {
			return null;
		}
		LOGGER.fine("Evaluating XPath '" + XPath + "'");
		try {
			Node node = (Node) XPATH.evaluate(XPath, this.document, XPathConstants.NODE);
			if (node == null) {
				LOGGER.warning("'"+ XPath + "' returns a null value");
			} else {
				return node.getTextContent();
			}
		} catch (XPathExpressionException e) {
			LOGGER.severe(e.getLocalizedMessage());
		}

		return null;
	}
}
