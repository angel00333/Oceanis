<%-- 
    Document   : dashboard
    Created on : 18 nov 2026, 11:31:06 a. m.
    Author     : a503o
--%>

<%@page import="java.util.*"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.aqua.oceanis.models.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
            "No tiene permisos para acceder al Dashboard."
        );
        return;
    }

    // INSTANCIAS DAO

    PuertoDAO puertoDAO = new PuertoDAO();
    MuelleDAO muelleDAO = new MuelleDAO();
    BarcoDAO barcoDAO = new BarcoDAO();
    ClienteDAO clienteDAO = new ClienteDAO();
    AtraqueDAO atraqueDAO = new AtraqueDAO();
    CargaDAO cargaDAO = new CargaDAO();

    // OBTENER REGISTROS

    List<Puerto> puertos = puertoDAO.MostrarPuertos();
    List<Muelle> muelles = muelleDAO.MostrarMuelles();
    List<Barco> barcos = barcoDAO.MostrarBarcos();
    List<Cliente> clientes = clienteDAO.MostrarClientes();
    List<Atraque> atraques = atraqueDAO.MostrarAtraques();
    List<Carga> cargas = cargaDAO.MostrarCargas();

    // TOTALES GENERALES

    int totalPuertos = puertos != null ? puertos.size() : 0;
    int totalMuelles = muelles != null ? muelles.size() : 0;
    int totalBarcos = barcos != null ? barcos.size() : 0;
    int totalClientes = clientes != null ? clientes.size() : 0;
    int totalAtraques = atraques != null ? atraques.size() : 0;
    int totalCargas = cargas != null ? cargas.size() : 0;

    // ESTADOS DE MUELLES

    int disponibles = 0;
    int ocupados = 0;
    int mantenimiento = 0;
    int fueraServicio = 0;

    if(muelles != null){

        for(Muelle m : muelles){

            if("Disponible".equalsIgnoreCase(m.getEstado()))
                disponibles++;

            if("Ocupado".equalsIgnoreCase(m.getEstado()))
                ocupados++;

            if("En Mantenimiento".equalsIgnoreCase(m.getEstado()))
                mantenimiento++;

            if("Fuera de Servicio".equalsIgnoreCase(m.getEstado()))
                fueraServicio++;
        }
    }

    // ESTADOS DE ATRAQUES

    int programados = 0;
    int enOperacion = 0;
    int finalizados = 0;

    if(atraques != null){

        for(Atraque a : atraques){

            if("Programado".equalsIgnoreCase(a.getEstado()))
                programados++;

            if("En Operación".equalsIgnoreCase(a.getEstado()))
                enOperacion++;

            if("Finalizado".equalsIgnoreCase(a.getEstado()))
                finalizados++;
        }
    }

    // PESO TOTAL DE CARGAS

    BigDecimal pesoTotal = BigDecimal.ZERO;

    if(cargas != null){

        for(Carga carga : cargas){

            if(carga.getPeso() != null)
                pesoTotal = pesoTotal.add(carga.getPeso());
        }
    }

    // PORCENTAJES DE MUELLES

    int porcentajeDisponibles = totalMuelles > 0
            ? (int) Math.round(disponibles * 100.0 / totalMuelles) : 0;

    int porcentajeOcupados = totalMuelles > 0
            ? (int) Math.round(ocupados * 100.0 / totalMuelles) : 0;

    int porcentajeMantenimiento = totalMuelles > 0
            ? (int) Math.round(mantenimiento * 100.0 / totalMuelles) : 0;

    int porcentajeFuera = totalMuelles > 0
            ? (int) Math.round(fueraServicio * 100.0 / totalMuelles) : 0;

    // DATOS PARA LA TABLA

    request.setAttribute("listaAtraquesDashboard", atraques);
