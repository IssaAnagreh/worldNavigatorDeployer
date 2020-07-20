package com.worldNavigator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "UserServer", urlPatterns = {"/user"})
public class UserServer extends HttpServlet {
    Map<Integer, Game> games = new HashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.games.size() == 0) {
            System.out.println("new game is BEING implemented");
            this.games.put(0, new Game(this.games.size()));
            System.out.println("new game is implemented");
        }
        Game game = null;
        for (Integer gameIdx : this.games.keySet()) {
            System.out.println("this.games: "+this.games.size());
            game = this.games.get(gameIdx);
            if (!game.isStarted) {
                System.out.println("game is not started");
                if (!game.isPlayerExisted(request.getParameter("sessionId"))) {
                    System.out.println("player is not existed");
                    String name = request.getParameter("name");
                    System.out.println("player is BEING prepared");
                    game.preparePlayer(name, request.getParameter("sessionId"));
                    System.out.println("player is prepared");
                    game.start();

                    request.setAttribute("games", games);
                    break;
                }
            }
            game = null;
        }
        if (game == null) {
            System.out.println("in USERSERVER game is null");
            game = new Game(this.games.size());
            System.out.println("game is generated");
            this.games.put(this.games.size(), game);
            String name = request.getParameter("name");
            game.preparePlayer(name, request.getParameter("sessionId"));
            System.out.println("player is prepared");
            game.start();
            System.out.println("game might be started");

            request.setAttribute("games", games);
        }
        getServletContext().getRequestDispatcher("/command").forward(request, response);
    }
}
