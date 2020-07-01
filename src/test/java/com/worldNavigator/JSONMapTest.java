package com.worldNavigator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class JSONMapTest {
  MapFactory map;
  JSONArray jsonRooms;
  List<Room> rooms;
  JSONObject n_wall;
  JSONObject e_wall;
  JSONObject s_wall;
  JSONObject w_wall;
  String[] itemsArray = {
    "chest", "door", "window", "table", "mirror", "painting", "seller", "gate", "safe"
  };
  ArrayList<String> items = new ArrayList<>(Arrays.asList(itemsArray));
  String[] contentArray = {"golds", "flashLights", "keys", "masterKeys"};
  ArrayList<String> content = new ArrayList<>(Arrays.asList(contentArray));

  public JSONMapTest() {
    Maps maps = new Maps();
    this.map = maps.generate("map.json");
    this.jsonRooms = this.map.jsonRooms;
    this.rooms = this.map.rooms;
  }

  @Test
  public void endTimeTest() {
    assertTrue(this.map.endTime >= 0);
  }

  @Test
  public void mapNameTest() {
    assertTrue(this.map.mapName.length() > 0);
  }

  @Test
  public void roomsListTest() {
    assertFalse(this.map.rooms.isEmpty());
  }

  @Test
  public void wallsNamesTest() {
    for (Object room : this.jsonRooms) {
      this.n_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("n_wall"));
      this.e_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("e_wall"));
      this.s_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("n_wall"));
      this.w_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("w_wall"));

      assertNotNull(this.n_wall);
      assertNotNull(this.e_wall);
      assertNotNull(this.s_wall);
      assertNotNull(this.w_wall);
    }
  }

  @Test
  public void itemsNamesTest() {
    for (Object room : this.jsonRooms) {
      this.n_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("n_wall"));
      this.e_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("e_wall"));
      this.s_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("n_wall"));
      this.w_wall = (JSONObject) (((JSONObject) ((JSONObject) room).get("room")).get("w_wall"));

      for (Object itemName : this.n_wall.keySet()) {
        String name = itemName.toString();
        assertTrue(this.items.contains(name));
      }
    }
  }

  @Test
  public void itemsLocationsTest() {
    for (Room room : this.rooms) {
      Wall n_wall = room.walls.get("north");
      Wall e_wall = room.walls.get("east");
      Wall s_wall = room.walls.get("south");
      Wall w_wall = room.walls.get("west");

      for (Item item : n_wall.items.values()) {
        assertNotNull(item.getLocation());
      }
      for (Item item : e_wall.items.values()) {
        assertNotNull(item.getLocation());
      }
      for (Item item : s_wall.items.values()) {
        assertNotNull(item.getLocation());
      }
      for (Item item : w_wall.items.values()) {
        assertNotNull(item.getLocation());
      }
    }
  }

  @Test
  public void itemsLocationsNamingTest() {
    String pattern = "[a-e][1-5]";
    for (Room room : this.rooms) {
      Wall n_wall = room.walls.get("north");
      Wall e_wall = room.walls.get("east");
      Wall s_wall = room.walls.get("south");
      Wall w_wall = room.walls.get("west");

      for (Item item : n_wall.items.values()) {
        assertTrue(item.getLocation().matches(pattern));
      }
      for (Item item : e_wall.items.values()) {
        assertTrue(item.getLocation().matches(pattern));
      }
      for (Item item : s_wall.items.values()) {
        assertTrue(item.getLocation().matches(pattern));
      }
      for (Item item : w_wall.items.values()) {
        assertTrue(item.getLocation().matches(pattern));
      }
    }
  }

  @Test
  public void contentsNamingTest() {
    for (Room room : this.rooms) {
      Wall n_wall = room.walls.get("north");
      Wall e_wall = room.walls.get("east");
      Wall s_wall = room.walls.get("south");
      Wall w_wall = room.walls.get("west");

      for (Item item : n_wall.items.values()) {
          for (String contentName : item.checkBehavior.getContents().getContents().keySet()) {
            assertTrue(this.content.contains(contentName));
          }
      }
      for (Item item : e_wall.items.values()) {
          for (String contentName : item.checkBehavior.getContents().getContents().keySet()) {
          assertTrue(this.content.contains(contentName));
        }
      }
      for (Item item : s_wall.items.values()) {
          for (String contentName : item.checkBehavior.getContents().getContents().keySet()) {
          assertTrue(this.content.contains(contentName));
        }
      }
      for (Item item : w_wall.items.values()) {
        for (String contentName : item.checkBehavior.getContents().getContents().keySet()) {
          assertTrue(this.content.contains(contentName));
        }
      }
    }
  }
}
