package com.worldNavigator;
import java.util.List;

public abstract class Item {
  CheckBehavior checkBehavior;
  UseKeyBehavior useKeyBehavior;

  public void setCheckBehavior(CheckBehavior cb) {
    this.checkBehavior = cb;
  }

  public void setUseKeyBehavior(UseKeyBehavior ukb) {
    this.useKeyBehavior = ukb;
  }

  public abstract String getLocation();

  public abstract String getName();

  public abstract String getType();

  public void applyAcquire(String location, PlayerModel playerModel) {
    checkBehavior.acquireContents(location, playerModel);
  }

  public String applyUseKey(List<KeyChecker> keys) {
    return useKeyBehavior.useKey(keys);
  }

  public abstract int compareTo(String location);

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
