package com.worldNavigator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MapFactory {
  public String name;
  public List<Room> rooms = new ArrayList<>();
  public int endTime;
  public String mapName;
  public JSONArray jsonRooms;
  public JSONObject jsonMap;
  private int roomsCount;
  public Map<Integer, Boolean> starterRooms = new HashMap<>();
  Game game;

  private JSONObject castToJSONObject(Object o) {
    return (JSONObject) o;
  }

  private JSONArray castToJSONArray(Object o) {
    return (JSONArray) o;
  }

  @SuppressWarnings("unchecked")
  public MapFactory(String mapName, Game game) {
    this.mapName = "MapFactory";
    this.game = game;
    
    JSONParser jsonParser = new JSONParser();

    File file = new File("map.json");
    try (FileReader reader = new FileReader(file)) {
      Object obj = jsonParser.parse(reader);

      JSONArray maps = castToJSONArray(obj);
      System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());

      maps.forEach(map -> parseMapObject(castToJSONObject(map)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());
      this.name = file.getAbsolutePath();//"FileNotFoundException";
    } catch (IOException e) {
      e.printStackTrace();
      this.name = "IOException";
    } catch (ParseException e) {
      e.printStackTrace();
      System.out.println("ParseException");
      this.name = "ParseException";
    }
  }

  private void parseMapObject(JSONObject map) {
    if (map == null) {
      throw new IllegalArgumentException();
    } else {
      this.name = (String) map.get("name");
      this.endTime = Integer.parseInt(map.get("endTime").toString());

      this.generateCollection(map);

      this.jsonMap = map;
      this.jsonRooms = castToJSONArray(map.get("rooms"));
      this.jsonRooms.forEach(room -> {
        try {
          parseRoomObject(castToJSONObject(room));
        } catch (ParseException e) {
          e.printStackTrace();
        }
      });
    }
  }

  private void generateCollection(JSONObject map) {
    HashMap<String, Object> dbHashMap = new HashMap<>();
    dbHashMap.put("endTime", map.get("endTime").toString());
    dbHashMap.put("name", this.name);
    dbHashMap.put("game", Integer.toString(this.game.id));
    this.game.db.insertOne("Maps", dbHashMap);
  }

  private void parseRoomObject(JSONObject room) throws ParseException {
    JSONObject roomObject;
    try {
      roomObject = castToJSONObject(room.get("room"));
    } catch (Exception e) {
      throw new NullPointerException();
    }
    if (roomObject.get("starter") != null) {
      this.starterRooms.put(this.starterRooms.size(), Boolean.parseBoolean(roomObject.get("starter").toString()));
    } else {
      this.starterRooms.put(this.starterRooms.size(), false);
    }
    this.rooms.add(new Room(roomObject, this.roomsCount, this.game));
    this.roomsCount++;
  }

  @Override
  public String toString() {
    return name;
  }
}
