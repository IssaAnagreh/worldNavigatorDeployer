package com.worldNavigator;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client {
    public static void main(String[] args) {
            try {
                Socket s = new Socket("localhost", 8080);
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                while (true) {
                    System.out.println("Enter map number");
                    Scanner sc = new Scanner(System.in);

                    dout.writeUTF(sc.nextLine());
                    dout.flush();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
    }
}