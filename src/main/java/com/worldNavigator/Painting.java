package com.worldNavigator;
import org.json.simple.JSONObject;

public class Painting extends Item {
  public final String LOCATION;
  public final String NAME = "Painting";

  public Painting(JSONObject painting) {
    this.LOCATION = painting.get("location").toString();
    super.setCheckBehavior(new Unlocked_Checkable(painting, this.LOCATION));
  }

  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME;
  }

  public String getType() {
    return "painting";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Painting) {
      Painting painting = (Painting) o;
      return painting.LOCATION.equals(this.LOCATION);
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
    return "Painting: " + this.NAME + ", in: " + this.LOCATION;
  }
}
