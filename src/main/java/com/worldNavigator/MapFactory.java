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
  private String name;
  public List<Room> rooms = new ArrayList<>();
  public int endTime;
  public String mapName;
  public String location;
  public JSONArray jsonRooms;
  public JSONObject jsonMap;
  private int roomsCount;

  private JSONObject castToJSONObject(Object o) {
    return (JSONObject) o;
  }

  private JSONArray castToJSONArray(Object o) {
    return (JSONArray) o;
  }

  @SuppressWarnings("unchecked")
  public MapFactory(String mapName) {
    this.mapName = mapName;
    this.name = "MapFactory";

    JSONParser jsonParser = new JSONParser();

    File file = new File("map.json");
    try (FileReader reader = new FileReader(file)) {
      Object obj = jsonParser.parse(reader);

      JSONArray maps = castToJSONArray(obj);

      maps.forEach(map -> parseMapObject(castToJSONObject(map)));
      System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());
      this.name = "FileNotFoundException";
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
      System.out.println(map.get("end_time"));
      name = (String) map.get("name");
      endTime = Integer.parseInt(map.get("end_time").toString());
      this.jsonMap = map;
      this.jsonRooms = castToJSONArray(map.get("rooms"));
      this.jsonRooms.forEach(room -> parseRoomObject(castToJSONObject(room)));
    }
  }

  private void parseRoomObject(JSONObject room) {
    JSONObject roomObject;
    try {
      roomObject = castToJSONObject(room.get("room"));
    } catch (Exception e) {
      throw new NullPointerException();
    }
    this.rooms.add(new Room(roomObject, roomsCount));
    this.roomsCount++;
  }

  @Override
  public String toString() {
    return name;
  }
}
