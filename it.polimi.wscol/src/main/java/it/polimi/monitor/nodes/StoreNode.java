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

/**
 *
 */
package it.polimi.monitor.nodes;

import it.polimi.exception.DataException;
import it.polimi.exception.HVarSericeException;
import it.polimi.exception.IncompleteInformationException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.invoker.Invoker;

import java.util.logging.Logger;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * @author Luca Galluppi
 * 
 */
public class StoreNode extends NodeWSCoL {
	private static final Logger LOGGER = Logger.getLogger(StoreNode.class.getCanonicalName());

	// variabili per una store
	private String processID = null;
	private String wsdl = null;
	private String store_wm = null;
	private int assertionType = -1;
	private String location = null;
	private String userID = null;
	private Long instanceID = null;
	private XmlCursor data = null;
	public static final String root = "webservice/";

	public static final String PROCESSID = "processID";
	public static final String WSDL = "wsdl";
	public static final String STORE_WM = "store_wm";
	public static final String ASSERTION_TYPE = "assertionType";
	public static final String LOCATION = "location";
	public static final String USERID = "userID";
	public static final String INSTANCEID = "instanceID";
	private String prexpath = "for $e in $this/";
	private String postxpath = " return $e";

	private static final long serialVersionUID = 5020952659097381563L;

	/**
         *
         */
	public StoreNode() {
		serializeTag = "store";
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes tempAliases) throws WSCoLException {
		setConfiguration(inputMonitor.getConfigHvar());
		SimpleAST simple = (SimpleAST) getFirstChild();
		simple.evaluate(inputMonitor, aliases, tempAliases);
		NodeWSCoL rule = (NodeWSCoL) simple.getNextSibling();
		rule.evaluate(inputMonitor, aliases, tempAliases);
		if (rule instanceof Variable) {
			if (!invokeMultipleVar(simple.getMonitoringValue(),
					this.extractMultivariable((Variable) rule)))
				throw new HVarSericeException(
						"Error in invoke Historical Variable Service");
			/*
			 * if (! invokeService(simple.getMonitoringValue(),
			 * ((Variable)rule).serializeVar())) throw new
			 * HVarSericeException("Error in invoke Historical Variable Service"
			 * );
			 */
		} else if (!invokeService(simple.getMonitoringValue(), rule
				.getMonitoringValue().toString()))
			throw new HVarSericeException(
					"Error in invoke Historical Variable Service");

	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		return "Store HVAR";
	}

	private void setConfiguration(String config) throws WSCoLException {
		this.parseXml(config);// ,ExtractorValue.GENERIC_SOURCE);
		String val = null;
		val = this.extractValue(root + PROCESSID);
		// controllo i valori ma sicuramente sono forniti perche devo validare
		// lo schema Conf_HVAR.xsd
		if (val != null)
			processID = val;
		else
			// errore
			throw new IncompleteInformationException("ProcessId not Found");

		val = this.extractValue(root + WSDL);
		// controllo i valori ma sicuramente sono forniti perche devo validare
		// lo schema Conf_HVAR.xsd
		if (val != null)
			wsdl = val;
		else
			// errore
			throw new IncompleteInformationException("WSDL not Found");

		val = this.extractValue(root + STORE_WM);
		if (val != null)
			store_wm = val;
		else
			// errore
			throw new IncompleteInformationException(
					"Store web method not Found");

		val = this.extractValue(root + ASSERTION_TYPE);
		if (val != null)
			assertionType = Integer.valueOf(val);
		else
			// errore
			throw new IncompleteInformationException("AssertionType not Found");

		val = this.extractValue(root + LOCATION);
		if (val != null)
			location = val;
		else
			// errore
			throw new IncompleteInformationException("Location not Found");

		val = this.extractValue(root + USERID);
		if (val != null)
			userID = val;

		val = this.extractValue(root + INSTANCEID);
		if (val != null)
			instanceID = Long.valueOf(val);
		else
			instanceID = null;
	}

