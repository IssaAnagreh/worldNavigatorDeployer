package com.worldNavigator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class PlayerModel extends Observable {
    private final MapFactory map;
    public String name;
    private int roomIndex;
    public Map<String, Object> contents;
    private String orientation;
    private String location;
    public GameTimer timer;
    public Game game;
    private boolean isPlaying;
    static BufferedReader br;
    public Boolean isInline = false;
    public ConsoleColors consoleColor;
    private ContentManager contentManager;
    public Map<String, Object> fight = new HashMap<>();
    boolean winner = false;

    public PlayerModel(MapFactory map, Game game, String name) {
        this.fight.put("isFighting", false);
        this.fight.put("against", null);
        this.map = map;
        this.game = game;
        this.name = name;
        this.contentManager = new ContentManager();
        String player_string = "player";
        HashMap<String, Object> player_details = (HashMap) map.jsonMap.get(player_string);
        this.contentManager.managePlayer(player_details);
        this.contents = this.contentManager.getContents();
        this.location = "b5";
        this.orientation = "south";
        this.roomIndex =
            player_details.get("roomIndex") != null
                ? 1//(0 + (int)(Math.random() * ((1 - 0) + 1)))//Integer.parseInt(player_details.get("roomIndex").toString())
                : 0;
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

    private int castToInt(Object o) {
        return (int) o;
    }

    public void startGame() {
        this.isPlaying = true;
        this.timer = new GameTimer(this.map.endTime, this);
//    this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    public int power() {
        return this.contentManager.calculatePower();
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
        return new RoomDrawer(location, this.map.rooms.get(this.roomIndex));
    }

    public void wall() {
        if (this.map.rooms.get(this.roomIndex).getIsLit() != null && this.map.rooms.get(this.roomIndex).getIsLit()) {
            notify_player(this.map.rooms.get(this.roomIndex).walls.get(this.orientation).toString());
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
        return isPlaying;
    }

    public void myItems() {
        System.out.println("name: " + this.name);
        notify_player(this.contents == null ? "Nothing" : this.contents.toString());
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
    }

    public void rotateLeft() {
        this.orientation = new Rotate(this.orientation, this).left();
    }

    public void rotateRight() {
        this.orientation = new Rotate(this.orientation, this).right();
    }

    public void look() {
        if (this.map.rooms.get(this.roomIndex).getIsLit() != null && this.map.rooms.get(this.roomIndex).getIsLit()) {
            Wall opposite_wall = this.map.rooms.get(this.roomIndex).walls.get(this.orientation);
            notify_player(opposite_wall.checkItems());
        } else {
            notify_player("Dark");
        }
    }

    public String getType() {
        return this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.getType(this.location);
    }

    public void check() {
        notify_player(this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.checkItemByLocation(this.location));
    }

    public void acquire_items() {
        Item item = this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.getItem(this.location);
        if (!item.toString().equals("Space")) {
            System.out.println("player name: " + name);
            item.applyAcquire(this.location, this);
        }
    }

    private ArrayList<KeyChecker> castToKeyCheckerArrayList(Object o) {
        return (ArrayList<KeyChecker>) o;
    }

    public void use_key() {
        String print = "";
        if (this.contents != null) {
            if (this.contents.get("keys") != null) {
                if ((castToKeyCheckerArrayList(this.contents.get("keys"))).isEmpty()) {
                    print = "You have no keys";
                } else {
                    Item item = this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.getItem(this.location);
                    print =
                        item.toString().equals("Space")
                            ? "Opening nothing"
                            : item.applyUseKey(castToKeyCheckerArrayList(this.contents.get("keys")));
                }
            } else {
                print = "You have no keys";
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
            Item item = this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.getItem(this.location);
            print =
                item.toString().equals("Space") ? "Opening nothing" : item.applyUseKey(masterKeysList);
            this.contents.put(masterKeysString, castToInt(this.contents.get(masterKeysString)) - 1);
        } else {
            print = "You have no master keys";
        }
        notify_player(print);
    }

    public void open() {
        Item item = this.map.rooms.get(this.roomIndex).walls.get(this.orientation).itemsFactory.getItem(this.location);
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
        for (Room room_candidate : this.map.rooms) {
            if (room_candidate.ROOM_NAME.equals(nextRoom)) {
                this.roomIndex = this.map.rooms.indexOf(room_candidate);
                room_candidate.addOccupant(this.game.whoIsIt_name(this.name), this.game);
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
        Seller seller = (Seller) this.map.rooms.get(this.roomIndex).walls.get(this.orientation).items.get("seller");

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
        this.map.rooms.get(this.roomIndex).switchLights(this);
    }

    public void flashLight() {
        if (this.map.rooms.get(this.roomIndex).getIsLit() != null && this.map.rooms.get(this.roomIndex).getIsLit()) {
            notify_player("You don't need to light a lit room");
            return;
        }
        if (castToInt(this.contents.get("flashLights")) > 0) {
            this.contents.put(
                "flashLights", this.map.rooms.get(this.roomIndex).useFlashLight(castToInt(this.contents.get("flashLights")), this));
        } else {
            notify_player("You have no flashLights", ConsoleColors.red);
        }
    }

    @Override
    public String toString() {
        return "Player model";
    }
}
