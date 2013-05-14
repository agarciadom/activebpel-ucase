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

package it.polimi.recovery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser
{
	DocumentBuilder doc;
	XPath xpath;
	Document document;
	
	DocumentWrapper saxDoc;
	
	public XMLParser()
	{
		// TODO Auto-generated constructor stub
		try
		{
			this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.xpath = XPathFactory.newInstance().newXPath();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void SetXML(String xml)
	{
		try
		{
			this.document = this.doc.parse(new ByteArrayInputStream(xml.getBytes()));
			
			saxDoc = new DocumentWrapper(this.document, null, new Configuration());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Vector<String> GetXPath(String root)
	{
		Vector<String> xpathList = new Vector<String>();

		try
		{
			NodeList nodeset = (NodeList) this.xpath.evaluate(root, this.document, XPathConstants.NODESET);
			
			if(nodeset != null)
			{
				for(int i = 0; i < nodeset.getLength(); i++)
				{
					Node node = nodeset.item(i);
					
					if(node.hasChildNodes())
					{
						Vector<String> result = this.getXPathNodeChildren(node);
						
						for(int l = 0; l < result.size(); l++)
						{
							xpathList.add(result.get(l));
						}
					}
					else
					{
						NodeWrapper nodeWrapper = this.saxDoc.wrap(node);
						System.out.println("XPath: " + net.sf.saxon.om.Navigator.getPath(nodeWrapper));
						xpathList.add(net.sf.saxon.om.Navigator.getPath(nodeWrapper));;
					}
				}
			}
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xpathList;
	}

	private Vector<String> getXPathNodeChildren(Node parent)
	{
		Vector<String> list = new Vector<String>();
		
		NodeList children = parent.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			
			if(child.hasChildNodes())
			{
				Vector<String> nodeChildren = this.getXPathNodeChildren(child);
				
				for(int l = 0; l < nodeChildren.size(); l++)
				{
					list.add(nodeChildren.get(l));
				}
			}
			else
			{
				if(child.getNodeType() == Node.TEXT_NODE)
					child = child.getParentNode();
				
				NodeWrapper node = this.saxDoc.wrap(child);
				String temp = net.sf.saxon.om.Navigator.getPath(node);
//				System.out.println("XPath: " + temp);
				list.add(temp);
			}
			
		}
		
		return list;
		
	}
	
	public String GetValue(String xpathAddress)
	{
//		System.out.println("XPath: " + xpathAddress);
		
		try
		{
			Node resultNode = (Node) this.xpath.evaluate(xpathAddress, this.document, XPathConstants.NODE);
			
			if(resultNode != null)
			{
				return resultNode.getTextContent();
			}
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void SetValue(String xpathAddress, String newValue)
	{
		try
		{
			Node resultNode = (Node) this.xpath.evaluate(xpathAddress, this.document, XPathConstants.NODE);
			
			if(resultNode != null)
			{
				Node parentNode = resultNode.getParentNode();
				System.out.println("Node: " + xpathAddress + " | newValue: " + newValue);
				Node newNode = this.document.createElement(resultNode.getNodeName());
				newNode.setTextContent(newValue);
				parentNode.removeChild(resultNode);
				parentNode.appendChild(newNode);
			}
			else
			{
				System.out.println("Node not found: " + xpathAddress);
			}
		}
		catch (XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetDocument()
	{
        Transformer transformer;
		try
		{
	        Source source = new DOMSource(this.document);
	        StringWriter stringWriter = new StringWriter();
	        Result result = new StreamResult(stringWriter);
	        TransformerFactory factory = TransformerFactory.newInstance();
			transformer = factory.newTransformer();
	        transformer.transform(source, result);
	        return stringWriter.getBuffer().toString();
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
