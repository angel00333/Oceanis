
package com.aqua.oceanis.models;


import java.sql.*;
import java.util.*;
public class PuertoDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Puerto puerto;
    
    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }
    
    public int InsertarPuerto(Puerto puerto) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("INSERT INTO puertos(nombre, direccion)" + "VALUES(?,?)");
            sql.setString(1, puerto.getNombre());
            sql.setString(2, puerto.getDireccion());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }
    
    public int ModificarPuerto(Puerto puerto) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("UPDATE puertos SET nombre=?, direccion=? WHERE Id=?");
            sql.setString(1, puerto.getNombre());
            sql.setString(2, puerto.getDireccion());
            sql.setInt(3, puerto.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }
    
    public int EliminarPuerto(Puerto puerto) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("DELETE FROM puertos WHERE Id=?");
            sql.setInt(1, puerto.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }
    
    public ArrayList<Puerto> MostrarPuertos() {
        ArrayList<Puerto> puertos = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM puertos");
            rs = sql.executeQuery();
            while(rs.next()) {
                puerto = new Puerto (rs.getInt(1), rs.getString(2), rs.getString(3));
                puertos.add(puerto);
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return puertos;
    }
    
    public ArrayList<Puerto> BuscarPuertos(String str) {
    ArrayList<Puerto> ar = new ArrayList<Puerto>();
    try {
        sql = conectar().prepareStatement("SELECT * FROM puertos WHERE nombre LIKE ?");
        sql.setString(1, "%" + str + "%");
        rs = sql.executeQuery();

        while (rs.next()) {
            puerto = new Puerto(rs.getInt("id"), rs.getString("nombre"),rs.getString("direccion"));
            ar.add(puerto);
        }

        rs.close();
        sql.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return ar;
}
    
    public Puerto ObtenerPuertoPorId(int id) {
        try{
            sql = conectar().prepareStatement("SELECT * FROM puertos WHERE Id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();
            while(rs.next()) {
                puerto = new Puerto(rs.getInt(1), rs.getString(2), rs.getString(3));
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return puerto;
    }
}
