<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWSLocator" %>
<%@ page import="monitor.polimi.it.configurationmanager.ProcessInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.rpc.ServiceException" %>

<%@page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper"%>
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

	
	boolean precondition = Boolean.parseBoolean(request.getParameter("precondition"));
	
	
	SupervisionRuleInfoWrapper ruleInfo = new SupervisionRuleInfoWrapper();
	
	ruleInfo.setLocation(request.getParameter("location"));
	ruleInfo.setPrecondition(precondition);
	ruleInfo.setPriority(Integer.parseInt(request.getParameter("priority")));
	ruleInfo.setProcessID(request.getParameter("pID"));
	ruleInfo.setProviders(request.getParameter("providers"));
	ruleInfo.setRecoveryStrategy(request.getParameter("recoveryStrategy"));
	ruleInfo.setTimeFrame(request.getParameter("timeframe"));
	ruleInfo.setUserID(request.getParameter("uID"));
	ruleInfo.setWscolRule(request.getParameter("wscolRule"));
	
	
	ConfigurationManagerWSLocator locator = new ConfigurationManagerWSLocator();
	ConfigurationManager cm = null;
	
	boolean result = false;
	
	try {
		
		cm = locator.getConfigurationManagerPort();
		
		result = cm.insertNewSupervisionRule(ruleInfo);	
		
	} catch (ServiceException e) {
	out.println(e.getMessage());
	} catch (RemoteException e) {
	out.println(e.getMessage());
	}
	
	if (result == true) {
		out.println("Your rule has been inserted correctly!");
	}
%>


</body>
</html>