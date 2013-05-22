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
		<title>Change the rule</title>
	</head>
	<body>
		<img src="figures/testa_dynamo_small.jpg"/>

<%  
	final String pID = request.getParameter("pID");
	final String uID = request.getParameter("uID");
	final String loc = request.getParameter("loc");
	final boolean precondition = (Boolean.valueOf(request.getParameter("precondition"))).booleanValue();
	
	SupervisionRuleInfoWrapper rule = null;
	try {
		ConfigurationManager cm = new ConfigurationManagerWS().getConfigurationManagerPort();
		rule = cm.getSupervisionRule(pID,uID,loc, precondition);
	} catch (WebServiceException e) {
		out.println(e.getMessage());
	}

	if (rule != null) {
%>
		<form method=post action="changeRuleComplete.jsp" target="_blank">
			<table border=2>
				<tr>
					<th>User ID</th>
					<td>
						<%=uID%>
						<input type="hidden" name="uID" size="20" value="<%=uID%>">
					</td>
				</tr>
				<tr>
					<th>Process ID</th>
					<td>
						<%=pID%>
						<input type="hidden" name="pID" size="20" value="<%=pID%>">
					</td>
				</tr>
				<tr>
					<th>Location</th>
					<td>
						<%=loc%>
						<input type="hidden" name="location" size="50" value="<%=loc%>">
					</td>
				</tr>
				<tr>
					<th>Type</th>
					<td>
						<%= rule.isPrecondition() ? "pre-condition" : "post-condition" %>
						<input type="hidden" name="precondition" value="<%=rule.isPrecondition()%>"/>
					</td>
				</tr>
				<tr>
					<th>Priority</th>
					<td>
						<select name="priority">
<% 		for (int i = 1; i <= 5; i = i + 1) { %>
						<option value="<%=i %>" <%if (rule.getPriority() == i) {out.print("selected");} %>><%=i %></option>
<% 		} %>
						</select>
					</td>
				</tr>
				<tr>
					<th>Providers</th>
					<td>
						<textarea name="providers" cols="40" rows="5" style="font-size:.8em;"><%=rule.getProviders() %></textarea>
					</td>
				</tr>
				<tr>
					<th>Time-frame</th>
					<td>
						<input type="text" name="timeframe" size="20" value="<%=rule.getTimeFrame() %>">
					</td>
				</tr>
				<tr>
					<th>Monitoring expression</th>
					<td>
						<textarea name="wscolRule" cols="150" rows="5" style="font-size:.8em;"><%=rule.getWscolRule() %></textarea>
					</td>
				</tr>
				<tr>
					<th>Recovery strategy</th>
					<td>
						<textarea name="recoveryStrategy" cols="150" rows="5" style="font-size:.8em;"><%=rule.getRecoveryStrategy() %></textarea>
					</td>
				</tr>
			</table>
			<p><input type=submit value="Change it!"></p>
		</form>
<% } %>
	</body>
</html>