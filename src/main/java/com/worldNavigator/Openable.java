package com.worldNavigator;
import org.json.simple.JSONObject;

import java.util.List;

public class Openable implements UseKeyBehavior {
  private Key key;
  private Boolean isLocked;
  private final String NAME;

  public Openable(JSONObject item, String name) {
    this.setKey(new Key(item.get("key").toString()));
    this.init_isLocked(item.get("is_locked").equals("true"));
    this.NAME = name;
  }

  @Override
  public Boolean get_isLocked() {
    return isLocked;
  }

  @Override
  public void init_isLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  @Override
  public void set_isLocked(Boolean isLocked) {
    this.isLocked = (this.isLocked != null && !this.isLocked) ? false : isLocked;
  }

  @Override
  public String getKey() {
    return key.toString();
  }

  @Override
  public void setKey(Key key) {
    this.key = key;
  }

  @Override
  public String useKey(List<KeyChecker> keys) {
    String print = "";
    if (this.get_isLocked() != null && this.get_isLocked()) {
      boolean locked = true;
      for (KeyChecker keyItem : keys) {
        locked = locked && !keyItem.unlock(this);
        this.set_isLocked(locked);
      }
      if (locked) {
        print = "Look for a suitable key";
      } else {
        print = this.NAME + " is open now";
      }
    } else {
      print = this.NAME + " is already open";
    }
    return print;
  }

  @Override
  public String toString() {
    return "Openable";
  }
}
