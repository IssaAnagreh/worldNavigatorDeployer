package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Table extends Item {
  private final String LOCATION;
  private final String NAME = "Table";
  Game game;

  public Table(JSONObject table, Game game) {
    this.game = game;
    this.LOCATION = table.get("location").toString();
    super.setCheckBehavior(new Unlocked_Checkable(table, this.LOCATION));

    this.generateCollection();
  }

  private void generateCollection() {
    HashMap<String, String> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("location", this.LOCATION);
    dbHashMap.put("contents", super.checkBehavior.getContents().getContents().toString());
    this.game.db.insertOne("Tables", dbHashMap);
  }

  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME;
  }

  public String getType() {
    return "table";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Table) {
      Table table = (Table) o;
      return table.LOCATION.equals(this.LOCATION);
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
    return "Table: " + this.NAME + " in: " + this.LOCATION;
  }
}
