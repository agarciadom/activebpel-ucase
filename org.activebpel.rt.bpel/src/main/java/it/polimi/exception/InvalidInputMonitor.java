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

package it.polimi.exception;
/**
 * Class for manage with an error in validating the input of the WSCoL Monitor
 * 
 * @author Luca Galluppi
 *
 */
public class InvalidInputMonitor extends WSCoLException {

	private static final long serialVersionUID = 1499175017078895968L;
	private String input=null;
	private String xmlschema=null;
	/**
	 * Costruct a new InvalidInputMonitor with the parameters.
	 * @param message A message of error.
	 * @param xmlschema The schema that is not respect.
	 * @param input The input that not validate the schema.
	 */
	public InvalidInputMonitor(String message,String xmlschema,String input) {
		super(message);
		this.input=input;
		this.xmlschema=xmlschema;
	}

	
	@Override
	public String toString() {
		return this.getMessage()+"\n XSD:"+xmlschema+"\nInput: "+input;
	}
	
}
