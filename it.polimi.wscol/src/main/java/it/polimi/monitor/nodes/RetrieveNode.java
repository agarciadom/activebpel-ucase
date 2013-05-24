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

/**
 * 
 */
package it.polimi.monitor.nodes;

import org.apache.xmlbeans.XmlCursor;

import it.polimi.exception.DataException;
import it.polimi.exception.IncompleteInformationException;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;
import it.polimi.monitor.invoker.Invoker;

/**
 * @author luca
 *
 */
public class RetrieveNode extends Variable {
	public static final String root="webservice/";
	public static final String WSDL="wsdl";
	public static final String RETRIEVE_WM="retrieve_wm";
	
	private String xpath="Response/return";
	private String processID=null;
	private String wsdl=null;
	private String retrieve_wm=null;
	private int assertionType=-1;
	private String location=null;
	private String userID=null;
	private Long instanceID=null;
	
	String aliasHVar=null;
	int numberOfResult=-1;
	
	
	private static final long serialVersionUID = 6205039987743717144L;
	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases, AliasNodes tempAliases) throws WSCoLException {
		//carico wsdl e retrieve method
		this.parseXml(inputMonitor.getConfigHvar());
		String val = this.extractValue(root+WSDL);
		//controllo i valori ma sicuramente sono forniti perche devo validare lo schema Conf_HVAR.xsd
		if (val!=null)
			wsdl=val;
			else
			//errore
			throw new IncompleteInformationException("WSDL not Found");
			
			val = this.extractValue(root+RETRIEVE_WM);
			if (val!=null)
				retrieve_wm=val;
			else
				//errore
				throw new IncompleteInformationException("Retrieve web method not Found");
		//carico i parametri da retrieve
		ProcessIdNode nodeProcessID=(ProcessIdNode)this.getFirstChild();
		nodeProcessID.evaluate(inputMonitor, aliases, tempAliases);
		processID=nodeProcessID.getMonitoringValue();

		UserIdNode nodeUserID=(UserIdNode)nodeProcessID.getNextSibling();
		nodeUserID.evaluate(inputMonitor, aliases, tempAliases);
		userID=nodeUserID.getMonitoringValue();
		
		InstanceIdNode nodeInstanceID=(InstanceIdNode)nodeUserID.getNextSibling();
		nodeInstanceID.evaluate(inputMonitor, aliases, tempAliases);
		instanceID = nodeInstanceID.getMonitoringValue();
		
		LocationNode nodeLocation=(LocationNode)nodeInstanceID.getNextSibling();
		nodeLocation.evaluate(inputMonitor, aliases, tempAliases);
		location=nodeLocation.getMonitoringValue();
			
		AssertionTypeNode nodeAssertionType=(AssertionTypeNode)nodeLocation.getNextSibling();
		nodeAssertionType.evaluate(inputMonitor, aliases, tempAliases);
		assertionType=nodeAssertionType.getMonitoringValue();
		
		AliasNameNode nodeAliasHVar=(AliasNameNode)nodeAssertionType.getNextSibling();
		nodeAliasHVar.evaluate(inputMonitor, aliases, tempAliases);
		aliasHVar=nodeAliasHVar.getMonitoringValue();
		
		NumMaxResultsNode nodeMaxRes=(NumMaxResultsNode)nodeAliasHVar.getNextSibling();
		nodeMaxRes.evaluate(inputMonitor, aliases, tempAliases);
		numberOfResult=nodeMaxRes.getMonitoringValue();
		
	//	invokeServiceAxis();
		invokeService();
	

	}

	@Override
	public Object getMonitoringValue() throws WSCoLException {
		return this.extractValue(xpath);
	}
	
	public String toString(){
		return "Retrieve";
	}
	
	private String costructMessage() {
		if(userID == null && instanceID == null)
			return "<InvokeServiceParameters>" +
				"<"+retrieve_wm+">" +
				"<processID>"+processID+"</processID>" +
				"<location>"+location+"</location>" +
				"<assertionType>"+assertionType+"</assertionType>" +
				"<aliasName>"+aliasHVar+"</aliasName>" +
				"<numberOfResults>"+numberOfResult+"</numberOfResults>"+
				"</"+retrieve_wm+">"+
				"</InvokeServiceParameters>";
		else if (userID == null)
			return "<InvokeServiceParameters>" +
				"<"+retrieve_wm+">" +
				"<processID>"+processID+"</processID>" +
				"<instanceID>"+instanceID+"</instanceID>" +
				"<location>"+location+"</location>" +
				"<assertionType>"+assertionType+"</assertionType>" +
				"<aliasName>"+aliasHVar+"</aliasName>" +
				"<numberOfResults>"+numberOfResult+"</numberOfResults>"+
				"</"+retrieve_wm+">"+
				"</InvokeServiceParameters>";
		else if (instanceID == null)
			return "<InvokeServiceParameters>" +
				"<"+retrieve_wm+">" +
				"<processID>"+processID+"</processID>" +
				"<userID>"+userID+"</userID>" +
				"<instanceID>-1</instanceID>" +
				"<location>"+location+"</location>" +
				"<assertionType>"+assertionType+"</assertionType>" +
				"<aliasName>"+aliasHVar+"</aliasName>" +
				"<numberOfResults>"+numberOfResult+"</numberOfResults>"+
				"</"+retrieve_wm+">"+
				"</InvokeServiceParameters>";
		else
			return "<InvokeServiceParameters>" +
				"<"+retrieve_wm+">" +
				"<processID>"+processID+"</processID>" +
				"<userID>"+userID+"</userID>" +
				"<instanceID>"+instanceID+"</instanceID>" +
				"<location>"+location+"</location>" +
				"<assertionType>"+assertionType+"</assertionType>" +
				"<aliasName>"+aliasHVar+"</aliasName>" +
				"<numberOfResults>"+numberOfResult+"</numberOfResults>"+
				"</"+retrieve_wm+">"+
				"</InvokeServiceParameters>";
	}

	public AliasInfo getAliasValue() throws WSCoLException {
		return new AliasInfo(xpath,(data));
	}

	public XmlCursor getData() {
		return data;
	}

	public String getXpath() throws WSCoLException {
		return xpath;
	}
	
	/*private String[] invokeServiceAxis() {
		try {
			HistoricalVariableBeansService service=new HistoricalVariableBeansServiceLocator();
			HistoricalVariableBeans port= service.getHistoricalVariableBeansPort();
			String[] results=port.findHistoricalVariable(processID, userID, instanceID, location, assertionType, aliasHVar, numberOfResult);
			for(String i :results)
				System.err.println(i);
			return port.findHistoricalVariable(processID, userID, instanceID, location, assertionType, aliasHVar, numberOfResult);
		} catch (Exception e) {
			return null;
		} 
		
	}*/
	private void invokeService() throws WSCoLException {
//		 creo input
		String input=costructMessage();
		Invoker invoker=new Invoker();
		System.err.println(input);
		String out=invoker.Invoke(wsdl, retrieve_wm, input);
		if (out==null)
			throw new DataException("Data from external variable not found","null","");
		parseXml(out);	
	}

	
	@Override
	public String serializeVar() throws WSCoLException {
		String x=data.xmlText();
		x=x.replaceAll("<Response>","");
		
		x=x.replaceAll("</Response>","");

		x=x.replaceAll("<result>","<"+ aliasHVar+">");

		x=x.replaceAll("</result>","</"+ aliasHVar+">");
		return x;
	}
	
}
