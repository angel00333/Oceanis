<%-- 
    Document   : VistaMuelle
    Created on : 17 nov 2026, 2:45:50 p.m.
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
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>
        <title>Vista Muelle</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <h2>Lista de muelles</h2>
            
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


            <a href="FormMuelle.jsp?id=0&n=1" class="btn btn-primary">
                Nuevo muelle
            </a>

            <c:if test="${DatosMuelle == null}">
                <c:redirect url="muelle?mostrar" />
            </c:if>

            <c:catch var="ex">
                <c:set var="listaMuelles" value="${DatosMuelle}" />
            </c:catch>

            <c:if test="${ex != null}">
                <b>${ex}</b>
            </c:if>

            <br/>
            <br/>

            <form action="muelle" method="post" class="ocean-search">

                <label for="buscarMuelle">BUSCAR MUELLE</label>

                <div class="input-group">

                    <span class="input-group-text">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>

                    <input type="text"
                           id="buscarMuelle"
                           name="texto"
                           class="form-control"
                           placeholder="Ingrese el nombre del muelle..."
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

                    <table id="tablaMuelles"
                           class="ocean-table ocean-table-striped ocean-table-hover"
                           style="width: 100%">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>NOMBRE</th>
                                <th>PUERTO</th>
                                <th>ESTADO</th>
                                <th></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="m" items="${listaMuelles}">
                                <tr>
                                    <td>${m.id}</td>
                                    <td>${m.nombre}</td>
                                    <td>${m.puerto.nombre}</td>
                                    <td>${m.estado}</td>
                                    <td>
                                        <a href="FormMuelle.jsp?id=${m.id}"
                                           class="btn btn-success">
                                            <i class="fa-solid fa-pen-to-square fa-1x"></i>
                                        </a>

                                        <a href="muelle?eliminar&id=${m.id}"
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

            <c:if test="${empty listaMuelles}">
                <p class="mt-3" style="color: #94A3B8;">
                    No se encontraron muelles.
                </p>
            </c:if>

        </div>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
        <script src="resources/js/pagination.js"></script>

        <script>
            iniciarPaginacion("tablaMuelles", 5);
        </script>

    </body>
</html>