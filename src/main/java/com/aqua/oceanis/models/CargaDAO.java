
package com.aqua.oceanis.models;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class CargaDAO {

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
                + "VALUES (?, ?, ?, 'Cargas', ?, ?)";

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

    // VALIDAR ATRAQUE ACTIVO

    private boolean AtraqueActivo(
            Connection conexion, int idatraque) throws SQLException {

        String consulta = "SELECT a.id FROM atraques a "
                + "INNER JOIN barcos b ON a.idbarco = b.id "
                + "INNER JOIN muelles m ON a.idmuelle = m.id "
                + "INNER JOIN puertos p ON m.idpuerto = p.id "
                + "WHERE a.id = ? "
                + "AND a.activo = 1 "
                + "AND b.activo = 1 "
                + "AND m.activo = 1 "
                + "AND p.activo = 1 "
                + "FOR UPDATE";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idatraque);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // VALIDAR CLIENTE ACTIVO

    private boolean ClienteActivo(
            Connection conexion, int idcliente) throws SQLException {

        String consulta = "SELECT id FROM clientes "
                + "WHERE id = ? AND activo = 1 FOR UPDATE";

        try (PreparedStatement ps =
                conexion.prepareStatement(consulta)) {

            ps.setInt(1, idcliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // INSERTAR CARGA

    public boolean InsertarCarga(
            Carga carga, int idusuario, String usuario) {

        String consulta = "INSERT INTO cargas "
                + "(idatraque, idcliente, descripcion, peso, tipo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                if (carga.getAtraque() == null
                        || carga.getCliente() == null
                        || carga.getPeso() == null
                        || carga.getPeso().compareTo(BigDecimal.ZERO) <= 0) {

                    conexion.rollback();
                    return false;
                }

                int idatraque = carga.getAtraque().getId();
                int idcliente = carga.getCliente().getId();

                if (!AtraqueActivo(conexion, idatraque)
                        || !ClienteActivo(conexion, idcliente)) {

                    conexion.rollback();
                    return false;
                }

                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, idatraque);
                    ps.setInt(2, idcliente);
                    ps.setString(3, carga.getDescripcion());
                    ps.setBigDecimal(4, carga.getPeso());
                    ps.setString(5, carga.getTipo());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo insertar la carga."
                        );
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID de la carga."
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
                        "Se registró la carga: "
                        + carga.getDescripcion()
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

    // MODIFICAR CARGA

    public boolean ModificarCarga(
            Carga carga, int idusuario, String usuario) {

        String consulta = "UPDATE cargas "
                + "SET idatraque = ?, idcliente = ?, "
                + "descripcion = ?, peso = ?, tipo = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                if (carga.getAtraque() == null
                        || carga.getCliente() == null
                        || carga.getPeso() == null
                        || carga.getPeso().compareTo(BigDecimal.ZERO) <= 0) {

                    conexion.rollback();
                    return false;
                }

                int idatraque = carga.getAtraque().getId();
                int idcliente = carga.getCliente().getId();

                if (!AtraqueActivo(conexion, idatraque)
                        || !ClienteActivo(conexion, idcliente)) {

                    conexion.rollback();
                    return false;
                }

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, idatraque);
                    ps.setInt(2, idcliente);
                    ps.setString(3, carga.getDescripcion());
                    ps.setBigDecimal(4, carga.getPeso());
                    ps.setString(5, carga.getTipo());
                    ps.setInt(6, carga.getId());

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
                        carga.getId(),
                        "Se modificó la carga: "
                        + carga.getDescripcion()
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

    // ELIMINAR CARGA (BORRADO LÓGICO)

    //  1 = Eliminada correctamente
    // -1 = Error
    // -2 = No existe o ya está desactivada

    public int EliminarCarga(
            Carga carga, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                String descripcionCarga;

                String consultaCarga =
                        "SELECT descripcion FROM cargas "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaCarga)) {

                    ps.setInt(1, carga.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conexion.rollback();
                            return -2;
                        }

                        descripcionCarga =
                                rs.getString("descripcion");
                    }
                }

                String consulta = "UPDATE cargas "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, carga.getId());
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
                        carga.getId(),
                        "Se desactivó la carga: "
                        + descripcionCarga
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

    // MOSTRAR CARGAS ACTIVAS

    public ArrayList<Carga> MostrarCargas() {

        ArrayList<Carga> lista = new ArrayList<>();

        String consulta = "SELECT c.* FROM cargas c "
                + "INNER JOIN atraques a ON c.idatraque = a.id "
                + "INNER JOIN clientes cl ON c.idcliente = cl.id "
                + "WHERE c.activo = 1 "
                + "AND a.activo = 1 "
                + "AND cl.activo = 1 "
                + "ORDER BY c.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            AtraqueDAO atraqueDAO = new AtraqueDAO();
            ClienteDAO clienteDAO = new ClienteDAO();

            while (rs.next()) {

                Atraque atraque =
                        atraqueDAO.ObtenerAtraquePorId(
                                rs.getInt("idatraque")
                        );

                Cliente cliente =
                        clienteDAO.ObtenerClientePorId(
                                rs.getInt("idcliente")
                        );

                Carga carga = new Carga(
                        rs.getInt("id"),
                        atraque,
                        cliente,
                        rs.getString("descripcion"),
                        rs.getBigDecimal("peso"),
                        rs.getString("tipo")
                );

                lista.add(carga);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR CARGAS ACTIVAS

    public ArrayList<Carga> BuscarCargas(String texto) {

        ArrayList<Carga> lista = new ArrayList<>();

        String consulta = "SELECT c.* FROM cargas c "
                + "INNER JOIN atraques a ON c.idatraque = a.id "
                + "INNER JOIN clientes cl ON c.idcliente = cl.id "
                + "WHERE c.activo = 1 "
                + "AND a.activo = 1 "
                + "AND cl.activo = 1 "
                + "AND (c.descripcion LIKE ? "
                + "OR c.tipo LIKE ? "
                + "OR cl.nombre LIKE ?) "
                + "ORDER BY c.id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            String buscar = "%" + texto + "%";

            ps.setString(1, buscar);
            ps.setString(2, buscar);
            ps.setString(3, buscar);

            AtraqueDAO atraqueDAO = new AtraqueDAO();
            ClienteDAO clienteDAO = new ClienteDAO();

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Atraque atraque =
                            atraqueDAO.ObtenerAtraquePorId(
                                    rs.getInt("idatraque")
                            );

                    Cliente cliente =
                            clienteDAO.ObtenerClientePorId(
                                    rs.getInt("idcliente")
                            );

                    Carga carga = new Carga(
                            rs.getInt("id"),
                            atraque,
                            cliente,
                            rs.getString("descripcion"),
                            rs.getBigDecimal("peso"),
                            rs.getString("tipo")
                    );

                    lista.add(carga);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER CARGA POR ID

    public Carga ObtenerCargaPorId(int id) {

        Carga carga = null;

        String consulta = "SELECT c.* FROM cargas c "
                + "INNER JOIN atraques a ON c.idatraque = a.id "
                + "INNER JOIN clientes cl ON c.idcliente = cl.id "
                + "WHERE c.id = ? "
                + "AND c.activo = 1 "
                + "AND a.activo = 1 "
                + "AND cl.activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    AtraqueDAO atraqueDAO = new AtraqueDAO();
                    ClienteDAO clienteDAO = new ClienteDAO();

                    Atraque atraque =
                            atraqueDAO.ObtenerAtraquePorId(
                                    rs.getInt("idatraque")
                            );

                    Cliente cliente =
                            clienteDAO.ObtenerClientePorId(
                                    rs.getInt("idcliente")
                            );

                    carga = new Carga(
                            rs.getInt("id"),
                            atraque,
                            cliente,
                            rs.getString("descripcion"),
                            rs.getBigDecimal("peso"),
                            rs.getString("tipo")
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return carga;
    }
}
