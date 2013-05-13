<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Demo Manager</title>
</head>
<body>
<img src="figures/testa_demo_small.jpg"/>
<br>
<br>
<br>
<b>Step zero</b>
<br>
Run the process without supervision.<br>
We will notice that the process completes correctly but with a map that has a resolution that is too high!
<br>
<br>
<a href = "http://localhost:8080/pizzaDelivery/PizzaDelivery.jsp"/>Do it!</a>
<br>
<br>
<br>

<b>Step one</b>
<br>
Add two supervision rules to the Pizza Delivery Process.<br>
The first checks the map resolution. If it is too high the process is halted and Dynamo notifies the process manager.
<br>
The second regards the correctness of the GPS coordinates but is never run due to priority issues.
<br><br>
<a href = "stepOne.jsp">Do it!</a>

<br>
<br>
<br>
<b>Step two</b>
<br>
Change the supervision rules.<br>
In this case if the map resolution is too high Dynamo looks for a substitute service that can provide a better map.
<br>
The GPS coordinates are still not checked.
<br><br>
<a href="stepTwo.jsp"/>Do it!</a>
<br>
<br>
<br>

<b>Step three</b>
<br>
First change the process priority to "1". <a href="viewProcesses.jsp"/>Go</a><br><br>
If the GPS coordinates are incorrect Dynamo first retries to see if the problem is transient.<br> 
If the problem persists Dynamo notifies the process manager.  

<br>
<br>
<br>

<b>Step four</b>
<br>
First change the process priority back to "3". <a href="viewProcesses.jsp"/>Go</a><br><br>

Change the supervision rules.
<br>
If the resolution of the map is too high we can relax the constraints and accept a
bigger map.
<br><br>
<a href="stepFour.jsp"/>Do it!</a>
<br>
<br>
<br>

</body>
</html>