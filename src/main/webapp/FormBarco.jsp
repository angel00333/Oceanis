<%-- 
    Document   : FormBarco
    Created on : 16 nov 2026, 7:29:19 p. m.
    Author     : a503o
--%>

<%@page import="java.util.*, com.aqua.oceanis.models.*" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>
        <title>Formulario Barco</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <%
            Barco barco;
            String accion = "";

            if(request.getParameter("id") != null){

                if(request.getParameter("n") != null){
                    accion = "guardar";
                    barco = new Barco(0, "", "", "");
                }else{
                    accion = "modificar";
                    BarcoDAO barcoDAO = new BarcoDAO();
                    barco = new Barco();
                    barco = barcoDAO.ObtenerBarcoPorId(
                        Integer.parseInt(request.getParameter("id"))
                    );
                }
        %>

        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <h1><%=barco.getId() == 0 ? "Nuevo barco" : "Modificar barco"%></h1>

            <form action="barco?<%=accion%>" method="post">
             <input type="hidden" name="id" value="<%=barco.getId()%>"/>

                <div class="mb-3">
                    <label>Nombre</label>
                    <input type="text" name="nombre"
                           value="<%=barco.getNombre()%>"
                           required class="form-control"/>
                </div>

                <div class="mb-3">
                    <label>Tipo de barco</label>

                    <select name="tipo" required class="form-select">
                        <option value="">Seleccione un tipo</option>

                        <option value="Portacontenedores"
                            <%=barco.getTipo().equals("Portacontenedores") ? "selected" : ""%>>
                            Portacontenedores
                        </option>

                        <option value="Granelero"
                            <%=barco.getTipo().equals("Granelero") ? "selected" : ""%>>
                            Granelero
                        </option>

                        <option value="Petrolero"
                            <%=barco.getTipo().equals("Petrolero") ? "selected" : ""%>>
                            Petrolero
                        </option>

                        <option value="Carga General"
                            <%=barco.getTipo().equals("Carga General") ? "selected" : ""%>>
                            Carga General
                        </option>

                        <option value="Pesquero"
                            <%=barco.getTipo().equals("Pesquero") ? "selected" : ""%>>
                            Pesquero
                        </option>

                        <option value="Pasajeros"
                            <%=barco.getTipo().equals("Pasajeros") ? "selected" : ""%>>
                            Pasajeros
                        </option>

                        <option value="Otro"
                            <%=barco.getTipo().equals("Otro") ? "selected" : ""%>>
                            Otro
                        </option>
                    </select>
                </div>

                <div class="mb-3">
                    <label>País</label>
                    <input type="text" name="pais"
                           value="<%=barco.getPais()%>"
                           required class="form-control"/>
                </div>

                <input type="submit" value="Guardar" class="btn btn-primary"/>
                <a href="barco?mostrar" class="btn btn-primary">Volver</a>

            </form>

        </div>

        <%
            }else
                response.sendRedirect("barco?mostrar");
        %>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
