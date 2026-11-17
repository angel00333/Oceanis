
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;

public class BarcoDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Barco barco;

    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }

    public int InsertarBarco(Barco barco) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("INSERT INTO barcos(nombre, tipo, pais) VALUES(?,?,?)");
            sql.setString(1, barco.getNombre());
            sql.setString(2, barco.getTipo());
            sql.setString(3, barco.getPais());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public int ModificarBarco(Barco barco) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("UPDATE barcos SET nombre=?, tipo=?, pais=? WHERE id=?");
            sql.setString(1, barco.getNombre());
            sql.setString(2, barco.getTipo());
            sql.setString(3, barco.getPais());
            sql.setInt(4, barco.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public int EliminarBarco(Barco barco) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("DELETE FROM barcos WHERE id=?");
            sql.setInt(1, barco.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public ArrayList<Barco> MostrarBarcos() {
        ArrayList<Barco> barcos = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM barcos");
            rs = sql.executeQuery();
            while(rs.next()) {
                barco = new Barco(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                barcos.add(barco);
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return barcos;
    }

    public ArrayList<Barco> BuscarBarcos(String str) {
        ArrayList<Barco> ar = new ArrayList<Barco>();
        try {
            sql = conectar().prepareStatement("SELECT * FROM barcos WHERE nombre LIKE ?");
            sql.setString(1, "%" + str + "%");
            rs = sql.executeQuery();

            while (rs.next()) {
                barco = new Barco(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("tipo"), rs.getString("pais"));
                ar.add(barco);
            }

            rs.close();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ar;
    }

    public Barco ObtenerBarcoPorId(int id) {
        try{
            sql = conectar().prepareStatement("SELECT * FROM barcos WHERE id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();
            while(rs.next()) {
                barco = new Barco(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return barco;
    }
}
