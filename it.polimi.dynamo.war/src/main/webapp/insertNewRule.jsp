<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert a new rule</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>

<%  
	String pID = request.getParameter("pID");
	String uID = request.getParameter("uID");
%>



<form method=post action="insertRuleComplete.jsp" target="_blank">
<table border=2>
<tr><td><b>UserID</b></td><td><input type=text name=uID size=20 value="<%= uID %>"></td></tr>
<tr><td><b>ProcessID</b></td><td><input type=text name=pID size=20 value="<%= pID %>"></td></tr>
<tr><td><b>Location</b></td><td><input type=text name=location size=50></td></tr>
<tr><td><b>Condition type</b></td><td><select name = precondition>
<option value="true">Pre-condition</option>
<option value="false">Post-condition</option>
</select></td></tr>
<tr><td><b>Priority</b></td><td><select name = priority>
<option value="1">1</option>
<option value="2">2</option>
<option value="3">3</option>
<option value="4">4</option>
<option value="5">5</option>
</select></td></tr>
<tr><td><b>Providers</b></td><td><textarea name = providers cols=40 rows=5 style="font-size:12px;"></textarea></td></tr>
<tr><td><b>Time-frame</b></td><td><input type=text name=timeframe size=20></td></tr>
<tr><td><b>Monitoring expression</b></td><td><textarea name = wscolRule cols=150 rows=5 style="font-size:12px;"></textarea></td></tr>
<tr><td><b>Recovery strategy</b></td><td><textarea name= recoveryStrategy cols=150 rows=5 style="font-size:12px;"></textarea></td></tr>
</table>
<p><input type=submit value="Insert"></p></form>
</body>
</html>