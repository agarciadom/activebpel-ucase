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
 * Class for describe an error in extract data form an xml file.
 * 
 * @author Luca Galluppi
 *
 */
public class DataException extends WSCoLException {

	private static final long serialVersionUID = -4579739463967995126L;
	private String data=null;
	private String xpath=null;
	/**
	 * Costruct a new DataException.
	 * @param message An error message.
	 * @param data The set of data.
	 * @param xpath The xpath not present.
	 */
	public DataException(String message,String data, String xpath) {
		super(message);
		this.data=data;
		this.xpath=xpath;
	}
	/**
	 * Get the set of data
	 * @return the data.
	 */
	public String getData() {
		return data;
	}
	
	/**
	 * Get the xpath.
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}
	
	@Override
	public String toString() {
		return this.getMessage()+"\nData: "+data+"\nXpath: "+xpath;
	}
	
	
}
