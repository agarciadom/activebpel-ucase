
/* Copyright 2007, 2008 , DEEP SE group, Dipartimento di Elettronica e Informazione (DEI), Politecnico di Milano */


/*  
 *  Licence: 
 *
 *
 *  This file is part of  DYNAMO .
 *
 *
 *	DYNAMO is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DYNAMO is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DYNAMO.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
 
 
 
package org.activebpel.rt.bpel.impl.monitoring.wscolinterpreter;

import java.util.*;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;

public class WSCoLFinder {
	public final String FORALL="Forall";
	public final String EXIST="Exists";
	public final String AVG="Avg";
	public final String SUM="Sum";
	public final String MIN="Min";
	public final String MAX="Max";
	public final String PRODUCT="Product";
	Vector<WSCoLBPEL_VAR> variables = new Vector<WSCoLBPEL_VAR>();

	String xpath = "";

	public Vector<WSCoLBPEL_VAR> findAllBPELVar(AST tree) {

		recursiveTrip(tree);
		Vector<WSCoLBPEL_VAR> copy;
		copy = (Vector<WSCoLBPEL_VAR>) variables.clone();
		variables.clear();
		return copy;

	}

	private void recursiveTrip(AST tree) {
		if (tree != null) {
			//controllo che non ci sia un operatore aggregato o esistenziale
			if (tree.toString() == FORALL || 
					tree.toString() == EXIST || 
					tree.toString() == AVG ||
					tree.toString() == SUM ||
					tree.toString() == MAX ||
					tree.toString() == MIN ||
					tree.toString() == PRODUCT) {
				tree= tree.getFirstChild().getFirstChild().getNextSibling().getFirstChild().getFirstChild();
				String nome_var = tree.toString();
				//xpath = "";
				WSCoLBPEL_VAR variable = new WSCoLBPEL_VAR();
				variable.setComplexOp(true);
				variable.setVar(nome_var);
				//variable.setXPath(xpath);
				if(!this.isDuplicate(variable))
				{
					variables.add(variable);
				}
				//else
				//	System.out.println("Duplicate variable not inserted");
				return;
			}
			if (tree.toString() == "BPEL_VAR") {
				String nome_var = tree.getFirstChild().getFirstChild()
						.toString();
				xpath = "";
				
				AST xpathNode = tree.getFirstChild().getNextSibling();
			
				if(xpathNode != null)
				{
					findXpath(xpathNode.getFirstChild().getFirstChild());
				//	System.out.println("Xpath variabile = " + xpath);
					WSCoLBPEL_VAR variable = new WSCoLBPEL_VAR();
					variable.setVar(nome_var);
					variable.setXPath(xpath);
					variable.setComplexOp(false);
					if(!this.isDuplicate(variable))
					{
						variables.add(variable);
					}
					//else
					//	System.out.println("Duplicate variable not inserted");
				}
			}
			if (tree.getNextSibling() != null) {
				recursiveTrip(tree.getNextSibling());
			}
			if (tree.getFirstChild() != null) {
				recursiveTrip(tree.getFirstChild());
			}
		}

	}

	private void findXpath(AST tree) {
		if(tree.getFirstChild()!=null){
			findXpath(tree.getFirstChild());
		}
		
		xpath = xpath + tree.toString();
		if (tree.getNextSibling() != null) {
			if (tree.getNextSibling().toString().charAt(0) == ':') {
				xpath = xpath + tree.getNextSibling().toString()
						+ tree.getNextSibling().getNextSibling().toString();
			}
		}
		if(tree.getFirstChild()!=null){
			if(tree.getFirstChild().getNextSibling()!=null){
				//Delete namespace prefixes from the rule
//				if(tree.getFirstChild().getNextSibling().toString().charAt(0)==  ':'){
//					xpath = xpath + tree.getFirstChild().getNextSibling().getNextSibling().getNextSibling().toString();
//					if(tree.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()!=null){
//						xpath = xpath + tree.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().toString() + tree.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().toString();
//					}
//				}else{
					xpath = xpath + tree.getFirstChild().getNextSibling().toString();
					if(tree.getFirstChild().getNextSibling().getNextSibling()!=null){
						xpath = xpath + tree.getFirstChild().getNextSibling().getNextSibling().toString() + tree.getFirstChild().getNextSibling().getNextSibling().getNextSibling().toString();
//					}
				}
			}
		}
	}
	
	private boolean isDuplicate(WSCoLBPEL_VAR variable)
	{
		for(int i = 0; i < this.variables.size(); i++)
		{
			WSCoLBPEL_VAR temp = this.variables.elementAt(i);
			
			if(temp.equals(variable))
				return true;
		}
		
		return false;
	}
}

