<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWSLocator" %>
<%@ page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.rpc.ServiceException" %>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Supervision rules</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>

<%

String pID = request.getParameter("pID");
String uID = request.getParameter("uID");


ConfigurationManagerWSLocator locator = new ConfigurationManagerWSLocator();
ConfigurationManager cm = null;
SupervisionRuleInfoWrapper[] rules = null;


try {
	
	cm = locator.getConfigurationManagerPort();
	rules = cm.getProcessSupervisionRules(pID,uID); 
	
		
} catch (ServiceException e) {
out.println(e.getMessage());
} catch (RemoteException e) {
out.println(e.getMessage());
}

%>
Rules for process <b><%=pID %></b> and user <b><%=uID %></b>
<br>
<br>

<%

	for (int i=0; i < rules.length; i++) {
		
		SupervisionRuleInfoWrapper rule = rules[i];
		
		String location = rule.getLocation();
		int priority = rule.getPriority();
		String providers = rule.getProviders();
		String timeframe =rule.getTimeFrame();
		String monitoring = rule.getWscolRule();
		monitoring = monitoring.replaceAll("<","&lt;");
		monitoring = monitoring.replaceAll(">","&gt;");
		String recovery = rule.getRecoveryStrategy();
		recovery = recovery.replaceAll("<","&lt;");
		recovery=recovery.replaceAll(">","&gt;");
		boolean precondition = rule.isPrecondition();
		String type=null;
		if (precondition == false) {
			type = "post-condition";
		}
		else {
			type = "pre-condition";
		}
		
		%>
		<b>Rule numer <%=i+1 %></b>
		<br>
		<br>
		<table border=2>
		<tr><td><b>Location</b></td><td><%=location %></td></tr>
		<tr><td><b>Type</b></td><td><%=type %></td></tr>
		<tr><td><b>Priority</b></td><td><%=priority %></td></tr>
		<tr><td><b>Providers</b></td><td><%=providers %></td></tr>
		<tr><td><b>Timeframe</b></td><td><%=timeframe %></td></tr>
		<tr><td><b>Monitoring Expression</b></td><td><%=monitoring %></td></tr>
		<tr><td><b>Recovery Strategy</b></td><td><%=recovery %></td></tr>
		</table>
		<br>
		<a href="changeRule.jsp?pID=<%=pID %>&uID=<%=uID %>&loc=<%=location %>&pre=<%=precondition %>">Modify Rule</a><br>
		<a href="viewLogging.jsp?pID=<%=pID %>&uID=<%=uID %>&loc=<%=location %>&pre=<%=precondition %>">View Log</a>
		<br>
		<br>
		<br>
		<%
		
		
	}

%>

<a href="insertNewRule.jsp?pID=<%=pID %>&uID=<%=uID %>"><b>Insert a new rule</b></a>

</body>
</html>