	private String costructMessage(String aliasName, String value) {
		if (value.startsWith("<")) {
			value = value.replace("<", "&lt;");
			value = value.replace(">", "&gt;");
		}
		if (userID == null && instanceID == null)
			return "<InvokeServiceParameters>" + "<" + store_wm + ">"
					+ "<processID>" + processID + "</processID>" + "<location>"
					+ location + "</location>" + "<assertionType>"
					+ assertionType + "</assertionType>" + "<aliasName>"
					+ aliasName + "</aliasName>" + "<value>" + value
					+ "</value>" + "</" + store_wm + ">"
					+ "</InvokeServiceParameters>";
		else if (userID == null)
			return "<InvokeServiceParameters>" + "<" + store_wm + ">"
					+ "<processID>" + processID + "</processID>"
					+ "<instanceID>" + instanceID + "</instanceID>"
					+ "<location>" + location + "</location>"
					+ "<assertionType>" + assertionType + "</assertionType>"
					+ "<aliasName>" + aliasName + "</aliasName>" + "<value>"
					+ value + "</value>" + "</" + store_wm + ">"
					+ "</InvokeServiceParameters>";
		else if (instanceID == null)
			return "<InvokeServiceParameters>" + "<" + store_wm + ">"
					+ "<processID>" + processID + "</processID>" + "<userID>"
					+ userID + "</userID>" + "<location>" + location
					+ "</location>" + "<assertionType>" + assertionType
					+ "</assertionType>" + "<aliasName>" + aliasName
					+ "</aliasName>" + "<value>" + value + "</value>" + "</"
					+ store_wm + ">" + "</InvokeServiceParameters>";
		else
			return "<InvokeServiceParameters>" + "<" + store_wm + ">"
					+ "<processID>" + processID + "</processID>" + "<userID>"
					+ userID + "</userID>" + "<instanceID>" + instanceID
					+ "</instanceID>" + "<location>" + location + "</location>"
					+ "<assertionType>" + assertionType + "</assertionType>"
					+ "<aliasName>" + aliasName + "</aliasName>" + "<value>"
					+ value + "</value>" + "</" + store_wm + ">"
					+ "</InvokeServiceParameters>";
	}

	@Override
	public String toString() {
		return "Store";
	}

	private boolean invokeService(String aliasName, String value)
			throws WSCoLException {
		String input = costructMessage(aliasName, value);
		Invoker invoke = new Invoker();
		String out = invoke.invoke(wsdl, store_wm, input);
		parseXml(out);
		return Boolean.parseBoolean(this.extractValue("Response/return"));
	}

	private void parseXml(String xmlFile) throws WSCoLException {
		try {
			data = XmlObject.Factory.parse(xmlFile).newCursor();
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WSCoLException("Error parse file");
		}
	}

	private String extractValue(String xpath) throws DataException {
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
			LOGGER.info("extracted value: " + resultCursor.getTextValue());
			return resultCursor.getTextValue();
		}
		throw new DataException("No element found", data.xmlText(), xpath);
	}

	private String[] extractMultivariable(Variable var) throws WSCoLException {
		int j = 0;
		XmlObject[] result = extractXML(var.getData(), var.serializeTag);
		String[] values = new String[result.length];
		for (XmlObject i : result)
			values[j++] = i.xmlText();
		return values;
	}

	private boolean invokeMultipleVar(String aliasName, String[] values)
			throws WSCoLException {
		for (String i : values) {
			String input = costructMessage(aliasName, i);
			Invoker invoke = new Invoker();
			String out = invoke.invoke(wsdl, store_wm, input);
			parseXml(out);
			if (Boolean.parseBoolean(this.extractValue("Response/result")) == false)
				return false;
		}
		return true;

	}

	private XmlObject[] extractXML(XmlCursor cursor, String xpath)
			throws DataException {
		if (xpath.startsWith("/"))
			xpath = xpath.substring(1);
		String queryExpression = prexpath + xpath + postxpath;
		XmlObject[] results = cursor.getObject().execQuery(queryExpression);
		if (results.length == 0)
			throw new DataException("No element found", data.xmlText(), xpath);
		return results;
	}

}
