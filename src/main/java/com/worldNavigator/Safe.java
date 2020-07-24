package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Safe extends Item {
  private final String NAME;
  private final String LOCATION;
  Game game;

  public Safe(JSONObject safe, Game game) {
    this.game = game;
    this.NAME = safe.get("name").toString();
    this.LOCATION = safe.get("location").toString();

    if (safe.get("key") != null) {
      super.setUseKeyBehavior(new Openable(safe, "Safe"));
      super.setCheckBehavior(new Locked_Checkable(safe, this.LOCATION, super.useKeyBehavior));
    } else {
      super.setCheckBehavior(new Unlocked_Checkable(safe, this.LOCATION));
    }

    this.generateCollection();
  }

  private void generateCollection() {
    HashMap<String, Object> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("location", this.LOCATION);
    dbHashMap.put("type", this.getType());
    dbHashMap.put("contents", super.checkBehavior.getContents().getContents().toString());
    this.game.db.insertOne("Safes", dbHashMap);
  }

  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME;
  }

  @Override
  public String getType() {
    return "safe";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Safe) {
      Safe safe = (Safe) o;
      return safe.NAME.equals(this.NAME) && safe.LOCATION.equals(this.LOCATION);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.NAME.hashCode() + this.LOCATION.hashCode();
  }

  @Override
  public String toString() {
    return (super.useKeyBehavior.get_isLocked() != null && super.useKeyBehavior.get_isLocked())
        ? "LOCKED Safe: " + this.NAME + ", in: " + this.LOCATION
        : "UNLOCKED Safe: " + this.NAME + ", in: " + this.LOCATION;
  }
}
