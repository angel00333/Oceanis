
package com.aqua.oceanis.models;

import java.sql.*;
public class UsuarioDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    
    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }
    
    public Usuario ValidarUsuario(String nombre, String contraseña){
        Usuario usuario = null;
        try{
            sql = conectar().prepareStatement("SELECT * FROM usuarios WHERE nombre=? AND contraseña=?");
            sql.setString(1, nombre);
            sql.setString(2, contraseña);
            rs = sql.executeQuery();
            while(rs.next()){
                usuario = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
            sql.close();
            rs.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        } 
        return usuario;
    }
}