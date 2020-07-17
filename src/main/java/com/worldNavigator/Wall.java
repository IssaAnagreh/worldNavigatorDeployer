package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wall {
  public final String NAME;
  public ItemsFactory itemsFactory;
  public Map<String, Item> items = new HashMap<>();
  public ArrayList<String> locations = new ArrayList<>();
  public Map<String, Boolean> itemsLocations = new HashMap<>();
  int roomNumber;
  Game game;

  public Wall(String name, JSONObject wall, int roomNumber, Game game) {
    this.game = game;
    this.NAME = name;
    this.roomNumber = roomNumber;

    this.generateWall(wall);
    this.wallLocations(name);
    this.generateCollection();
  }

  private void generateWall(JSONObject wall) {
    this.itemsFactory = new ItemsFactory(wall, this.game);
    this.items = this.itemsFactory.items;
    this.itemsLocations = this.itemsFactory.itemsLocations;
  }

  private void wallLocations(String name) {
    switch (name) {
      case "north_wall":
        this.locations.add("a1");
        this.locations.add("b1");
        this.locations.add("c1");
        this.locations.add("d1");
        this.locations.add("e1");
        break;
      case "west_wall":
        this.locations.add("a1");
        this.locations.add("a2");
        this.locations.add("a3");
        this.locations.add("a4");
        this.locations.add("a5");
        break;
      case "south_wall":
        this.locations.add("a5");
        this.locations.add("b5");
        this.locations.add("c5");
        this.locations.add("d5");
        this.locations.add("e5");
        break;
      default:
        this.locations.add("e1");
        this.locations.add("e2");
        this.locations.add("e3");
        this.locations.add("e4");
        this.locations.add("e5");
        break;
    }
  }

  private void generateCollection() {
    HashMap<String, String> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("roomNumber", Integer.toString(this.roomNumber));
    dbHashMap.put("items", this.items.toString());
    dbHashMap.put("itemsLocations", this.itemsLocations.toString());
    this.game.db.insertOne("Walls", dbHashMap);
  }

  public String checkItems() {
    return this.items.isEmpty() ? "Nothing to look at" : this.items.values().toString();
  }

  public boolean hasItems() {
    return !this.items.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Wall) {
      Wall wall = (Wall) o;
      return this.NAME.equals(wall.NAME) && this.items.equals(wall.items);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.NAME.hashCode() + this.items.hashCode();
  }

  @Override
  public String toString() {
    return "Wall name: " + this.NAME + ", items: " + checkItems();
  }
}
