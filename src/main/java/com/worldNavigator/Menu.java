package com.worldNavigator;
import java.io.IOException;
import java.util.Scanner;

public class Menu {
  private Maps maps;
  private int map_index;
  public PlayerControllerInterface player;
  public PlayerModel playerModel;
  public PlayerViewer playerViewer;

  public void setMaps(Maps maps, String sc) {
    this.maps = maps;
    this.map_chooser(sc);
  }

  public void map_chooser(String sc) {
    System.out.println("Choose one of the available maps: ");
    int counter = 0;
    while (counter != this.maps.maps.size()) {
      System.out.println(counter + ": " + this.maps.maps.get(counter));
      ++counter;
    }
    System.out.println("Enter map number");
    this.map_index = Integer.parseInt(sc);//sc.nextInt();
  }

  public void preparePlayer(MapFactory map) {
    this.playerModel = new PlayerModel(map, this);
    this.player = new PlayerControllerMaster();
    this.playerViewer = new PlayerViewer(this.player, "Isa");
  }

  public void start() throws IOException {
    preparePlayer(this.maps.maps.get(this.map_index));
    this.player.startGame();
  }

  public void restart() throws IOException {
    String mapName = this.maps.maps.get(this.map_index).mapName;
    MapFactory new_map = this.maps.generate(mapName);
    this.maps.replace(new_map, this.map_index);

    preparePlayer(this.maps.maps.get(0));
    player.startGame();
  }

  public void quit() {
    System.exit(1);
  }

  @Override
  public String toString() {
    return "Menu";
  }
}
