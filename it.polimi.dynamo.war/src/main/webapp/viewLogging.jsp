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
		<title>Supervision log</title>
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

		List<MonitoringResultInfoWrapper> monitoring = null;
		List<RecoveryResultInfoWrapper> recovery = null;

		try {
			final MonitorLogger logger = new MonitorLoggerWS().getMonitorLoggerPort();

			final MonitoringResultInfoWrapperArray monitoringArray = logger.getMonitoringResults(mQuery);
			if (monitoringArray != null) {
				monitoring = monitoringArray.getItem();
			}

			final RecoveryResultInfoWrapperArray recoveryArray = logger.getRecoveryResults(rQuery);
			if (recoveryArray != null) {
				recovery = recoveryArray.getItem();
			}
		} catch (WebServiceException e) {
			out.println(e.getMessage());
		}

		if (monitoring == null) {
			out.println("<p>Nothing has been logged for this rule.</p>");
		} else {
			try {
%>
	<h2>Monitoring Expression:</h2>
<%
				String wscol = monitoring.get(monitoring.size() - 1).getWscolRule();
				wscol = wscol.replaceAll("<", "&lt;");
				wscol = wscol.replaceAll(">", "&gt;");
				out.println(wscol);
			} catch (Exception e) {}
%>
	<table border="2">
		<thead>
			<tr>
				<th>Date</th>
				<th>Monitoring Result</th>
				<th>Monitoring Data</th>
			</tr>
			<tbody>
<%
			try {
					final int cycles = Math.min(10, monitoring.size());
					for (int i = 0; i < cycles; i++) {
						final MonitoringResultInfoWrapper item = monitoring.get(i);
						String mData = item.getMonitoringData();
						mData = mData.replaceAll("<", "&lt;");
						mData = mData.replaceAll(">", "&gt;");
%>
		<tr>
			<td><%=item.getDate().toGregorianCalendar().getTime().toString()%></td>
			<td><%=item.isMonitoringResult()%></td>
			<td><%=mData%></td>
		</tr>
		<%
			}
				} catch (Exception e) {
				}
			}
		%>
		</tbody>
	</table>

<%
		if (recovery == null) {
		} else {
			try {
%>
	<h2>Recovery Expression:</h2>
<%
		String r = recovery.get(recovery.size() - 1)
						.getCompleteRecoveryStrategy();
				r = r.replaceAll("<", "&lt;");
				r = r.replaceAll(">", "&gt;");
				out.println(r);
			} catch (Exception e) {
			}
%>
	<table border="2">
		<thead>
			<tr>
				<th>Date</th>
				<th>Recovery Result</th>
				<th>Recovery Strategy</th>
			</tr>
		</thead>
		<tbody>
<%
			try {
					final int cycles = Math.min(10, recovery.size());
					for (int i = 0; i < cycles; i++) {
						final RecoveryResultInfoWrapper item = recovery.get(i);
						String e = item.getExecutedRecoveryStrategy();
						e = e.replaceAll("<", "&lt;");
						e = e.replaceAll(">", "&gt;");
%>
		<tr>
			<td><%=item.getDate().toGregorianCalendar().getTime().toString()%></td>
			<td><%=item.isSuccessful()%></td>
			<td><%=e%></td>
		</tr>
<%
			}
				} catch (Exception e) {
				}
			}
%>
		</tbody>
	</table>

	<p>
		<a href="clearLog.jsp?pID=<%=pID%>&uID=<%=uID%>&loc=<%=loc%>&precondition=<%=precondition%>">Clear log</a>
	</p>
</body>
</html>