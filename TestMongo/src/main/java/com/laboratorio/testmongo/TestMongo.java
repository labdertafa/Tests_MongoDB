package com.laboratorio.testmongo;

import com.laboratorio.testmongo.entidad.Articulo;
import com.laboratorio.testmongo.entidad.Cliente;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMongo {
    private static final Logger log = LoggerFactory.getLogger("***TestMongoDB***");

    public static void main(String[] args) {
        Conexion conexion = new Conexion("TestMongo", "localhost", 27017, "admin", "1234");
        MongoCollection<Cliente> clientes;
     
        try {
            clientes = (MongoCollection<Cliente>)conexion.getPOJOCollection("redsocial", "clientes", Cliente.class);
        } catch (Exception e) {
            log.error("Ha sido imposible conectarse a la colección clientes. Finaliza el programa!");
            log.error("Error: " + e.getMessage());
            return;
        }
        log.info("Me he conectado a la colección: clientes");
        
        conexion.showClusterDescription();
        
        conexion.showDatabases();
        
        Cliente cliente1 = new Cliente("Rafael Tineo", "633958676", "Calle Silverio Sánchez 18", 0, 0);
        clientes.insertOne(cliente1);
        Cliente cliente2 = new Cliente("María Vielma", "681692977", "Calle Silverio Sánchez 18", 0, 0);
        clientes.insertOne(cliente2);
        Cliente cliente3 = new Cliente("Natalia Vielma", "641381970", "Calle Silverio Sánchez 18", 0, 0);
        clientes.insertOne(cliente3);
        
        List<Cliente> lista = new ArrayList<>();
        clientes.find().into(lista);
        
        log.info("Listado de clientes registrados");
        for (Cliente cli: lista) {
            log.info(cli.toString());
        }
        
        MongoCollection<Articulo> articulos;
        
        try {
            articulos = (MongoCollection<Articulo>)conexion.getCollection("redsocial", "articulos", Articulo.class);
        } catch (Exception e) {
            log.error("Ha sido imposible conectarse a la colección articulos. Finaliza el programa!");
            log.error("Error: " + e.getMessage());
            conexion.close();
            return;
        }
        log.info("Me he conectado a la colección: articulos");
        
        articulos.insertOne(new Articulo(new ObjectId(), "Laptop", 1000));
        articulos.insertOne(new Articulo(new ObjectId(), "Móvil", 500));
        articulos.insertOne(new Articulo(new ObjectId(), "Monitor", 150));
        
        List<Articulo> lista2 = new ArrayList<>();
        articulos.find().into(lista2);
        
        log.info("Listado de artículos registrados");
        for (Articulo art: lista2) {
            log.info(art.toString());
        }
        
        log.info("Cerrando la conexión para finalizar el programa");
        
        conexion.close();
    }
}