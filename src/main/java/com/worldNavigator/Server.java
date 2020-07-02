package com.worldNavigator;

import java.io.*;
import java.net.*;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Server {

    public Server() {
        try {
            ServerSocket ss = new ServerSocket(8080);
            Socket s = ss.accept();//establishes connection

            System.out.println("Connection is established on port 8080");

            DataInputStream inputFromClient = new DataInputStream(
                    s.getInputStream());
            while (true) {
                // Receive radius from the client
                String str = (String) inputFromClient.readUTF();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}