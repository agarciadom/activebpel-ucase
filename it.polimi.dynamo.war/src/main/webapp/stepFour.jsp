<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.ws.WebServiceException" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change the supervision rule</title>
</head>
<body>
<img src="figures/testa_demo_small.jpg"/>
<br>
<br>

<b>The following change to the supervision rules have been made.</b>
<br>
<br>

<%

ConfigurationManagerWS locator = new ConfigurationManagerWS();
ConfigurationManager cm = null;

SupervisionRuleInfoWrapper ruleInfo = new SupervisionRuleInfoWrapper();

String location="/process/sequence/invoke[@name='InvokeMap']";
boolean precondition = false;
int priority = 4;
String processID = "PizzaDeliveryCompany";
String providers = "MagicMap";
String recoveryStrategy = "if($hRes &lt; 180;){ignore()}else{change_supervision_rules(\"let $hRes = returnNum('http://127.0.0.1:8080/ImageVerifierServiceBeanService/ImageVerifierServiceBean?wsdl','getHRes','&lt;InvokeServiceParameters&gt;&lt;imageURL&gt;' + $MapService_getMapResponse/result + '&lt;/imageURL&gt;&lt;/InvokeServiceParameters&gt;',/Response/result);$hRes &lt;= 750;\",\"{ignore() and notify('The map is still too big.','antonio@localhost')}\",'permanent')}";
recoveryStrategy = recoveryStrategy.replaceAll("&lt;", "<");
recoveryStrategy = recoveryStrategy.replaceAll("&gt;", ">");
String timeFrame = "every 1 hour";
String userID="luciano";
String wscolRule = "let $hRes = returnNum('http://127.0.0.1:8080/ImageVerifierServiceBeanService/ImageVerifierServiceBean?wsdl','getHRes','&lt;InvokeServiceParameters&gt;&lt;imageURL&gt;' + $MapService_getMapResponse/result + '&lt;/imageURL&gt;&lt;/InvokeServiceParameters&gt;',/Response/result);$hRes &lt;= 150;";
wscolRule = wscolRule.replaceAll("&lt;", "<");
wscolRule = wscolRule.replaceAll("&gt;", ">");

ruleInfo.setLocation(location);
ruleInfo.setPrecondition(precondition);		
ruleInfo.setPriority(priority);		
ruleInfo.setProcessID(processID);
ruleInfo.setProviders(providers);
ruleInfo.setRecoveryStrategy(recoveryStrategy);		
ruleInfo.setTimeFrame(timeFrame);	
ruleInfo.setUserID(userID);	
ruleInfo.setWscolRule(wscolRule);	

try {
	cm = locator.getConfigurationManagerPort();
	cm.changeSupervisionRuleParams(ruleInfo);
} catch (WebServiceException e) {
	out.println(e.getMessage());
}

%>
<br>
<b>Original rule number 1:</b>
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
<td>l<%=wscolRule %></td>
</tr>
<tr>
<td><b>Recovery rule</b></td>
<td>if($hRes &lt; 180;){ignore()}else{rebind('http://127.0.0.1:8080/MapServiceBackUpBeanService/MapServiceBackUpBean?wsdl') and notify('During recovery Dynamo had to rebind to a backup service.','antonio@localhost') or notify('Could not find a map with a suitable size','antonio@localhost') and halt()}
</td>
</tr>
</table>
<br><br>
<b>New rule number 1:</b>
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
<td><%=wscolRule%></td>
</tr>
<tr>
<td><b>Recovery rule</b></td>
<td><%=recoveryStrategy %></td>
</tr>
</table>



</body>
</html>