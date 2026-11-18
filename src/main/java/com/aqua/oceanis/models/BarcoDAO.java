
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.ArrayList;

public class BarcoDAO {

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

    // REGISTRAR ACTIVIDAD EN EL HISTORIAL

    private void RegistrarActividad(
            Connection conexion,
            int idusuario,
            String usuario,
            String accion,
            int idregistro,
            String descripcion) throws SQLException {

        String consulta = "INSERT INTO historial_actividades "
                + "(idusuario, usuario, accion, modulo, "
                + "idregistro, descripcion) "
                + "VALUES (?, ?, ?, 'Barcos', ?, ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idusuario);
            ps.setString(2, usuario);
            ps.setString(3, accion);
            ps.setInt(4, idregistro);
            ps.setString(5, descripcion);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "No se pudo registrar la actividad."
                );
            }
        }
    }

    // INSERTAR BARCO

    public boolean InsertarBarco(
            Barco barco, int idusuario, String usuario) {

        String consulta = "INSERT INTO barcos "
                + "(nombre, tipo, pais) VALUES (?, ?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, barco.getNombre());
                    ps.setString(2, barco.getTipo());
                    ps.setString(3, barco.getPais());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo insertar el barco."
                        );
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID del barco."
                            );
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "CREACION",
                        idGenerado,
                        "Se registró el barco: " + barco.getNombre()
                );

                conexion.commit();
                return true;

            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // MODIFICAR BARCO

    public boolean ModificarBarco(
            Barco barco, int idusuario, String usuario) {

        String consulta = "UPDATE barcos "
                + "SET nombre = ?, tipo = ?, pais = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setString(1, barco.getNombre());
                    ps.setString(2, barco.getTipo());
                    ps.setString(3, barco.getPais());
                    ps.setInt(4, barco.getId());

                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return false;
                }

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "MODIFICACION",
                        barco.getId(),
                        "Se modificó el barco: " + barco.getNombre()
                );

                conexion.commit();
                return true;

            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ELIMINAR BARCO (BORRADO LÓGICO)

    public int EliminarBarco(
            Barco barco, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                String nombreBarco;

                // COMPROBAR QUE EL BARCO ESTÉ ACTIVO

                String consultaBarco = "SELECT nombre FROM barcos "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaBarco)) {

                    ps.setInt(1, barco.getId());

                    try (ResultSet resultado = ps.executeQuery()) {

                        if (!resultado.next()) {
                            conexion.rollback();
                            return -2;
                        }

                        nombreBarco = resultado.getString("nombre");
                    }
                }

                // COMPROBAR SI TIENE ATRAQUES RELACIONADOS

                String consultaRelaciones =
                        "SELECT COUNT(*) FROM atraques WHERE idbarco = ?";

                int relacionados;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaRelaciones)) {

                    ps.setInt(1, barco.getId());

                    try (ResultSet resultado = ps.executeQuery()) {

                        resultado.next();
                        relacionados = resultado.getInt(1);
                    }
                }

                if (relacionados > 0) {
                    conexion.rollback();
                    return 0;
                }

                // DESACTIVAR EL BARCO

                String consulta = "UPDATE barcos "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, barco.getId());
                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return -2;
                }

                // GUARDAR EL EVENTO EN EL HISTORIAL

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "ELIMINACION",
                        barco.getId(),
                        "Se desactivó el barco: " + nombreBarco
                );

                conexion.commit();
                return 1;

            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    // MOSTRAR BARCOS ACTIVOS

    public ArrayList<Barco> MostrarBarcos() {

        ArrayList<Barco> lista = new ArrayList<>();

        String consulta = "SELECT * FROM barcos "
                + "WHERE activo = 1 ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet resultado = ps.executeQuery()) {

            while (resultado.next()) {

                Barco barco = new Barco(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("tipo"),
                        resultado.getString("pais")
                );

                lista.add(barco);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR BARCOS ACTIVOS

    public ArrayList<Barco> BuscarBarcos(String texto) {

        ArrayList<Barco> lista = new ArrayList<>();

        String consulta = "SELECT * FROM barcos "
                + "WHERE activo = 1 AND nombre LIKE ? "
                + "ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    Barco barco = new Barco(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("tipo"),
                            resultado.getString("pais")
                    );

                    lista.add(barco);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER BARCO POR ID

    public Barco ObtenerBarcoPorId(int id) {

        Barco barco = null;

        String consulta = "SELECT * FROM barcos "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet resultado = ps.executeQuery()) {

                if (resultado.next()) {

                    barco = new Barco(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("tipo"),
                            resultado.getString("pais")
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return barco;
    }
}
