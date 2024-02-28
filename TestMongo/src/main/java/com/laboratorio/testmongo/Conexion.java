package com.laboratorio.testmongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import java.util.concurrent.TimeUnit;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conexion {
    private static final Logger log = LoggerFactory.getLogger("***ConexiónMongoDB***");
    private final String appName;
    private final ConnectionString connectionString;
    private MongoClient client;

    public Conexion(String appName, String host, int port, String username, String password) {
        this.appName = appName;
        String uri = String.format("mongodb://%s:%s@%s:%d/", username, password, host, port);
        this.connectionString = new ConnectionString(uri);
        this.client = null;
    }

    public MongoClient getClient() {
        return client;
    }
    
    public void showClusterDescription() {
        if (this.client == null) {
            log.warn("showClusterDescription(): no hay conexión establecida!");
            return;
        }
        
        log.info("Información del cluster");
        log.info(this.client.getClusterDescription().toString());
    }

    public void showDatabases() {
        if (this.client == null) {
            log.warn("showDatabases(): no hay conexión establecida!");
            return;
        }
        
        ListDatabasesIterable<Document> databases = this.client.listDatabases();
       
        log.info("Listado de bases de datos para la conexión establecida");
        int i = 1;
        for (Document doc: databases) {
            log.info(String.format("%d-) %s", i, doc.toString()));
            i++;
        }
    }
    
    private boolean createConnection() throws MongoException {
        try {
            //MongoClient clienteMongo = MongoClients.create(this.uri);
            MongoClient mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applicationName(appName)
                            .applyConnectionString(this.connectionString)
                            .applyToConnectionPoolSettings(builder ->
                                    builder.minSize(3)
                                    .maxSize(25)
                                    .maxWaitTime(60, TimeUnit.SECONDS)
                                    .build()
                            )
                    .build()
            );
            log.info("Conexión a MongoDB creada");
            this.client = mongoClient;
            
            MongoDatabase database = this.client.getDatabase("admin");
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document result = database.runCommand(command);
            log.info("Conexión establecida. Se hizo ping a la base de datos admin: " + result.toString());
            return true;
        } catch (MongoException e) {
            log.error("Error: " + e.getMessage());
            throw e;
        }
    }
    
    private MongoDatabase getDatabaseWithCodec(String databaseName) throws Exception {
        if (this.client == null) {
            try {
                if (!createConnection()) {
                    throw new Exception("No se ha podido establecer la conexión con el servidor MongoDB");
                }
                
            } catch (MongoException e) {
                log.error("Error: " + e.getMessage());
                throw e;
            }
        }
        
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = this.client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document result = database.runCommand(command);
            log.info(String.format("Conexión establecida. Se hizo ping a la base de datos %s: %s", databaseName, result.toString()));
        } catch (MongoException e) {
            log.error("Error: " + e.getMessage());
            throw e;
        }
        
        return database;
    }
    
    public MongoCollection<?> getPOJOCollection(String databaseName, String collectionName, Class clase) throws Exception {
        MongoDatabase database;
        
        try {
            database = getDatabaseWithCodec(databaseName);
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            throw e;
        }
        
        return database.getCollection(collectionName, clase);
    }
    
    private MongoDatabase getDatabase(String databaseName) throws Exception {
        if (this.client == null) {
            try {
                if (!createConnection()) {
                    throw new Exception("No se ha podido establecer la conexión con el servidor MongoDB");
                }
                
            } catch (MongoException e) {
                log.error("Error: " + e.getMessage());
                throw e;
            }
        }
        
        MongoDatabase database = this.client.getDatabase(databaseName);
        
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document result = database.runCommand(command);
            log.info(String.format("Conexión establecida. Se hizo ping a la base de datos %s: %s", databaseName, result.toString()));
        } catch (MongoException e) {
            log.error("Error: " + e.getMessage());
            throw e;
        }
        
        return database;
    }
    
    public MongoCollection<?> getCollection(String databaseName, String collectionName, Class clase) throws Exception {
        MongoDatabase database;
        
        try {
            database = getDatabase(databaseName);
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            throw e;
        }
        
        return database.getCollection(collectionName, clase);
    }
    
    public void close() {
        if (this.client != null) {
            this.client.close();
        }
    }
}