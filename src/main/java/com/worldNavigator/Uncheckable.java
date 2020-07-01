package com.worldNavigator;
import java.util.HashMap;
import java.util.Map;

public class Uncheckable implements CheckBehavior {

  public Map<String, Object> checkContent(String location, PlayerModel playerModel) {
    return new HashMap();
  }

  public void acquireContents(String location, PlayerModel playerModel) {
    // this method is unuseful in Uncheckable items
  }

  public ContentManager getContents() {
    return new ContentManager();
  }

  @Override
  public String toString() {
    return "Uncheckable";
  }
}
