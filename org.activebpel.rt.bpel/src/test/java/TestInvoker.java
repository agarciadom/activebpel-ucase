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

import it.polimi.monitor.invoker.Invoker;


public class TestInvoker
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String input = "<InvokeServiceParameters><imageURL>http://localhost:8080/DemoImages/images/im001.jpg</imageURL></InvokeServiceParameters>";
		
		input = "<InvokeServiceParameters><processID>test1</processID>" +
         		"<userID>bubi</userID>" +
         		"<instanceID>2</instanceID>" +
         		"<location>milano</location>" +
         		"<assertionType>pre</assertionType>" +
         		"<aliasName>azzz</aliasName>" +
         		"<value>cippirimerlo</value></InvokeServiceParameters>";
		
		input = "<InvokeServiceParameters><createHistoricalVariable><processID>process</processID><userID>luca</userID><instanceID>1233</instanceID><location>pippo</location><assertionType>0</assertionType><aliasName>a</aliasName><value>pippo</value></createHistoricalVariable></InvokeServiceParameters>";
//		long start = System.currentTimeMillis();
		
		input = "<InvokeServiceParameters><utmCoordinates><easting></easting><northing>5.9879N</northing><zone>4</zone></utmCoordinates></InvokeServiceParameters>";
		
		Invoker invoker = new Invoker();
		
//		System.out.println(invoker.Invoke("http://localhost:8080/ImageVerifierService/ImageVerifierServiceBean?wsdl", "getHRes", input));
//		System.out.println(invoker.Invoke("http://localhost:8080/historicalVariable/HistoricalVariableBeans?wsdl", "createHistoricalVariable", input));
//		System.out.println(invoker.Invoke("http://localhost:8080/HistoricalVariable/HistoricalVariableBeans?wsdl", "createHistoricalVariable", input));
		System.out.println(invoker.Invoke("http://localhost:8080/MapService/MapServiceBean?wsdl", "getMap", input));

//		long stop = System.currentTimeMillis();
//		System.out.println("Start: " + start + " | Stop: " + stop + " | time to complete: " + (stop - start));
	}

}
