
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;

public class MuelleDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Puerto puerto;
    Muelle muelle;

    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }

    public int InsertarMuelle(Muelle muelle){
        int n = 0;
        try{
            sql = conectar().prepareStatement("INSERT INTO muelles "
                    + "(idpuerto, nombre, estado) VALUES(?,?,?)");
            sql.setInt(1, muelle.getPuerto().getId());
            sql.setString(2, muelle.getNombre());
            sql.setString(3, muelle.getEstado());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int ModificarMuelle(Muelle muelle){
        int n = 0;
        try{
            sql = conectar().prepareStatement("UPDATE muelles "
                    + "SET idpuerto=?, nombre=?, estado=? WHERE id=?");
            sql.setInt(1, muelle.getPuerto().getId());
            sql.setString(2, muelle.getNombre());
            sql.setString(3, muelle.getEstado());
            sql.setInt(4, muelle.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int EliminarMuelle(Muelle muelle){
        int n = 0;
        try{
            sql = conectar().prepareStatement("DELETE FROM muelles WHERE id=?");
            sql.setInt(1, muelle.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public ArrayList<Muelle> MostrarMuelles(){
        ArrayList<Muelle> muelles = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM muelles");
            rs = sql.executeQuery();
            while(rs.next()){
                puerto = new PuertoDAO().ObtenerPuertoPorId(rs.getInt("idpuerto"));
                muelle = new Muelle(rs.getInt("id"), puerto,
                        rs.getString("nombre"), rs.getString("estado"));
                muelles.add(muelle);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return muelles;
    }

    public ArrayList<Muelle> BuscarMuelles(String str){
        ArrayList<Muelle> ar = new ArrayList<Muelle>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM muelles WHERE nombre LIKE ?");
            sql.setString(1, "%" + str + "%");
            rs = sql.executeQuery();
            while(rs.next()){
                puerto = new PuertoDAO().ObtenerPuertoPorId(rs.getInt("idpuerto"));
                muelle = new Muelle(rs.getInt("id"), puerto,
                        rs.getString("nombre"), rs.getString("estado"));
                ar.add(muelle);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ar;
    }

    public Muelle ObtenerMuellePorId(int id){
        muelle = null;
        try{
            sql = conectar().prepareStatement("SELECT * FROM muelles WHERE id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();
            while(rs.next()){
                puerto = new PuertoDAO().ObtenerPuertoPorId(rs.getInt("idpuerto"));
                muelle = new Muelle(rs.getInt("id"), puerto,
                        rs.getString("nombre"), rs.getString("estado"));
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return muelle;
    }
}
