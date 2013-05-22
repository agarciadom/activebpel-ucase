<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.xml.ws.WebServiceException" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
		<title>Supervision rules</title>
	</head>
	<body>
		<img src="figures/testa_dynamo_small.jpg"/>
<%
final String pID = request.getParameter("pID");
final String uID = request.getParameter("uID");

ConfigurationManagerWS locator = new ConfigurationManagerWS();
ConfigurationManager cm = null;
List<SupervisionRuleInfoWrapper> rules = null;
try {
	cm = locator.getConfigurationManagerPort();
	rules = cm.getProcessSupervisionRules(pID,uID).getItem(); 
} catch (WebServiceException e) {
	out.println(e.getMessage());
}

%>
<h2>Rules for process <b><%=pID %></b> and user <b><%=uID %></b></h2>

<%
	int iRuleCounter = 0;
	for (SupervisionRuleInfoWrapper rule : rules) {
		final String location = rule.getLocation();
		final int priority = rule.getPriority();
		final String providers = rule.getProviders();
		final String timeframe =rule.getTimeFrame();

		String monitoring = rule.getWscolRule();
		monitoring = monitoring.replaceAll("<","&lt;");
		monitoring = monitoring.replaceAll(">","&gt;");

		String recovery = rule.getRecoveryStrategy();
		recovery = recovery.replaceAll("<","&lt;");
		recovery = recovery.replaceAll(">","&gt;");

		boolean precondition = rule.isPrecondition();
		String type=null;
		if (precondition == false) {
			type = "post-condition";
		}
		else {
			type = "pre-condition";
		}
		
		%>
		<h3>Rule number <%=iRuleCounter + 1 %></b></h3>
		<br>
		<br>
		<table border=2>
		<tr><td><b>Location</b></td><td><%=location %></td></tr>
		<tr><td><b>Type</b></td><td><%=type %></td></tr>
		<tr><td><b>Priority</b></td><td><%=priority %></td></tr>
		<tr><td><b>Providers</b></td><td><%=providers %></td></tr>
		<tr><td><b>Time frame</b></td><td><%=timeframe %></td></tr>
		<tr><td><b>Monitoring Expression</b></td><td><%=monitoring %></td></tr>
		<tr><td><b>Recovery Strategy</b></td><td><%=recovery %></td></tr>
		</table>
		<ul style="list-style: none">
			<li><a href="changeRule.jsp?pID=<%=pID%>&uID=<%=uID%>&loc=<%=location%>&precondition=<%=precondition%>">Modify</a></li>
			<li><a href="removeRule.jsp?pID=<%=pID%>&uID=<%=uID%>&loc=<%=location%>&precondition=<%=precondition%>">Remove</a></li>
			<li><a href="viewLogging.jsp?pID=<%=pID%>&uID=<%=uID%>&loc=<%=location%>&precondition=<%=precondition%>">Log</a></li>
		</ul>
		<br>
		<br>
		<br>
<%
		iRuleCounter = iRuleCounter + 1;
	}
%>

<a href="insertNewRule.jsp?pID=<%=pID %>&uID=<%=uID %>"><b>Insert a new rule</b></a>

</body>
</html>