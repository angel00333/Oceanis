

package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;

public class ClienteDAO {
    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();
    Cliente cliente;

    Connection conectar() throws SQLException, ClassNotFoundException{
        Class.forName(con.getDriver());
        return DriverManager.getConnection(con.getUrl(), con.getUser(), con.getPassword());
    }

    public int InsertarCliente(Cliente cliente) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("INSERT INTO clientes(nombre, telefono) VALUES(?,?)");
            sql.setString(1, cliente.getNombre());
            sql.setString(2, cliente.getTelefono());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public int ModificarCliente(Cliente cliente) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("UPDATE clientes SET nombre=?, telefono=? WHERE id=?");
            sql.setString(1, cliente.getNombre());
            sql.setString(2, cliente.getTelefono());
            sql.setInt(3, cliente.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public int EliminarCliente(Cliente cliente) {
        int n = 0;
        try {
            sql = conectar().prepareStatement("DELETE FROM clientes WHERE id=?");
            sql.setInt(1, cliente.getId());
            n = sql.executeUpdate();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    public ArrayList<Cliente> MostrarClientes() {
        ArrayList<Cliente> clientes = new ArrayList<>();
        try{
            sql = conectar().prepareStatement("SELECT * FROM clientes");
            rs = sql.executeQuery();
            while(rs.next()) {
                cliente = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3));
                clientes.add(cliente);
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return clientes;
    }

    public ArrayList<Cliente> BuscarClientes(String str) {
        ArrayList<Cliente> ar = new ArrayList<Cliente>();
        try {
            sql = conectar().prepareStatement("SELECT * FROM clientes WHERE nombre LIKE ?");
            sql.setString(1, "%" + str + "%");
            rs = sql.executeQuery();

            while (rs.next()) {
                cliente = new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono"));
                ar.add(cliente);
            }

            rs.close();
            sql.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ar;
    }

    public Cliente ObtenerClientePorId(int id) {
        try{
            sql = conectar().prepareStatement("SELECT * FROM clientes WHERE id=?");
            sql.setInt(1, id);
            rs = sql.executeQuery();
            while(rs.next()) {
                cliente = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3));
            }
            sql.close();
            rs.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return cliente;
    }
}
