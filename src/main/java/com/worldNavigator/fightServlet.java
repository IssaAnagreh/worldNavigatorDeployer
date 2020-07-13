package com.worldNavigator;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/fight"})
public class fightServlet extends HttpServlet {
    Game game;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionId = "";
        if (request.getParameter("sessionId") != null) {
            sessionId = request.getParameter("sessionId");
        }

        if (request.getAttribute("game") != null) {
            this.game = (Game) request.getAttribute("game");
        }
        System.out.println("sessionId: "+sessionId);
        System.out.println("this.game: "+this.game);

        if (request.getParameter("rock") != null) {
            System.out.println("rock");
        } else if (request.getParameter("paper") != null) {
            System.out.println("paper");
        } else if (request.getParameter("scissor") != null) {
            System.out.println("scissor");
        }
        response.sendRedirect("commander.jsp");
    }
}
