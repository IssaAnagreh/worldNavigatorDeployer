package com.worldNavigator;

import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Chest extends Item {
  private final String NAME;
  private final String LOCATION;
  Game game;

  public Chest(JSONObject chest, Game game) {
    this.game = game;
    this.NAME = chest.get("name").toString();
    this.LOCATION = chest.get("location").toString();

    if (chest.get("key") == null) {
      super.setCheckBehavior(new Unlocked_Checkable(chest, this.LOCATION));
    } else {
      super.setUseKeyBehavior(new Openable(chest, "Chest"));
      super.setCheckBehavior(new Locked_Checkable(chest, this.LOCATION, super.useKeyBehavior));
    }

    this.generateCollection();
  }

  public Chest(JSONObject chest) {
    this.NAME = chest.get("name").toString();
    this.LOCATION = chest.get("location").toString();

    if (chest.get("key") == null) {
      super.setCheckBehavior(new Unlocked_Checkable(chest, this.LOCATION));
    } else {
      super.setUseKeyBehavior(new Openable(chest, "Chest"));
      super.setCheckBehavior(new Locked_Checkable(chest, this.LOCATION, super.useKeyBehavior));
    }
  }

  private void generateCollection() {
    HashMap<String, Object> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("location", this.LOCATION);
    dbHashMap.put("type", this.getType());
    Document document = new Document();
    for (String key : super.checkBehavior.getContents().contentsStrings.keySet()) {
      document.append(key, super.checkBehavior.getContents().contentsStrings.get(key));
    }
    dbHashMap.put("contents", document);
    this.game.db.insertOne("Chests", dbHashMap);
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
    return "chest";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Chest) {
      Chest chest = (Chest) o;
      return chest.NAME.equals(this.NAME) && chest.LOCATION.equals(this.LOCATION);
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
    System.out.println("super.useKeyBehavior: "+super.useKeyBehavior);
    if (super.useKeyBehavior == null) {
      return "UNLOCKED Chest: " + this.NAME + ", in: " + this.LOCATION;
    }
    return (super.useKeyBehavior.get_isLocked() != null && super.useKeyBehavior.get_isLocked())
        ? "LOCKED Chest: " + this.NAME + ", in: " + this.LOCATION
        : "UNLOCKED Chest: " + this.NAME + ", in: " + this.LOCATION;
  }
}
