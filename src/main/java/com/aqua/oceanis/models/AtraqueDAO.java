
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class AtraqueDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Barco barco;
    Muelle muelle;
    Atraque atraque;

    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }

    public int InsertarAtraque(Atraque atraque){
        int n = 0;
        try{
            sql = conectar().prepareStatement("INSERT INTO atraques "
                    + "(idbarco, idmuelle, fecha_llegada, fecha_salida, estado) "
                    + "VALUES(?,?,?,?,?)");
            sql.setInt(1, atraque.getBarco().getId());
            sql.setInt(2, atraque.getMuelle().getId());
            sql.setTimestamp(3, Timestamp.valueOf(atraque.getFecha_llegada()));

            if(atraque.getFecha_salida() != null){
                sql.setTimestamp(4, Timestamp.valueOf(atraque.getFecha_salida()));
            }else{
                sql.setNull(4, Types.TIMESTAMP);
            }

            sql.setString(5, atraque.getEstado());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int ModificarAtraque(Atraque atraque){
        int n = 0;
        try{
            sql = conectar().prepareStatement("UPDATE atraques "
                    + "SET idbarco=?, idmuelle=?, fecha_llegada=?, "
                    + "fecha_salida=?, estado=? WHERE id=?");
            sql.setInt(1, atraque.getBarco().getId());
            sql.setInt(2, atraque.getMuelle().getId());
            sql.setTimestamp(3, Timestamp.valueOf(atraque.getFecha_llegada()));

            if(atraque.getFecha_salida() != null){
                sql.setTimestamp(4, Timestamp.valueOf(atraque.getFecha_salida()));
            }else{
                sql.setNull(4, Types.TIMESTAMP);
            }

            sql.setString(5, atraque.getEstado());
            sql.setInt(6, atraque.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public int EliminarAtraque(Atraque atraque){
        int n = 0;
        try{
            sql = conectar().prepareStatement("DELETE FROM atraques WHERE id=?");
            sql.setInt(1, atraque.getId());
            n = sql.executeUpdate();
            sql.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return n;
    }

    public ArrayList<Atraque> MostrarAtraques(){
        ArrayList<Atraque> atraques = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM atraques");
            rs = sql.executeQuery();
            while(rs.next()){
                barco = new BarcoDAO().ObtenerBarcoPorId(rs.getInt("idbarco"));
                muelle = new MuelleDAO().ObtenerMuellePorId(rs.getInt("idmuelle"));

                Timestamp salida = rs.getTimestamp("fecha_salida");
                LocalDateTime fechaSalida = null;
                if(salida != null){
                    fechaSalida = salida.toLocalDateTime();
                }

                atraque = new Atraque(rs.getInt("id"), barco, muelle,
                        rs.getTimestamp("fecha_llegada").toLocalDateTime(),
                        fechaSalida, rs.getString("estado"));
                atraques.add(atraque);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return atraques;
    }

    public ArrayList<Atraque> BuscarAtraques(String str){
        ArrayList<Atraque> ar = new ArrayList<Atraque>();
        try{
            sql = conectar().prepareStatement("SELECT a.* FROM atraques a "
                    + "INNER JOIN barcos b ON a.idbarco=b.id "
                    + "WHERE b.nombre LIKE ?");
            sql.setString(1, "%" + str + "%");
            rs = sql.executeQuery();

            while(rs.next()){
                barco = new BarcoDAO().ObtenerBarcoPorId(rs.getInt("idbarco"));
                muelle = new MuelleDAO().ObtenerMuellePorId(rs.getInt("idmuelle"));

                Timestamp salida = rs.getTimestamp("fecha_salida");
                LocalDateTime fechaSalida = null;
                if(salida != null){
                    fechaSalida = salida.toLocalDateTime();
                }

                atraque = new Atraque(rs.getInt("id"), barco, muelle,
                        rs.getTimestamp("fecha_llegada").toLocalDateTime(),
                        fechaSalida, rs.getString("estado"));
                ar.add(atraque);
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ar;
    }

    public Atraque ObtenerAtraquePorId(int id){
        atraque = null;
        try{
            sql = conectar().prepareStatement("SELECT * FROM atraques WHERE id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();

            while(rs.next()){
                barco = new BarcoDAO().ObtenerBarcoPorId(rs.getInt("idbarco"));
                muelle = new MuelleDAO().ObtenerMuellePorId(rs.getInt("idmuelle"));

                Timestamp salida = rs.getTimestamp("fecha_salida");
                LocalDateTime fechaSalida = null;
                if(salida != null){
                    fechaSalida = salida.toLocalDateTime();
                }

                atraque = new Atraque(rs.getInt("id"), barco, muelle,
                        rs.getTimestamp("fecha_llegada").toLocalDateTime(),
                        fechaSalida, rs.getString("estado"));
            }
            sql.close();
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return atraque;
    }
}
