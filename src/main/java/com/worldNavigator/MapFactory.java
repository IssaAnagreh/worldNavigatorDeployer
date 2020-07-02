package com.worldNavigator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MapFactory {
  private String name = "empty";
  public List<Room> rooms = new ArrayList<>();
  public int endTime;
  private int room_counter = 0;
  public final String mapName;
  public Map<String, Object> contents;
  public String location;
  public String orientation;
  public int roomIndex;
  public JSONArray jsonRooms;

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
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    try (FileReader reader = new FileReader("map.json")) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);
      this.name = "try";

      JSONArray maps = castToJSONArray(obj);
      maps.forEach(map -> parseMapObject(castToJSONObject(map)));

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      this.name = jsonParser.toString();
    } catch (IOException e) {
      e.printStackTrace();
      this.name = "IOException";
    } catch (ParseException e) {
      e.printStackTrace();
      this.name = "ParseException";
    }
  }

  private void parseMapObject(JSONObject map) {
    if (map == null) {
      throw new IllegalArgumentException();
    } else {
      // Get map object within list
      name = "JSON map";//(String) map.get("name");
      endTime = Integer.parseInt(map.get("end_time").toString());
      ContentManager contentManager = new ContentManager();
      String player_string = "player";
      HashMap<String, Object> player_details = (HashMap) map.get(player_string);
      contentManager.managePlayer(player_details);
      this.contents = contentManager.getContents();
      this.location =
          (player_details).get("location") != null
              ? (player_details).get("location").toString()
              : "c3";
      this.orientation =
          (player_details).get("orientation") != null
              ? (player_details).get("orientation").toString()
              : "north";
      this.roomIndex =
          (player_details).get("roomIndex") != null
              ? Integer.parseInt((player_details).get("roomIndex").toString())
              : 0;

      this.jsonRooms = castToJSONArray(map.get("rooms"));
      this.jsonRooms.forEach(room -> parseRoomObject(castToJSONObject(room)));
    }
  }

  private void parseRoomObject(JSONObject room) {
    // Get room object within list
    JSONObject roomObject;
    try {
      roomObject = castToJSONObject(room.get("room"));
    } catch (Exception e) {
      throw new NullPointerException();
    }
    this.rooms.add(new Room(roomObject, room_counter));
    this.room_counter++;
  }

  @Override
  public String toString() {
    return name;
  }
}
