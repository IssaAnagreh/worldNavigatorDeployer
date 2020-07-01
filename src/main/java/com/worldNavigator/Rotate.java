package com.worldNavigator;
public class Rotate {
  String ORIENTATION;
  PlayerModel playerModel;
  private final String NORTH = "north";
  private final String EAST = "east";
  private final String SOUTH = "south";
  private final String WEST = "west";

  public Rotate(String orientation, PlayerModel playerModel) {
    this.ORIENTATION = orientation;
    this.playerModel = playerModel;
  }

  public String left() {
    switch (this.ORIENTATION) {
      case "north":
        this.ORIENTATION = this.WEST;
        break;
      case "east":
        this.ORIENTATION = this.NORTH;
        break;
      case "south":
        this.ORIENTATION = this.EAST;
        break;
      case "west":
        this.ORIENTATION = this.SOUTH;
        break;
      default:
        this.playerModel.notify_player("Semething went wrong while rotating", ConsoleColors.red);
        break;
    }
    this.playerModel.notify_player(this.ORIENTATION, ConsoleColors.blue);
    return this.ORIENTATION;
  }

  public String right() {
    switch (this.ORIENTATION) {
      case "north":
        this.ORIENTATION = this.EAST;
        break;
      case "east":
        this.ORIENTATION = this.SOUTH;
        break;
      case "south":
        this.ORIENTATION = this.WEST;
        break;
      case "west":
        this.ORIENTATION = this.NORTH;
        break;
      default:
        this.playerModel.notify_player("Something went wrong while rotating", ConsoleColors.red);
        break;
    }
    this.playerModel.notify_player(this.ORIENTATION, ConsoleColors.blue);
    return this.ORIENTATION;
  }

  @Override
  public String toString() {
    return "Rotate";
  }
}
