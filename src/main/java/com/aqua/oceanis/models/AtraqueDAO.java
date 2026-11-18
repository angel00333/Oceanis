
package com.aqua.oceanis.models;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AtraqueDAO {

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
                + "VALUES (?, ?, ?, 'Atraques', ?, ?)";

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

    // VALIDAR FECHAS

    private boolean FechasValidas(Atraque atraque) {

        LocalDateTime llegada = atraque.getFecha_llegada();
        LocalDateTime salida = atraque.getFecha_salida();

        if (llegada == null)
            return false;

        return salida == null || !salida.isBefore(llegada);
    }

    // VALIDAR BARCO ACTIVO

    private boolean BarcoActivo(
            Connection conexion, int idbarco) throws SQLException {

        String consulta = "SELECT id FROM barcos "
                + "WHERE id = ? AND activo = 1 FOR UPDATE";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idbarco);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // VALIDAR MUELLE Y PUERTO ACTIVOS

    private boolean MuelleActivo(
            Connection conexion, int idmuelle) throws SQLException {

        String consulta = "SELECT m.id FROM muelles m "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE m.id = ? "
                + "AND m.activo = 1 "
                + "AND p.activo = 1 "
                + "FOR UPDATE";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idmuelle);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // INSERTAR ATRAQUE

    public boolean InsertarAtraque(
            Atraque atraque, int idusuario, String usuario) {

        String consulta = "INSERT INTO atraques "
                + "(idbarco, idmuelle, fecha_llegada, "
                + "fecha_salida, estado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                if (atraque.getBarco() == null
                        || atraque.getMuelle() == null
                        || !FechasValidas(atraque)) {

                    conexion.rollback();
                    return false;
                }

                int idbarco = atraque.getBarco().getId();
                int idmuelle = atraque.getMuelle().getId();

                if (!BarcoActivo(conexion, idbarco)
                        || !MuelleActivo(conexion, idmuelle)) {

                    conexion.rollback();
                    return false;
                }

                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, idbarco);
                    ps.setInt(2, idmuelle);

                    ps.setTimestamp(
                            3,
                            Timestamp.valueOf(
                                    atraque.getFecha_llegada()
                            )
                    );

                    if (atraque.getFecha_salida() != null) {

                        ps.setTimestamp(
                                4,
                                Timestamp.valueOf(
                                        atraque.getFecha_salida()
                                )
                        );

                    } else {
                        ps.setNull(4, Types.TIMESTAMP);
                    }

                    ps.setString(5, atraque.getEstado());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo insertar el atraque."
                        );
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID del atraque."
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
                        "Se registró el atraque #" + idGenerado
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

    // MODIFICAR ATRAQUE

    public boolean ModificarAtraque(
            Atraque atraque, int idusuario, String usuario) {

        String consulta = "UPDATE atraques "
                + "SET idbarco = ?, idmuelle = ?, "
                + "fecha_llegada = ?, fecha_salida = ?, "
                + "estado = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                if (atraque.getBarco() == null
                        || atraque.getMuelle() == null
                        || !FechasValidas(atraque)) {

                    conexion.rollback();
                    return false;
                }

                int idbarco = atraque.getBarco().getId();
                int idmuelle = atraque.getMuelle().getId();

                if (!BarcoActivo(conexion, idbarco)
                        || !MuelleActivo(conexion, idmuelle)) {

                    conexion.rollback();
                    return false;
                }

                // EVITAR CAMBIAR BARCO O MUELLE SI HAY CARGAS

                String consultaAnterior =
                        "SELECT idbarco, idmuelle FROM atraques "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                int barcoAnterior;
                int muelleAnterior;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaAnterior)) {

                    ps.setInt(1, atraque.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conexion.rollback();
                            return false;
                        }

                        barcoAnterior = rs.getInt("idbarco");
                        muelleAnterior = rs.getInt("idmuelle");
                    }
                }

                if (barcoAnterior != idbarco
                        || muelleAnterior != idmuelle) {

                    String consultaRelaciones =
                            "SELECT COUNT(*) FROM cargas "
                            + "WHERE idatraque = ?";

                    try (PreparedStatement ps =
                            conexion.prepareStatement(
                                    consultaRelaciones)) {

                        ps.setInt(1, atraque.getId());

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

                    ps.setInt(1, idbarco);
                    ps.setInt(2, idmuelle);

                    ps.setTimestamp(
                            3,
                            Timestamp.valueOf(
                                    atraque.getFecha_llegada()
                            )
                    );

                    if (atraque.getFecha_salida() != null) {

                        ps.setTimestamp(
                                4,
                                Timestamp.valueOf(
                                        atraque.getFecha_salida()
                                )
                        );

                    } else {
                        ps.setNull(4, Types.TIMESTAMP);
                    }

                    ps.setString(5, atraque.getEstado());
                    ps.setInt(6, atraque.getId());

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
                        atraque.getId(),
                        "Se modificó el atraque #"
                        + atraque.getId()
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

    // ELIMINAR ATRAQUE (BORRADO LÓGICO)

    //  1 = Eliminado correctamente
    //  0 = Tiene cargas relacionadas
    // -1 = Error
    // -2 = No existe o ya está desactivado

    public int EliminarAtraque(
            Atraque atraque, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                // COMPROBAR QUE EL ATRAQUE ESTÉ ACTIVO

                String consultaAtraque =
                        "SELECT id FROM atraques "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaAtraque)) {

                    ps.setInt(1, atraque.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conexion.rollback();
                            return -2;
                        }
                    }
                }

                // COMPROBAR CARGAS RELACIONADAS

                String consultaRelaciones =
                        "SELECT COUNT(*) FROM cargas "
                        + "WHERE idatraque = ?";

                int relacionados;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaRelaciones)) {

                    ps.setInt(1, atraque.getId());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        relacionados = rs.getInt(1);
                    }
                }

                if (relacionados > 0) {
                    conexion.rollback();
                    return 0;
                }

                // DESACTIVAR EL ATRAQUE

                String consulta = "UPDATE atraques "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, atraque.getId());
                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return -2;
                }

                // REGISTRAR LA ELIMINACIÓN

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "ELIMINACION",
                        atraque.getId(),
                        "Se desactivó el atraque #"
                        + atraque.getId()
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

    // MOSTRAR ATRAQUES ACTIVOS

    public ArrayList<Atraque> MostrarAtraques() {

        ArrayList<Atraque> lista = new ArrayList<>();

        String consulta =
                "SELECT a.*, "
                + "b.nombre AS nombrebarco, "
                + "b.tipo AS tipobarco, "
                + "b.pais AS paisbarco, "
                + "m.nombre AS nombremuelle, "
                + "m.estado AS estadomuelle, "
                + "p.id AS idpuerto, "
                + "p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM atraques a "
                + "INNER JOIN barcos b ON a.idbarco = b.id "
                + "INNER JOIN muelles m ON a.idmuelle = m.id "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE a.activo = 1 "
                + "AND b.activo = 1 "
                + "AND m.activo = 1 "
                + "AND p.activo = 1 "
                + "ORDER BY a.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(ConstruirAtraque(rs));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR ATRAQUES ACTIVOS

    public ArrayList<Atraque> BuscarAtraques(String texto) {

        ArrayList<Atraque> lista = new ArrayList<>();

        String consulta =
                "SELECT a.*, "
                + "b.nombre AS nombrebarco, "
                + "b.tipo AS tipobarco, "
                + "b.pais AS paisbarco, "
                + "m.nombre AS nombremuelle, "
                + "m.estado AS estadomuelle, "
                + "p.id AS idpuerto, "
                + "p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM atraques a "
                + "INNER JOIN barcos b ON a.idbarco = b.id "
                + "INNER JOIN muelles m ON a.idmuelle = m.id "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE a.activo = 1 "
                + "AND b.activo = 1 "
                + "AND m.activo = 1 "
                + "AND p.activo = 1 "
                + "AND (b.nombre LIKE ? "
                + "OR m.nombre LIKE ? "
                + "OR p.nombre LIKE ? "
                + "OR a.estado LIKE ?) "
                + "ORDER BY a.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            String buscar = "%" + texto + "%";

            ps.setString(1, buscar);
            ps.setString(2, buscar);
            ps.setString(3, buscar);
            ps.setString(4, buscar);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(ConstruirAtraque(rs));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER ATRAQUE POR ID

    public Atraque ObtenerAtraquePorId(int id) {

        Atraque atraque = null;

        String consulta =
                "SELECT a.*, "
                + "b.nombre AS nombrebarco, "
                + "b.tipo AS tipobarco, "
                + "b.pais AS paisbarco, "
                + "m.nombre AS nombremuelle, "
                + "m.estado AS estadomuelle, "
                + "p.id AS idpuerto, "
                + "p.nombre AS nombrepuerto, "
                + "p.direccion AS direccionpuerto "
                + "FROM atraques a "
                + "INNER JOIN barcos b ON a.idbarco = b.id "
                + "INNER JOIN muelles m ON a.idmuelle = m.id "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE a.id = ? "
                + "AND a.activo = 1 "
                + "AND b.activo = 1 "
                + "AND m.activo = 1 "
                + "AND p.activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    atraque = ConstruirAtraque(rs);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return atraque;
    }

    // CONSTRUIR OBJETO ATRAQUE

    private Atraque ConstruirAtraque(ResultSet rs)
            throws SQLException {

        Puerto puerto = new Puerto(
                rs.getInt("idpuerto"),
                rs.getString("nombrepuerto"),
                rs.getString("direccionpuerto")
        );

        Muelle muelle = new Muelle(
                rs.getInt("idmuelle"),
                puerto,
                rs.getString("nombremuelle"),
                rs.getString("estadomuelle")
        );

        Barco barco = new Barco(
                rs.getInt("idbarco"),
                rs.getString("nombrebarco"),
                rs.getString("tipobarco"),
                rs.getString("paisbarco")
        );

        Timestamp llegada = rs.getTimestamp("fecha_llegada");
        Timestamp salida = rs.getTimestamp("fecha_salida");

        return new Atraque(
                rs.getInt("id"),
                barco,
                muelle,
                llegada != null ? llegada.toLocalDateTime() : null,
                salida != null ? salida.toLocalDateTime() : null,
                rs.getString("estado")
        );
    }
}
