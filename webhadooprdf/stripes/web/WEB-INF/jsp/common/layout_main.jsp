<%-- 
    Document   : layout_main
    Created on : May 20, 2010, 8:57:12 AM
    Author     : hadoop
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<s:layout-definition>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

    <html>
        <head>
             <title>HadoopRDF-${title}</title>
             <link href="${contextPath}/css/gestured.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
    <div id="wrapper">
    <!-- start header -->
    <div id="header">
            <div id="menu">
                    <ul id="main">
                        <li class="current_page_item"><a href="index.jsp">Home</a></li>
                        <li><s:link href="/BrowseDataset.action"> Browse Datasets</s:link></li>
                        <li><s:link href="/AddDataset.action"> Add Datasets</s:link></li>
                        <li><s:link href="/ViewPolicies.action"> View Policies</s:link></li>
                        <li><a href="#">About Us</a></li>
                        <li><a href="#">Contact Us</a></li>
                    </ul>
            </div>
            <div id="logo">
                <h1><a href="#"><span>HadoopRDF-${title}</span></a></h1>
                  <%--  <img src="../images/hadoop.jpeg" alt="hadoop"/>
                    <img src="../images/jena.png" alt="jena"/>
                    <img src="../images/rdf.gif" alt="rdf"/>--%>
                    
            </div>
    </div>
    <!-- end header -->
            <!-- start page -->
            <div id="page">
                    <div id="sidebar1" class="sidebar">
                            <ul>
                                    <li>
                                            <h2>Menu</h2>
                                            <ul>
                                                <li><s:link href="/BrowseDataset.action"> Browse Datasets</s:link></li>
                                                <li><s:link href="/AddDataset.action"> Add Datasets</s:link></li>
                                                <li><s:link href="/ViewPolicies.action"> View Policies</s:link></li>
                                            </ul>
                                    </li>
                                    <%--    <li>
                                            <h2>Recent Comments</h2>
                                    </li>
                                    <li>
                                            <h2>Categories</h2>
                                    </li>
                                    <li>
                                            <h2>Archives</h2>
                                    </li>--%>
                            </ul>
                    </div>
                    <!-- start content -->
                    <div id="content">
                            <div class="post">
                                <s:layout-component name="body">
                                 
                                </s:layout-component>
                            </div>
                            
                    </div>
                    <!-- end content -->
                    <!-- start sidebars -->
                   <%-- <div id="sidebar2" class="sidebar">
                            <ul>
                                    <li>
                                            <form id="searchform" method="get" action="#">
                                                    <div>
                                                            <h2>Site Search</h2>
                                                            <input type="text" name="s" id="s" size="15" value="" />
                                                    </div>
                                            </form>
                                    </li>
                                    <li>
                                            <h2>Tags</h2>
                                            <p class="tag"><a href="#">dolor</a> <a href="#">ipsum</a> <a href="#">lorem</a> <a href="#">sit amet</a> <a href="#">dolor</a> <a href="#">ipsum</a> <a href="#">lorem</a> <a href="#">sit amet</a></p></li>
                                    <li>
                                            <h2>Calendar</h2>
                                            <div id="calendar_wrap">
                                                    <table summary="Calendar">
                                                            <caption>
                                                            October 2009
                                                            </caption>
                                                            <thead>
                                                                    <tr>
                                                                            <th abbr="Monday" scope="col" title="Monday">M</th>
                                                                            <th abbr="Tuesday" scope="col" title="Tuesday">T</th>
                                                                            <th abbr="Wednesday" scope="col" title="Wednesday">W</th>
                                                                            <th abbr="Thursday" scope="col" title="Thursday">T</th>
                                                                            <th abbr="Friday" scope="col" title="Friday">F</th>
                                                                            <th abbr="Saturday" scope="col" title="Saturday">S</th>
                                                                            <th abbr="Sunday" scope="col" title="Sunday">S</th>
                                                                    </tr>
                                                            </thead>
                                                            <tfoot>
                                                                    <tr>
                                                                            <td abbr="September" colspan="3" id="prev"><a href="#" title="View posts for September 2009">&laquo; Sep</a></td>
                                                                            <td class="pad">&nbsp;</td>
                                                                            <td colspan="3" id="next">&nbsp;</td>
                                                                    </tr>
                                                            </tfoot>
                                                            <tbody>
                                                                    <tr>
                                                                            <td>1</td>
                                                                            <td>2</td>
                                                                            <td>3</td>
                                                                            <td id="today">4</td>
                                                                            <td>5</td>
                                                                            <td>6</td>
                                                                            <td>7</td>
                                                                    </tr>
                                                                    <tr>
                                                                            <td>8</td>
                                                                            <td>9</td>
                                                                            <td>10</td>
                                                                            <td>11</td>
                                                                            <td>12</td>
                                                                            <td>13</td>
                                                                            <td>14</td>
                                                                    </tr>
                                                                    <tr>
                                                                            <td>15</td>
                                                                            <td>16</td>
                                                                            <td>17</td>
                                                                            <td>18</td>
                                                                            <td>19</td>
                                                                            <td>20</td>
                                                                            <td>21</td>
                                                                    </tr>
                                                                    <tr>
                                                                            <td>22</td>
                                                                            <td>23</td>
                                                                            <td>24</td>
                                                                            <td>25</td>
                                                                            <td>26</td>
                                                                            <td>27</td>
                                                                            <td>28</td>
                                                                    </tr>
                                                                    <tr>
                                                                            <td>29</td>
                                                                            <td>30</td>
                                                                            <td>31</td>
                                                                            <td class="pad" colspan="4">&nbsp;</td>
                                                                    </tr>
                                                            </tbody>
                                                    </table>
                                            </div>
                                    </li>
                                    <li>
                                            <h2>Categories</h2>
                                            <ul>
                                                    <li><a href="#">Aliquam libero</a></li>
                                                    <li><a href="#">Consectetuer adipiscing elit</a></li>
                                                    <li><a href="#">Metus aliquam pellentesque</a></li>
                                                    <li><a href="#">Suspendisse iaculis mauris</a></li>
                                                    <li><a href="#">Urnanet non molestie semper</a></li>
                                                    <li><a href="#">Proin gravida orci porttitor</a></li>
                                                    <li><a href="#">Aliquam libero</a></li>
                                                    <li><a href="#">Consectetuer adipiscing elit</a></li>
                                                    <li><a href="#">Metus aliquam pellentesque</a></li>
                                                    <li><a href="#">Urnanet non molestie semper</a></li>
                                                    <li><a href="#">Metus aliquam pellentesque</a></li>
                                                    <li><a href="#">Suspendisse iaculis mauris</a></li>
                                                    <li><a href="#">Urnanet non molestie semper</a></li>
                                                    <li><a href="#">Proin gravida orci porttitor</a></li>
                                                    <li><a href="#">Metus aliquam pellentesque</a></li>
                                            </ul>
                                    </li>
                            </ul>
                    </div>--%>
                    <!-- end sidebars -->
                    <div style="clear: both;">&nbsp;</div>
            </div>
            <!-- end page -->
    </div>
    <div id="footer">
            <p class="copyright">&copy;&nbsp;&nbsp;2009 All Rights Reserved &nbsp;&bull;&nbsp; Design by <a href="http://www.freecsstemplates.org/">Free CSS Templates</a>.</p>
    </div>
    </body>
    </html>
</s:layout-definition>
