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

import antlr.collections.AST;
import it.polimi.exception.DataException;
import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.invoker.Invoker;

public class ExtVarNode extends Variable {
	private NodeWSCoL wsdl=null;
	private NodeWSCoL wm=null;
	private NodeWSCoL ins=null;
	private String returnType=null;
	private String xpath=null;
	
	public static final String RETURN_NUMBER="RETURN_NUMBER";
	public static final String RETURN_BOOL="RETURN_BOOL";
	public static final String RETURN_STRING="RETURN_STRING";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2089350297307764068L;
	
	public ExtVarNode(){
	}
	
	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases ) throws WSCoLException {
		AST temp=this.getFirstChild();
		returnType=temp.getText();
		
		wsdl=(NodeWSCoL)temp.getFirstChild();
		wsdl.evaluate(inputMonitor,  aliases,  tempAliases);
		
		wm=(NodeWSCoL)wsdl.getNextSibling();
		wm.evaluate(inputMonitor,  aliases,  tempAliases);
		
		ins=(NodeWSCoL)wm.getNextSibling();
		ins.evaluate(inputMonitor,  aliases,  tempAliases);
		
		XPathExpressionNode outs=(XPathExpressionNode)ins.getNextSibling();
		outs.evaluate(inputMonitor,  aliases,  tempAliases);
		xpath=outs.getMonitoringValue();
		Invoker invoker=new Invoker();
		String out=invoker.invoke((String)wsdl.getMonitoringValue(), (String)wm.getMonitoringValue(), (String)ins.getMonitoringValue());
		this.parseXml(out);
		this.getXmlCursor(xpath);
	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		String out=this.extractValue("");
		if (out==null) 
			throw new DataException("Data from external variable not found","null","");
		if (returnType.equals(RETURN_BOOL))
			return new Boolean(out);
		else if (returnType.equals(RETURN_STRING))
			return out;
		else if (returnType.equals(RETURN_NUMBER)) {
			try {
				return Double.parseDouble(out);
			} catch (NumberFormatException e) {
				throw new WSCoLCastException("Error determine returnNumber");
			}
		}
		else
			throw new WSCoLException("Error determine return type");
	}
	@Override
	public String toString(){
		return "ExtVar";
	}

	public AliasInfo getAliasValue() throws WSCoLException {
		return new AliasInfo(xpath,(data));
    }

	public String getXpath() throws WSCoLException {
		return xpath;
	}

	public XmlCursor getData() {
		return data;
	}

	@Override
	public String serializeVar() throws WSCoLException {
		String xml;
		xml = data.xmlText();
		if(xml.contains("<xml-fragment>")){
			xml=xml.replaceAll("<xml-fragment>", "");
			xml=xml.replaceAll("</xml-fragment>", "");
		} 
		return xml;
	}
}
