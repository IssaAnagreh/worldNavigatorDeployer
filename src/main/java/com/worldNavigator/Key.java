package com.worldNavigator;
public class Key implements KeyChecker {
  private final String NAME;

  public Key(String name) {
    this.NAME = name;
  }

  public String getName() {
    return this.NAME;
  }

  public Boolean unlock(Openable openable) {
    return this.compareTo(openable.getKey()) == 0;
  }

  public int compareTo(String key) {
    return this.NAME.compareTo(key);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Key) {
      Key key = (Key) o;
      return key.NAME.equals(this.NAME);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.NAME.hashCode();
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
