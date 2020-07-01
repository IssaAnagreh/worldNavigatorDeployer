package com.worldNavigator;
import org.json.simple.JSONObject;

public class Gate extends Item implements NextGoing {
  public final String NAME;
  private final Boolean IS_GOLDEN;
  private final String NEXT_ROOM;
  private final String LOCATION;

  public Gate(JSONObject gate) {
    this.NAME = gate.get("name").toString();
    this.LOCATION = gate.get("location").toString();
    this.IS_GOLDEN = gate.get("golden").equals("true");
    this.NEXT_ROOM = gate.get("to").toString();

    if (gate.get("key") != null) {
      super.setUseKeyBehavior(new Openable(gate, "Gate"));
    }
    super.setCheckBehavior(new Uncheckable());
  }

  @Override
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
    return "gate";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Gate) {
      Gate gate = (Gate) o;
      return gate.NAME.equals(this.NAME) && gate.LOCATION.equals(this.LOCATION);
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
    return "Gate: " + this.NAME + ", in " + this.LOCATION;
  }
}
