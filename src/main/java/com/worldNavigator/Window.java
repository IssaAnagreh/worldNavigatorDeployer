package com.worldNavigator;
import org.json.simple.JSONObject;

public class Window extends Item {
  public final String NAME;
  private final String LOCATION;

  public Window(JSONObject window) {
    this.NAME = "Window";
    this.LOCATION = window.get("location").toString();

    super.setCheckBehavior(new Uncheckable());
  }

  @Override
  public String getLocation() {
    return this.LOCATION;
  }

  @Override
  public String getName() {
    return this.NAME + " Looks to the blue sky";
  }

  @Override
  public String getType() {
    return "Window";
  }

  @Override
  public int compareTo(String location) {
    return this.getLocation().compareTo(location);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Window) {
      Window window = (Window) o;
      return window.LOCATION.equals(this.LOCATION);
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
    return "Window: " + this.NAME + ", in " + this.LOCATION;
  }
}
