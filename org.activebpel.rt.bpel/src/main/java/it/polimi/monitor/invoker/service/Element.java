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

package it.polimi.monitor.invoker.service;

public class Element
{
	private String elementName;
	private String elementTypeNamespacePrefix;
	private String elementType;
	
	public void SetName(String name)
	{
		elementName = name;
	}

	public void SetType(String type)
	{
		if(type.indexOf(":") > 0)
		{
			elementType = type.substring(type.indexOf(":") + 1);
			elementTypeNamespacePrefix = type.substring(0, type.indexOf(":"));
		}
		else
		{
			elementTypeNamespacePrefix = null;
			elementType = type;
		}
	}
	
	public String GetName()
	{
		return elementName;
	}
	
	public boolean hasNamespacePrefix()
	{
		if(elementTypeNamespacePrefix != null)
			return true;
		
		return false;
	}
	
	public String GetTypeNamespacePrefix()
	{
		return elementTypeNamespacePrefix;
	}
	
	public String GetType()
	{
		return elementType;
	}
}
