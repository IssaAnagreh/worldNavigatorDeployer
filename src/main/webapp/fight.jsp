<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import = "java.io.*,java.util.*" %>
<!DOCTYPE html>
<html>
<body>
<form method="POST" action="fight">
    <table>
        <tr>
            <td>You are in a tie of a fight, choose an option to win:</td>
            <td><input type=submit name=rock value=Rock></td>
            <td><input type=submit name=paper value=Paper></td>
            <td><input type=submit name=scissor value=Scissor></td>
        </tr>
    </table>
</form>
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