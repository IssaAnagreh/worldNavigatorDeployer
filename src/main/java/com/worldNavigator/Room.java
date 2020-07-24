package com.worldNavigator;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class Room {
    public HashMap<String, Wall> walls = new HashMap<>();
    public final String ROOM_NAME;
    private Boolean isLit;
    public Boolean lightSwitch;
    public final Integer ROOM_INDEX;
    public List<String> occupants = new ArrayList<>();
    Game game;

    public Room(JSONObject room, Game game) {
        System.out.println("room: "+room);
        this.game = game;
        this.ROOM_INDEX = Integer.parseInt(room.get("roomIndex").toString());
        this.ROOM_NAME =
            room.get("name") == null ? "room_" + (this.ROOM_INDEX + 1) : room.get("name").toString();
        this.isLit = room.get("isLit") == null || Boolean.parseBoolean(room.get("isLit").toString());
        this.lightSwitch =
            (room.get("lightSwitch") != null) && Boolean.parseBoolean(room.get("lightSwitch").toString());
    }

    public Room(JSONObject room, int roomIndex, Game game) {
        this.game = game;
        this.ROOM_INDEX = roomIndex;
        this.ROOM_NAME =
            room.get("name") == null ? "room_" + (this.ROOM_INDEX + 1) : room.get("name").toString();
        this.isLit = room.get("isLit") == null || Boolean.parseBoolean(room.get("isLit").toString());
        this.lightSwitch =
            (room.get("lightSwitch") != null) && Boolean.parseBoolean(room.get("lightSwitch").toString());

        generateRoom(room);
    }

    private JSONObject castToJSONObject(Object o) {
        return (JSONObject) o;
    }

    private void generateRoom(JSONObject room) {
        this.generateCollection();

        try {
            Wall north_wall = new Wall("north_wall", castToJSONObject(room.get("n_wall")), this, this.game);
            Wall east_wall = new Wall("east_wall", castToJSONObject(room.get("e_wall")), this, this.game);
            Wall south_wall = new Wall("south_wall", castToJSONObject(room.get("s_wall")), this, this.game);
            Wall west_wall = new Wall("west_wall", castToJSONObject(room.get("w_wall")), this, this.game);

            walls.put("north", north_wall);
            walls.put("east", east_wall);
            walls.put("south", south_wall);
            walls.put("west", west_wall);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void generateCollection() {
        HashMap<String, Object> dbHashMap = new HashMap<>();
        dbHashMap.put("name", this.ROOM_NAME);
        dbHashMap.put("roomIndex", Integer.toString(this.ROOM_INDEX));
        dbHashMap.put("isLit", this.isLit.toString());
        dbHashMap.put("lightSwitch", this.lightSwitch.toString());
        dbHashMap.put("game", Integer.toString(this.game.id));
        dbHashMap.put("occupants", this.occupants.toString());
        this.game.db.insertOne("Rooms", dbHashMap);
    }

    public HashMap getRoom(int roomIndex) {
        DB db = new DB();
        Bson filter = and(eq("roomIndex", Integer.toString(roomIndex)), eq("game", Integer.toString(this.game.id)));
        MongoCursor<Document> roomString = db.findOneWithFilters("Rooms", filter);
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

    public void switchLights(PlayerModel playerModel) {
        System.out.println("playerModel: "+playerModel);
        System.out.println("this.lightSwitch: "+this.lightSwitch);
        System.out.println("this.isLit: "+this.isLit);
        if (this.lightSwitch) {
            this.isLit = !this.isLit;
            if (this.isLit) {
                playerModel.notify_player("Room is lit now");
                this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "isLit", this.isLit.toString());
            } else {
                playerModel.notify_player("Room is dark now");
                this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "isLit", this.isLit.toString());
            }
        } else {
            playerModel.notify_player("This room has no lights switch, use a flash light");
        }
    }

    public int useFlashLight(int flashLights, PlayerModel playerModel) {
        if (this.isLit) {
            return flashLights;
        } else {
            this.isLit = true;
            playerModel.notify_player("Room is lit now");
            this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "isLit", this.isLit.toString());
            return --flashLights;
        }
    }

    public Boolean getIsLit() {
        return isLit;
    }

    public void addOccupant(PlayerViewer playerViewer, Game game) {
        if (this.occupants.size() > 0) {
            game.fight(playerViewer.playerSession, this.occupants.get(0));
            return;
        }
        if (playerViewer != null) {
            this.occupants.add(playerViewer.playerSession);
            this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "occupants", this.occupants.toString());
        } else {
            System.out.println("addOccupant: no player is to be added");
        }

        this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "occupants", this.occupants.toString());
    }

    public void addItemLocation(String location) {
        ArrayList<String> list = new ArrayList<>();
//        System.out.println("new Object[]{this.getRoom(this.ROOM_INDEX).get(\"itemsLocation\")}: "+ ((HashMap) this.getRoom(this.ROOM_INDEX).get("itemsLocation")).keySet());
//        list.toArray(new Object[]{this.getRoom(this.ROOM_INDEX).get("itemsLocation")});
//        System.out.println("list: "+new Object[]{this.getRoom(this.ROOM_INDEX).get("itemsLocation")}[0]);
        //        MongoCursor<Document> cursor = (MongoCursor<Document>) this.getRoom(this.ROOM_INDEX).get("itemsLocation");
//
//        try {
//            while (cursor.hasNext()) {
//                Document doc = cursor.next();
//                System.out.println(
//                    "name: " + doc.get("name") + "\n" + "artist: " + doc.get("artist")
//                );
//            }
//        } finally {
//            cursor.close();
//        }
//        this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_INDEX), "itemsLocation", this.getRoom(this.ROOM_INDEX).get("itemsLocation").toString());
    }

    public Map<String, Object> getEmptyLocation() {
        Map<String, Object> output = new HashMap<>();
        for (Wall wall : this.walls.values()) {
            if (wall.hasItems()) {
                for (String location : wall.locations) {
                    if (wall.itemsLocations.get(location) == null) {
                        output.put("wall", wall);
                        output.put("location", location);
                        return output;
                    }
                }
            } else {
                output.put("wall", wall);
                output.put("location", wall.locations.get(1));
                return output;
            }
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Room) {
            Room room = (Room) o;
            return room.ROOM_NAME.equals(this.ROOM_NAME) && (room.ROOM_INDEX == this.ROOM_INDEX);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.ROOM_NAME.hashCode() + this.ROOM_INDEX.hashCode();
    }

    @Override
    public String toString() {
        return "You are in room: " + this.ROOM_NAME;
    }
}
