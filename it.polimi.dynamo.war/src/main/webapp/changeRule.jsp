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
<title>Change the rule</title>
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
	
	
	ConfigurationManagerWSLocator locator = new ConfigurationManagerWSLocator();
	ConfigurationManager cm = null;
	SupervisionRuleInfoWrapper rule = null;
	
	try {
		
		cm = locator.getConfigurationManagerPort();
		rule = cm.getSupervisionRule(pID,uID,loc, precondition);

	} catch (ServiceException e) {
	out.println(e.getMessage());
	} catch (RemoteException e) {
	out.println(e.getMessage());
	}

	
%>



<form method=post action="changeRuleComplete.jsp" target="_blank">
<table border=2>
<tr><td><b>UserID</b></td><td><input type=text name=uID size=20 value="<%= uID %>"></td></tr>
<tr><td><b>ProcessID</b></td><td><input type=text name=pID size=20 value="<%= pID %>"></td></tr>
<tr><td><b>Location</b></td><td><input type=text name=location size=50 value="<%=loc %>"></td></tr>
<tr><td><b>Condition type</b></td><td><select name = precondition>
<option value="true" <% if (rule.isPrecondition()) {out.print("selected");}%> >Pre-condition</option>
<option value="false" <% if (!(rule.isPrecondition())) {out.print("selected");}%> >Post-condition</option>
</select></td></tr>
<tr><td><b>Priority</b></td><td><select name = priority>
<option value="1" <%if (rule.getPriority()==1) {out.print("selected");} %>>1</option>
<option value="2" <%if (rule.getPriority()==2) {out.print("selected");} %>>2</option>
<option value="3" <%if (rule.getPriority()==3) {out.print("selected");} %>>3</option>
<option value="4" <%if (rule.getPriority()==4) {out.print("selected");} %>>4</option>
<option value="5" <%if (rule.getPriority()==5) {out.print("selected");} %>>5</option>
</select></td></tr>
<tr><td><b>Providers:</b></td><td><textarea name = providers cols=40 rows=5 style="font-size:12px;"><%=rule.getProviders() %></textarea></td></tr>
<tr><td><b>Time-frame</b></td><td><input type=text name=timeframe size=20 value="<%=rule.getTimeFrame() %>"></td></tr>
<tr><td><b>Monitoring expression</b></td><td><textarea name = wscolRule cols=150 rows=5 style="font-size:12px;"><%=rule.getWscolRule() %></textarea></td></tr>
<tr><td><b>Recovery strategy</b></td><td><textarea name= recoveryStrategy cols=150 rows=5 style="font-size:12px;"><%=rule.getRecoveryStrategy() %></textarea></td></tr>
</table>
<p><input type=submit value="Change it!">
</p></form>
</body>
</html>