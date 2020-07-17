package com.worldNavigator;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import java.util.HashMap;

public class DB {
    MongoDatabase db;

    public DB() {
        MongoClientURI uri = new MongoClientURI("mongodb://Isa:Isa123456@ds235877.mlab.com:35877/worldnavigator?retryWrites=false");
        MongoClient client = new MongoClient(uri);
        this.db = client.getDatabase(uri.getDatabase());

        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

        // Note that the insert method can take either an array or a document.

//        songs.insertOne(new Document("decade", "1970s")
//            .append("artist", "Debby Boone")
//            .append("song", "You Light Up My Life")
//            .append("weeksAtOne", 10));

        // Create seed data
//        List<Document> seedData = new ArrayList<Document>();
//
//        seedData.add(new Document("decade", "1970s")
//            .append("artist", "Debby Boone")
//            .append("song", "You Light Up My Life")
//            .append("weeksAtOne", 10)
//        );
//
//        Document student = new Document();
//        student.append("name", "FROM CODE")
//            .append("artist", "ARTIST FROM CODE");
//
//        songs.insertOne(student);

        /*
         * Finally we run a query which returns all the hits that spent 10
         * or more weeks at number 1.
         */

//        Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
//        Document orderBy = new Document("decade", 1);
//
//        MongoCursor<Document> cursor = songs.find(findQuery).sort(orderBy).iterator();
//
//        Document findQuery = new Document("name", "FROM CODE");
//        MongoCursor<Document> cursor = songs.find(findQuery).iterator();

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

        // Since this is an example, we'll clean up after ourselves.
        // songs.drop();

        // Only close the connection when your app is terminating
        // client.close();
    }

    public void generateCollection(String collectionName) {
        this.db.createCollection(collectionName);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        MongoCollection<Document> collection = this.db.getCollection(collectionName);
        return collection;
    }

    public void insertOne(String collectionName, HashMap<String, String> doc) {
        MongoCollection<Document> collection = this.getCollection(collectionName);
        Document document = new Document();
        for (String key : doc.keySet()) {
            document.append(key, doc.get(key));
        }
        collection.insertOne(document);
    }

    public MongoCursor<Document> findOne(String collection, String key, String value) {
        MongoCollection<Document> coll = this.getCollection(collection);

        Document findQuery = new Document(key, value);
        return coll.find(findQuery).iterator();
    }

    public MongoCursor<Document> findOneWithFilters(String collection, Bson filter) {
        MongoCollection<Document> coll = this.getCollection(collection);
        return coll.find(filter).iterator();
    }

    public String readFound(MongoCursor<Document> cursor, String key) {
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println(key + ": " + doc.get(key));
            if (doc.get(key) != null) {
                return doc.get(key).toString();
            }
        }
        return "";
    }

    public HashMap<String, String> foundMap(MongoCursor<Document> cursor) {
        HashMap<String, String> output = new HashMap<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println("doc: "+doc);
        }
        return output;
    }

    public void deleteOne(MongoCollection<Document> collection, HashMap<String, String> doc) {
        Document document = new Document();
        for (String key : doc.keySet()) {
            document.append(key, doc.get(key));
        }
        collection.deleteOne(document);
    }

    public void updateItem(String collection, String location, String field, String change) {
        MongoCollection<Document> coll = this.getCollection(collection);

        Bson filter = eq("location", location);
        Bson updateOperation = set(field, change);
        coll.updateOne(filter, updateOperation);
    }

    public void updateRoom(String game, String roomNumber, String field, String change) {
        MongoCollection<Document> coll = this.getCollection("Rooms");

        Bson filter = and(eq("roomNumber", roomNumber), eq("game", game));
        Bson updateOperation = set(field, change);
        coll.updateOne(filter, updateOperation);
    }

    public void updatePlayer(String collection, String sessionId, String field, String change) {
        MongoCollection<Document> coll = this.getCollection(collection);

        Bson filter = eq("sessionId", sessionId);
        Bson updateOperation = set(field, change);
        coll.updateOne(filter, updateOperation);
    }
}
