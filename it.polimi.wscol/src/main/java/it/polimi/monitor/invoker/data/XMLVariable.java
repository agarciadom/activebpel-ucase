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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLVariable
{
        private String variableValue;

        private XPath xpath;

        private Document document;

        public XMLVariable(String value)
        {
                try
                {
                        this.variableValue = value;

                        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        this.document = builder.parse(new ByteArrayInputStream(value.getBytes()));

                        this.xpath = XPathFactory.newInstance().newXPath();
                }
                catch (ParserConfigurationException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (SAXException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (IOException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        public String GetValue(String XPath)
        {
                if(XPath.equals("") || (XPath == null))
                        return null;
                System.err.println(XPath);
                try
                {
                        Node node = (Node) xpath.evaluate(XPath, this.document, XPathConstants.NODE);

                        if(node == null)
                        {
                                System.err.println("'param' is null!!");
                        }
                        else
                        {
                                return node.getTextContent();
                        }
                }
                catch (XPathExpressionException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                return null;
        }
}
