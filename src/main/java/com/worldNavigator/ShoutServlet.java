package com.worldNavigator;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/shoutServlet"})
public class ShoutServlet extends HttpServlet {
    PlayerViewer user;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("varName") != null) this.user = (PlayerViewer) request.getAttribute("varName");

        String message = request.getParameter("message");
        if (message != null) {
            this.user.playerController.use_method(message.trim());

            String htmlMessage = "<p><b>" + this.user.getName() + "</b><br/>" + this.user.msg + "</p>";
            ServletContext sc = request.getServletContext();
            if (sc.getAttribute("messages") == null) {
                sc.setAttribute("messages", htmlMessage);
            } else {
                String currentMessages = (String) sc.getAttribute("messages");
                sc.setAttribute("messages", htmlMessage + currentMessages);
            }
        }
        response.sendRedirect("index.jsp");
    }
}
