package com.worldNavigator;
public class MasterKey extends Key implements KeyChecker {
  private final String NAME;

  public MasterKey() {
    super("master");
    this.NAME = "master";
  }

  @Override
  public String getName() {
    return this.NAME;
  }

  @Override
  public Boolean unlock(Openable openable) {
    return true;
  }

  @Override
  public int compareTo(String masterKey) {
    return this.NAME.compareTo(masterKey);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MasterKey) {
      MasterKey masterKey = (MasterKey) o;
      return masterKey.NAME.equals(this.NAME);
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
