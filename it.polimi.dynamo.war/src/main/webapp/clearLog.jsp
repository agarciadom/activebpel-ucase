<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="monitor.polimi.it.monitorlogger.MonitorLoggerWS"%>
<%@ page import="monitor.polimi.it.monitorlogger.MonitorLogger"%>
<%@ page import="monitor.polimi.it.monitorlogger.MonitoringResultInfoWrapper"%>
<%@ page import="monitor.polimi.it.monitorlogger.MonitoringResultInfoWrapperArray"%>
<%@ page import="monitor.polimi.it.monitorlogger.RecoveryResultInfoWrapper"%>
<%@ page import="monitor.polimi.it.monitorlogger.RecoveryResultInfoWrapperArray"%>

<%@ page import="java.rmi.RemoteException"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.xml.ws.WebServiceException"%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Clear supervision log</title>
	</head>
	<body>
		<img src="figures/testa_dynamo_small.jpg" />
<%
		final String pID = request.getParameter("pID");
		final String uID = request.getParameter("uID");
		final String loc = request.getParameter("loc");
		final boolean precondition = (Boolean.valueOf(request.getParameter("precondition"))).booleanValue();

		final MonitoringResultInfoWrapper mQuery = new MonitoringResultInfoWrapper();
		mQuery.setProcessID(pID);
		mQuery.setUserID(uID);
		mQuery.setLocation(loc);
		mQuery.setPrecondition(precondition);

		final RecoveryResultInfoWrapper rQuery = new RecoveryResultInfoWrapper();
		rQuery.setProcessID(pID);
		rQuery.setUserID(uID);
		rQuery.setLocation(loc);
		rQuery.setPrecondition(precondition);

		try {
			final MonitorLogger logger = new MonitorLoggerWS().getMonitorLoggerPort();
			final boolean successMR = logger.removeMonitoringResults(mQuery);
			final boolean successRR = logger.removeRecoveryResults(rQuery);

			out.println(successMR ? "<p>All monitoring results were removed.</p>" : "<p>Could not remove any monitoring results.</p>");
			out.println(successRR ? "<p>All recovery results were removed.</p>" : "<p>Could not remove any recovery results.</p>");
		} catch (WebServiceException e) {
			out.println(e.getMessage());
		}
%>
		<a href="viewLogging.jsp?pID=<%=pID %>&uID=<%=uID %>&loc=<%=loc %>&precondition=<%=precondition %>">Return to log</a>
	</body>
</html>