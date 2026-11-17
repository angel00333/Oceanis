<%-- 
    Document   : FormAtraque
    Created on : 18 nov 2026
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

        <title>Formulario Atraque</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <%-- Determina si se creará o modificará un atraque --%>
        <%
            Atraque atraque;
            String accion = "";

            if(request.getParameter("id") != null){

                if(request.getParameter("n") != null){

                    // Prepara un atraque nuevo (id=0, sin barco ni muelle asignados aún)
                    accion = "guardar";
                    atraque = new Atraque(0, new Barco(), new Muelle(),
                            java.time.LocalDateTime.now(), null, "");

                }else{

                    // Consulta los datos del atraque a modificar
                    accion = "modificar";
                    AtraqueDAO atraqueDAO = new AtraqueDAO();

                    atraque = atraqueDAO.ObtenerAtraquePorId(
                        Integer.parseInt(request.getParameter("id"))
                    );
                }
        %>

        <%-- Muestra la barra de navegación --%>
        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <%-- Cambia el título según la operación --%>
            <h1>
                <%=atraque.getId() == 0 ? "Nuevo atraque" : "Modificar atraque"%>
            </h1>

            <%-- Envía los datos al Servlet atraque --%>
            <form action="atraque?<%=accion%>" method="post">

                <%-- Conserva el ID para identificar el registro --%>
                <input type="hidden" name="id" value="<%=atraque.getId()%>"/>

                <%-- Selector de barco: relación maestro-detalle --%>
                <div class="mb-3">
                    <label>Barco</label>

                    <select name="idbarco" required class="form-select">

                        <option value="">Seleccione un barco</option>

                        <%
                            // Consulta los barcos directamente desde el DAO
                            BarcoDAO barcoDAO = new BarcoDAO();

                            // Obtiene la lista de barcos registrados
                            ArrayList<Barco> listaBarcos = barcoDAO.MostrarBarcos();

                            // Recorre los barcos para llenar el selector
                            if(listaBarcos != null){
                                for(Barco b : listaBarcos){

                                    // Mantiene seleccionado el barco al modificar
                                    String selected = "";

                                    if(atraque.getBarco() != null &&
                                       atraque.getBarco().getId() == b.getId()){

                                        selected = "selected";
                                    }
                        %>

                        <option value="<%=b.getId()%>" <%=selected%>>
                            <%=b.getNombre()%>
                        </option>

                        <%
                                }
                            }
                        %>

                    </select>
                </div>

                <%-- Selector de muelle: relación maestro-detalle --%>
                <div class="mb-3">
                    <label>Muelle</label>

                    <select name="idmuelle" required class="form-select">

                        <option value="">Seleccione un muelle</option>

                        <%
                            // Consulta los muelles directamente desde el DAO
                            MuelleDAO muelleDAO = new MuelleDAO();

                            // Obtiene la lista de muelles registrados
                            ArrayList<Muelle> listaMuelles = muelleDAO.MostrarMuelles();

                            // Recorre los muelles para llenar el selector
                            if(listaMuelles != null){
                                for(Muelle m : listaMuelles){

                                    // Mantiene seleccionado el muelle al modificar
                                    String selected = "";

                                    if(atraque.getMuelle() != null &&
                                       atraque.getMuelle().getId() == m.getId()){

                                        selected = "selected";
                                    }
                        %>

                        <option value="<%=m.getId()%>" <%=selected%>>
                            <%=m.getNombre()%>
                        </option>

                        <%
                                }
                            }
                        %>

                    </select>
                </div>

                <%-- Campo para ingresar la fecha y hora de llegada --%>
                <div class="mb-3">
                    <label>Fecha de llegada</label>
                    <input type="datetime-local"
                           name="fecha_llegada"
                           value="<%=atraque.getFecha_llegada() != null ? atraque.getFecha_llegada().toString().substring(0, 16) : ""%>"
                           required
                           class="form-control"/>
                </div>

                <%-- Campo para ingresar la fecha y hora de salida (opcional) --%>
                <div class="mb-3">
                    <label>Fecha de salida</label>
                    <input type="datetime-local"
                           name="fecha_salida"
                           value="<%=atraque.getFecha_salida() != null ? atraque.getFecha_salida().toString().substring(0, 16) : ""%>"
                           class="form-control"/>
                </div>

                <%-- Selector de estado (enum de la BD) --%>
                <%-- Los valores coinciden con el enum de la tabla atraques --%>
                <div class="mb-3">
                    <label>Estado</label>
                    <select name="estado" required class="form-select">
                        <option value="">Seleccione un estado</option>

                        <%
                           
                            String[] estados = {"Programado", "En Operación", "Finalizado"};

                            for(String estado : estados){
                                // Se marca como seleccionado si coincide con el estado actual del atraque
                                String selected = "";
                                if(estado.equals(atraque.getEstado())){
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

                <a href="atraque?mostrar" class="btn btn-primary">
                    Volver
                </a>

            </form>

        </div>

        <%-- Redirige al listado si no se recibe un ID --%>
        <%
            }else{
                response.sendRedirect("atraque?mostrar");
            }
        %>

        <%-- Carga las funciones JavaScript de Bootstrap --%>
        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>
</html>