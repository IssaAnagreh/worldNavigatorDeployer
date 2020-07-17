<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*,java.util.*" %>
<%
    // Get session creation time.
    Date createTime = new Date(session.getCreationTime());

    // Get last access time of this Webpage.
    Date lastAccessTime = new Date(session.getLastAccessedTime());

    String title = "Welcome Back to my website";
    int visitCount = 0;
    String visitCountKey = new String("visitCount");
    String userIDKey = new String("userID");

    // Check if this is new comer on your Webpage.
    if (session.isNew() || session.getAttribute(visitCountKey) == null) {
        title = "Welcome to my website";
        session.setAttribute(visitCountKey, visitCount);
    }
    visitCount = (int) session.getAttribute(visitCountKey);
    visitCount = visitCount + 1;
    session.setAttribute(visitCountKey, visitCount);
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Isa Anagreh</title>
</head>
<body>
<h1>WORLD NAVIGATOR!</h1>
<h2>Commander</h2>
<% if (application.getAttribute("user") != null) {%>
<%= application.getAttribute("user")%>
<% }%>
<form method="POST" action="command">
    <table border="1" align="center">
        <tr bgcolor="#949494">
            <th>Session info</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>id</td>
            <td><% out.print(session.getId()); %></td>
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
<%--            <td>Change your name:</td>--%>
<%--            <td><input type="text" id="name" name="name"/></td>--%>
            <td><input type="hidden" name="sessionId" value="<%=session.getId() %>"></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="forward" value="Forward"/></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="left" value="Left"/></td>
            <td><input type="submit" name="backward" value="Backward"/></td>
            <td><input type="submit" name="right" value="Right"/></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="location" value="Location"/></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="check" value="Check"/></td>
            <td><input type="submit" name="key" value="Use Key"/></td>
            <td><input type="submit" name="open" value="Open"/></td>
            <td><input type="submit" name="light" value="Switch Light"/></td>
            <td><input type="submit" name="flash" value="Use Flash Light"/></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="look" value="Look"/></td>
            <td><input type="submit" name="room" value="Room"/></td>
            <td><input type="submit" name="items" value="My items"/></td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" name="quit" value="Quit"/></td>
        </tr>
    </table>
</form>
<h2> Current Commands </h2>
<div id="content">
    <% if (application.getAttribute("messages") != null) {%>
    <%= application.getAttribute("messages")%>
    <% }%>
</div>
<script>
    var messagesWaiting = false;

    function getMessages() {
        if (!messagesWaiting) {
            messagesWaiting = true;
            var xmlhttp = new XMLHttpRequest();
            xmlhttp.onreadystatechange = function () {
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    messagesWaiting = false;
                    var contentElement = document.getElementById("content");
                    contentElement.innerHTML = xmlhttp.responseText + contentElement.innerHTML;
                }
            }
            xmlhttp.open("GET", "command?t=" + new Date(), true);
            xmlhttp.send();
        }
    }

    // setInterval(getMessages, 1000);
</script>
</body>
</html>