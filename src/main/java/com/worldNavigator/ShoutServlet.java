package com.worldNavigator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/shoutServlet"})
public class ShoutServlet extends HttpServlet {
    PlayerViewer[] users;
    String[] usersId = new String[6];
    PlayerViewer user;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("varNames") != null) {
            this.users = (PlayerViewer[]) request.getAttribute("varNames");
            this.usersId = (String[]) request.getAttribute("varIds");
        }
        for (int i = 0; i < 6; ++i) {
            if (this.usersId[i] != null) {
                if (this.usersId[i].equals(request.getParameter("sessionid"))) {
                    this.user = this.users[i];
                }
            }
        }

        String output = "";
        System.out.println("this.user: "+this.user.playerController.playerModel.isPlaying());
        if (this.user.playerController.playerModel.isPlaying()) {
            String message = "";
            if (request.getParameter("left") != null) {
                message = "l";
            } else if (request.getParameter("forward") != null) {
                message = "f";
            } else if (request.getParameter("right") != null) {
                message = "r";
            } else if (request.getParameter("backward") != null) {
                message = "b";
            } else if (request.getParameter("check") != null) {
                message = "c";
            } else if (request.getParameter("key") != null) {
                message = "key";
            } else if (request.getParameter("open") != null) {
                message = "open";
            } else if (request.getParameter("look") != null) {
                message = "look";
            } else if (request.getParameter("room") != null) {
                message = "room";
            } else if (request.getParameter("light") != null) {
                message = "light";
            } else if (request.getParameter("flash") != null) {
                message = "flash";
            } else if (request.getParameter("location") != null) {
                message = "location";
            } else if (request.getParameter("items") != null) {
                message = "items";
            }
            this.user.playerController.use_method(message.trim());
//            String finalMessage = message;
//            new Thread( () -> this.user.playerController.use_method(finalMessage.trim()));
            output = this.user.msg;
        } else {
            output = "Game didn't start yet, " + Arrays.stream(this.users).filter(Objects::nonNull).count() + " players available, game must have " + this.users.length + " players";
        }


        String htmlMessage = "<p><b>" + this.user.getName() + "</b><br/>" + output + "</p>";
        ServletContext sc = request.getServletContext();
        if (sc.getAttribute("messages") == null) {
            sc.setAttribute("messages", htmlMessage);
        } else {
            String currentMessages = (String) sc.getAttribute("messages");
            sc.setAttribute("messages", htmlMessage + currentMessages);
        }
        response.sendRedirect("commandor.jsp");
    }
}
