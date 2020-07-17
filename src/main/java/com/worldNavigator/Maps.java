package com.worldNavigator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maps {
  public List<MapFactory> maps = new ArrayList<>();
  public Map<Integer, Boolean> starterRooms = new HashMap<>();
  Game game;

  public Maps(Game game) {
    this.game = game;
  }

  public Maps() {
  }

  public MapFactory generate(String json) {
    MapFactory mapFactory = new MapFactory(json, this.game);
    this.starterRooms = mapFactory.starterRooms;
    return mapFactory;
  }

  public void addMap(String json) {
    maps.add(this.generate(json));
  }

  public void replace(MapFactory map, int index) {
    this.maps.set(index, map);
  }

  @Override
  public String toString() {
    return "Maps: " + this.maps.toString();
  }
}
