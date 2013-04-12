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

package it.polimi.monitor;

import it.polimi.exception.DuplicateIdentifierException;
import it.polimi.exception.InvalidInputMonitor;
import it.polimi.exception.WSCoLException;
import it.polimi.monitor.nodes.AliasNodes;
import it.polimi.monitor.nodes.Aliases;
import it.polimi.monitor.nodes.NodeWSCoL;
import it.polimi.WSCoL.WSCoLLexer;
import it.polimi.WSCoL.WSCoLLexerTokenTypes;
import it.polimi.WSCoL.WSCoLParser;


import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
/**
 * This class can use for monitoring a WSCoL rules. This receive a rules, a set of data
 * and information for interact with historical variable. It can evaluate the monitoring
 * value through a {@link ResultMonitor}. Can give the aliases define in the rules.
 * 
 * @author Luca Galluppi
 *
 */
public class Monitor implements Monitoring {
	private Logger logger=Logger.getLogger("Monitor WSCoL");
	private Level defaultLoggerLevel=Level.OFF;
	private ResultMonitor resultMonitor=null;
	private AST rules=null;
	//strutture per memoria temporanea
	private Aliases aliases = null;
	private AliasNodes aliasNodes = null;
	private InputMonitor inputMonitor=null;
	
	/**
	 * Costruct a new Monitor.
	 *
	 */
	public Monitor() {
		logger.setLevel(defaultLoggerLevel);
		aliases = new Aliases();
		aliasNodes = new AliasNodes();
		resultMonitor=new ResultMonitor();
		
	}
	/**
	 * Costruct a new Monitor with {@link Aliases} and {@link AliasNodes} already define.
	 * @param aliases A list of aliases.
	 * @param aliasNodes  A list of aliasNodes.
	 */
	public Monitor(Aliases aliases, AliasNodes aliasNodes) {
		this.aliases = aliases;
		this.aliasNodes = aliasNodes;
		resultMonitor=new ResultMonitor();
	}


	/**
	 * Get the list of aliases define in the rules.
	 * @return the aliases
	 */
	public Aliases getAliases() {
		return aliases;
	}


	/**
	 * Get the list of aliasNodes define in the rules.
	 * @return the aliasNodes
	 */
	public AliasNodes getAliasNodes() {
		return aliasNodes;
	}


	
	
	/**
	 * Set the level of the logging of all the monitor operation.
	 * @param l The level to set.
	 */	
	public void setLevelLogger(Level l){
		logger.setLevel(l);
	}
	
	
	private boolean parseRules(String rules){
		logger.info("Start parse rules with wscol grammar");
		if (rules == null) {
			resultMonitor.addMessage("Not found rules. Terminate monitoring with false.");
			resultMonitor.addValue(new Boolean(false));
			logger.severe("Not found rules.");
			return false; 
		}
		
		Reader input = new StringReader(rules);
		WSCoLLexer lexer = new WSCoLLexer(input);
		WSCoLParser parser = new WSCoLParser(lexer);
		try {
        	parser.analyzer();
		} catch (DuplicateIdentifierException e) {
			resultMonitor.addMessage(e.toString());
			resultMonitor.addValue(new Boolean(false));
			logger.severe(e.toString());
			return false; 
		} catch (RecognitionException e) {
			resultMonitor.addMessage(e.toString());
			resultMonitor.addValue(new Boolean(false));
			logger.severe(e.toString());
			return false; 
		} catch (TokenStreamException e) {
			resultMonitor.addMessage(e.toString());
			resultMonitor.addValue(new Boolean(false));
			logger.severe(e.toString());
			return false; 
		} 
//      don't show the grammar tree
//		
//		ASTFrame frame = new ASTFrame("The tree",parser.getAST() );
//		frame.setVisible(true);
		
		this.rules=parser.getAST();
		logger.info("Finish parse rules with wscol grammar");
		return true; 
	}
	/**
	 * Calculate monitoring value based on the parameters. If rules are more than one 
	 * than the AND logical operation is calculate over them.
	 *  
	 * @param rules A {@link String} with the rules.
	 * @param data The data of the process.
	 * @param configHvar The data for interact with historical variable.
	 * @return calculated AND logical operation over rules. 
	 */
	public ResultMonitor evaluateMonitoring(String rules, String data, String configHvar){
		logger.info("Start WSCoL monitor");
		if (parseRules(rules) == false)
			return resultMonitor;
		if (parseInputMonitor(data, configHvar) == false)
			return resultMonitor;
		evaluate();
		logger.info("Finish WSCoL monitor, result: "+resultMonitor.getValueMonitor());
		return resultMonitor;
	}
	
	public ResultMonitor evaluateRulesTree(AST rules, String data, String configHvar) {
		logger.info("Start WSCoL monitor");
		this.rules = rules;
		if (parseInputMonitor(data, configHvar) == false)
			return resultMonitor;
		evaluate();
		logger.info("Finish WSCoL monitor, result: "+resultMonitor.getValueMonitor());
		
		return resultMonitor;
	}
	
	private boolean parseInputMonitor(String data, String configHvar){
		logger.info("Start analize data input");
		try {
			inputMonitor=new InputMonitor(data,configHvar);
			logger.info("Finish analize data input");
			return true;
		} catch (InvalidInputMonitor e) {
			resultMonitor.addMessage(e.toString());
			resultMonitor.addValue(new Boolean(false));
			logger.severe(e.toString());
			return false;
		}
	}
	
	private boolean evaluate() {
		logger.info("Start evaluation monitor");
		if (rules.getType() != WSCoLLexerTokenTypes.RULES) {
			resultMonitor.addMessage("Error: not define correct root of grammar");
			resultMonitor.addValue(new Boolean(false));
			logger.severe("Error: not define correct root of grammar");
			return false;
		}
		//		aggiungo i vari termini
		
		logger.fine("The rules are " +rules.getNumberOfChildren());
		
		Vector<NodeWSCoL> nodesWSCoL=new Vector<NodeWSCoL>();
		AST temp=null;
		for(int i=0; i<rules.getNumberOfChildren();i++) {
			if(i==0){
				// la prima regola
        		NodeWSCoL rule=(NodeWSCoL)(temp=rules.getFirstChild());
        		rule.setResultMonitor(resultMonitor);
        		nodesWSCoL.add(rule);
        	}        		
    		else {
    			//le altre regole
    			NodeWSCoL rule=(NodeWSCoL)(temp=temp.getNextSibling());
    			rule.setResultMonitor(resultMonitor);
        		nodesWSCoL.add(rule);
    		}
        }
		logger.info("Evaluate single rule");
		for ( NodeWSCoL i : nodesWSCoL ) {
			try {
				i.evaluate(inputMonitor,aliases, aliasNodes);
				Object result=i.getMonitoringValue();
				i.calculateResultMonitor();
				logger.info("Result single rule: "+result.toString());
			} catch (WSCoLException e) {
				resultMonitor.addMessage(e.toString());
				resultMonitor.addValue(new Boolean(false));
				logger.severe(e.toString());
				return false;
			}
		}
		logger.info("Finish evaluation monitor");
		return true;
	}
}

