package com.worldNavigator;

import org.json.simple.JSONObject;

import java.util.*;

public class ItemsFactory {
    public Map<String, Item> items = new HashMap<>();
    public Map<String, String> itemsLocations = new HashMap<>();
    Game game;
    Wall wall;

    public ItemsFactory(JSONObject jsonWall, Wall wall) {
        this.game = wall.game;
        this.wall = wall;
        if (jsonWall != null) {
            prepareDoor(jsonWall);
            prepareChest(jsonWall);
            prepareMirror(jsonWall);
            prepareSeller(jsonWall);
            preparePainting(jsonWall);
            prepareSafe(jsonWall);
            prepareGate(jsonWall);
            prepareTable(jsonWall);
            prepareWindow(jsonWall);
        }
    }

    public ItemsFactory() {
    }

    private JSONObject castToJSONObject(Object o) {
        return (JSONObject) o;
    }

    private void prepareDoor(JSONObject wall) {
        Object door = wall.get("door");
        if (door != null) {
            itemsLocations.put(castToJSONObject(door).get("location").toString(), "door");
            this.wall.room.addItemLocation(castToJSONObject(door).get("location").toString());
            items.put("door", new Door(castToJSONObject(door), this.game));
        }
    }

    private void prepareChest(JSONObject wall) {
        Object chest = wall.get("chest");
        if (chest != null) {
            itemsLocations.put(castToJSONObject(chest).get("location").toString(), "chest");
            items.put("chest", new Chest(castToJSONObject(chest), this.game));
        }
    }

    private void prepareMirror(JSONObject wall) {
        Object mirror = wall.get("mirror");
        if (mirror != null) {
            itemsLocations.put(castToJSONObject(mirror).get("location").toString(), "mirror");
            items.put("mirror", new Mirror(castToJSONObject(mirror), this.game));
        }
    }

    private void preparePainting(JSONObject wall) {
        Object painting = wall.get("painting");
        if (painting != null) {
            itemsLocations.put(castToJSONObject(painting).get("location").toString(), "painting");
            items.put("painting", new Painting(castToJSONObject(painting), this.game));
        }
    }

    public void preparePainting(JSONObject painting, Boolean direct) {
        if (painting != null) {
            itemsLocations.put(painting.get("location").toString(), "painting");
            items.put("painting", new Painting(castToJSONObject(painting), this.game));
        }
    }

    private void prepareSeller(JSONObject wall) {
        Object seller = wall.get("seller");
        if (seller != null) {
            itemsLocations.put(castToJSONObject(seller).get("location").toString(), "seller");
            items.put("seller", new Seller(castToJSONObject(seller), this.game));
        }
    }

    private void prepareSafe(JSONObject wall) {
        Object safe = wall.get("safe");
        if (safe != null) {
            itemsLocations.put(castToJSONObject(safe).get("location").toString(), "safe");
            items.put("safe", new Safe(castToJSONObject(safe), this.game));
        }
    }

    private void prepareWindow(JSONObject wall) {
        Object window = wall.get("window");
        if (window != null) {
            itemsLocations.put(castToJSONObject(window).get("location").toString(), "window");
            items.put("window", new Window(castToJSONObject(window), this.game));
        }
    }

    private void prepareTable(JSONObject wall) {
        Object table = wall.get("table");
        if (table != null) {
            itemsLocations.put(castToJSONObject(table).get("location").toString(), "table");
            items.put("table", new Table(castToJSONObject(table), this.game));
        }
    }

    private void prepareGate(JSONObject wall) {
        Object gate = wall.get("gate");
        if (gate != null) {
            itemsLocations.put(castToJSONObject(gate).get("location").toString(), "gate");
            items.put("gate", new Gate(castToJSONObject(gate), this.game));
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
