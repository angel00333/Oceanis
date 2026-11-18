
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.ArrayList;

public class PuertoDAO {

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
                + "VALUES (?, ?, ?, 'Puertos', ?, ?)";

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

    // INSERTAR PUERTO

    public boolean InsertarPuerto(
            Puerto puerto, int idusuario, String usuario) {

        String consulta = "INSERT INTO puertos "
                + "(nombre, direccion) VALUES (?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, puerto.getNombre());
                    ps.setString(2, puerto.getDireccion());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo insertar el puerto."
                        );
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID del puerto."
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
                        "Se registró el puerto: " + puerto.getNombre()
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

    // MODIFICAR PUERTO

    public boolean ModificarPuerto(
            Puerto puerto, int idusuario, String usuario) {

        String consulta = "UPDATE puertos "
                + "SET nombre = ?, direccion = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setString(1, puerto.getNombre());
                    ps.setString(2, puerto.getDireccion());
                    ps.setInt(3, puerto.getId());

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
                        puerto.getId(),
                        "Se modificó el puerto: " + puerto.getNombre()
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

    // ELIMINAR PUERTO (BORRADO LÓGICO)

    public int EliminarPuerto(
            Puerto puerto, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                String nombrePuerto;

                // COMPROBAR QUE EL PUERTO ESTÉ ACTIVO

                String consultaPuerto =
                        "SELECT nombre FROM puertos "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaPuerto)) {

                    ps.setInt(1, puerto.getId());

                    try (ResultSet resultado = ps.executeQuery()) {

                        if (!resultado.next()) {
                            conexion.rollback();
                            return -2;
                        }

                        nombrePuerto = resultado.getString("nombre");
                    }
                }

                // VALIDAR MUELLES RELACIONADOS

                String consultaRelaciones =
                        "SELECT COUNT(*) FROM muelles WHERE idpuerto = ?";

                int relacionados;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaRelaciones)) {

                    ps.setInt(1, puerto.getId());

                    try (ResultSet resultado = ps.executeQuery()) {

                        resultado.next();
                        relacionados = resultado.getInt(1);
                    }
                }

                if (relacionados > 0) {
                    conexion.rollback();
                    return 0;
                }

                // DESACTIVAR PUERTO

                String consulta = "UPDATE puertos "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, puerto.getId());
                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return -2;
                }

                // GUARDAR EN HISTORIAL

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "ELIMINACION",
                        puerto.getId(),
                        "Se desactivó el puerto: " + nombrePuerto
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

    // MOSTRAR PUERTOS ACTIVOS

    public ArrayList<Puerto> MostrarPuertos() {

        ArrayList<Puerto> lista = new ArrayList<>();

        String consulta = "SELECT * FROM puertos "
                + "WHERE activo = 1 ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet resultado = ps.executeQuery()) {

            while (resultado.next()) {

                Puerto puerto = new Puerto(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("direccion")
                );

                lista.add(puerto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR PUERTOS ACTIVOS

    public ArrayList<Puerto> BuscarPuertos(String texto) {

        ArrayList<Puerto> lista = new ArrayList<>();

        String consulta = "SELECT * FROM puertos "
                + "WHERE activo = 1 AND nombre LIKE ? "
                + "ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    Puerto puerto = new Puerto(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("direccion")
                    );

                    lista.add(puerto);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER PUERTO POR ID

    public Puerto ObtenerPuertoPorId(int id) {

        Puerto puerto = null;

        String consulta = "SELECT * FROM puertos "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet resultado = ps.executeQuery()) {

                if (resultado.next()) {

                    puerto = new Puerto(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("direccion")
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return puerto;
    }
}
