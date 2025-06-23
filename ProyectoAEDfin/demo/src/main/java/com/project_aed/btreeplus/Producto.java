package com.project_aed.btreeplus;

// imports necesarios

public class Producto {
    private String nombre;
    private String lote;
    private String categoria;
    private String fechaCaducidad;

    public Producto(String nombre, String lote, String categoria, String fechaCaducidad) {
        this.nombre = nombre;
        this.lote = lote;
        this.categoria = categoria;
        this.fechaCaducidad = fechaCaducidad;
    }

    @Override
    public String toString() {
        return "[" + nombre + ", Lote:" + lote + ", Cat:" + categoria + ", Caduca:" + fechaCaducidad + "]";
    }
}
