 import java.util.HashMap;

public class RoomDrawer {
  private HashMap<Integer, String> mapStrings = new HashMap<>();
  private String location;
  private Room room;
  private PlayerModel playerModel;

  public RoomDrawer(String location, Room room, PlayerModel playerModel) {
    this.playerModel = playerModel;
    if (room.getIsLit()) {
      this.mapStrings.put(0, "a");
      this.mapStrings.put(1, "b");
      this.mapStrings.put(2, "c");
      this.mapStrings.put(3, "d");
      this.mapStrings.put(4, "e");

      this.location = location;
      this.room = room;

      raw(1);
      raw(2);
      raw(3);
      raw(4);
      raw(5);
    }
  }

  private void raw(int num) {
    for (int i = 0; i < 5; ++i) {
      if (this.location.equals(mapStrings.get(i) + num)) {
        playerModel.inLine_notify_player("\u29EF\u29EF ");
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
        playerModel.inLine_notify_player(!item ? mapStrings.get(i) + num + " " : "■■ ");
      }
    }
    playerModel.notify_player("");
  }
}
