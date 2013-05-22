<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManager" %>
<%@ page import="monitor.polimi.it.configurationmanager.ConfigurationManagerWS" %>
<%@ page import="monitor.polimi.it.configurationmanager.SupervisionRuleInfoWrapper" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="javax.xml.ws.WebServiceException" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Remove rule</title>
	</head>
	<body>
		<img src="figures/testa_dynamo_small.jpg"/>
<%  
	final String pID = request.getParameter("pID");
	final String uID = request.getParameter("uID");
	final String loc = request.getParameter("loc");
	final boolean precondition = (Boolean.valueOf(request.getParameter("precondition"))).booleanValue();

	final String ruleListURL = String.format("viewRules.jsp?pID=%s&uID=%s&loc=%s&precondition=%s", pID, uID, loc, precondition);

	boolean success = false;
	try {
		final ConfigurationManager cm = new ConfigurationManagerWS().getConfigurationManagerPort();
		success = cm.releaseSupervisionRule(pID, uID, loc, precondition);
	} catch (WebServiceException e) {
		out.println(e.getMessage());
	}

	if (success) { %>
		<p>The rule has been removed.</p>
		<script type="text/javascript">
			setTimeout(function() { document.location = '<%=ruleListURL%>'; }, 3000);
		</script>
<%	} else { %>
		<p>We have had a problem. Please contact us.</p>
<%	} %>
		<p><a href="<%=ruleListURL%>">Return to process list</a></p>
	</body>
</html>