package com.worldNavigator;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class RoomDrawer {
    private HashMap<Integer, String> mapStrings = new HashMap<>();
    private String location;
    private Room room;
    public String roomDraw;
    private Game game;

    public RoomDrawer(String location, Room room, Game game) {
        if (room.getIsLit()) {
            this.mapStrings.put(0, "a");
            this.mapStrings.put(1, "b");
            this.mapStrings.put(2, "c");
            this.mapStrings.put(3, "d");
            this.mapStrings.put(4, "e");

            this.location = location;
            this.room = room;
            this.game = game;

            if (room.getIsLit()) {
                this.roomDraw = row(1);
                this.roomDraw += "<br>" + row(2);
                this.roomDraw += "<br>" + row(3);
                this.roomDraw += "<br>" + row(4);
                this.roomDraw += "<br>" + row(5);
                this.roomDraw += "<br>" + this.room.ROOM_NAME;
            } else {
                this.roomDraw = "Room is not lit";
            }
        }
    }

    public HashMap<String, JSONObject> getWalls() {
        DB db = new DB();
        Bson filter = and(eq("roomIndex", Integer.toString(this.room.ROOM_INDEX)), eq("game", Integer.toString(this.game.id)));
        MongoCursor<Document> wallsString = db.findOneWithFilters("Walls", filter);
        JSONArray jsonArray = new JSONArray();
        while (wallsString.hasNext()) {
            Document doc = wallsString.next();
            jsonArray.add(doc.toJson());
        }

        HashMap<String, JSONObject> output = new HashMap();
        for (Object obj : jsonArray) {
            System.out.println("ROOM DRAWER obj: "+obj);
            JSONObject json;
            try {
                json = (JSONObject) new JSONParser().parse(obj.toString());
                System.out.println("ROOM DRAWER json: "+json);
                output.put(json.get("name").toString(), json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    private String row(int num) {
        String row = "";
        for (int i = 0; i < 5; ++i) {
            if (this.location.equals(this.mapStrings.get(i) + num)) {
//        row += "\u29EF\u29EF   ";
                row += "□□ ";
            } else {
                boolean item = false;
                for (JSONObject jsonWall : this.getWalls().values()) {
                    if (!item) {
                        item = ((JSONArray) jsonWall.get("items")).contains(mapStrings.get(i) + num);
                    }
                }
                row += !item ? mapStrings.get(i) + num + "   " : "■■   ";
            }
        }
        return row;
    }
}
