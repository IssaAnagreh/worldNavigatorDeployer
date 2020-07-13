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

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("get from UserServer");
//        if (this.game == null) {
//            System.out.println("game is null from GET in UserServer");
//            this.game = new Game();
//        }
//
//        getServletContext().getRequestDispatcher("/commander.jsp").forward(request, response);
//    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.games.size() == 0) {
            System.out.println("game is null from POST in UserServer");
            this.games.put(0, new Game());
        }
        Game game = null;
        for (Integer gameIdx : this.games.keySet()) {
            game = this.games.get(gameIdx);
            if (!game.isStarted) {
                if (!game.isPlayerExisted(request.getParameter("sessionId"))) {
                    String name = request.getParameter("name");
                    game.preparePlayer(name, request.getParameter("sessionId"));
                    game.start();

                    request.setAttribute("games", games);
                    break;
                }
            }
            game = null;
        }
        if (game == null) {
            game = new Game();
            this.games.put(this.games.size(), game);
            String name = request.getParameter("name");
            game.preparePlayer(name, request.getParameter("sessionId"));
            game.start();

            request.setAttribute("games", games);
        }
        getServletContext().getRequestDispatcher("/command").forward(request, response);
    }
}
