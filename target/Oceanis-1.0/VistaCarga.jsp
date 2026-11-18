<%-- 
    Document   : VistaCarga
    Created on : 17 nov 2026, 4:28:26 p.m.
    Author     : AB_Geo
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>
        <title>Vista Carga</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <h2>Lista de cargas</h2>
            
            <c:if test="${not empty mensaje}">

                <div class="alert alert-success" role="alert">
                    <i class="fa-solid fa-circle-check"></i>
                    <c:out value="${mensaje}"/>
                </div>

            </c:if>

            <c:if test="${not empty error}">

                <div class="alert alert-danger" role="alert">
                    <i class="fa-solid fa-circle-exclamation"></i>
                    <c:out value="${error}"/>
                </div>

            </c:if>


            <a href="FormCarga.jsp?id=0&n=1" class="btn btn-primary">
                Nueva carga
            </a>

            <c:if test="${DatosCarga == null}">
                <c:redirect url="carga?mostrar" />
            </c:if>

            <c:catch var="ex">
                <c:set var="listaCargas" value="${DatosCarga}" />
            </c:catch>

            <c:if test="${ex != null}">
                <b>${ex}</b>
            </c:if>

            <br/>
            <br/>

            <form action="carga" method="post" class="ocean-search">

                <label for="buscarCarga">BUSCAR CARGA</label>

                <div class="input-group">

                    <span class="input-group-text">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>

                    <input type="text"
                           id="buscarCarga"
                           name="texto"
                           class="form-control"
                           placeholder="Ingrese la descripción de la carga..."
                           value="<c:out value='${param.texto}'/>">

                    <input type="submit"
                           value="Buscar"
                           name="mostrar"
                           class="btn btn-search">

                </div>

            </form>

            <br/>
            <br/>

            <div class="ocean-listado">

                <div class="ocean-table-responsive">

                    <table id="tablaCargas"
                           class="ocean-table ocean-table-striped ocean-table-hover"
                           style="width: 100%">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>ATRAQUE</th>
                                <th>CLIENTE</th>
                                <th>DESCRIPCIÓN</th>
                                <th>PESO</th>
                                <th>TIPO</th>
                                <th></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="c" items="${listaCargas}">
                                <tr>
                                    <td>${c.id}</td>
                                    <td>${c.atraque.barco.nombre}</td>
                                    <td>${c.cliente.nombre}</td>
                                    <td>${c.descripcion}</td>
                                    <td>${c.peso}</td>
                                    <td>${c.tipo}</td>
                                    <td>
                                        <a href="FormCarga.jsp?id=${c.id}"
                                           class="btn btn-success">
                                            <i class="fa-solid fa-pen-to-square fa-1x"></i>
                                        </a>

                                        <a href="carga?eliminar&id=${c.id}"
                                           class="btn btn-danger"
                                           onclick="return confirm('¿Está seguro de eliminar este registro?');">
                                            <i class="fa-solid fa-trash fa-1x"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>

                    </table>

                </div>

                <jsp:include page="resources/components/pagination.jsp" />

            </div>

            <c:if test="${empty listaCargas}">
                <p class="mt-3" style="color: #94A3B8;">
                    No se encontraron cargas.
                </p>
            </c:if>

        </div>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
        <script src="resources/js/pagination.js"></script>

        <script>
            iniciarPaginacion("tablaCargas", 5);
        </script>

    </body>
</html>