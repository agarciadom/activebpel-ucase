<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.ProcessInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.ws.WebServiceException" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Process inserted!</title>
</head>
<body>

<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>
<br>


<%
	String username = request.getParameter("username");
	String pID = request.getParameter("pID");
	Integer priority = new Integer(request.getParameter("priority"));
	ProcessInfoWrapper pInfo = new ProcessInfoWrapper();
	
	pInfo.setUserId(username);
	pInfo.setProcessId(pID);
	pInfo.setPriority(priority);

	ConfigurationManagerWS locator = new ConfigurationManagerWS();
	ConfigurationManager cm = null;

	boolean result = false;
	try {
		cm = locator.getConfigurationManagerPort();
		result = cm.insertNewProcess(pInfo);	
	} catch (WebServiceException e) {
		out.println(e.getMessage());
	}

	if (result == true) {
		out.println("Your process has been inserted correctly!");
	}
%>


</body>
</html>