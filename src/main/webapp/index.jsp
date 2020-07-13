<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import = "java.io.*,java.util.*" %>
<%@ page import="com.worldNavigator.MapFactory" %>
<%
    // Get session creation time.
    Date createTime = new Date(session.getCreationTime());

    MapFactory mapFactory = new MapFactory("map.json");
    // Get last access time of this Webpage.
    String lastAccessTime = mapFactory.location;

    String title = "Welcome Back to my website";
    int visitCount = 0;
    String visitCountKey = new String("visitCount");
    String userIDKey = new String("userID");

    // Check if this is new comer on your Webpage.
    if (session.isNew() || session.getAttribute(visitCountKey) == null){
        title = "Welcome to my website";
        session.setAttribute(visitCountKey,  visitCount);
    }
    visitCount = (int) session.getAttribute(visitCountKey);
    visitCount = visitCount + 1;
    session.setAttribute(visitCountKey,  visitCount);
    String id = session.getId();
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Isa Anagreh</title>
</head>
<body>
<h1>WORLD NAVIGATOR!</h1>
<h2>User</h2>
<form method="POST" action="user">
    <table border = "1" align = "center">
        <tr bgcolor = "#949494">
            <th>Session info</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>id</td>
            <td><% out.print( session.getId()); %></td>
        </tr>
        <tr>
            <td>Creation Time</td>
            <td><% out.print(createTime); %></td>
        </tr>
        <tr>
            <td>Time of Last Access</td>
            <td><% out.print(lastAccessTime); %></td>
        </tr>
        <tr>
            <td>Number of visits</td>
            <td><% out.print(visitCount); %></td>
        </tr>
    </table>
    <table>
        <tr>
            <td>Your name:</td>
            <td><input type="text" id="name" name="name"/></td>
        </tr>
        <tr>
            <td><input type = "hidden" name="sessionId" value="<%=session.getId() %>"></td>
            <td><input style="padding-left: 15px; padding-right: 15px;" type="submit" value="Join"/></td>
        </tr>
    </table>
</form>
<h2> Current Shouts </h2>
<div id="content">
    <% if (application.getAttribute("messages") != null) {%>
    <%= application.getAttribute("messages")%>
    <% }%>
</div>
<script>
    var messagesWaiting = false;
    function getMessages(){
        if(!messagesWaiting){
            messagesWaiting = true;
            var xmlhttp = new XMLHttpRequest();
            xmlhttp.onreadystatechange=function(){
                if (xmlhttp.readyState==4 && xmlhttp.status==200) {
                    messagesWaiting = false;
                    var contentElement = document.getElementById("content");
                    contentElement.innerHTML = xmlhttp.responseText + contentElement.innerHTML;
                }
            }
            xmlhttp.open("GET", "command?t="+new Date(), true);
            xmlhttp.send();
        }
    }
    // setInterval(getMessages, 1000);
</script>
</body>
</html>
