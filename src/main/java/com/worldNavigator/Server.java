package com.worldNavigator;

import java.io.*;
import java.net.*;

public class Server {

    public Server(Menu menu) {
        try {
            ServerSocket ss = new ServerSocket(8080);
            Socket s = ss.accept();//establishes connection

            System.out.println("Connection is established on port 8080");

            DataInputStream inputFromClient = new DataInputStream(
                    s.getInputStream());
            while (true) {
                // Receive radius from the client
                String str = (String) inputFromClient.readUTF();

                PrintWriter out = new PrintWriter(s.getOutputStream());

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
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}