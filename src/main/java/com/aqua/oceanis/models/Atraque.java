
package com.aqua.oceanis.models;
import java.time.LocalDateTime;

public class Atraque {
    int id;
    Barco barco;
    Muelle muelle;
    LocalDateTime fecha_llegada;
    LocalDateTime fecha_salida;
    String estado;

    public Atraque() {
    }

    public Atraque(int id, Barco barco, Muelle muelle, LocalDateTime fecha_llegada, LocalDateTime fecha_salida, String estado) {
        this.id = id;
        this.barco = barco;
        this.muelle = muelle;
        this.fecha_llegada = fecha_llegada;
        this.fecha_salida = fecha_salida;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Barco getBarco() {
        return barco;
    }

    public void setBarco(Barco barco) {
        this.barco = barco;
    }

    public Muelle getMuelle() {
        return muelle;
    }

    public void setMuelle(Muelle muelle) {
        this.muelle = muelle;
    }

    public LocalDateTime getFecha_llegada() {
        return fecha_llegada;
    }

    public void setFecha_llegada(LocalDateTime fecha_llegada) {
        this.fecha_llegada = fecha_llegada;
    }

    public LocalDateTime getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(LocalDateTime fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
