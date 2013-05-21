<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.ProcessInfoWrapper" %>
<%@ page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.ws.WebServiceException" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Rule insertion</title>
</head>
<body>
<img src="figures/testa_demo_small.jpg"/>
<br>
<br>

<%

ConfigurationManagerWS locator = new ConfigurationManagerWS();
ConfigurationManager cm = null;

ProcessInfoWrapper pInfo = new ProcessInfoWrapper();
pInfo.setPriority(new Integer(3));
pInfo.setProcessId("PizzaDeliveryCompany");
pInfo.setUserId("luciano");

SupervisionRuleInfoWrapper ruleInfo = new SupervisionRuleInfoWrapper();
String location="/process/sequence/invoke[@name='InvokeGPS']";
boolean precondition = false;
int priority = 2;
String processID = "PizzaDeliveryCompany";
String recoveryStrategy1 = "{retry(1) or notify('Error in retreiving GPS coordinates!!','antonio@localhost') and halt()}";
recoveryStrategy1 = recoveryStrategy1.replaceAll("&lt;", "<");
recoveryStrategy1 = recoveryStrategy1.replaceAll("&gt;", ">");
String timeFrame = "always";
String userID="luciano";
String wscolRule1 = "($GPSService_getCoordResponse/result/easting).length()==7 && ($GPSService_getCoordResponse/result/easting).endsWith('E');";
wscolRule1 = wscolRule1.replaceAll("&lt;", "<");
wscolRule1 = wscolRule1.replaceAll("&gt;", ">");

ruleInfo.setLocation(location);
ruleInfo.setPrecondition(precondition);		
ruleInfo.setPriority(priority);		
ruleInfo.setProcessID(processID);			
ruleInfo.setRecoveryStrategy(recoveryStrategy1);		
ruleInfo.setTimeFrame(timeFrame);	
ruleInfo.setUserID(userID);	
ruleInfo.setWscolRule(wscolRule1);	


boolean result = false;

String wscolRule2 = new String();
String recoveryStrategy2 = new String();
try {
	
	cm = locator.getConfigurationManagerPort();
	
	result = cm.insertNewProcess(pInfo);
	
	result = cm.insertNewSupervisionRule(ruleInfo);
	
	location="/process/sequence/invoke[@name='InvokeMap']";
	precondition = false;
	priority = 4;
	processID = "PizzaDeliveryCompany";
	String providers = "MagicMap";
	recoveryStrategy2 = "if($hRes &lt; 180;){ignore()}else{notify('Could not load a map with a suitable size','antonio@localhost') and halt()}";
	recoveryStrategy2 = recoveryStrategy2.replaceAll("&lt;", "<");
	recoveryStrategy2 = recoveryStrategy2.replaceAll("&gt;", ">");
	timeFrame = "every 1 hour";
	userID="luciano";
	wscolRule2 = "let $hRes = returnNum('http://127.0.0.1:8080/ImageVerifierServiceBeanService/ImageVerifierServiceBean?wsdl','getHRes','&lt;InvokeServiceParameters&gt;&lt;imageURL&gt;' + $MapService_getMapResponse/result + '&lt;/imageURL&gt;&lt;/InvokeServiceParameters&gt;',/Response/result);$hRes &lt;= 150;";
	wscolRule2 = wscolRule2.replaceAll("&lt;", "<");
	wscolRule2 = wscolRule2.replaceAll("&gt;", ">");

	ruleInfo.setLocation(location);
	ruleInfo.setPrecondition(precondition);		
	ruleInfo.setPriority(priority);		
	ruleInfo.setProcessID(processID);	
	ruleInfo.setProviders(providers);
	ruleInfo.setRecoveryStrategy(recoveryStrategy2);		
	ruleInfo.setTimeFrame(timeFrame);	
	ruleInfo.setUserID(userID);	
	ruleInfo.setWscolRule(wscolRule2);
	
	result = cm.insertNewSupervisionRule(ruleInfo);

	
	
} catch (WebServiceException e) {
  out.println(e.getMessage());
}
%>

<b>The following rules have been inserted correctly!</b>

<br>
<br>

<b>Rule number 1:</b>
<br>
<br>
<table border=2>

<tr>
<td><b>Location</b></td>
<td>/process/sequence/invoke[@name='InvokeMap']</td>
</tr>
<tr>
<td><b>Supervision priority</b></td>
<td>4</td>
</tr>
<tr>
<td><b>Monitoring rule</b></td>
<td><%=wscolRule2 %></td>
</tr>
<tr>
<td><b>Recovery rule</b></td>
<td><%=recoveryStrategy2 %>
</td>
</tr>
</table>
<br><br>

<b>Rule number 2:</b>
<br>
<br>

<table border=2>
<tr>
<td><b>Location</b></td>
<td>/process/sequence/invoke[@name='InvokeGPS']</td>
</tr>
<tr>
<td><b>Supervision priority</b></td>
<td>2</td>
</tr>
<tr>
<td><b>Monitoring rule</b></td>
<td><%=wscolRule1%>
</tr>
<tr>
<td><b>Recovery rule</b></td>
<td><%=recoveryStrategy2 %>
</td>
</tr>
</table>
<br><br>

</body>
</html>