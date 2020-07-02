package com.worldNavigator;

import javafx.application.Application;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        new Thread( () -> new Server()).start();

        Maps maps = new Maps();
        maps.addMap("map.json");

        Menu menu = new Menu();
        menu.setMaps(maps);
        menu.start();

    }
}
