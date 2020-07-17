package com.worldNavigator;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    public HashMap<String, Wall> walls = new HashMap<>();
    public final String ROOM_NAME;
    private Boolean isLit;
    public Boolean lightSwitch;
    public final Integer ROOM_NUMBER;
    public List<PlayerViewer> occupants = new ArrayList<>();
    Game game;

    public Room(JSONObject room, int room_counter, Game game) {
        this.game = game;
        this.ROOM_NAME =
            room.get("name") == null ? "room_" + (room_counter + 1) : room.get("name").toString();
        this.ROOM_NUMBER = room_counter;
        generateRoom(room);
    }

    private JSONObject castToJSONObject(Object o) {
        return (JSONObject) o;
    }

    private void generateRoom(JSONObject room) {
        try {
            Wall north_wall = new Wall("north_wall", castToJSONObject(room.get("n_wall")), this.ROOM_NUMBER, this.game);
            Wall east_wall = new Wall("east_wall", castToJSONObject(room.get("e_wall")), this.ROOM_NUMBER, this.game);
            Wall south_wall = new Wall("south_wall", castToJSONObject(room.get("s_wall")), this.ROOM_NUMBER, this.game);
            Wall west_wall = new Wall("west_wall", castToJSONObject(room.get("w_wall")), this.ROOM_NUMBER, this.game);

            walls.put("north", north_wall);
            walls.put("east", east_wall);
            walls.put("south", south_wall);
            walls.put("west", west_wall);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        this.isLit = room.get("lit") == null || Boolean.parseBoolean(room.get("lit").toString());
        this.lightSwitch =
            (room.get("switch") != null) && Boolean.parseBoolean(room.get("switch").toString());

        this.generateCollection();
    }

    private void generateCollection() {
        HashMap<String, String> dbHashMap = new HashMap<>();
        dbHashMap.put("name", this.ROOM_NAME);
        dbHashMap.put("roomNumber", Integer.toString(this.ROOM_NUMBER));
        dbHashMap.put("isLit", this.isLit.toString());
        dbHashMap.put("lightSwitch", this.lightSwitch.toString());
        dbHashMap.put("game", Integer.toString(this.game.id));
        dbHashMap.put("occupants", this.occupants.toString());
        this.game.db.insertOne("Rooms", dbHashMap);
    }

    public void switchLights(PlayerModel playerModel) {
        if (this.lightSwitch) {
            this.isLit = !this.isLit;
            if (this.isLit) {
                playerModel.notify_player("Room is lit now");
                this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_NUMBER), "isLit", this.isLit.toString());
            } else {
                playerModel.notify_player("Room is dark now");
                this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_NUMBER), "isLit", this.isLit.toString());
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
            this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_NUMBER), "isLit", this.isLit.toString());
            return --flashLights;
        }
    }

    public Boolean getIsLit() {
        return isLit;
    }

    public void addOccupant(PlayerViewer playerViewer, Game game) {
        if (this.occupants.size() > 0) {
            game.fight(playerViewer, this.occupants.get(0));
            return;
        }
        if (playerViewer != null) {
            this.occupants.add(playerViewer);
        } else {
            System.out.println("addOccupant: no player is to be added");
        }

        this.game.db.updateRoom(Integer.toString(this.game.id), Integer.toString(this.ROOM_NUMBER), "occupants", this.occupants.toString());
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
            return room.ROOM_NAME.equals(this.ROOM_NAME) && (room.ROOM_NUMBER == this.ROOM_NUMBER);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.ROOM_NAME.hashCode() + this.ROOM_NUMBER.hashCode();
    }

    @Override
    public String toString() {
        return "You are in room: " + this.ROOM_NAME;
    }
}