%>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width, initial-scale=1.0">

        <title>Dashboard - Oceanis System</title>

        <link href="resources/css/bootstrap.min.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/fontawesome/css/all.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/css/Oceanis_dark.css"
              rel="stylesheet" type="text/css"/>
    </head>

    <body class="ocean-dashboard-page">

        <jsp:include page="resources/components/navbar.jsp"/>

        <main class="ocean-dashboard-container">

            <!-- ENCABEZADO -->

            <section class="ocean-dashboard-header">

                <div>
                    <p class="ocean-dashboard-label">
                        OCEANIS SYSTEM / ADMINISTRACIÓN
                    </p>

                    <h1>Panel de Control</h1>

                    <p>
                        Resumen general de las operaciones
                        del sistema de gestión portuaria.
                    </p>
                </div>

                <div class="ocean-dashboard-role">
                    <i class="fa-solid fa-user-shield"></i>
                    Administrador
                </div>

            </section>

            <!-- INDICADORES PRINCIPALES -->

            <section class="ocean-dashboard-stats">

                <article class="ocean-stat-card">

                    <div class="ocean-stat-top">
                        <span>Puertos registrados</span>
                        <i class="fa-solid fa-building"></i>
                    </div>

                    <h2><%=totalPuertos%></h2>

                    <p>Total de puertos del sistema</p>

                </article>

                <article class="ocean-stat-card">

                    <div class="ocean-stat-top">
                        <span>Muelles disponibles</span>
                        <i class="fa-solid fa-anchor"></i>
                    </div>

                    <h2><%=disponibles%></h2>

                    <p>De <%=totalMuelles%> muelles registrados</p>

                </article>

                <article class="ocean-stat-card">

                    <div class="ocean-stat-top">
                        <span>Embarcaciones</span>
                        <i class="fa-solid fa-ship"></i>
                    </div>

                    <h2><%=totalBarcos%></h2>

                    <p>Barcos registrados</p>

                </article>

                <article class="ocean-stat-card">

                    <div class="ocean-stat-top">
                        <span>Atraques en operación</span>
                        <i class="fa-solid fa-water"></i>
                    </div>

                    <h2><%=enOperacion%></h2>

                    <p>De <%=totalAtraques%> atraques</p>

                </article>

            </section>

            <!-- RESUMEN OPERATIVO -->

            <section class="ocean-dashboard-summary">

                <!-- ESTADO DE MUELLES -->

                <article class="ocean-dashboard-panel">

                    <div class="ocean-panel-header">
                        <h2>Estado de los muelles</h2>
                        <i class="fa-solid fa-chart-simple"></i>
                    </div>

                    <p class="ocean-panel-description">
                        Distribución actual de los muelles registrados.
                    </p>

                    <div class="ocean-status-list">

                        <div class="ocean-status-item">

                            <div class="ocean-status-heading">
                                <span>Disponibles</span>
                                <strong><%=disponibles%></strong>
                            </div>

                            <progress class="ocean-progress disponible"
                                      value="<%=disponibles%>"
                                      max="<%=Math.max(totalMuelles, 1)%>">
                            </progress>

                            <small><%=porcentajeDisponibles%>%</small>

                        </div>

                        <div class="ocean-status-item">

                            <div class="ocean-status-heading">
                                <span>Ocupados</span>
                                <strong><%=ocupados%></strong>
                            </div>

                            <progress class="ocean-progress ocupado"
                                      value="<%=ocupados%>"
                                      max="<%=Math.max(totalMuelles, 1)%>">
                            </progress>

                            <small><%=porcentajeOcupados%>%</small>

                        </div>

                        <div class="ocean-status-item">

                            <div class="ocean-status-heading">
                                <span>En mantenimiento</span>
                                <strong><%=mantenimiento%></strong>
                            </div>

                            <progress class="ocean-progress mantenimiento"
                                      value="<%=mantenimiento%>"
                                      max="<%=Math.max(totalMuelles, 1)%>">
                            </progress>

                            <small><%=porcentajeMantenimiento%>%</small>

                        </div>

                        <div class="ocean-status-item">

                            <div class="ocean-status-heading">
                                <span>Fuera de servicio</span>
                                <strong><%=fueraServicio%></strong>
                            </div>

                            <progress class="ocean-progress fuera-servicio"
                                      value="<%=fueraServicio%>"
                                      max="<%=Math.max(totalMuelles, 1)%>">
                            </progress>

                            <small><%=porcentajeFuera%>%</small>

                        </div>

                    </div>

                </article>

                <!-- ESTADO DE ATRAQUES -->

                <article class="ocean-dashboard-panel">

                    <div class="ocean-panel-header">
                        <h2>Operaciones de atraque</h2>
                        <i class="fa-solid fa-clipboard-list"></i>
                    </div>

                    <p class="ocean-panel-description">
                        Estado de las operaciones registradas.
                    </p>

                    <div class="ocean-operation-list">

                        <div class="ocean-operation-item">
                            <div>
                                <i class="fa-solid fa-clock ocean-programado"></i>
                                <span>Programados</span>
                            </div>
                            <strong><%=programados%></strong>
                        </div>

                        <div class="ocean-operation-item">
                            <div>
                                <i class="fa-solid fa-ship ocean-operacion"></i>
                                <span>En operación</span>
                            </div>
                            <strong><%=enOperacion%></strong>
                        </div>

                        <div class="ocean-operation-item">
                            <div>
                                <i class="fa-solid fa-circle-check ocean-finalizado"></i>
                                <span>Finalizados</span>
                            </div>
                            <strong><%=finalizados%></strong>
                        </div>

                    </div>

                    <div class="ocean-operation-total">

                        <span>Total de atraques</span>
                        <strong><%=totalAtraques%></strong>

                    </div>

                </article>

            </section>

            <!-- INDICADORES SECUNDARIOS -->

            <section class="ocean-dashboard-secondary">

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon">
                        <i class="fa-solid fa-users"></i>
                    </div>

                    <div>
                        <span>Clientes registrados</span>
                        <h3><%=totalClientes%></h3>
                    </div>

                </article>

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon">
                        <i class="fa-solid fa-boxes-stacked"></i>
                    </div>

                    <div>
                        <span>Cargas registradas</span>
                        <h3><%=totalCargas%></h3>
                    </div>

                </article>

                <article class="ocean-mini-stat">

                    <div class="ocean-mini-icon">
                        <i class="fa-solid fa-weight-hanging"></i>
                    </div>

                    <div>
                        <span>Peso total registrado</span>
                        <h3><%=pesoTotal%></h3>
                        <small>Unidad según registro</small>
                    </div>

                </article>

            </section>

            <!-- TABLA DE ATRAQUES -->

            <section class="ocean-dashboard-panel ocean-dashboard-table">

                <div class="ocean-panel-header">
                    <h2>Atraques registrados</h2>
                    <i class="fa-solid fa-list"></i>
                </div>

                <p class="ocean-panel-description">
                    Primeros cinco registros obtenidos del sistema.
                </p>

                <div class="ocean-table-responsive">

                    <table class="ocean-table ocean-table-striped ocean-table-hover">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>EMBARCACIÓN</th>
                                <th>MUELLE</th>
                                <th>FECHA DE LLEGADA</th>
                                <th>ESTADO</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="a"
                                       items="${listaAtraquesDashboard}"
                                       begin="0"
                                       end="4">

                                <tr>
                                    <td>
                                        <c:out value="${a.id}"/>
                                    </td>

                                    <td>
                                        <c:out value="${a.barco.nombre}"
                                               default="Sin barco"/>
                                    </td>

                                    <td>
                                        <c:out value="${a.muelle.nombre}"
                                               default="Sin muelle"/>
                                    </td>

                                    <td>
                                        <c:out value="${a.fecha_llegada}"/>
                                    </td>

                                    <td>
                                        <span class="ocean-dashboard-status">
                                            <c:out value="${a.estado}"/>
                                        </span>
                                    </td>
                                </tr>

                            </c:forEach>

                            <c:if test="${empty listaAtraquesDashboard}">
                                <tr>
                                    <td colspan="5" class="text-center">
                                        No hay atraques registrados.
                                    </td>
                                </tr>
                            </c:if>

                        </tbody>

                    </table>

                </div>

            </section>

            <!-- FOOTER -->

            <footer class="ocean-dashboard-footer">
                OCEANIS SYSTEM © 2026
                | Panel de Administración
            </footer>

        </main>

        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>

</html>
