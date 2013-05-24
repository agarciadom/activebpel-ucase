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

package
it.polimi.exception;

import it.polimi.monitor.nodes.WSCoLFunction;

/**
 * Class for manage an error respect with a wrong number of parameter of a fuction. 
 * The possible function are listen in {@link WSCoLFunction} . 
 * 
 * @author Luca Galluppi
 *
 */
public class ParametersException extends WSCoLException {
	private int numOfParameters;
	private int numOfParametersRequested;
	private String function;
	private static final long serialVersionUID = 8230609496220493644L;
	/**
	 * Costruct a new ParametersException.
	 * 
	 * @param message A message of error.
	 * @param function The name of the fuction.
	 * @param numOfParameters The number of parameters gived to the fuction.
	 * @param numOfParametersRequested The number of parameters required by the fuction.
	 */
	public ParametersException(String message,String function,  int numOfParameters, int numOfParametersRequested) {
		super(message);
		this.function=function;
		this.numOfParameters=numOfParameters;
		this.numOfParametersRequested=numOfParametersRequested;
	}

	
	@Override
	public String toString() {
		return  this.getMessage()+" function: "+this.function+" number of parameters found "+numOfParameters+" requested "+numOfParametersRequested;
	}
	

}
