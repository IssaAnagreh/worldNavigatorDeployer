package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.*;

public class ItemsFactory {
  public Map<String, Item> items = new HashMap<>();

  public ItemsFactory(JSONObject wall) {
    prepareDoor(wall);
    prepareChest(wall);
    prepareMirror(wall);
    prepareSeller(wall);
    preparePainting(wall);
    prepareSafe(wall);
    prepareGate(wall);
    prepareTable(wall);
    prepareWindow(wall);
  }

  private JSONObject castToJSONObject(Object o) {
    return (JSONObject) o;
  }

  private void prepareDoor(JSONObject wall) {
    Object door = wall.get("door");
    if (door != null) {
      items.put("door", new Door(castToJSONObject(door)));
    }
  }

  private void prepareChest(JSONObject wall) {
    Object chest = wall.get("chest");
    if (chest != null) {
      items.put("chest", new Chest(castToJSONObject(chest)));
    }
  }

  private void prepareMirror(JSONObject wall) {
    Object mirror = wall.get("mirror");
    if (mirror != null) {
      items.put("mirror", new Mirror(castToJSONObject(mirror)));
    }
  }

  private void preparePainting(JSONObject wall) {
    Object painting = wall.get("painting");
    if (painting != null) {
      items.put("painting", new Painting(castToJSONObject(painting)));
    }
  }

  private void prepareSeller(JSONObject wall) {
    Object seller = wall.get("seller");
    if (seller != null) {
      items.put("seller", new Seller(castToJSONObject(seller)));
    }
  }

  private void prepareSafe(JSONObject wall) {
    Object safe = wall.get("safe");
    if (safe != null) {
      items.put("safe", new Safe(castToJSONObject(safe)));
    }
  }

  private void prepareWindow(JSONObject wall) {
    Object window = wall.get("window");
    if (window != null) {
      items.put("window", new Window(castToJSONObject(window)));
    }
  }

  private void prepareTable(JSONObject wall) {
    Object table = wall.get("table");
    if (table != null) {
      items.put("table", new Table(castToJSONObject(table)));
    }
  }

  private void prepareGate(JSONObject wall) {
    Object gate = wall.get("gate");
    if (gate != null) {
      items.put("gate", new Gate(castToJSONObject(gate)));
    }
  }

  public String checkItemByLocation(String location) {
    for (Item item : this.items.values()) {
      if (item.compareTo(location) == 0) {
        return "This location has: " + item.getName();
      }
    }
    return "Nothing in this location";
  }

  public String getType(String location) {
    for (Item item : this.items.values()) {
      if (item.compareTo(location) == 0) {
        return item.getType();
      }
    }
    return "";
  }

  public Item getItem(String location) {
    for (Item item : this.items.values()) {
      if (item.compareTo(location) == 0) {
        return item;
      }
    }
    return new Space();
  }

  @Override
  public String toString() {
    return "Item Factory";
  }
}
