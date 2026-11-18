<%-- 
    Document   : VistaPuerto
    Created on : 17 nov 2026, 12:28:02 p.m.
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
        <title>Vista Puerto</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <h2>Lista de puertos</h2>
            
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


            <a href="FormPuerto.jsp?id=0&n=1" class="btn btn-primary">
                Nuevo puerto
            </a>

            <c:if test="${DatosPuerto == null}">
                <c:redirect url="puerto?mostrar" />
            </c:if>

            <c:catch var="ex">
                <c:set var="listaPuertos" value="${DatosPuerto}" />
            </c:catch>

            <c:if test="${ex != null}">
                <b>${ex}</b>
            </c:if>

            <br/>
            <br/>

            <form action="puerto" method="post" class="ocean-search">

                <label for="buscarPuerto">BUSCAR PUERTO</label>

                <div class="input-group">

                    <span class="input-group-text">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>

                    <input type="text"
                           id="buscarPuerto"
                           name="texto"
                           class="form-control"
                           placeholder="Ingrese el nombre del puerto..."
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

                    <table id="tablaPuertos"
                           class="ocean-table ocean-table-striped ocean-table-hover"
                           style="width: 100%">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>NOMBRE</th>
                                <th>DIRECCIÓN</th>
                                <th></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="p" items="${listaPuertos}">
                                <tr>
                                    <td>${p.id}</td>
                                    <td>${p.nombre}</td>
                                    <td>${p.direccion}</td>
                                    <td>
                                        <a href="FormPuerto.jsp?id=${p.id}"
                                           class="btn btn-success">
                                            <i class="fa-solid fa-pen-to-square fa-1x"></i>
                                        </a>

                                        <a href="puerto?eliminar&id=${p.id}"
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

            <c:if test="${empty listaPuertos}">
                <p class="mt-3" style="color: #94A3B8;">
                    No se encontraron puertos.
                </p>
            </c:if>

        </div>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
        <script src="resources/js/pagination.js"></script>

        <script>
            iniciarPaginacion("tablaPuertos", 5);
        </script>

    </body>
</html>