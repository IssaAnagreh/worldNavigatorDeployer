package com.worldNavigator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class PlayerModel extends Observable {
  private final MapFactory map;
  private List<Room> rooms;
  private Room room;
  private Wall wall;
  private int roomIndex;
  private Map<String, Object> contents;
  private String orientation;
  private String location;
  public GameTimer timer;
  public Menu menu;
  private boolean playing;
  static BufferedReader br;
  public Boolean isInline = false;
  public ConsoleColors consoleColor;

  public PlayerModel(MapFactory map, Menu menu) {
    this.map = map;
    this.rooms = map.rooms;
    this.menu = menu;
    this.contents = map.contents;
    this.location = map.location;
    this.orientation = map.orientation;
    this.roomIndex = map.roomIndex;
    this.room = this.rooms.get(this.roomIndex);
    this.wall = this.room.walls.get(this.orientation);
  }

  public void notify_player(String msg) {
    this.setChanged();
    notifyObservers(msg);
  }

  public void notify_player(String msg, ConsoleColors color) {
    this.consoleColor = color;
    this.setChanged();
    notifyObservers(msg);
    this.consoleColor = null;
  }

  public void inLine_notify_player(String msg) {
    this.isInline = true;
    this.setChanged();
    notifyObservers(msg);
    this.isInline = false;
  }

  private int castToInt(Object o) {
    return (int) o;
  }

  public void startGame() {
    this.playing = true;
    this.timer = new GameTimer(this.map.endTime, this);
//    this.br = new BufferedReader(new InputStreamReader(System.in));
  }

  public void addToContents(String contentType, Object newContent) {
    this.contents.put(contentType, newContent);
  }

  public Object getContent(String contentType) {
    return this.contents.get(contentType);
  }

  public Map<String, Object> getContents() {
    return this.contents;
  }

  public String getRoom() {
    return drawRoom().roomDraw;
  }

  public RoomDrawer drawRoom() {
    return new RoomDrawer(location, this.room);
  }

  public void wall() {
    if (this.room.getIsLit() != null && this.room.getIsLit()) {
      this.wall = this.room.walls.get(this.orientation);
      notify_player(wall.toString());
    } else {
      notify_player("Dark", ConsoleColors.red);
    }
  }

  public String getOrientation() {
    return this.orientation;
  }

  public String getLocation() {
    return location;
  }

  public boolean isPlaying() {
    return playing;
  }

  public void myItems() {
    notify_player(this.contents.toString());
  }

  public void move(MoveTypes move) {
    Transition new_location = new Transition(this);
    new_location.move(this.location, this.orientation, move);
    this.location = new_location.toString();
    notify_player(new_location.printOut(), ConsoleColors.blue);
  }

  public void nextRoom_move() {
    Transition new_location = new Transition(this);
    new_location.openNextRoom(this.location, this.orientation, MoveTypes.forward);
    int index = this.roomIndex + 1;
    notify_player("You are in: " + new_location.toString() + " in room number: " + index, ConsoleColors.blue);
    this.location = new_location.toString();
    this.room = this.rooms.get(this.roomIndex);
    this.wall = this.room.walls.get(this.orientation);
  }

  public void rotateLeft() {
    this.orientation = new Rotate(this.orientation, this).left();
    this.wall = room.walls.get(this.orientation);
  }

  public void rotateRight() {
    this.orientation = new Rotate(this.orientation, this).right();
    this.wall = room.walls.get(this.orientation);
  }

  public void look() {
    if (this.room.getIsLit() != null && this.room.getIsLit()) {
      Wall opposite_wall = this.room.walls.get(this.orientation);
      notify_player(opposite_wall.checkItems());
    } else {
      notify_player("Dark");
    }
  }

  public String getType() {
    return this.wall.itemsFactory.getType(this.location);
  }

  public void check() {
    notify_player(this.wall.itemsFactory.checkItemByLocation(this.location));
  }

  public void acquire_items() {
    Item item = this.wall.itemsFactory.getItem(this.location);
    if (!item.toString().equals("Space")) {
      item.applyAcquire(this.location, this);
    }
  }

  private ArrayList<KeyChecker> castToKeyCheckerArrayList(Object o) {
    return (ArrayList<KeyChecker>) o;
  }

  public void use_key() {
    String print = "";
    if (this.contents.get("keys") != null) {
      if ((castToKeyCheckerArrayList(this.contents.get("keys"))).isEmpty()) {
        print = "You have no keys";
      } else {
        Item item = this.wall.itemsFactory.getItem(this.location);
        print =
            item.toString().equals("Space")
                ? "Opening nothing"
                : item.applyUseKey(castToKeyCheckerArrayList(this.contents.get("keys")));
      }
    } else {
      print = "You have no keys";
    }
    notify_player(print);
  }

  public void use_masterKey() {
    String print = "";
    List<KeyChecker> masterKeysList = new ArrayList<>();
    String masterKeysString = "masterKeys";
    if ((castToInt(this.contents.get(masterKeysString))) > 0) {
      masterKeysList.add(new MasterKey());
      Item item = this.wall.itemsFactory.getItem(this.location);
      print =
          item.toString().equals("Space") ? "Opening nothing" : item.applyUseKey(masterKeysList);
      this.contents.put(masterKeysString, castToInt(this.contents.get(masterKeysString)) - 1);
    } else {
      print = "You have no master keys";
    }
    notify_player(print);
  }

  public void open() {
    Item item = this.wall.itemsFactory.getItem(this.location);
    if (item instanceof NextGoing) {
      NextGoing openable = (NextGoing) item;
      if (item.getLocation().equals(this.location)) {
        this.checkNextRoom(openable);
      } else {
        notify_player("Nothing to be opened", ConsoleColors.red);
      }
    } else {
      notify_player("Nothing to be opened", ConsoleColors.red);
    }
  }

  private void checkNextRoom(NextGoing openable) {
    boolean isOpened = false;
    String nextRoom = openable.getNextRoom();
    if (nextRoom.contentEquals("golden")) {
      notify_player("CONGRATULATIONS! YOU WON THE GAME");
      System.exit(1);
    }
    if (nextRoom.equals("")) {
      notify_player("This " + openable.getName() + " opens to nothing", ConsoleColors.red);
      return;
    }
    for (Room room_candidate : this.rooms) {
      if (room_candidate.ROOM_NAME.equals(nextRoom)) {
        this.roomIndex = this.rooms.indexOf(room_candidate);
        this.nextRoom_move();
        isOpened = true;
      }
    }
    if (!isOpened && nextRoom.equals("locked")) {
      notify_player("The " + openable.getName() + " is locked", ConsoleColors.red);
    }
  }

  public void setLocation() {
    Scanner sc = new Scanner(System.in);
    this.location = sc.nextLine();
  }

  public void trade() {
    Seller seller = (Seller) this.wall.items.get("seller");

    if (seller != null) {
      notify_player("You can use buy, sell, list or finish commands");
      Scanner sc = new Scanner(System.in);
      String command = sc.nextLine();
      switch (command) {
        case "buy":
          seller_buy(seller);
          break;
        case "sell":
          seller_sell(seller);
          break;
        case "list":
          seller_list(seller);
          break;
        case "finish":
          this.notify_player("Your items:");
          this.myItems();
          break;
        default:
          trade();
          break;
      }
    } else {
      notify_player("No sellers in this orientation", ConsoleColors.red);
    }
  }

  public void seller_list(Seller seller) {
    notify_player(seller.contents.getContents().toString());
    this.trade();
  }

  public void seller_buy(Seller seller) {
    seller.buy(castToInt(this.contents.get("golds")), this);
  }

  public void seller_sell(Seller seller) {
    seller.sell(this);
  }

  public void switchLights() {
    this.room.switchLights(this);
  }

  public void flashLight() {
    if (this.room.getIsLit() != null && this.room.getIsLit()) {
      notify_player("You don't need to light a lit room");
      return;
    }
    if (castToInt(this.contents.get("flashLights")) > 0) {
      this.contents.put(
          "flashLights", this.room.useFlashLight(castToInt(this.contents.get("flashLights")), this));
    } else {
      notify_player("You have no flashLights", ConsoleColors.red);
    }
  }

  @Override
  public String toString() {
    return "Player model";
  }
}
