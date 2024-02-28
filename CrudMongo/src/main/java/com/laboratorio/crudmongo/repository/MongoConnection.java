package com.laboratorio.crudmongo.repository;

import com.laboratorio.crudmongo.configuration.PropertiesFile;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("MongoConnection")
public class MongoConnection {
    private static final Logger log = LoggerFactory.getLogger("**ConexiónMongoDB**");
    private String applicationName = "";
    private ConnectionString connectionString = null;
    private MongoClient client;
    private int minPoolSize;
    private int maxPoolSize;
    private int maxWaitTime;

    public MongoConnection() {
        Properties properties = null;
        
        try {
            PropertiesFile propertiesFile = new PropertiesFile("mongodb.properties");
            properties = propertiesFile.readPropertiesFile();
        } catch (Exception e) {
            trazarError("Error al recuperar la configuración de le base de datos.", e);
            return;
        }
        
        this.applicationName = "";
        this.connectionString = null;
        this.client = null;
        this.minPoolSize = 3;
        this.maxPoolSize = 50;
        this.maxWaitTime = 60;
        if (properties != null) {
            this.applicationName = properties.getProperty("applicationName");
            String host = properties.getProperty("host");
            String port = properties.getProperty("port");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String uri = String.format("mongodb://%s:%s@%s:%s/", username, password, host, port);
            this.connectionString = new ConnectionString(uri);
            this.minPoolSize = Integer.parseInt(properties.getProperty("minPoolSize"));
            this.maxPoolSize = Integer.parseInt(properties.getProperty("maxPoolSize"));
            this.maxWaitTime = Integer.parseInt(properties.getProperty("maxWaitTime"));
        }
    }
    
    private void trazarError(String mensaje, Exception e) {
        log.error(mensaje);
        log.error("Error: " + e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: " + e.getCause().getMessage());
        }
    }
    
    private boolean createConnection() throws MongoException {
        try {
            //MongoClient clienteMongo = MongoClients.create(this.uri);
            MongoClient mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applicationName(this.applicationName)
                            .applyConnectionString(this.connectionString)
                            .applyToConnectionPoolSettings(builder ->
                                    builder.minSize(this.minPoolSize)
                                    .maxSize(this.maxPoolSize)
                                    .maxWaitTime(this.maxWaitTime, TimeUnit.SECONDS)
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
    
    @PostConstruct
    public void iniciar() {
        log.info("Iniciando el pool de conexiones a MongoDB");
        if (this.connectionString != null) {
            try {
                createConnection();
            } catch (MongoException e) {
                trazarError("No se pudo crear la conexión a MongoDB", e);
                this.client = null;
            }
        } else {
            log.error("No se pudo crear la conexión a MongoDB. La connectionString es NULL.");
        }
    }
    
    private MongoDatabase getDatabase(String databaseName) throws Exception {
        if (this.client == null) {
            log.error("No se pudo acceder a la base de datos porque la conexión es NULL.");
            throw new Exception("No se pudo acceder a la base de datos porque no hay conexión establecida.");
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
    
    @PreDestroy
    public void cerrar() {
        log.info("Cerrando la conexión a la base de datos");
        if (this.client != null) {
            this.client.close();
            log.info("La conexión a la base de datos ha sido cerrada");
        }
    }
}