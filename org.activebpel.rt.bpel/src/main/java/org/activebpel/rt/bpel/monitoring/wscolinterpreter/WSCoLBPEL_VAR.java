
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

public class WSCoLBPEL_VAR {
	
	private String var;
	private String xpath;
	private String value;
	private boolean complexOp;

	public void setVar(String var){
		this.var=var;
	}
	
	public void setXPath(String xpath){
		this.xpath=xpath;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getVar(){
		return var;
	}
	
	public String getXPath(){
		return xpath;
	}
	
	public String getValue(){
		return value;
	}
	
	public boolean isComplexOp() {
		return complexOp;
	}

	public void setComplexOp(boolean complexOp) {
		this.complexOp = complexOp;
	}
	
	public String toString(){
		return "nome variabile = " + var + " xpath = " + xpath + " valore = " + value + " è una operazione complessa "+ complexOp;
	}

	public boolean equals(WSCoLBPEL_VAR variable)
	{
		if(!this.var.equals(variable.var))
			return false;
		if(!this.xpath.equals(variable.xpath))
			return false;
//		if(!this.value.equals(variable.value))
//			return false;
		
		return true;
	}
}
