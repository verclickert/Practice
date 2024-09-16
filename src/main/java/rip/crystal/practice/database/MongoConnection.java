package rip.crystal.practice.database;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import rip.crystal.practice.cPractice;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Hysteria Development
 * @project cPractice
 * @date 3/6/2023
 */

@Getter
public class MongoConnection {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoConnection(String uri) {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();

        this.mongoClient = MongoClients.create(settings);
        this.mongoDatabase = this.mongoClient.getDatabase(Objects.requireNonNull(connectionString.getDatabase()));
    }

    public MongoConnection(String host, int port, String database) {
        this.mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build());
        this.mongoDatabase = this.mongoClient.getDatabase(database);
    }

    // Constructor using authentication
    public MongoConnection(String host, int port, String username, String password, String database) {
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        this.mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .credential(credential)
                .build());
        this.mongoDatabase = this.mongoClient.getDatabase(database);
    }

    public void checkConnection() {
        BasicDBObject ping = new BasicDBObject("ping", 1);
        mongoDatabase.runCommand(ping);
        System.out.println("Ping successful: Connected to MongoDB.");

        String databaseName = cPractice.get().getDatabaseConfig().getString("MONGO.DATABASE");
        mongoDatabase = mongoClient.getDatabase(databaseName);
        System.out.println("Database created or accessed: " + databaseName);

        // List available databases
        for (String dbName : mongoClient.listDatabaseNames()) {
            System.out.println("Database: " + dbName);
        }
    }
}
