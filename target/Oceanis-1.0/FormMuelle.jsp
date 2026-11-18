<%-- 
    Document   : FormMuelle
    Created on : 17 nov 2026, 2:53:11 p.m.
    Author     : AB_Geo
--%>

<%@page import="java.util.*, com.aqua.oceanis.models.*" contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession(false);

    if(sesion == null || sesion.getAttribute("sesion") == null){
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <%-- Carga los estilos de la aplicación --%>
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>

        <title>Formulario Muelle</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <%-- Determina si se creará o modificará un muelle --%>
        <%
            Muelle muelle;
            String accion = "";

            if(request.getParameter("id") != null){

                if(request.getParameter("n") != null){

                    // Prepara un muelle nuevo (id=0, sin puerto asignado aún)
                    accion = "guardar";
                    muelle = new Muelle(0, new Puerto(), "", "");

                }else{

                    // Consulta los datos del muelle a modificar
                    accion = "modificar";
                    MuelleDAO muelleDAO = new MuelleDAO();

                    muelle = muelleDAO.ObtenerMuellePorId(
                        Integer.parseInt(request.getParameter("id"))
                    );
                }
        %>

        <%-- Muestra la barra de navegación --%>
        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <%-- Cambia el título según la operación --%>
            <h1>
                <%=muelle.getId() == 0 ? "Nuevo muelle" : "Modificar muelle"%>
            </h1>

            <%-- Envía los datos al Servlet muelle --%>
            <form action="muelle?<%=accion%>" method="post">

                <%-- Conserva el ID para identificar el registro --%>
                <input type="hidden" name="id" value="<%=muelle.getId()%>"/>

               <%-- Selector de puerto: relación maestro-detalle --%>
                <div class="mb-3">
                    <label>Puerto</label>

                    <select name="idpuerto" required class="form-select">

                        <option value="">Seleccione un puerto</option>

                        <%
                            // CAMBIO: Consulta los puertos directamente desde el DAO
                            PuertoDAO puertoDAO = new PuertoDAO();

                            // Obtiene la lista de puertos registrados
                            ArrayList<Puerto> listaPuertos = puertoDAO.MostrarPuertos();

                            // Recorre los puertos para llenar el selector
                            if(listaPuertos != null){
                                for(Puerto p : listaPuertos){

                                    // Mantiene seleccionado el puerto al modificar
                                    String selected = "";

                                    if(muelle.getPuerto() != null &&
                                       muelle.getPuerto().getId() == p.getId()){

                                        selected = "selected";
                                    }
                        %>

                        <option value="<%=p.getId()%>" <%=selected%>>
                            <%=p.getNombre()%>
                        </option>

                        <%
                                }
                            }
                        %>

                    </select>
                </div>

                <%-- Campo para ingresar el nombre --%>
                <div class="mb-3">
                    <label>Nombre</label>
                    <input type="text"
                           name="nombre"
                           value="<%=muelle.getNombre()%>"
                           required
                           class="form-control"/>
                </div>

                <%-- CAMBIO: Selector de estado (enum de la BD) --%>
                <%-- Los valores coinciden con el enum de la tabla muelles --%>
                <div class="mb-3">
                    <label>Estado</label>
                    <select name="estado" required class="form-select">
                        <option value="">Seleccione un estado</option>

                        <%
                            String[] estados = {"Disponible", "Ocupado", "En Mantenimiento", "Fuera de servicio"};
                            for(String estado : estados){
                                // ALTERADO: se marca como seleccionado si coincide con el estado actual del muelle
                                String selected = "";
                                if(estado.equals(muelle.getEstado())){
                                    selected = "selected";
                                }
                        %>
                            <option value="<%=estado%>" <%=selected%>>
                                <%=estado%>
                            </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <%-- Guarda los datos o regresa al listado --%>
                <input type="submit"
                       value="Guardar"
                       class="btn btn-primary"/>

                <a href="muelle?mostrar" class="btn btn-primary">
                    Volver
                </a>

            </form>

        </div>

        <%-- Redirige al listado si no se recibe un ID --%>
        <%
            }else{
                response.sendRedirect("muelle?mostrar");
            }
        %>

        <%-- Carga las funciones JavaScript de Bootstrap --%>
        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>
</html>