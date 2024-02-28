package com.laboratorio.crudmongo.model;

import org.bson.types.ObjectId;

public record Articulo(
        ObjectId id,
        String nombre,
        double precio
) {}