package com.worldNavigator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class Wall {
  public final String NAME;
  public ItemsFactory itemsFactory;
  public Map<String, Item> items = new HashMap<>();
  public ArrayList<String> locations = new ArrayList<>();
  public Map<String, String> itemsLocations = new HashMap<>();
  int roomIndex;
  Game game;
  Room room;

  public Wall(String name, JSONObject wall, Room room, Game game) {
    this.game = game;
    this.NAME = name;
    this.room = room;
    this.roomIndex = room.ROOM_INDEX;

    this.generateWall(wall);
    this.wallLocations(name);
    this.generateCollection(wall);
  }

  public Wall(JSONObject wall, Game game) {
    this.game = game;
    this.NAME = wall.get("name").toString();
    this.roomIndex = Integer.parseInt(wall.get("roomIndex").toString());

    this.generateWall(wall);
    this.wallLocations(this.NAME);
  }

  private void generateWall(JSONObject wall) {
    this.itemsFactory = new ItemsFactory(wall, this);
    this.items = this.itemsFactory.items;
    this.itemsLocations = this.itemsFactory.itemsLocations;
  }

  private void wallLocations(String name) {
    switch (name) {
      case "north_wall":
        this.locations.add("a1");
        this.locations.add("b1");
        this.locations.add("c1");
        this.locations.add("d1");
        this.locations.add("e1");
        break;
      case "west_wall":
        this.locations.add("a1");
        this.locations.add("a2");
        this.locations.add("a3");
        this.locations.add("a4");
        this.locations.add("a5");
        break;
      case "south_wall":
        this.locations.add("a5");
        this.locations.add("b5");
        this.locations.add("c5");
        this.locations.add("d5");
        this.locations.add("e5");
        break;
      default:
        this.locations.add("e1");
        this.locations.add("e2");
        this.locations.add("e3");
        this.locations.add("e4");
        this.locations.add("e5");
        break;
    }
  }

  public MongoCollection<Document> getCollection(String collectionName) {
    MongoCollection<Document> collection = this.game.db.getCollection(collectionName);
    return collection;
  }

  private void generateCollection(JSONObject wall) {
    HashMap<String, Object> dbHashMap = new HashMap<>();
    dbHashMap.put("game", Integer.toString(this.game.id));
    dbHashMap.put("name", this.NAME);
    dbHashMap.put("roomIndex", Integer.toString(this.roomIndex));
//    Document document = new Document();
//    for (Object key : wall.keySet()) {
//      document.append(key.toString(), wall.get(key));
//      dbHashMap.put(key.toString(), wall.get(key).toString());
//    }
    dbHashMap.put("items", new ArrayList());
    dbHashMap.put("itemsLocations", this.itemsLocations);
    this.game.db.insertOne("Walls", dbHashMap);
    MongoCollection<Document> collection = this.getCollection("Walls");
    Bson filter = and(eq("name", this.NAME), eq("roomIndex", Integer.toString(this.roomIndex)), eq("game", Integer.toString(this.game.id)));
    collection.findOneAndUpdate(filter, Updates.pushEach("items", new ArrayList<>(this.itemsLocations.keySet())));

//    List<String> items = new ArrayList<String>();
//    items.add("Business");
//    items.add("Technology");
//    items.add("Sports");
//    items.add("Career");
//    MongoCursor<Document> incomingWall = this.game.db.findOne("Walls", "name", this.NAME);
//    JSONArray jsonArray = new JSONArray();
//    while (incomingWall.hasNext()) {
//      Document doc = incomingWall.next();
//      jsonArray.add(doc.toJson());
//    }
//
//    JSONObject json = null;
//    try {
//      json = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
//    } catch (ParseException e) {
//      e.printStackTrace();
//    }
//
//    System.out.println("json: "+((ArrayList) json.get("items")).get(0));

  }

  public String checkItems() {
    return this.items.isEmpty() ? "Nothing to look at" : this.items.values().toString();
  }

  public boolean hasItems() {
    return !this.items.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Wall) {
      Wall wall = (Wall) o;
      return this.NAME.equals(wall.NAME) && this.items.equals(wall.items);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.NAME.hashCode() + this.items.hashCode();
  }

  @Override
  public String toString() {
    return "Wall name: " + this.NAME + ", items: " + checkItems();
  }
}
