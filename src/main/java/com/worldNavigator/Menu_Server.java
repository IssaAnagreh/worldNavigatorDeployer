package com.worldNavigator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/MenuServer")
public class Menu_Server extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Menu_Server() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Step 1: set content type
        response.setContentType("text/html");

        // Step 2: get the printwriter
        PrintWriter out = response.getWriter();

        String command = request.getParameter("command");

        Maps maps = new Maps();
        maps.addMap("map.json");

        Menu menu = new Menu();
        menu.setMaps(maps, command);
        menu.start();

        // Step 3: generate the HTML content
        out.println("<html><body>");

        out.println("Player Name is: "
                + menu.playerViewer.getName());
        out.println("<form action=\"CommandsServer\" method=\"GET\">\n" +
                "\n" +
                "    Type your command: <input type=\"text\" name=\"command\"/>\n" +
                "\n" +
                "    <br/><br/>\n" +
                "\n" +
                "    <input type=\"submit\" value=\"Submit\"/>\n" +
                "\n" +
                "    <p>try4</p>\n" +
                "\n" +
                "</form>");

        out.println("</body></html>");

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
}
