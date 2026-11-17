<%-- 
    Document   : FormPuerto
    Created on : 17 nov 2026, 12:39:05 p.m.
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

        <title>Formulario Puerto</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <%-- Determina si se creará o modificará un puerto --%>
        <%
            Puerto puerto;
            String accion = "";

            if(request.getParameter("id") != null){

                if(request.getParameter("n") != null){

                    // Prepara un puerto nuevo
                    accion = "guardar";
                    puerto = new Puerto(0, "", "");

                }else{

                    // Consulta los datos del puerto a modificar
                    accion = "modificar";
                    PuertoDAO puertoDAO = new PuertoDAO();

                    puerto = puertoDAO.ObtenerPuertoPorId(
                        Integer.parseInt(request.getParameter("id"))
                    );
                }
        %>

        <%-- Muestra la barra de navegación --%>
        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <%-- Cambia el título según la operación --%>
            <h1>
                <%=puerto.getId() == 0 ? "Nuevo puerto" : "Modificar puerto"%>
            </h1>

            <%-- Envía los datos al Servlet puerto --%>
            <form action="puerto?<%=accion%>" method="post">

                <%-- Conserva el ID para identificar el registro --%>
                <input type="hidden" name="id" value="<%=puerto.getId()%>"/>

                <%-- Campo para ingresar el nombre --%>
                <div class="mb-3">
                    <label>Nombre</label>
                    <input type="text"
                           name="nombre"
                           value="<%=puerto.getNombre()%>"
                           required
                           class="form-control"/>
                </div>

                <%-- Campo para ingresar la dirección --%>
                <div class="mb-3">
                    <label>Dirección</label>
                    <input type="text"
                           name="direccion"
                           value="<%=puerto.getDireccion()%>"
                           required
                           class="form-control"/>
                </div>

                <%-- Guarda los datos o regresa al listado --%>
                <input type="submit"
                       value="Guardar"
                       class="btn btn-primary"/>

                <a href="puerto?mostrar" class="btn btn-primary">
                    Volver
                </a>

            </form>

        </div>

        <%-- Redirige al listado si no se recibe un ID --%>
        <%
            }else{
                response.sendRedirect("puerto?mostrar");
            }
        %>

        <%-- Carga las funciones JavaScript de Bootstrap --%>
        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>
</html>
