<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWSLocator" %>
<%@ page import="monitor.polimi.it.configurationmanager.ProcessInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.rpc.ServiceException" %>



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

ConfigurationManagerWSLocator locator = new ConfigurationManagerWSLocator();
ConfigurationManager cm = null;
ProcessInfoWrapper[] processes = null;

try {
	
	cm = locator.getConfigurationManagerPort();
	processes = cm.getMonitoredProcesses();
	
		
} catch (ServiceException e) {
out.println(e.getMessage());
} catch (RemoteException e) {
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
for (int i=0; i<processes.length; i++) {
	
	ProcessInfoWrapper pInfo = processes[i];
	
	String pID = pInfo.getProcessId();
	Long iID = pInfo.getProcessInstanceId();
	String uID = pInfo.getUserId();
	Integer priority = pInfo.getPriority();
	
	%>
	<tr>
	<form method=post action="changeProcess.jsp" target="_blank"/>
	<td><input type=text name=pID size=20 value = "<%=pID %>"></td>
	<td><input type=text name = uID size=20 value = "<%=uID %>"></td>
	<td><select name = priority>
<option value="1" <%if (priority.intValue()==1) {out.print("selected");} %>>1</option>
<option value="2" <%if (priority.intValue()==2) {out.print("selected");} %>>2</option>
<option value="3" <%if (priority.intValue()==3) {out.print("selected");} %>>3</option>
<option value="4" <%if (priority.intValue()==4) {out.print("selected");} %>>4</option>
<option value="5" <%if (priority.intValue()==5) {out.print("selected");} %>>5</option>
</select></td>
	<td><a href="viewRules.jsp?pID=<%=pID %>&uID=<%=uID %>">View</a></td>
	<td><input type=submit value="Save changes"></td>
	<form>
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