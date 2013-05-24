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
 * Class for manage with an error in invoking the external service for interact with Historical
 * Variable.
 * 
 * @author Luca Galluppi
 *
 */
public class HVarSericeException extends WSCoLException {
	
	private static final long serialVersionUID = -7357403543476466946L;

	/**
	 * Costruct a new HVarSericeException with the errror message.
	 * @param message The error.
	 */
	public HVarSericeException(String message) {
		super(message);
	}
}
