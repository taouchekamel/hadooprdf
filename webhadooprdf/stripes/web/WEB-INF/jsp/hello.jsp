<%-- 
    Document   : Hello
    Created on : May 20, 2010, 7:10:59 AM
    Author     : hadoop
--%>

<%@page contentType="text/html" pageEncoding="ISO-8859-1" language="java"%>
<%@taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Hello Stripes!</title>
    </head>
    <body>
        <h3>Hello, Stripes!</h3>
        <p>
            Date and time:
            <br>
            <b>
                <fmt:formatDate type="both" dateStyle="full" value="${actionBean.date}" />
            </b>
        </p>
        <p>
            <s:link href="/Hello.action" event="currentDate" >
            Show the current date and time
            </s:link> |
            <s:link href="/Hello.action" event="randomDate" >
            Show a random date and time
            </s:link>
        </p>
    </body>
</html>
