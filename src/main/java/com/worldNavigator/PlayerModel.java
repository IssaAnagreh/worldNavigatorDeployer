package com.worldNavigator;

import com.mongodb.client.MongoCursor;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.api.scripting.JSObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class PlayerModel extends Observable {
    //    public final MapFactory map;
    public Map<String, Object> contents;
    public GameTimer timer;
    public Game game;
    public ConsoleColors consoleColor;
    private ContentManager contentManager;
    public Map<String, Object> fight = new HashMap<>();
    DB db;
    String sessionId;


    public PlayerModel(MapFactory map, Game game, String name, String sessionId) {
        this.generateCollection(map, game, name, sessionId);
        this.sessionId = sessionId;
        this.fight.put("isFighting", false);
        this.fight.put("against", null);
        this.game = game;
        this.contentManager = new ContentManager();
        String player_string = "player";
        HashMap<String, Object> player_details = (HashMap) map.jsonMap.get(player_string);
        this.contentManager.managePlayer(player_details);
        this.contents = this.contentManager.getContents();
    }

    private void generateCollection(MapFactory map, Game game, String name, String sessionId) {
        ContentManager contentManager = new ContentManager();
        HashMap<String, Object> player_details = (HashMap) map.jsonMap.get("player");
        contentManager.managePlayer(player_details);

        HashMap<String, Object> dbHashMap = new HashMap<>();
        dbHashMap.put("game", Integer.toString(game.id));
        dbHashMap.put("name", name);
        dbHashMap.put("sessionId", sessionId);
        for (Integer roomIndex : map.starterRooms.keySet()) {
            if (map.starterRooms.get(roomIndex)) {
                dbHashMap.put("roomIndex", Integer.toString(roomIndex));
                map.starterRooms.put(roomIndex, false);
                break;
            }
        }
        dbHashMap.put("location", "a4");
        dbHashMap.put("contents", contentManager.getContents().toString());
        dbHashMap.put("orientation", "west");
        dbHashMap.put("isPlaying", Boolean.toString(false));
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

    public HashMap getRoom(int roomIndex) {
        this.db = new DB();
        Bson filter = and(eq("roomIndex", Integer.toString(roomIndex)), eq("game", Integer.toString(this.game.id)));
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

    public void setRoomIndex(String newRoomIndex) {
        this.db.updatePlayer("Players", this.sessionId, "roomIndex", newRoomIndex);
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

    public String getName() {
        return this.getPlayer(this.sessionId).get("name").toString();
    }

    public String getIsPlaying() {
        return this.getPlayer(this.sessionId).get("isPlaying").toString();
    }

    public void setIsPlaying() {
        this.game.db.updatePlayer("Players", this.sessionId, "isPlaying", "true");
    }

    public String getOrientation() {
        return this.getPlayer(this.sessionId).get("orientation").toString();
    }

    public void setOrientation(String newOrientation) {
        this.game.db.updatePlayer("Players", this.sessionId, "orientation", newOrientation);
    }

    public void setIsWinner() {
        this.game.db.updatePlayer("Players", this.sessionId, "winner", "true");
    }

    public Boolean getIsWinner() {
        return Boolean.parseBoolean(this.getPlayer(this.sessionId).get("winner").toString());
    }

    public String getLocation() {
        return this.getPlayer(this.sessionId).get("location").toString();
    }

    public void setLocation(String newLocation) {
        this.game.db.updatePlayer("Players", this.sessionId, "location", newLocation);
    }

    public void addToRoom() {
        new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).addOccupant(this.game.whoIsIt_name(this.getName()), this.game);
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
        this.setIsPlaying();
        this.timer = new GameTimer(Integer.parseInt(this.getMap().get("endTime").toString()), this);
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
        return new RoomDrawer(this.getLocation(), new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game), this.game);
    }

    public void wall() {
        if (new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).getIsLit() != null && new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).getIsLit()) {
            notify_player(new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).walls.get(this.getOrientation()).toString());
        } else {
            notify_player("Dark", ConsoleColors.red);
        }
    }

    public boolean isPlaying() {
        return Boolean.parseBoolean(this.getIsPlaying());
    }

    public void myItems() {
        notify_player(this.contents == null ? "Nothing" : this.contents.toString());
    }

    public void move(MoveTypes move) {
        Transition new_location = new Transition(this);
        new_location.move(this.getLocation(), this.getOrientation(), move);
        this.setLocation(new_location.toString());
        notify_player(new_location.printOut(), ConsoleColors.blue);
        this.game.db.updatePlayer("Players", this.sessionId, "location", this.getLocation());
    }

    public void nextRoom_move() {
        Transition new_location = new Transition(this);
        new_location.openNextRoom(this.getLocation(), this.getOrientation(), MoveTypes.forward);
        notify_player("You are in: " + new_location.toString() + " in room number: " + (this.getRoomIndex(this.sessionId) + 1), ConsoleColors.blue);
        this.setLocation(new_location.toString());
        this.game.db.updatePlayer("Players", this.sessionId, "location", this.getLocation());
        this.game.db.updatePlayer("Players", this.sessionId, "roomIndex", Integer.toString(this.getRoomIndex(this.sessionId)));
    }

    public void rotateLeft() {
        this.setOrientation(new Rotate(this.getOrientation(), this).left());
        this.game.db.updatePlayer("Players", this.sessionId, "orientation", this.getOrientation());
    }

    public void rotateRight() {
        this.setOrientation(new Rotate(this.getOrientation(), this).right());
        this.game.db.updatePlayer("Players", this.sessionId, "orientation", this.getOrientation());
    }

    public void look() {
        int sessionId = this.getRoomIndex(this.sessionId);
        HashMap room = this.getRoom(sessionId);
        if (room != null && Boolean.parseBoolean(room.get("isLit").toString())) {
            HashMap<String, String> map = (HashMap) this.getWall().get("itemsLocations");
            notify_player(map.toString());
        } else {
            notify_player("Dark");
        }
    }

    public HashMap<String, JSONObject> getWalls() {
        DB db = new DB();
        Bson filter = and(eq("roomIndex", Integer.toString(this.getRoomIndex(this.sessionId))), eq("game", Integer.toString(this.game.id)));
        MongoCursor<Document> wallString = db.findOneWithFilters("Walls", filter);
        JSONArray jsonArray = new JSONArray();
        while (wallString.hasNext()) {
            Document doc = wallString.next();
            jsonArray.add(doc.toJson());
        }

        HashMap<String, JSONObject> output = new HashMap();
        for (Object obj : jsonArray) {
            JSONObject json;
            try {
                json = (JSONObject) new JSONParser().parse(obj.toString());
                output.put(json.get("name").toString(), json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public HashMap getWall() {
        DB db = new DB();
        HashMap player = this.getPlayer(this.sessionId);
        Bson filter = and(eq("roomIndex", player.get("roomIndex")), eq("game", Integer.toString(this.game.id)), eq("name", player.get("orientation") + "_wall"));
        MongoCursor<Document> wallString = db.findOneWithFilters("Walls", filter);

        JSONArray jsonArray = new JSONArray();
        while (wallString.hasNext()) {
            Document doc = wallString.next();
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

    public String getType() {
        return (new Wall(this.getWalls().get(this.getOrientation() + "_wall"), this.game)).itemsFactory.getType(this.getLocation());
    }

    public HashMap getItem(String collection, String location) {
        String col = "";
        switch (collection) {
            case "door":
                col = "Doors";
                break;
            case "chest":
                col = "Chests";
                break;
            case "gate":
                col = "Gates";
                break;
            case "mirror":
                col = "Mirrors";
                break;
            case "painting":
                col = "Paintings";
                break;
            case "safe":
                col = "Safes";
                break;
            case "table":
                col = "Tables";
                break;
            default:
                // window
                col = "Windows";
                break;
        }
        DB db = new DB();
        Bson filter = and(eq("location", location), eq("game", Integer.toString(this.game.id)));
        MongoCursor<Document> wallString = db.findOneWithFilters(col, filter);
        JSONArray jsonArray = new JSONArray();
        while (wallString.hasNext()) {
            Document doc = wallString.next();
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

    public void check() {
        HashMap<String, String> map = (HashMap) this.getWall().get("itemsLocations");
        System.out.println(map);
        for (String key : map.keySet()) {
            String location = this.getLocation();
            if (key.equals(location)) {
                this.getItem(map.get(key), location);
            }
        }
    }

    public void acquire_items() {
        HashMap<String, String> map = (HashMap) this.getWall().get("itemsLocations");
        HashMap itemMap = new HashMap();
        for (String key : map.keySet()) {
            String location = this.getLocation();
            if (key.equals(location)) {
                itemMap = this.getItem(map.get(key), location);
            }
        }

        Item item;
        switch (itemMap.get("type").toString()) {
            case "door":
                item = new Door((JSONObject) itemMap, this.game);
                break;
            case "chest":
                item = new Chest((JSONObject) itemMap);
                break;
            case "gate":
                item = new Gate((JSONObject) itemMap, this.game);
                break;
            case "mirror":
                item = new Mirror((JSONObject) itemMap, this.game);
                break;
            case "painting":
                item = new Painting((JSONObject) itemMap, this.game);
                break;
            case "safe":
                item = new Safe((JSONObject) itemMap, this.game);
                break;
            case "table":
                item = new Table((JSONObject) itemMap, this.game);
                break;
            default:
                // window
                item = new Window((JSONObject) itemMap, this.game);
                break;
        }

        if (!item.toString().equals("Space")) {
            item.applyAcquire(this.getLocation(), this);
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
                    Item item = new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).walls.get(this.getOrientation()).itemsFactory.getItem(this.getLocation());
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
            Item item = new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).walls.get(this.getOrientation()).itemsFactory.getItem(this.getLocation());
            print =
                item.toString().equals("Space") ? "Opening nothing" : item.applyUseKey(masterKeysList);
            this.contents.put(masterKeysString, castToInt(this.contents.get(masterKeysString)) - 1);
        } else {
            print = "You have no master keys";
        }
        notify_player(print);
    }

    public void open() {
        Item item = new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).walls.get(this.getOrientation()).itemsFactory.getItem(this.getLocation());
        if (item instanceof NextGoing) {
            NextGoing openable = (NextGoing) item;
            if (item.getLocation().equals(this.getLocation())) {
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
            this.setIsWinner();
            this.game.db.updatePlayer("Players", this.sessionId, "winner", "true");
            this.game.endGame(this.game.whoIsIt_name(this.getName()));
        }
        if (nextRoom.equals("")) {
            notify_player("This " + openable.getName() + " opens to nothing", ConsoleColors.red);
            return;
        }
        int roomIndex = 0;
        for (Object jsonRoom : this.getRooms().values()) {
            Room room_candidate = new Room((JSONObject) jsonRoom, this.game);
            if (room_candidate.ROOM_NAME.equals(nextRoom)) {
                this.setRoomIndex(Integer.toString(roomIndex));
                room_candidate.addOccupant(this.game.whoIsIt_name(this.getName()), this.game);
                this.nextRoom_move();
                isOpened = true;
            }
            roomIndex++;
        }
        if (!isOpened && nextRoom.equals("locked")) {
            notify_player("The " + openable.getName() + " is locked", ConsoleColors.red);
        }
    }

    public void trade() {
        Seller seller = (Seller) new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).walls.get(this.getOrientation()).items.get("seller");

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
        int roomIndex = this.getRoomIndex(this.sessionId);
        HashMap room = this.getRoom(roomIndex);
        new Room((JSONObject) room, this.game).switchLights(this);
    }

    public void flashLight() {
        if (new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).getIsLit() != null && new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).getIsLit()) {
            notify_player("You don't need to light a lit room");
            return;
        }
        if (castToInt(this.contents.get("flashLights")) > 0) {
            this.contents.put(
                "flashLights", new Room((JSONObject) this.getRoom(this.getRoomIndex(this.sessionId)), this.game).useFlashLight(castToInt(this.contents.get("flashLights")), this));
        } else {
            notify_player("You have no flashLights", ConsoleColors.red);
        }
    }

    @Override
    public String toString() {
        return "Player model";
    }
}
