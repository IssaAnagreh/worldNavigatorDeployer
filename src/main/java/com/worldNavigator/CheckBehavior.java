package com.worldNavigator;

import java.util.Map;

public interface CheckBehavior {
  Map<String, Object> checkContent(String location, PlayerModel playerModel);

  void acquireContents(String location, PlayerModel playerModel);

  ContentManager getContents();
}
