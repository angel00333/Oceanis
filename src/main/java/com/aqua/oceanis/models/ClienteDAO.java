
package com.aqua.oceanis.models;

import java.sql.*;
import java.util.ArrayList;

public class ClienteDAO {

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
                + "VALUES (?, ?, ?, 'Clientes', ?, ?)";

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

    // INSERTAR CLIENTE

    public boolean InsertarCliente(
            Cliente cliente, int idusuario, String usuario) {

        String consulta = "INSERT INTO clientes "
                + "(nombre, telefono) VALUES (?, ?)";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(
                                consulta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getTelefono());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo insertar el cliente."
                        );
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID del cliente."
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
                        "Se registró el cliente: " + cliente.getNombre()
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

    // MODIFICAR CLIENTE

    public boolean ModificarCliente(
            Cliente cliente, int idusuario, String usuario) {

        String consulta = "UPDATE clientes "
                + "SET nombre = ?, telefono = ? "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getTelefono());
                    ps.setInt(3, cliente.getId());

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
                        cliente.getId(),
                        "Se modificó el cliente: " + cliente.getNombre()
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

    // ELIMINAR CLIENTE (BORRADO LÓGICO)

    public int EliminarCliente(
            Cliente cliente, int idusuario, String usuario) {

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {
                String nombreCliente;

                // VERIFICAR QUE EL CLIENTE ESTÉ ACTIVO

                String consultaCliente =
                        "SELECT nombre FROM clientes "
                        + "WHERE id = ? AND activo = 1 FOR UPDATE";

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaCliente)) {

                    ps.setInt(1, cliente.getId());

                    try (ResultSet resultado = ps.executeQuery()) {

                        if (!resultado.next()) {
                            conexion.rollback();
                            return -2;
                        }

                        nombreCliente = resultado.getString("nombre");
                    }
                }

                // VERIFICAR SI TIENE CARGAS RELACIONADAS

                String consultaRelaciones =
                        "SELECT COUNT(*) FROM cargas WHERE idcliente = ?";

                int relacionados;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consultaRelaciones)) {

                    ps.setInt(1, cliente.getId());

                    try (ResultSet resultado = ps.executeQuery()) {
                        resultado.next();
                        relacionados = resultado.getInt(1);
                    }
                }

                if (relacionados > 0) {
                    conexion.rollback();
                    return 0;
                }

                // DESACTIVAR CLIENTE

                String consulta = "UPDATE clientes "
                        + "SET activo = 0, fecha_eliminacion = NOW() "
                        + "WHERE id = ? AND activo = 1";

                int resultado;

                try (PreparedStatement ps =
                        conexion.prepareStatement(consulta)) {

                    ps.setInt(1, cliente.getId());
                    resultado = ps.executeUpdate();
                }

                if (resultado != 1) {
                    conexion.rollback();
                    return -2;
                }

                // REGISTRAR ELIMINACIÓN

                RegistrarActividad(
                        conexion,
                        idusuario,
                        usuario,
                        "ELIMINACION",
                        cliente.getId(),
                        "Se desactivó el cliente: " + nombreCliente
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

    // MOSTRAR CLIENTES ACTIVOS

    public ArrayList<Cliente> MostrarClientes() {

        ArrayList<Cliente> lista = new ArrayList<>();

        String consulta = "SELECT * FROM clientes "
                + "WHERE activo = 1 ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta);
             ResultSet resultado = ps.executeQuery()) {

            while (resultado.next()) {

                Cliente cliente = new Cliente(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("telefono")
                );

                lista.add(cliente);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // BUSCAR CLIENTES ACTIVOS

    public ArrayList<Cliente> BuscarClientes(String texto) {

        ArrayList<Cliente> lista = new ArrayList<>();

        String consulta = "SELECT * FROM clientes "
                + "WHERE activo = 1 AND nombre LIKE ? "
                + "ORDER BY id DESC";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    Cliente cliente = new Cliente(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("telefono")
                    );

                    lista.add(cliente);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // OBTENER CLIENTE POR ID

    public Cliente ObtenerClientePorId(int id) {

        Cliente cliente = null;

        String consulta = "SELECT * FROM clientes "
                + "WHERE id = ? AND activo = 1";

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(consulta)) {

            ps.setInt(1, id);

            try (ResultSet resultado = ps.executeQuery()) {

                if (resultado.next()) {

                    cliente = new Cliente(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("telefono")
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return cliente;
    }
}
