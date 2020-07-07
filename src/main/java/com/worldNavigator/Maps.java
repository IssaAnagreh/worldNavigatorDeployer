package com.worldNavigator;
import java.util.ArrayList;
import java.util.List;

public class Maps {
  public List<MapFactory> maps = new ArrayList<>();

  public MapFactory generate(String json) {
    MapFactory mapFactory = new MapFactory(json);
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
