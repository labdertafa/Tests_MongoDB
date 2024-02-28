package com.laboratorio.testmongo.entidad;

import org.bson.types.ObjectId;

public record Articulo(
        ObjectId id,
        String nombre,
        double precio
) {}