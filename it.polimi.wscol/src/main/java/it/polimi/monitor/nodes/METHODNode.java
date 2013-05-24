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

import java.util.Vector;

import it.polimi.exception.ParametersException;
import it.polimi.exception.WSCoLCastException;
import it.polimi.exception.WSCoLException;
import it.polimi.exception.UnknownFunctionException;
import it.polimi.monitor.InputMonitor;

public class METHODNode extends NodeWSCoL implements WSCoLFunction {
	private Variable var=null;
	private SimpleAST function=null;
	private PARAM_LISTNode parameters=null;
	private Vector<NodeWSCoL> parm=null;
	private int fun=-1;
	private String func=null;
	
	private static final long serialVersionUID = 1100402159775651101L;


	/**
	 * 
	 */
	public METHODNode() {
		serializeTag="method";
	}

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases , AliasNodes tempAliases ) throws WSCoLException {
		//carico i figli
		var=(Variable)this.getFirstChild();
		function=(SimpleAST)var.getNextSibling();
		parameters=(PARAM_LISTNode)function.getNextSibling();
		//valuto i parametri
		var.evaluate(inputMonitor, aliases, tempAliases);
		function.evaluate(inputMonitor, aliases, tempAliases);
		parameters.evaluate(inputMonitor, aliases, tempAliases);
		
		//determino la funzione
		func = function.getMonitoringValue();
		fun = determineFunction(func);
		parm=parameters.getMonitoringValue();
	}
	
	@Override
	public Object getMonitoringValue() throws WSCoLException {
		switch (fun) {
			case LENGTH: 
				return String.valueOf(var.getMonitoringValue().toString().length());
			case COMPARE:
				if (parm.size()!=1)
					throw new ParametersException("A few parameters found:",func,parm.size(),1);
				if (var.getMonitoringValue().equals(parm.firstElement().getMonitoringValue()))
					return new Boolean(true);
				else
					return new Boolean(false);
			case REPLACE:
				if (parm.size()!=2)
					throw new ParametersException("A few parameters found:",func,parm.size(),2);
				String s=(String)var.getMonitoringValue();
				return (s.replaceAll((String)parm.firstElement().getMonitoringValue(),(String)parm.elementAt(1).getMonitoringValue()));
			case SUBSTRING:
				if (parm.size()!=2)
					throw new ParametersException("A few parameters found:",func,parm.size(),2);
				int beginIndex=Integer.parseInt((String)parm.firstElement().getMonitoringValue());
				int endIndex=Integer.parseInt(((String)parm.elementAt(2).getMonitoringValue()));
				return ((String)var.getMonitoringValue()).substring(beginIndex, endIndex);
			case CONTAINS:
				if (parm.size()!=1)
					throw new ParametersException("A few parameters found:",func,parm.size(),1);
				if (((String)var.getMonitoringValue()).contains((String)parm.firstElement().getMonitoringValue()))
					return new Boolean(true);
				else
					return new Boolean(false); 
			case STARTWITH:
				if (parm.size()!=1)
					throw new ParametersException("A few parameters found:",func,parm.size(),1);
				if (((String)var.getMonitoringValue()).startsWith((String)parm.firstElement().getMonitoringValue()))
					return new Boolean(true);
				else
					return new Boolean(false); 
			case ENDWITH: 
				if (parm.size()!=1)
					throw new ParametersException("A few parameters found:",func,parm.size(),1);
				if (((String)var.getMonitoringValue()).endsWith((String)parm.firstElement().getMonitoringValue()))
					return new Boolean(true);
				else
					return new Boolean(false); 
			case SERIALIZE:
				return var.serializeVar();
			case SIZE:
				return var.numberOfNode();
			case ABS: 
				return Math.abs(parseDouble(var.getMonitoringValue()));
			case FLOOR:
				return Math.floor(parseDouble(var.getMonitoringValue()));
			case ROUND:
				return Math.round(parseDouble(var.getMonitoringValue()));
			case CEILING:
				return Math.ceil(parseDouble(var.getMonitoringValue()));
			default: 
				throw new UnknownFunctionException("Function not know: "+ func);
		}
	}
	private int determineFunction(String s) throws UnknownFunctionException {
		if (s.equals(VALUE_LENGTH))
			return LENGTH;
		else if (s.equals(VALUE_COMPARE))
			return COMPARE;
		else if (s.equals(VALUE_REPLACE))
			return REPLACE;
		else if (s.equals(VALUE_SUBSTRING))
			return SUBSTRING;
		else if (s.equals(VALUE_CONTAINS ))
			return CONTAINS;
		else if (s.equals(VALUE_STARTWITH ))
			return STARTWITH;
		else if (s.equals(VALUE_ENDWITH))
			return ENDWITH;
		else if (s.equals(VALUE_SIZE))
			return SIZE;
		else if (s.equals(VALUE_SERIALIZE))
			return SERIALIZE;
		else if (s.equals(VALUE_ABS))
			return ABS;
		else if (s.equals(VALUE_FLOOR))
			return FLOOR;
		else if (s.equals(VALUE_CEILING))
			return CEILING;
		else if (s.equals(VALUE_ROUND))
			return ROUND;
		else 
			throw new UnknownFunctionException("Unknown function:" +s);
	}
	public String toString(){
		return "Method";
	}
	private Double parseDouble(Object o) throws WSCoLException{
		if (o instanceof Double) 
			return (Double)o;
		else if (o instanceof String)
			try {
				return Double.parseDouble((String)o);
			} catch (NumberFormatException e) {
				throw new WSCoLCastException("Can't parse a String as Double");
			}
		else if (o instanceof Boolean)
			throw new WSCoLCastException("Can't parse a Boolean as Double");
		else 
			throw new WSCoLCastException("Can't parse a "+ o.getClass() +" as Double");
	}
}
