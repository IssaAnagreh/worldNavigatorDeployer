package com.worldNavigator;

import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class PlayerModel extends Observable {
    public final MapFactory map;
    public String name;
    public int roomIndex;
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
    DB db;
    String sessionId;


    public PlayerModel(MapFactory map, Game game, String name, String sessionId) {
        this.generateCollection(map, game, name, sessionId);
        this.sessionId = sessionId;
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
        for (Integer roomIndex: map.starterRooms.keySet()) {
            if (map.starterRooms.get(roomIndex)) {
                this.roomIndex = roomIndex;
                map.starterRooms.put(roomIndex, false);
                break;
            }
        }

        this.getMap();
        this.getRooms();
        this.getRoom(0);
        this.getPlayer(this.sessionId);
        this.getRoomIndex(this.sessionId);
    }

    private void generateCollection(MapFactory map, Game game, String name, String sessionId) {
        ContentManager contentManager = new ContentManager();
        HashMap<String, Object> player_details = (HashMap) map.jsonMap.get("player");
        contentManager.managePlayer(player_details);

        HashMap<String, String> dbHashMap = new HashMap<>();
        dbHashMap.put("game", Integer.toString(game.id));
        dbHashMap.put("name", name);
        dbHashMap.put("sessionId", sessionId);
        for (Integer roomIndex: map.starterRooms.keySet()) {
            if (map.starterRooms.get(roomIndex)) {
                dbHashMap.put("roomIndex", Integer.toString(roomIndex));
                map.starterRooms.put(roomIndex, false);
                break;
            }
        }
        dbHashMap.put("location", "c3");
        dbHashMap.put("contents", contentManager.getContents().toString());
        dbHashMap.put("orientation", "north");
        dbHashMap.put("isPlaying", Boolean.toString(this.isPlaying));
        dbHashMap.put("winner", Boolean.toString(false));
        HashMap<String, Object> fight = new HashMap();
        fight.put("isFighting", false);
        fight.put("against", null);
        dbHashMap.put("fight", fight.toString());
        game.db.insertOne("Players", dbHashMap);
    }

    public HashMap getMap() {
        this.db = new DB();
        MongoCursor<Document> mapString = this.db.findOne("Maps", "game", Integer.toString(this.game.id));
        JSONArray jsonArray = new JSONArray();
        while (mapString.hasNext()) {
            Document doc = mapString.next();
            jsonArray.add(doc.toJson());
        }

        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return json;
    }

    public HashMap getRooms() {
        this.db = new DB();
        MongoCursor<Document> roomsString = this.db.findOne("Rooms", "game", Integer.toString(this.game.id));
        JSONArray jsonArray = new JSONArray();
        while (roomsString.hasNext()) {
            Document doc = roomsString.next();
            jsonArray.add(doc.toJson());
        }

        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return json;
    }

    public HashMap getRoom(int roomNumber) {
        this.db = new DB();
        Bson filter = and(eq("roomNumber", "0"), eq("game", "0"));
        MongoCursor<Document> roomString = this.db.findOneWithFilters("Rooms", filter);
        JSONArray jsonArray = new JSONArray();
        while (roomString.hasNext()) {
            Document doc = roomString.next();
            jsonArray.add(doc.toJson());
        }

        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return json;
    }

    public int getRoomIndex(String sessionId) {
        return Integer.parseInt(this.getPlayer(sessionId).get("roomIndex").toString());
    }

    public HashMap getPlayer(String sessionId) {
        this.db = new DB();
        MongoCursor<Document> playerString = this.db.findOne("Players", "sessionId", sessionId);
        JSONArray jsonArray = new JSONArray();
        while (playerString.hasNext()) {
            Document doc = playerString.next();
            jsonArray.add(doc.toJson());
        }

        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return json;
    }

    public void addToRoom(PlayerViewer playerViewer) {
        this.map.rooms.get(this.roomIndex).addOccupant(this.game.whoIsIt_name(this.name), this.game);
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
        this.game.db.updatePlayer("Players", this.sessionId, "location", this.location);
    }

    public void nextRoom_move() {
        Transition new_location = new Transition(this);
        new_location.openNextRoom(this.location, this.orientation, MoveTypes.forward);
        notify_player("You are in: " + new_location.toString() + " in room number: " + (this.roomIndex + 1), ConsoleColors.blue);
        this.location = new_location.toString();
        this.game.db.updatePlayer("Players", this.sessionId, "location", this.location);
        this.game.db.updatePlayer("Players", this.sessionId, "roomIndex", Integer.toString(this.roomIndex));
    }

    public void rotateLeft() {
        this.orientation = new Rotate(this.orientation, this).left();
        this.game.db.updatePlayer("Players", this.sessionId, "orientation", this.orientation);
    }

    public void rotateRight() {
        this.orientation = new Rotate(this.orientation, this).right();
        this.game.db.updatePlayer("Players", this.sessionId, "orientation", this.orientation);
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
        this.game.db.updatePlayer("Players", this.sessionId, "contents", this.contents.toString());
    }

    private ArrayList<KeyChecker> castToKeyCheckerArrayList(Object o) {
        return (ArrayList<KeyChecker>) o;
    }

    public ArrayList<KeyChecker> getKeys() {
        if (this.contents.get("keys") != null) {
            if ((castToKeyCheckerArrayList(this.contents.get("keys"))).isEmpty()) {
                return new ArrayList<>();
            } else {
                castToKeyCheckerArrayList(this.contents.get("keys"));
            }
        } else {
            return new ArrayList<>();
        }
        return new ArrayList<>();
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
            this.winner = true;
            this.game.db.updatePlayer("Players", this.sessionId, "winner", "true");
            this.game.endGame(this.game.whoIsIt_name(this.name));
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
