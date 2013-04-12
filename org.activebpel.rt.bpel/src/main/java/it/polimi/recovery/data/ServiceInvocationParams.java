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

package it.polimi.recovery.data;

public class ServiceInvocationParams
{
	private String wsdlURL;
	private String operation;
	private String input;
	
	public ServiceInvocationParams()
	{
		// TODO Auto-generated constructor stub
	}
	public ServiceInvocationParams(String wsdlURL, String operation, String input)
	{
		this.wsdlURL = wsdlURL;
		this.operation = operation;
		this.input = input;
	}
	public String getWsdlURL()
	{
		return wsdlURL;
	}
	public void setWsdlURL(String wsdlURL)
	{
		this.wsdlURL = wsdlURL;
	}
	public String getOperation()
	{
		return operation;
	}
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
	public String getInput()
	{
		return input;
	}
	public void setInput(String input)
	{
		this.input = input;
	}
	
	public ServiceInvocationParams clone()
	{
		return new ServiceInvocationParams(this.wsdlURL,
											this.operation,
											this.input);		
	}
}