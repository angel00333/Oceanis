
package com.aqua.oceanis.models;
import java.math.BigDecimal;

public class Carga {
    int id;
    Atraque atraque;
    Cliente cliente;
    String descripcion;
    BigDecimal peso;
    String tipo;

    public Carga() {
    }

    public Carga(int id, Atraque atraque, Cliente cliente, String descripcion, BigDecimal peso, String tipo) {
        this.id = id;
        this.atraque = atraque;
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.peso = peso;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Atraque getAtraque() {
        return atraque;
    }

    public void setAtraque(Atraque atraque) {
        this.atraque = atraque;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
}
