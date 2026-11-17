
package com.aqua.oceanis.models;


public class Muelle {
    int id;
    Puerto puerto;
    String nombre;
    String estado;

    public Muelle() {
    }

    public Muelle(int id, Puerto puerto, String nombre, String estado) {
        this.id = id;
        this.puerto = puerto;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Puerto getPuerto() {
        return puerto;
    }

    public void setPuerto(Puerto puerto) {
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
