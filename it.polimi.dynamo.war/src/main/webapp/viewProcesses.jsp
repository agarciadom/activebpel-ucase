<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.ProcessInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.xml.ws.WebServiceException" %>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Supervised processes</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>

<%

ConfigurationManagerWS locator = new ConfigurationManagerWS();
ConfigurationManager cm = null;
List<ProcessInfoWrapper> processes = null;

try {
	cm = locator.getConfigurationManagerPort();
	processes = cm.getMonitoredProcesses().getItem();
} catch (WebServiceException e) {
	out.println(e.getMessage());
}

%>

<b>Currently supervised processes</b>
<br>
<br>

<table border=2>
<tr>
	<td><b>Process ID</b></td>
	<td><b>User ID</b></td>
	<td><b>Priority</b></td>
	<td><b>View rules</b></td>
	<td><b>Save changes</b></td>
	</tr>

<%
for (ProcessInfoWrapper pInfo : processes) {
	final String pID = pInfo.getProcessId();
	final Long iID = pInfo.getProcessInstanceId();
	final String uID = pInfo.getUserId();
	final Integer priority = pInfo.getPriority();
%>
	<tr>
	<form method=post action="changeProcess.jsp" target="_blank">
	<td><input type=text name=pID size=20 value = "<%=pID %>"></td>
	<td><input type=text name = uID size=20 value = "<%=uID %>"></td>
	<td><select name = priority>
<% for (int i = 1; i <= 5; i++) { %>
		<option value="<%=i%>" <%if (priority.intValue()==i) {out.print("selected");} %>><%=i%></option>
<% } %>
	</select></td>
	<td><a href="viewRules.jsp?pID=<%=pID %>&uID=<%=uID %>">View</a></td>
	<td><input type=submit value="Save changes"></td>
	</form>
	</tr>
	<%
	
}

%>
</table>
<br>
<br>
<a href="insertNewProcess.jsp"><b>Insert a new process</b></a>

</body>
</html>