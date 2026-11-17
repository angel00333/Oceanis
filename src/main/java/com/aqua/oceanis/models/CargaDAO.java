
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;

public class CargaDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Atraque atraque;
    Cliente cliente;
    Carga carga;

    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }

    public int InsertarCarga(Carga carga){
        int n = 0;
        try{
            sql = conectar().prepareStatement("INSERT INTO cargas "
                    + "(idatraque, idcliente, descripcion, peso, tipo) "
                    + "VALUES(?,?,?,?,?)");
            sql.setInt(1, carga.getAtraque().getId());
            sql.setInt(2, carga.getCliente().getId());
            sql.setString(3, carga.getDescripcion());
            sql.setBigDecimal(4, carga.getPeso());
            sql.setString(5, carga.getTipo());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int ModificarCarga(Carga carga){
        int n = 0;
        try{
            sql = conectar().prepareStatement("UPDATE cargas "
                    + "SET idatraque=?, idcliente=?, descripcion=?, "
                    + "peso=?, tipo=? WHERE id=?");
            sql.setInt(1, carga.getAtraque().getId());
            sql.setInt(2, carga.getCliente().getId());
            sql.setString(3, carga.getDescripcion());
            sql.setBigDecimal(4, carga.getPeso());
            sql.setString(5, carga.getTipo());
            sql.setInt(6, carga.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int EliminarCarga(Carga carga){
        int n = 0;
        try{
            sql = conectar().prepareStatement("DELETE FROM cargas WHERE id=?");
            sql.setInt(1, carga.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public ArrayList<Carga> MostrarCargas(){
        ArrayList<Carga> cargas = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM cargas");
            rs = sql.executeQuery();
            while(rs.next()){
                atraque = new AtraqueDAO().ObtenerAtraquePorId(rs.getInt("idatraque"));
                cliente = new ClienteDAO().ObtenerClientePorId(rs.getInt("idcliente"));

                carga = new Carga(rs.getInt("id"), atraque, cliente,
                        rs.getString("descripcion"), rs.getBigDecimal("peso"),
                        rs.getString("tipo"));
                cargas.add(carga);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return cargas;
    }

    public ArrayList<Carga> BuscarCargas(String str){
        ArrayList<Carga> ar = new ArrayList<Carga>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM cargas "
                    + "WHERE descripcion LIKE ?");
            sql.setString(1, "%" + str + "%");
            rs = sql.executeQuery();

            while(rs.next()){
                atraque = new AtraqueDAO().ObtenerAtraquePorId(rs.getInt("idatraque"));
                cliente = new ClienteDAO().ObtenerClientePorId(rs.getInt("idcliente"));

                carga = new Carga(rs.getInt("id"), atraque, cliente,
                        rs.getString("descripcion"), rs.getBigDecimal("peso"),
                        rs.getString("tipo"));
                ar.add(carga);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ar;
    }

    public Carga ObtenerCargaPorId(int id){
        carga = null;
        try{
            sql = conectar().prepareStatement("SELECT * FROM cargas WHERE id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();

            while(rs.next()){
                atraque = new AtraqueDAO().ObtenerAtraquePorId(rs.getInt("idatraque"));
                cliente = new ClienteDAO().ObtenerClientePorId(rs.getInt("idcliente"));

                carga = new Carga(rs.getInt("id"), atraque, cliente,
                        rs.getString("descripcion"), rs.getBigDecimal("peso"),
                        rs.getString("tipo"));
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return carga;
    }
}
