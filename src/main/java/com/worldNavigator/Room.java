package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Room {
  public HashMap<String, Wall> walls = new HashMap<>();
  public final String ROOM_NAME;
  private Boolean isLit;
  public Boolean lightSwitch;
  public final Integer ROOM_NUMBER;

  public Room(JSONObject room, int room_counter) {
    this.ROOM_NAME =
        room.get("name") == null ? "room_" + (room_counter + 1) : room.get("name").toString();
    this.ROOM_NUMBER = room_counter;
    generateRoom(room);
  }

  private JSONObject castToJSONObject(Object o) {
    return (JSONObject) o;
  }

  private void generateRoom(JSONObject room) {
    try {
      Wall north_wall = new Wall("north_wall", castToJSONObject(room.get("n_wall")));
      Wall east_wall = new Wall("east_wall", castToJSONObject(room.get("e_wall")));
      Wall south_wall = new Wall("south_wall", castToJSONObject(room.get("s_wall")));
      Wall west_wall = new Wall("west_wall", castToJSONObject(room.get("w_wall")));

      walls.put("north", north_wall);
      walls.put("east", east_wall);
      walls.put("south", south_wall);
      walls.put("west", west_wall);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    this.isLit = room.get("lit") == null || Boolean.parseBoolean(room.get("lit").toString());
    this.lightSwitch =
        (room.get("switch") != null) && Boolean.parseBoolean(room.get("switch").toString());
  }

  public void switchLights(PlayerModel playerModel) {
    if (this.lightSwitch) {
      this.isLit = !this.isLit;
      if (this.isLit) {
        playerModel.notify_player("Room is lit now");
      } else {
        playerModel.notify_player("Room is dark now");
      }
    } else {
      playerModel.notify_player("This room has no lights switch, use a flash light");
    }
  }

  public int useFlashLight(int flashLights, PlayerModel playerModel) {
    if (this.isLit) {
      return flashLights;
    } else {
      this.isLit = true;
      playerModel.notify_player("Room is lit now");
      return --flashLights;
    }
  }

  public Boolean getIsLit() {
    return isLit;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Room) {
      Room room = (Room) o;
      return room.ROOM_NAME.equals(this.ROOM_NAME) && (room.ROOM_NUMBER == this.ROOM_NUMBER);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.ROOM_NAME.hashCode() + this.ROOM_NUMBER.hashCode();
  }

  @Override
  public String toString() {
    return "You are in room: " + this.ROOM_NAME;
  }
}
