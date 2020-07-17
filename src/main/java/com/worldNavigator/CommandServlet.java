package com.worldNavigator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/command"})
public class CommandServlet extends HttpServlet {
    Map<Integer, Game> games = new HashMap<>();
    Game game;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext sc = request.getServletContext();

        String sessionId = "";
        if (request.getParameter("sessionId") != null) {
            sessionId = request.getParameter("sessionId");
        }

        if (request.getAttribute("games") != null) {
            this.games = (Map) request.getAttribute("games");
        }

        for (Game game : this.games.values()) {
            if (game.whoIsIt(request) != null) {
                this.game = game;
            }
        }

        if (this.game == null) {
            sc.setAttribute("messages", "You're not logged in");
            response.sendRedirect("index.jsp");
            return;
        }

        if (this.game.whoIsIt(request) != null) {
            if (request.getParameter("rock") != null) {
                this.game.setFightingChoice(request, "rock");
            } else if (request.getParameter("paper") != null) {
                this.game.setFightingChoice(request, "paper");
            } else if (request.getParameter("scissor") != null) {
                this.game.setFightingChoice(request, "scissor");
            }
        }

        String htmlMessage = "";
        if (this.game.whoIsIt(request) == null) {
            htmlMessage = "<p><b>" + "You are out of the game, you lost!" + "</b><br/>";
            sc.setAttribute("user", "<p><b>" + "" + "</b><br/>" + request.getParameter("sessionId") + "</b><br/>");
        } else {
            sc.setAttribute("user", "<p><b>" + this.game.whoIsIt(request).getName() + "</b><br/>" + request.getParameter("sessionId") + "</b><br/>");
            if (this.game.whoIsIt(request) != null) {
                if (this.game.isFighting(this.game.whoIsIt(request))) {
                    htmlMessage = "<form method=\"POST\" action=\"command\">\n" +
                        "    <table>\n" +
                        "        <tr>\n" +
                        "            <td>You are in a tie of a fight, choose an option to win:</td>\n" +
                        "            <td><input type = \"hidden\" name=\"sessionId\" value=" + sessionId + "></td>\n" +
                        "            <td><input type = \"submit\" name=\"rock\" value=\"Rock\"/></td>\n" +
                        "            <td><input type = \"submit\" name=\"scissor\" value=\"Scissor\"/></td>" +
                        "            <td><input type = \"submit\" name=\"paper\" value=\"Paper\"/></td>" +
                        "        </tr>\n" +
                        "    </table>\n" +
                        "</form>";
                } else {
                    if (this.game.whoIsIt(request).playerController.isWinner()) {
                        htmlMessage = "<p><b>" + "YOU WON!" + "</b><br/>";
                        game.playerViewers = new ArrayList<>();
                        game.playersSessions = new ArrayList<>();
                    } else {
                        htmlMessage = "<p><b>" + this.game.whoIsIt(request).getName() + "</b><br/>" + this.game.playerCommand(request) + "</p>";
                    }
                }
            }
        }

        if (sc.getAttribute("messages") == null) {
            sc.setAttribute("messages", htmlMessage);
        } else {
//            String currentMessages = (String) sc.getAttribute("messages");
//            sc.setAttribute("messages", htmlMessage + currentMessages);
            sc.setAttribute("messages", htmlMessage);
        }
        response.sendRedirect("commander.jsp");
    }
}
