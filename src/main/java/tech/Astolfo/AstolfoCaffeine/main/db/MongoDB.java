package tech.Astolfo.AstolfoCaffeine.main.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDB {
    static class Database {
        public static ConnectionString conStr = new ConnectionString(System.getenv("CON_URL"));
        public static MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(conStr)
                .retryWrites(true)
                .build();
        public static MongoClient mongoClient = MongoClients.create(settings);
    }
}
