package com.worldNavigator;
import org.json.simple.JSONObject;

public class Table extends Item {
  private final String LOCATION;
  private final String NAME = "Table";

  public Table(JSONObject table) {
    this.LOCATION = table.get("location").toString();
    super.setCheckBehavior(new Unlocked_Checkable(table, this.LOCATION));
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
