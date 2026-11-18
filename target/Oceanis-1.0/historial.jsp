<%-- 
    Document   : historial.jsp
    Created on : 18 nov 2026, 2:16:27 p. m.
    Author     : a503o
--%>

<%@page import="java.util.*"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.aqua.oceanis.models.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
    // VALIDAR SESIÓN

    HttpSession sesion = request.getSession(false);

    if(sesion == null || sesion.getAttribute("sesion") == null){
        response.sendRedirect("login.jsp");
        return;
    }

    // VALIDAR ADMINISTRADOR

    if(!"administrador".equals(sesion.getAttribute("rol"))){
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN,
            "Acceso exclusivo para administradores."
        );
        return;
    }

    // CONSULTAR HISTORIAL

    HistorialDAO historialDAO = new HistorialDAO();

    List<Map<String, Object>> historial;

    String fecha = request.getParameter("fecha");

    try {
        if(fecha != null && !fecha.trim().isEmpty()){

            LocalDate.parse(fecha);

            historial = historialDAO.BuscarHistorialPorFecha(fecha);

        }else{
            historial = historialDAO.MostrarHistorial();
            fecha = "";
        }

    }catch(Exception ex){

        historial = new ArrayList<>();
        request.setAttribute(
            "errorHistorial",
            "No se pudo consultar el historial. Verifique la fecha o la conexión."
        );
    }

    // CONTAR ACTIVIDADES

    int creaciones = 0;
    int modificaciones = 0;
    int eliminaciones = 0;

    for(Map<String, Object> actividad : historial){

        String accion = actividad.get("accion").toString();

        if("CREACION".equals(accion))
            creaciones++;

        if("MODIFICACION".equals(accion))
            modificaciones++;

        if("ELIMINACION".equals(accion))
            eliminaciones++;
    }

    request.setAttribute("historial", historial);
