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

package it.polimi.monitor;

import it.polimi.exception.InvalidInputMonitor;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is used for store and retrieve information that are necessary for monitoring a
 * WSCoL rule. It contanis xmlData of the current evaluation and information for provide Historical
 * Variable service.<br>
 * <br>
 * The xsd of configHvar.xsd is show here:
 * <br> <pre>
 * {@code <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">}<br> 
 * 		{@code <xsd:element name="webservice">}<br> 
 * 			{@code <xsd:complexType>}<br> 
 * 				{@code <xsd:sequence>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="wsdl" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="store_wm" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="retrieve_wm" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="processID" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="assertionType" type="xsd:int"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="1" name="location" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="0" name="userID" type="xsd:string"/>}<br> 
 * 					{@code <xsd:element maxOccurs="1" minOccurs="0" name="instanceID" type="xsd:long"/>}<br> 
 * 				{@code </xsd:sequence>}<br> 
 * 			{@code </xsd:complexType>}<br> 
 * 		{@code </xsd:element>}<br> 
 * {@code </xsd:schema>}<br> 
 * </pre>
 * @author Luca Galluppi
 *
 */
public class InputMonitor {
	private String data=null;
	private String configHvar=null;
	public static String DATA_XSD="<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
	"<xsd:element name=\"monitor_data\">" +
	"</xsd:element>" +
	"</xsd:schema>";
	public static String CONFIG_HVAR_XSD="<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
			"<xsd:element name=\"webservice\">" +
			"<xsd:complexType>" +
			"<xsd:sequence>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"wsdl\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"store_wm\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"retrieve_wm\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"processID\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"assertionType\" type=\"xsd:int\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"location\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"userID\" type=\"xsd:string\"/>" +
			"<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"instanceID\" type=\"xsd:long\"/>" +
			"</xsd:sequence>" +
			"</xsd:complexType>" +
			"</xsd:element>" +
			"</xsd:schema>";
	/**
	 * Costruct a new  object that will have the parameters provide to the costructor.
	 * 
	 * 
	 * @param xmlData String that reppresents the xmlData of a BPEL process where some WSCoL monitor are specified.
	 * The file is format in an XML file and all values are incapsulate in a <monitor_data> ... xmlData ... </monitor_data>. 
	 *
	 * @param configHvar String that reppresents the informations for configure a Historical Variable service, this 
	 * informations must be provide if a WSCoL rule use Historical variable feature.
	 * The file is format in an XML file and must be costruct with the schema of Conf_HVAR.xsd. 
	 * 
	 * @throws InvalidInputMonitor if String configHvar doesn't validate the schema of Conf_HVAR.xsd.
	 */
	public InputMonitor(String data, String configHvar) throws InvalidInputMonitor {
		this.data = data;
		if (!validateData())
			throw new InvalidInputMonitor("Data doesn't validate with:",DATA_XSD,data);
		this.configHvar = configHvar;
		if (!validateConfigHvar())
			throw new InvalidInputMonitor("Config Hvar doesn't validate with:",CONFIG_HVAR_XSD,configHvar);
	}
	/**
	 * Get method for receive a String where the information of Historical variable service are store.
	 * The String is format in an XML file and satisfied the XML schema Conf_HVAR.xsd . 
	 * 
	 * @return the configHvar
	 */
	public String getConfigHvar() {
		return configHvar;
	}
	/**
	 * Get method for receive a String where xmlData of a BPEL process are stored.
	 * The String is format in an XML file and all values are incapsulate in a <monitor_data> ... xmlData ... </monitor_data>.
	 * @return the xmlData
	 */
	public String getData() {
		return data;
	}
	
	private boolean validateConfigHvar() {
		//		 parse an XML document into a DOM tree
	    DocumentBuilder parser;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document=parser.parse( new InputSource(new StringReader(configHvar)));
			
			

			 // create a SchemaFactory capable of understanding WXS schemas
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		    // load a WXS schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(new StringReader(CONFIG_HVAR_XSD));
		    Schema schema = factory.newSchema(schemaFile);

		    // create a Validator instance, which can be used to validate an instance document
		    Validator validator = schema.newValidator();


	    // validate the DOM tree
	        validator.validate(new DOMSource(document));

	        return true;
	        
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean validateData() {
		//		 parse an XML document into a DOM tree
	    DocumentBuilder parser;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document=parser.parse( new InputSource(new StringReader(data)));
			
			

			 // create a SchemaFactory capable of understanding WXS schemas
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		    // load a WXS schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(new StringReader(DATA_XSD));
		    Schema schema = factory.newSchema(schemaFile);

		    // create a Validator instance, which can be used to validate an instance document
		    Validator validator = schema.newValidator();


	    // validate the DOM tree
	        validator.validate(new DOMSource(document));

	        return true;
	        
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}
	}
}
