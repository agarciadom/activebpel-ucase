<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.monitorlogger.MonitorLoggerWSLocator" %>
<%@ page import="monitor.polimi.it.monitorlogger.MonitorLogger" %>
<%@ page import="monitor.polimi.it.monitorlogger.MonitoringResultInfoWrapper" %>
<%@ page import="monitor.polimi.it.monitorlogger.RecoveryResultInfoWrapper" %>

<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.rpc.ServiceException" %>


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

MonitorLoggerWSLocator locator = null;
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

MonitoringResultInfoWrapper[] monitoring =null;
RecoveryResultInfoWrapper[] recovery = null;

try {
	locator = new MonitorLoggerWSLocator();
	logger = locator.getMonitorLoggerPort();

	monitoring = logger.getMonitoringResults(mQuery);
	recovery = logger.getRecoveryResults(rQuery);
		
} catch (ServiceException e) {
out.println(e.getMessage());
} catch (RemoteException e) {
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
String wscol = monitoring[monitoring.length - 1].getWscolRule();
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
	int cycles=10;
	if (monitoring.length<10) {cycles=monitoring.length;}
for (int i=0; i< cycles; i++) {
	
	String mData = monitoring[i].getMonitoringData();
	mData = mData.replaceAll("<","&lt;");
	mData = mData.replaceAll(">","&gt;");
	
	%>
	
	<tr>
	<td><%=monitoring[i].getDate().getTime().toString() %></td>
	<td><%=monitoring[i].getMonitoringResult().booleanValue() %></td>
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
String r = recovery[recovery.length - 1].getCompleteRecoveryStrategy();
r= r.replaceAll("<","&lt;");
r=r.replaceAll(">", "&gt;");
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
	int cycles=10;
	if (recovery.length<10) {cycles=recovery.length;}
for (int i=0; i< cycles; i++) {
	
	String e = recovery[i].getExecutedRecoveryStrategy();
	e=e.replaceAll("<","&lt;");
	e=e.replaceAll(">","&gt;");
	
	%>
	
	<tr>
	<td><%=recovery[i].getDate().getTime().toString() %></td>
	<td><%=recovery[i].getSuccessful().booleanValue() %></td>
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