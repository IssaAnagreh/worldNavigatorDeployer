package com.worldNavigator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Maps maps = new Maps();
        maps.addMap("map.json");

        Menu menu = new Menu();
        menu.setMaps(maps);

//        new Thread( () -> new Server()).start();

        menu.start();

    }
}
