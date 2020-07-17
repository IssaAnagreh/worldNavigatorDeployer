package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Mirror extends Item {
  private final String LOCATION;
  private final String NAME = "Mirror";
  Game game;

  public Mirror(JSONObject mirror, Game game) {
    this.game = game;
    this.LOCATION = mirror.get("location").toString();
    super.setCheckBehavior(new Unlocked_Checkable(mirror, this.LOCATION));

    this.generateCollection();
  }

  private void generateCollection() {
    HashMap<String, String> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("location", this.LOCATION);
    dbHashMap.put("contents", super.checkBehavior.getContents().getContents().toString());
    this.game.db.insertOne("Mirrors", dbHashMap);
  }

  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME + ", You See a silhouette of you";
  }

  public String getType() {
    return "mirror";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Mirror) {
      Mirror mirror = (Mirror) o;
      return mirror.LOCATION.equals(this.LOCATION);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.LOCATION.hashCode();
  }

  @Override
  public String toString() {
    return "Mirror: " + this.NAME + " in: " + this.LOCATION;
  }
}
