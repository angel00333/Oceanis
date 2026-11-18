
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.*;

public class HistorialDAO {

    PreparedStatement sql;
    ResultSet rs;
    Conexion con = new Conexion();

    Connection conectar() throws SQLException, ClassNotFoundException {
        Class.forName(con.getDriver());
        return DriverManager.getConnection(
                con.getUrl(),
                con.getUser(),
                con.getPassword()
        );
    }

    // REGISTRAR ACTIVIDAD

    public boolean RegistrarActividad(
            int idusuario,
            String usuario,
            String accion,
            String modulo,
            int idregistro,
            String descripcion) {

        String consulta = "INSERT INTO historial_actividades "
                + "(idusuario, usuario, accion, modulo, "
                + "idregistro, descripcion) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            if(idusuario > 0)
                ps.setInt(1, idusuario);
            else
                ps.setNull(1, Types.INTEGER);

            ps.setString(2, usuario);
            ps.setString(3, accion);
            ps.setString(4, modulo);
            ps.setInt(5, idregistro);
            ps.setString(6, descripcion);

            return ps.executeUpdate() > 0;

        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // MOSTRAR HISTORIAL COMPLETO

    public List<Map<String, Object>> MostrarHistorial() {

        List<Map<String, Object>> historial = new ArrayList<>();

        String consulta = "SELECT * FROM historial_actividades "
                + "ORDER BY fecha DESC, id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet resultado = ps.executeQuery()) {

            while(resultado.next()) {

                Map<String, Object> actividad = new HashMap<>();

                actividad.put("id", resultado.getInt("id"));
                actividad.put("idusuario", resultado.getInt("idusuario"));
                actividad.put("usuario", resultado.getString("usuario"));
                actividad.put("accion", resultado.getString("accion"));
                actividad.put("modulo", resultado.getString("modulo"));
                actividad.put("idregistro", resultado.getInt("idregistro"));
                actividad.put("descripcion", resultado.getString("descripcion"));
                actividad.put("fecha", resultado.getTimestamp("fecha"));

                historial.add(actividad);
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Error al consultar el historial de actividades", ex
            );
        }

        return historial;
    }

    // BUSCAR ACTIVIDADES POR FECHA

    public List<Map<String, Object>> BuscarHistorialPorFecha(
            String fecha) {

        List<Map<String, Object>> historial = new ArrayList<>();

        String consulta = "SELECT * FROM historial_actividades "
                + "WHERE fecha >= ? "
                + "AND fecha < DATE_ADD(?, INTERVAL 1 DAY) "
                + "ORDER BY fecha DESC, id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            java.sql.Date dia = java.sql.Date.valueOf(fecha);

            ps.setDate(1, dia);
            ps.setDate(2, dia);

            try(ResultSet resultado = ps.executeQuery()) {

                while(resultado.next()) {

                    Map<String, Object> actividad = new HashMap<>();

                    actividad.put("id", resultado.getInt("id"));
                    actividad.put("idusuario", resultado.getInt("idusuario"));
                    actividad.put("usuario", resultado.getString("usuario"));
                    actividad.put("accion", resultado.getString("accion"));
                    actividad.put("modulo", resultado.getString("modulo"));
                    actividad.put("idregistro", resultado.getInt("idregistro"));
                    actividad.put("descripcion", resultado.getString("descripcion"));
                    actividad.put("fecha", resultado.getTimestamp("fecha"));

                    historial.add(actividad);
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Error al consultar el historial por fecha", ex
            );
        }

        return historial;
    }
}
