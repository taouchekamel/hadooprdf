<%-- 
    Document   : query_result.jsp
    Created on : May 21, 2010, 6:13:47 AM
    Author     : hadoop
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<s:layout-render name="/WEB-INF/jsp/common/layout_main.jsp" title="Query Result">
    <s:layout-component name="body">
        <h3>You have selected : ${datasetInfo.name}</h3>
        <div class="post">

        </div>
    </s:layout-component>
</s:layout-render>