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
<title>Process priority change</title>
</head>
<body>

<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>

<%  
	String pID = request.getParameter("pID");
	String uID = request.getParameter("uID");
	Integer priority = new Integer(request.getParameter("priority"));
	
	ConfigurationManagerWS locator = new ConfigurationManagerWS();
	ConfigurationManager cm = null;
	ProcessInfoWrapper pInfo = new ProcessInfoWrapper();
	pInfo.setPriority(priority);
	pInfo.setProcessId(pID);
	pInfo.setUserId(uID);

	boolean result = false;
	try {
		cm = locator.getConfigurationManagerPort();
		result = cm.setNewProcessPriority(pInfo);
	} catch (WebServiceException e) {
		out.println(e.getMessage());
	}

	if (result == true) {
		out.println("<b>The process priority has been changed.</b>");
	}
	else {
		out.println("We have had a problem. Please contact us.");
	}
%>
</body>
</html>