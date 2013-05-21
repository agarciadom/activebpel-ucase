<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.monitorlogger.MonitorLoggerWS" %>
<%@ page import="monitor.polimi.it.monitorlogger.MonitorLogger" %>
<%@ page import="monitor.polimi.it.monitorlogger.MonitoringResultInfoWrapper" %>
<%@ page import="monitor.polimi.it.monitorlogger.MonitoringResultInfoWrapperArray" %>
<%@ page import="monitor.polimi.it.monitorlogger.RecoveryResultInfoWrapper" %>
<%@ page import="monitor.polimi.it.monitorlogger.RecoveryResultInfoWrapperArray" %>

<%@ page import="java.rmi.RemoteException" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.xml.ws.WebServiceException" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Supervision log</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>
<%

String pID = request.getParameter("pID");
String uID = request.getParameter("uID");
String loc = request.getParameter("loc");
boolean precondition = (Boolean.valueOf(request.getParameter("precondition"))).booleanValue();

MonitorLoggerWS locator = null;
MonitorLogger logger = null;

MonitoringResultInfoWrapper mQuery = new MonitoringResultInfoWrapper();
mQuery.setProcessID(pID);
mQuery.setUserID(uID);
mQuery.setLocation(loc);
mQuery.setPrecondition(precondition);


RecoveryResultInfoWrapper rQuery = new RecoveryResultInfoWrapper();
rQuery.setProcessID(pID);
rQuery.setUserID(uID);
rQuery.setLocation(loc);
rQuery.setPrecondition(precondition);

List<MonitoringResultInfoWrapper> monitoring = null;
List<RecoveryResultInfoWrapper> recovery = null;

try {
	locator = new MonitorLoggerWS();
	logger = locator.getMonitorLoggerPort();

	MonitoringResultInfoWrapperArray monitoringArray = logger.getMonitoringResults(mQuery);
	if (monitoringArray != null) monitoring = monitoringArray.getItem();

	RecoveryResultInfoWrapperArray recoveryArray = logger.getRecoveryResults(rQuery);
	if (recoveryArray != null) recovery = recoveryArray.getItem();
} catch (WebServiceException e) {
	out.println(e.getMessage());
}

%>


<% 
if (monitoring == null) {
	out.println("<b>Nothing has been logged for this rule.</b");
}
else {
	try {
%>
<b>Monitoring Expression:</b>
<br>
<br>
<%
		String wscol = monitoring.get(monitoring.size() - 1).getWscolRule();
		wscol = wscol.replaceAll("<", "&lt;");
		wscol = wscol.replaceAll(">", "&gt;");
		out.println(wscol);
	}
	catch (Exception e) {}
%>
<br>
<br>
<table border=2>

	<tr>
	<td><b>Date</b></td>
	<td><b>Monitoring Result</b></td>
	<td><b>Monitoring Data</b></td>
	</tr>

<%
try {
	final int cycles = Math.min(10, monitoring.size());
	for (int i = 0; i < cycles; i++) {
		final MonitoringResultInfoWrapper item = monitoring.get(i);
		String mData = item.getMonitoringData();
		mData = mData.replaceAll("<","&lt;");
		mData = mData.replaceAll(">","&gt;");
%>
	
	<tr>
	<td><%=item.getDate().toGregorianCalendar().getTime().toString() %></td>
	<td><%=item.isMonitoringResult() %></td>
	<td><%=mData %></td>
	</tr>
	<%
	}
 }
 catch (Exception e) {}
}
%>

</table>
<br>
<br>
<br>


<% 

if (recovery == null) {
}
else {
try { %>
<b>Recovery Expression:</b>
<br>
<br>
<%
	String r = recovery.get(recovery.size() - 1).getCompleteRecoveryStrategy();
	r = r.replaceAll("<", "&lt;");
	r = r.replaceAll(">", "&gt;");
	out.println(r);
}
catch (Exception e) {}
%>
<br>
<br>
<table border=2>
	<tr>
	<td><b>Date</b></td>
	<td><b>Recovery Result</b></td>
	<td><b>Recovery Strategy</b></td>
	</tr>
<%
try {
	final int cycles = Math.min(10, recovery.size());
	for (int i = 0; i < cycles; i++) {
		final RecoveryResultInfoWrapper item = recovery.get(i);
		String e = item.getExecutedRecoveryStrategy();
		e=e.replaceAll("<","&lt;");
		e=e.replaceAll(">","&gt;");
%>
	<tr>
	<td><%=item.getDate().toGregorianCalendar().getTime().toString() %></td>
	<td><%=item.isSuccessful() %></td>
	<td><%=e %></td>
	</tr>
<%
}
}
catch (Exception e) {}
}
%>
</table>
</body>
</html>