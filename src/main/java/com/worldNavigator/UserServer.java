package com.worldNavigator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserServer", urlPatterns = {"/user"})
public class UserServer extends HttpServlet {
    Menu menu;
    PlayerViewer[] users = new PlayerViewer[6];
    String[] usersId = new String[6];
    int count = 0;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (menu == null) {
            System.out.println("menu is null from GET");
            Maps maps = new Maps();
            maps.addMap("map.json");
            this.menu = new Menu();
            this.menu.setMaps(maps, "0");
        }
        getServletContext().getRequestDispatcher("/commandor.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean repeated = false;
        for (String id : this.usersId) {
            if (id != null) {
                if (id.equals(request.getParameter("sessionid"))) {
                    repeated = true;
                }
            }
        }
        if (repeated) {
//            getServletContext().getRequestDispatcher("/user.jsp").forward(request, response);
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/index.jsp"));
        } else {
            if (menu == null) {
                System.out.println("menu is null from POST");
                Maps maps = new Maps();
                maps.addMap("map.json");
                this.menu = new Menu();
                this.menu.setMaps(maps, "0");
            }

            String name = request.getParameter("name");
            this.menu.start(name);
            this.usersId[count] = request.getParameter("sessionid");
            this.users[count++] = menu.playerViewer;

            System.out.println("count: " + count);
            if (count == 3) {
                for (PlayerViewer user : this.users) {
                    if (user != null) {
                        user.playerController.startGame();
                    }
                }
            }

            request.setAttribute("varNames", this.users);
            request.setAttribute("varIds", this.usersId);
            getServletContext().getRequestDispatcher("/shoutServlet").forward(request, response);
        }
    }
}
