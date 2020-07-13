package com.worldNavigator;

import java.util.ArrayList;
import java.util.List;

public class Players {
    public static List<PlayerViewer> players = new ArrayList<>();
    public void addPlayer(PlayerViewer player) {
        players.add(player);
    }
}
