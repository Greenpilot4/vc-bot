package me.greenpilot.vc;

import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public abstract class MongoDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDB.class);

    private static final MongoClient mongoClient = MongoClients.create(config.Get("MONGODB"));
    protected static MongoDatabase database = mongoClient.getDatabase("antizook");
    protected static MongoCollection<Document> collection = database.getCollection("guild-data");

    public static void makeGuildDoc(String guild, String guild_name) {
        Document guild_data = new Document("_id", guild)
                .append("guild_name", guild_name)
                .append("admins", Arrays.asList());

        collection.insertOne(guild_data);
    }
    public static void addAdmin(String guild, String uid) {
        collection.updateOne(
                eq("_id",guild),
                addToSet("admins", uid)
        );
    }
    public static void removeAdmin(String guild, String uid) {
        Bson query =Filters.eq("_id", guild);
        Bson delete = Updates.pull("admins", uid);

        collection.updateOne(query, delete);
    }
    public static Boolean checkAdmins(String guild, String uid) {
        final boolean[] isAdmin = {false};
        Consumer<Document> printConsumer = new Consumer<Document>() {
            @Override
            public void accept(final Document document) {
                if(document.toJson() != null)
                    isAdmin[0] = true;
            }
        };
        collection.find(and(eq("_id", guild), eq("admins", uid)))
                .forEach(printConsumer);
        return isAdmin[0];
    }
    public static void setTarget(String guild, String uid) {
        collection.updateOne(
                eq("_id",guild),
                set("target", uid)
        );
    }
    public static Boolean checkTarget(String guild, String uid) {
        final boolean[] isTarget = {false};
        Consumer<Document> printConsumer = new Consumer<Document>() {
            @Override
            public void accept(final Document document) {
                if(document.toJson() != null)
                    isTarget[0] = true;
            }
        };
        collection.find(and(eq("_id", guild), eq("target", uid)))
                .forEach(printConsumer);
        return isTarget[0];
    }
}