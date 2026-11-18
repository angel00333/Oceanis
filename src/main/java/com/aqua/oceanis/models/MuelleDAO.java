
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.ArrayList;

public class MuelleDAO {

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
                + "VALUES (?, ?, ?, 'Muelles', ?, ?)";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idusuario);
            ps.setString(2, usuario);
            ps.setString(3, accion);
            ps.setInt(4, idregistro);
            ps.setString(5, descripcion);

            if (ps.executeUpdate() != 1)
                throw new SQLException(
                        "No se pudo registrar la actividad."
                );
        }
    }

    // VALIDAR PUERTO ACTIVO Y BLOQUEAR SU REGISTRO

    private boolean PuertoActivo(
            Connection conexion, int idpuerto) throws SQLException {

        String consulta = "SELECT id FROM puertos "
                + "WHERE id = ? AND activo = 1 FOR UPDATE";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idpuerto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // INSERTAR MUELLE

    public boolean InsertarMuelle(
            Muelle muelle, int idusuario, String usuario) {

        String consulta = "INSERT INTO muelles "
                + "(idpuerto, nombre, estado) VALUES (?, ?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                if (muelle.getPuerto() == null
                        || !PuertoActivo(
                                conexion, muelle.getPuerto().getId())) {

                    conexion.rollback();
                    return false;
                }

                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, muelle.getPuerto().getId());
                    ps.setString(2, muelle.getNombre());
                    ps.setString(3, muelle.getEstado());

                    if (ps.executeUpdate() != 1)
                        throw new SQLException(
                                "No se pudo insertar el muelle."
                        );

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next())
                            throw new SQLException(
                                    "No se obtuvo el ID del muelle."
                            );

                        idGenerado = claves.getInt(1);
                    }
                }

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "CREACION",
                        idGenerado,
                        "Se registró el muelle: " + muelle.getNombre()
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

    // MODIFICAR MUELLE

    public boolean ModificarMuelle(
            Muelle muelle, int idusuario, String usuario) {

        String consulta = "UPDATE muelles "
                + "SET idpuerto = ?, nombre = ?, estado = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                if (muelle.getPuerto() == null
                        || !PuertoActivo(
                                conexion, muelle.getPuerto().getId())) {

                    conexion.rollback();
                    return false;
                }

                // Si tiene atraques, no permitir cambiar de puerto.

                String consultaMuelle = "SELECT idpuerto FROM muelles "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                int puertoAnterior;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaMuelle)) {

                    ps.setInt(1, muelle.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }

                        puertoAnterior = rs.getInt("idpuerto");
                    }
                }

                if (puertoAnterior != muelle.getPuerto().getId()) {

                    String consultaRelaciones =
                            "SELECT COUNT(*) FROM atraques "
                            + "WHERE idmuelle = ?";

                    try (PreparedStatement ps =
                            conexion.prepareStatement(
                                    consultaRelaciones)) {

                        ps.setInt(1, muelle.getId());

                        try (ResultSet rs = ps.executeQuery()) {

                            rs.next();

                            if (rs.getInt(1) > 0) {
                                conexion.rollback();
                                return false;
                            }
                        }
                    }
                }

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, muelle.getPuerto().getId());
                    ps.setString(2, muelle.getNombre());
                    ps.setString(3, muelle.getEstado());
                    ps.setInt(4, muelle.getId());

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
                        muelle.getId(),
                        "Se modificó el muelle: " + muelle.getNombre()
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

    // ELIMINAR MUELLE (BORRADO LÓGICO)

    //  1 = Eliminado
    //  0 = Tiene atraques relacionados
    // -1 = Error
    // -2 = No existe o ya está desactivado

    public int EliminarMuelle(
            Muelle muelle, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                String consultaMuelle =
                        "SELECT nombre FROM muelles "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                String nombreMuelle;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaMuelle)) {

                    ps.setInt(1, muelle.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conexion.rollback();
                            return -2;
                        }

                        nombreMuelle = rs.getString("nombre");
                    }
                }

                String consultaRelaciones =
                        "SELECT COUNT(*) FROM atraques "
                        + "WHERE idmuelle = ?";

                int relacionados;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaRelaciones)) {

                    ps.setInt(1, muelle.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        rs.next();
                        relacionados = rs.getInt(1);
                    }
                }

                if (relacionados > 0) {
                    conexion.rollback();
                    return 0;
                }

                String consulta = "UPDATE muelles "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, muelle.getId());
                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return -2;
                }

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "ELIMINACION",
                        muelle.getId(),
                        "Se desactivó el muelle: " + nombreMuelle
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

    // MOSTRAR MUELLES ACTIVOS

    public ArrayList<Muelle> MostrarMuelles() {

        ArrayList<Muelle> lista = new ArrayList<>();

        String consulta = "SELECT m.*, p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM muelles m "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE m.activo = 1 AND p.activo = 1 "
                + "ORDER BY m.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Puerto puerto = new Puerto(
                        rs.getInt("idpuerto"),
                        rs.getString("nombrepuerto"),
                        rs.getString("direccionpuerto")
                );

                Muelle muelle = new Muelle(
                        rs.getInt("id"),
                        puerto,
                        rs.getString("nombre"),
                        rs.getString("estado")
                );

                lista.add(muelle);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR MUELLES ACTIVOS

    public ArrayList<Muelle> BuscarMuelles(String texto) {

        ArrayList<Muelle> lista = new ArrayList<>();

        String consulta = "SELECT m.*, p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM muelles m "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE m.activo = 1 AND p.activo = 1 "
                + "AND (m.nombre LIKE ? OR p.nombre LIKE ?) "
                + "ORDER BY m.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Puerto puerto = new Puerto(
                            rs.getInt("idpuerto"),
                            rs.getString("nombrepuerto"),
                            rs.getString("direccionpuerto")
                    );

                    Muelle muelle = new Muelle(
                            rs.getInt("id"),
                            puerto,
                            rs.getString("nombre"),
                            rs.getString("estado")
                    );

                    lista.add(muelle);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER MUELLE POR ID

    public Muelle ObtenerMuellePorId(int id) {

        Muelle muelle = null;

        String consulta = "SELECT m.*, p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM muelles m "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE m.id = ? AND m.activo = 1 AND p.activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Puerto puerto = new Puerto(
                            rs.getInt("idpuerto"),
                            rs.getString("nombrepuerto"),
                            rs.getString("direccionpuerto")
                    );

                    muelle = new Muelle(
                            rs.getInt("id"),
                            puerto,
                            rs.getString("nombre"),
                            rs.getString("estado")
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return muelle;
    }
}
