package com.laboratorio.testmongo.entidad;

import org.bson.types.ObjectId;

public class Cliente {
    private ObjectId id;
    private String nombre;
    private String telefono;
    private String direccion;
    private int nroCompras;
    private double montoCompras;

    public Cliente() {
    }

    public Cliente(String nombre, String telefono, String direccion, int nroCompras, double montoCompras) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.nroCompras = nroCompras;
        this.montoCompras = montoCompras;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getNroCompras() {
        return nroCompras;
    }

    public void setNroCompras(int nroCompras) {
        this.nroCompras = nroCompras;
    }

    public double getMontoCompras() {
        return montoCompras;
    }

    public void setMontoCompras(double montoCompras) {
        this.montoCompras = montoCompras;
    }

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", nombre=" + nombre + ", telefono=" + telefono + ", direccion=" + direccion + ", nroCompras=" + nroCompras + ", montoCompras=" + montoCompras + '}';
    }            
}