package com.worldNavigator;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;

/**
 * Servlet implementation class StudentServlet
 */
@WebServlet(name = "client", urlPatterns = "/client")
public class Client extends HttpServlet {
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        // Step 3: generate the HTML content
        java.io.PrintWriter out = response.getWriter();

        out.println("<html><body>");

        out.println("<form action=\"\" method=\"GET\">\n" +
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

}