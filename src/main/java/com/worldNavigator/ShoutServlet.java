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
    Menu menu;
    String user = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (menu == null) {
            Maps maps = new Maps();
            maps.addMap("map.json");
            this.menu = new Menu();
            this.menu.setMaps(maps, "0");
            this.menu.start();
        }
        if (request.getAttribute("varName") != null) this.user = request.getAttribute("varName").toString();

        String message = request.getParameter("message");
        if (message != null) {
            if (this.menu.playerModel.isPlaying()) this.menu.playerViewer.playerController.use_method(message.trim());

            String htmlMessage = "<p><b>" + this.user + "</b><br/>" + this.menu.playerViewer.msg + "</p>";
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
