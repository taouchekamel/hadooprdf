<%-- 
    Document   : browse_dataset
    Created on : May 20, 2010, 8:47:06 AM
    Author     : hadoop
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <table>
            <tr>
                <th>DataSet Name</th>
           </tr>
             <c:forEach var="datasetinfo" items="${actionBean.datasets}">
             <tr>
                <td>${datasetinfo.name}</td>
             </tr>
             </c:forEach>
        </table>
    </body>
</html>
