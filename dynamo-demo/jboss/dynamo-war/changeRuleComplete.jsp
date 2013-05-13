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
<title>Supervision Rule Change</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br><br><br>


<%

ConfigurationManagerWSLocator locator = new ConfigurationManagerWSLocator();
ConfigurationManager cm = null;

SupervisionRuleInfoWrapper ruleInfo = new SupervisionRuleInfoWrapper();

ruleInfo.setLocation(request.getParameter("location"));
ruleInfo.setPrecondition((Boolean.valueOf(request.getParameter("precondition"))).booleanValue());
ruleInfo.setPriority((Integer.valueOf(request.getParameter("priority"))).intValue());
ruleInfo.setProcessID(request.getParameter("pID"));
ruleInfo.setProviders(request.getParameter("providers"));
ruleInfo.setRecoveryStrategy(request.getParameter("recoveryStrategy"));
ruleInfo.setTimeFrame(request.getParameter("timeframe"));
ruleInfo.setUserID(request.getParameter("uID"));
ruleInfo.setWscolRule(request.getParameter("wscolRule"));


boolean result = false;
try {
	
	cm = locator.getConfigurationManagerPort();
	result = cm.changeSupervisionRuleParams(ruleInfo);

} catch (ServiceException e) {
out.println(e.getMessage());
} catch (RemoteException e) {
out.println(e.getMessage());
}

if (result==true) {
	out.println("The rule has been changed succesfully!");
}
else {
	out.println("We have had some problem. Please contact us!");
}

%>




</body>
</html>