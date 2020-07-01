package com.worldNavigator;

import org.json.simple.JSONObject;

public class Door extends Item implements NextGoing {
  public final String NAME;
  private final Boolean IS_GOLDEN;
  private final String NEXT_ROOM;
  private final String LOCATION;

  public Door(JSONObject door) {
    this.NAME = door.get("name").toString();
    this.LOCATION = door.get("location").toString();
    this.IS_GOLDEN = door.get("golden").equals("true");
    this.NEXT_ROOM = door.get("to").toString();

    if (door.get("key") != null) {
      super.setUseKeyBehavior(new Openable(door, "Door"));
    }
    super.setCheckBehavior(new Uncheckable());
  }

  public String getNextRoom() {
    if (super.useKeyBehavior != null && super.useKeyBehavior.get_isLocked()) {
      return "locked";
    } else {
      if (this.getGolden() != null && this.getGolden()) {
        return "golden";
      }
      return this.NEXT_ROOM;
    }
  }

  private Boolean getGolden() {
    return this.IS_GOLDEN;
  }

  @Override
  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME;
  }

  @Override
  public String getType() {
    return "door";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Door) {
      Door door = (Door) o;
      return door.NAME.equals(this.NAME) && door.LOCATION.equals(this.LOCATION);
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
    return "Door: " + this.NAME + ", in " + this.LOCATION;
  }
}
