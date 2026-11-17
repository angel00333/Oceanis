<%-- 
    Document   : FormCliente
    Created on : 17 nov 2026, 2:38:10 p.m.
    Author     : AB_Geo
--%>
<%@page import="java.util.*, com.aqua.oceanis.models.*" contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <%-- Carga los estilos de la aplicación --%>
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>

        <title>Formulario Cliente</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <%-- Determina si se creará o modificará un cliente --%>
        <%
            Cliente cliente;
            String accion = "";

            if(request.getParameter("id") != null){

                if(request.getParameter("n") != null){

                    // Prepara un cliente nuevo
                    accion = "guardar";
                    cliente = new Cliente(0, "", "");

                }else{

                    // Consulta los datos del cliente a modificar
                    accion = "modificar";
                    ClienteDAO clienteDAO = new ClienteDAO();

                    cliente = clienteDAO.ObtenerClientePorId(
                        Integer.parseInt(request.getParameter("id"))
                    );
                }
        %>

        <%-- Muestra la barra de navegación --%>
        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <%-- Cambia el título según la operación --%>
            <h1>
                <%=cliente.getId() == 0 ? "Nuevo cliente" : "Modificar cliente"%>
            </h1>

            <%-- Envía los datos al Servlet cliente --%>
            <form action="cliente?<%=accion%>" method="post">

                <%-- Conserva el ID para identificar el registro --%>
                <input type="hidden" name="id" value="<%=cliente.getId()%>"/>

                <%-- Campo para ingresar el nombre --%>
                <div class="mb-3">
                    <label>Nombre</label>
                    <input type="text"
                           name="nombre"
                           value="<%=cliente.getNombre()%>"
                           required
                           class="form-control"/>
                </div>

                <%-- Campo para ingresar el teléfono --%>
                <div class="mb-3">
                    <label>Teléfono</label>
                    <input type="text"
                           name="telefono"
                           value="<%=cliente.getTelefono()%>"
                           required
                           class="form-control"/>
                </div>

                <%-- Guarda los datos o regresa al listado --%>
                <input type="submit"
                       value="Guardar"
                       class="btn btn-primary"/>

                <a href="cliente?mostrar" class="btn btn-primary">
                    Volver
                </a>

            </form>

        </div>

        <%-- Redirige al listado si no se recibe un ID --%>
        <%
            }else{
                response.sendRedirect("cliente?mostrar");
            }
        %>

        <%-- Carga las funciones JavaScript de Bootstrap --%>
        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>
</html>