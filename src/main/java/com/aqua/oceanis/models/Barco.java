
package com.aqua.oceanis.models;


public class Barco {
    int id;
    String nombre;
    String tipo;
    String pais;

    public Barco() {
    }

    public Barco(int id, String nombre, String tipo, String pais) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.pais = pais;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
    
    
}
