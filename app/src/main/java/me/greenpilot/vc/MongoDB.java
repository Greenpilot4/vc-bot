package me.greenpilot.vc;

import com.mongodb.*;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDB.class);
    private static MongoClient mongoClient;
    private static DB database;
    private static DBCollection collection;
    static boolean connected;

    private static void connect() {
        mongoClient = new MongoClient(new MongoClientURI(config.Get("MONGODB")));
        database = mongoClient.getDB("antizook");
        collection = database.getCollection("guild-data");

        try {
            mongoClient.getAddress();
            connected = true;
        } catch (Exception e) {
            LOGGER.info("MongoDB connection unsuccessful.");
            connected = false;
            return;
        }
    }

    public static void setAdmin(String guildID, String adminID) {
        if (!connected) {
            connect();
        }
        String json = "{ $push: { ADMINS: " + adminID + " } }";
        DBObject push = (DBObject) JSON.parse(json);
        DBObject find = new BasicDBObject("GUILD", guildID);
        collection.update(find, push);
    }

    public static void setTarget(String guildID, String targetID) {
        if (!connected) {
            connect();
        }
        String json = "{ $set: { TARGET: " + targetID + " } }";
        DBObject push = (DBObject) JSON.parse(json);
        DBObject find = new BasicDBObject("GUILD", guildID);
        collection.update(find, push);
    }

    public static String getAdmins(String guildID) {
        String admins = null;
        if (!connected) {
            connect();
        }

        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            if (obj.get("GUILD").toString().contains(guildID)) {
                admins = obj.get("ADMINS").toString();
            }
        }
        if (admins == null) {
            admins = "Failed to get admins.";
        }
        return admins;
    }

    public static String getTarget(String guildID) {
        String target = null;
        if (!connected) {
            connect();
        }

        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            if (obj.get("GUILD").toString().contains(guildID)) {
                target = obj.get("TARGET").toString();
            }
        }
        if (target == null) {
            target = "Failed to get target.";
        }
        return target;
    }
}
