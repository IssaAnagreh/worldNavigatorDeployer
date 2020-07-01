package com.worldNavigator;
import org.json.simple.JSONObject;

public class Mirror extends Item {
  private final String LOCATION;
  private final String NAME = "Mirror";

  public Mirror(JSONObject mirror) {
    this.LOCATION = mirror.get("location").toString();
    super.setCheckBehavior(new Unlocked_Checkable(mirror, this.LOCATION));
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