%>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Historial de Actividades - Oceanis System</title>

        <link href="resources/css/bootstrap.min.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/fontawesome/css/all.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/css/Oceanis_dark.css"
              rel="stylesheet" type="text/css"/>
    </head>

    <body class="ocean-dashboard-page">

        <!-- NAVBAR -->

        <jsp:include page="resources/components/navbar.jsp"/>

        <main class="ocean-dashboard-container">

            <!-- ENCABEZADO -->

            <section class="ocean-dashboard-header">

                <div>

                    <p class="ocean-dashboard-label">
                        OCEANIS SYSTEM / ADMINISTRACIÓN
                    </p>

                    <h1>Historial de Actividades</h1>

                    <p>
                        Registro de creaciones, modificaciones
                        y eliminaciones realizadas en el sistema.
                    </p>

                </div>

                <div class="ocean-dashboard-role">
                    <i class="fa-solid fa-clock-rotate-left"></i>
                    Auditoría del sistema
                </div>

            </section>

            <!-- INDICADORES -->

            <section class="ocean-dashboard-secondary">

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon ocean-history-create">
                        <i class="fa-solid fa-circle-plus"></i>
                    </div>

                    <div>
                        <span>Registros creados</span>
                        <h3><%=creaciones%></h3>
                    </div>

                </article>

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon ocean-history-update">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </div>

                    <div>
                        <span>Modificaciones</span>
                        <h3><%=modificaciones%></h3>
                    </div>

                </article>

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon ocean-history-delete">
                        <i class="fa-solid fa-trash-can"></i>
                    </div>

                    <div>
                        <span>Eliminaciones lógicas</span>
                        <h3><%=eliminaciones%></h3>
                    </div>

                </article>

            </section>

            <!-- PANEL PRINCIPAL -->

            <section class="ocean-dashboard-panel ocean-history-panel">

                <div class="ocean-panel-header">

                    <h2>Registro de operaciones</h2>

                    <i class="fa-solid fa-list-check"></i>

                </div>

                <p class="ocean-panel-description">
                    Consulta las actividades realizadas por
                    los usuarios de Oceanis System.
                </p>

                <!-- FILTRO POR FECHA -->

                <form action="historial.jsp"
                      method="get"
                      class="ocean-history-filter">

                    <div class="ocean-history-filter-field">

                        <label for="fecha">
                            Filtrar por fecha
                        </label>

                        <input type="date"
                               id="fecha"
                               name="fecha"
                               value="<%=fecha%>"
                               class="form-control">

                    </div>

                    <div class="ocean-history-filter-actions">

                        <button type="submit" class="btn btn-search">

                            <i class="fa-solid fa-magnifying-glass"></i>
                            Buscar

                        </button>

                        <a href="historial.jsp"
                           class="btn btn-secondary">

                            <i class="fa-solid fa-rotate-left"></i>
                            Mostrar todos

                        </a>

                    </div>

                </form>

                <!-- MENSAJE DE ERROR -->

                <c:if test="${not empty errorHistorial}">

                    <div class="alert alert-danger mt-3">
                        <i class="fa-solid fa-circle-exclamation"></i>
                        <c:out value="${errorHistorial}"/>
                    </div>

                </c:if>

                <!-- TABLA DE ACTIVIDADES -->

                <div class="ocean-listado">

                    <div class="ocean-table-responsive">

                        <table id="tablaHistorial"
                               class="ocean-table ocean-table-striped ocean-table-hover">

                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>FECHA Y HORA</th>
                                    <th>USUARIO</th>
                                    <th>ACCIÓN</th>
                                    <th>MÓDULO</th>
                                    <th>ID REGISTRO</th>
                                    <th>DESCRIPCIÓN</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:forEach var="h" items="${historial}">

                                    <tr>

                                        <td>
                                            <c:out value="${h.id}"/>
                                        </td>

                                        <td>
                                            <fmt:formatDate
                                                value="${h.fecha}"
                                                pattern="dd/MM/yyyy HH:mm:ss"/>
                                        </td>

                                        <td>
                                            <i class="fa-solid fa-user ocean-history-user-icon"></i>
                                            <c:out value="${h.usuario}"/>
                                        </td>

                                        <td>

                                            <c:choose>

                                                <c:when test="${h.accion == 'CREACION'}">

                                                    <span class="ocean-history-badge ocean-history-badge-create">
                                                        <i class="fa-solid fa-plus"></i>
                                                        Creación
                                                    </span>

                                                </c:when>

                                                <c:when test="${h.accion == 'MODIFICACION'}">

                                                    <span class="ocean-history-badge ocean-history-badge-update">
                                                        <i class="fa-solid fa-pen"></i>
                                                        Modificación
                                                    </span>

                                                </c:when>

                                                <c:when test="${h.accion == 'ELIMINACION'}">

                                                    <span class="ocean-history-badge ocean-history-badge-delete">
                                                        <i class="fa-solid fa-trash"></i>
                                                        Eliminación
                                                    </span>

                                                </c:when>

                                            </c:choose>

                                        </td>

                                        <td>
                                            <c:out value="${h.modulo}"/>
                                        </td>

                                        <td>
                                            <c:out value="${h.idregistro}"/>
                                        </td>

                                        <td>
                                            <c:out value="${h.descripcion}"/>
                                        </td>

                                    </tr>

                                </c:forEach>

                            </tbody>

                        </table>

                    </div>

                    <!-- PAGINACIÓN REUTILIZABLE -->

                    <jsp:include page="resources/components/pagination.jsp"/>

                </div>

                <c:if test="${empty historial && empty errorHistorial}">

                    <div class="ocean-history-empty">

                        <i class="fa-solid fa-folder-open"></i>

                        <p>
                            No se encontraron actividades registradas.
                        </p>

                    </div>

                </c:if>

            </section>

            <!-- FOOTER -->

            <footer class="ocean-dashboard-footer">
                OCEANIS SYSTEM © 2026 |
                Historial de Actividades
            </footer>

        </main>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
        <script src="resources/js/pagination.js"></script>

        <script>
            iniciarPaginacion("tablaHistorial", 10);
        </script>

    </body>

</html>
