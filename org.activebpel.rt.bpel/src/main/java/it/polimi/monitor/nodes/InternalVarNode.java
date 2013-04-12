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

import java.util.logging.Level;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import it.polimi.exception.DataException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

public class InternalVarNode extends Variable {
	private String prexpath="for $e in $this/";
	private String postxpath=" return $e";
	
	private static final long serialVersionUID = 4098857781757743041L;
	private VariableNode varNode=null;
	private Object value=null;
	private Object currentValue=null;
	private final String MONITOR = "monitor_data/";
	private String xpath=null;
	private Alias alias=null;
	private AliasNodeInfo aliasNode=null;
	private String[] values = null;
	private String identifier=null;
	
	/**
	 * @param varNode
	 */
	public InternalVarNode() {
		super();
		logger.info("BPEL Variable");
	}
    @Override
	public void  evaluate( InputMonitor inputMonitor  , Aliases aliases , AliasNodes tempAliases ) throws WSCoLException {
		this.inputMonitor=inputMonitor;
		logger.info("Evaluate");
		varNode=(VariableNode)this.getFirstChild();
		varNode.evaluate(inputMonitor,  aliases ,  tempAliases);
		identifier=varNode.getIdentifier();
		Object res=varNode.getMonitoringValue();
		if (res instanceof Alias) { 
			alias=(Alias)res;
			if(alias.getAliasType() == Alias.ALIAS_COSTANT)
				value = alias.getMonitoringValue();
			else {
				AliasInfo info=(AliasInfo)alias.getMonitoringValue();
				data=info.getCursorData();
				value = extractValue(info.getExtraPath());
			}
		} else if (res instanceof String) {
			parseXml(this.inputMonitor.getData());
			xpath=MONITOR+(String)res;
			data=getXmlCursor(xpath);
			serializeTag=calculateSerializeTag(xpath);
			
			value= this.extractValue("");
		} else if (res instanceof AliasNodeInfo) {
			aliasNode=(AliasNodeInfo)res;
			data=aliasNode.getCursorData();
			xpath=aliasNode.getXpath();
			serializeTag=calculateSerializeTag(aliasNode.getXpath());
			switch (aliasNode.getTypeOfExtraction()) {
				case AliasNode.EXTRACTALLTOGETHER:
					if (values == null) {
						values=this.extractValues(aliasNode.getXpath());
					} value=currentValue=values[aliasNode.getNumNode()];
				default: 
					value=currentValue=this.extractValue(aliasNode.getXpath());
			}
		} else
			throw new WSCoLException("Error in determining BPEL variable");
	}
	
    @Override
	public Object getMonitoringValue() throws WSCoLException {
     	return value;
    }
    public AliasInfo getAliasValue() throws WSCoLException {
    	Object v=varNode.getMonitoringValue();
      	if (v instanceof String)
    		return new AliasInfo(xpath,(this.getData()));
    	else if(v instanceof Alias) {
    		return (AliasInfo)((Alias)v).getMonitoringValue();
    	} else if (v instanceof AliasNodeInfo) {
    		return new AliasInfo(xpath,(((AliasNodeInfo)v).getCursorData()));
    	} else throw new WSCoLException("Can't create Alias");
    }
	
	public String getXpath() throws WSCoLException {
		if (alias != null)
			return ((AliasInfo)alias.getValue()).getXpath();
		else if (aliasNode != null)
			return aliasNode.getXpath();
		else if (xpath==null)
			return xpath=(String)varNode.getMonitoringValue();
		else 
			return xpath;
	}
	
	@Override
	public String toString() {
        return "BPEL_VAR";
    }
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @return the currentValue
	 */
	public Object getCurrentValue() {
		return currentValue;
	}
	public XmlCursor getData() {
		return data;
	}
	/* (non-Javadoc)
	 * @see it.polimi.monitor.nodes.Variable#serializeVar()
	 */
	@Override
	public String serializeVar() throws WSCoLException {
		String xml;
		if(alias!=null)
			if (alias.getAliasType() == Alias.ALIAS_COSTANT)
				xml = "<"+alias.serializeTag+">"+alias.getMonitoringValue()+"</"+alias.serializeTag+">";
			else 
				xml = ((AliasInfo)alias.getMonitoringValue()).getCursorData().xmlText();
		else if (aliasNode!=null)
			return "serializzo aliasnode";
		else 
			xml = data.xmlText();
		if(xml.contains("<xml-fragment>")){
			xml=xml.replaceAll("<xml-fragment>", "");
			xml=xml.replaceAll("</xml-fragment>", "");
		} 
		return xml;
	}
	private String calculateSerializeTag(String x) {
		char[] c=x.toCharArray();
		int j=0;
		for(int i=c.length-1; i>=0  ;i--)
			if (c[i] =='/') {
				j=i;
				break;
			}
		return x.substring(j);
	}
	private String[] extractValues(String xpath) throws DataException {
		if (xpath.startsWith("/"))
			xpath=xpath.substring(1);
		String queryExpression =  prexpath + xpath + postxpath; 
		XmlObject[] results = data.getObject().execQuery(queryExpression);
	   	String[] res=new String[results.length];
	   	if (results.length ==0)
	   		throw new DataException("No element found", data.xmlText(), xpath);
		int j=0;
		logger.log(Level.FINEST,"il valori presenti sono " +results.length);
		for (XmlObject i : results) {
			XmlCursor resultCursor=  i.newCursor();
			res[j]= resultCursor.getTextValue();
			logger.info("estratto il valore numero " + j + " con il testo " +i);
			j++;
		}
	    return res;	
	 }	
}
