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
		<h2>Currently supervised processes</h2>

		<table style="border: 0; padding: .5em">
			<thead>
				<tr>
					<th>Process ID</th>
					<th>User ID</th>
					<th>Priority</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
<%
for (ProcessInfoWrapper pInfo : processes) {
	final String pID = pInfo.getProcessId();
	final Long iID = pInfo.getProcessInstanceId();
	final String uID = pInfo.getUserId();
	final Integer priority = pInfo.getPriority();
%>
	<tr>
			<td><%=pID%></td>
			<td><%=uID%></td>
			<td>
				<form method="POST" action="changeProcess.jsp">
					<input type="hidden" name="pID" value="<%=pID%>"/>
					<input type="hidden" name="uID" value="<%=uID%>"/>
					<select name="priority">
<% for (int i = 1; i <= 5; i++) { %>
						<option value="<%=i%>" <%if (priority.intValue() == i) {out.print("selected");} %>><%=i%></option>
<% } %>
					</select>
					<input type="submit" value="Change priority"/>
				</form>
			</td>
			<td>
				<a href="viewRules.jsp?pID=<%=pID %>&uID=<%=uID %>">View rules</a>
				<a href="removeProcess.jsp?pID=<%=pID%>&uID=<%=uID%>">Remove</a>
			</td>
	</tr>
	<%
}
%>
			</tbody>
		</table>

		<p><a href="insertNewProcess.jsp">Insert a new process</a></p>
	</body>
</html>