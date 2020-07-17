package com.worldNavigator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class Menu {
    private Maps maps;
    private int map_index;

    public void restart(String name, PlayerModel playerModel) throws IOException {
//    String mapName = this.maps.maps.get(this.map_index).mapName;
//    MapFactory new_map = this.maps.generate(mapName);
//    this.maps.replace(new_map, this.map_index);
//    preparePlayer(name);
//    playerModel.startGame();
    }

    @Override
    public String toString() {
        return "Menu";
    }
}
