package com.worldNavigator;
import java.util.List;

public interface UseKeyBehavior {
  Boolean get_isLocked();

  void init_isLocked(Boolean isLocked);

  void set_isLocked(Boolean isLocked);

  String getKey();

  void setKey(Key key);

  String useKey(List<KeyChecker> keys);
}
