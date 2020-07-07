package com.worldNavigator;
import java.util.HashMap;

public class RoomDrawer {
  private HashMap<Integer, String> mapStrings = new HashMap<>();
  private String location;
  private Room room;
  public String roomDraw;

  public RoomDrawer(String location, Room room) {
    if (room.getIsLit()) {
      this.mapStrings.put(0, "a");
      this.mapStrings.put(1, "b");
      this.mapStrings.put(2, "c");
      this.mapStrings.put(3, "d");
      this.mapStrings.put(4, "e");

      this.location = location;
      this.room = room;

      this.roomDraw = row(1);
      this.roomDraw += "<br>" + row(2);
      this.roomDraw += "<br>" + row(3);
      this.roomDraw += "<br>" + row(4);
      this.roomDraw += "<br>" + row(5);
    }
  }

  private String row(int num) {
    String row = "";
    for (int i = 0; i < 5; ++i) {
      if (this.location.equals(mapStrings.get(i) + num)) {
        row += "\u29EF\u29EF   ";
      } else {
        boolean item = false;
        for (Wall wall : this.room.walls.values()) {
          if (!item) {
            item =
                !wall.itemsFactory
                    .checkItemByLocation(mapStrings.get(i) + num)
                    .equals("Nothing in this location");
          }
        }
        row += !item ? mapStrings.get(i) + num + "   " : "■■   ";
      }
    }
    return row;
  }
}
