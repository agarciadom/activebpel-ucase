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

package it.polimi.monitor.invoker.data;

import java.util.Iterator;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;

import org.w3c.dom.NodeList;

public class OutXMLVariable
{
    private String openAngularBracket = "<"; //"&lt;";
    private String closeAngularBracket = ">"; //"&gt;";

    public OutXMLVariable()
	{
		
	}
	
	public String GetXMLVariable(SOAPBody soapBody)
	{
		String result = null;
		
		if(soapBody.hasChildNodes())
		{
			Iterator iterator = soapBody.getChildElements();
			
			Node responseNode = (Node) iterator.next();
			
			if(responseNode.hasChildNodes())
			{
				result = openAngularBracket + "Response" + closeAngularBracket;
				
				NodeList list = responseNode.getChildNodes();
				int l = 0;
				
				while((list.getLength() > 0) && (l < list.getLength()/* < 2*/))
				{
					Node returnVariable = (Node) list.item(l++);
					
//					result += openAngularBracket + returnVariable.getNodeName() + closeAngularBracket;
					result += openAngularBracket + returnVariable.getLocalName() + closeAngularBracket;
					
					if(returnVariable.hasChildNodes())
					{
						NodeList variableParts = returnVariable.getChildNodes();
						
						Node temp;
							
						for(int i = 0; i < variableParts.getLength(); i++)
						{
							temp = (Node) variableParts.item(i);

							if(temp.getNodeType() == 1)
							{
//								result += openAngularBracket + temp.getNodeName() + closeAngularBracket;
								result += openAngularBracket + temp.getLocalName() + closeAngularBracket;
							}
							
							if(temp.hasChildNodes())
							{
//								System.out.println("Node type: " + temp.getNodeType());
								result += this.GetXMLComplexTypes(temp);
							}
							else
							{
								result += temp.getTextContent();
							}

							if(temp.getNodeType() == 1)
							{
//								result += openAngularBracket + "/" + temp.getNodeName() + closeAngularBracket;
								result += openAngularBracket + "/" + temp.getLocalName() + closeAngularBracket;
							}
						}
					}

//					result += openAngularBracket + "/" + returnVariable.getNodeName() + closeAngularBracket;
					result += openAngularBracket + "/" + returnVariable.getLocalName() + closeAngularBracket;
				}
				
				result += openAngularBracket + "/Response" + closeAngularBracket;
			}
		}
		
		return result;
	}
	
	private String GetXMLComplexTypes(Node node)
	{
		String result = "";
		
		if(node.hasChildNodes())
		{
			NodeList list = node.getChildNodes();

			Node temp;
			
			for(int i = 0; i < list.getLength(); i++)
			{
				temp = (Node) list.item(i);
				
				if(temp.getNodeType() == 1)
				{
//					result += openAngularBracket + temp.getNodeName() + closeAngularBracket;
					result += openAngularBracket + temp.getLocalName() + closeAngularBracket;
				}
				
				if(temp.hasChildNodes())
				{
					result += this.GetXMLComplexTypes(temp);
				}
				else
				{
//					System.out.println("Node type: " + temp.getNodeType());
					result += temp.getTextContent();
				}

				if(temp.getNodeType() == 1)
				{
//					result += openAngularBracket + "/" + temp.getNodeName() + closeAngularBracket;
					result += openAngularBracket + "/" + temp.getLocalName() + closeAngularBracket;
				}
			}
		}

//		System.out.println("Result ricorsivo: " + result);
		return result;
	}
}
