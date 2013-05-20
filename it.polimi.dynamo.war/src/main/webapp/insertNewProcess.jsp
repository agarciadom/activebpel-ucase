<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert a new process</title>
</head>
<body>
<img src="figures/testa_dynamo_small.jpg"/>
<br>
<br>

<b>Please insert the new process</b>
<br>
<br>

<form method=post action="insertProcessComplete.jsp" target="_blank">
<table border=2>
<tr><td><b>Username</b></td><td><input type=text name=username size=20></td></tr>
<tr><td><b>ProcessID</b></td><td><input type=text name=pID size=20></td></tr>
<tr><td><b>Priority</b></td><td> <select name = priority>
<option value="1">1</option>
<option value="2">2</option>
<option value="3">3</option>
<option value="4">4</option>
<option value="5">5</option>
</select></td></tr>
</table>
<br>
<br>
<input type=submit value="Insert">

</form>
</body>
</html>