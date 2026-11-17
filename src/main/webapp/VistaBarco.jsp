
<%-- 
    Document   : VistaBarco
    Created on : 16 nov 2026, 5:51:22 p. m.
    Author     : a503o
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="resources/fontawesome/css/all.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/Oceanis_dark.css" rel="stylesheet" type="text/css"/>
        <title>Vista Barco</title>
    </head>

    <body style="background: #0B1220; color: #E2E8F0;">

        <jsp:include page="resources/components/navbar.jsp" />

        <div class="container mt-3">

            <h2>Lista de barcos</h2>

            <a href="FormBarco.jsp?id=0&n=1" class="btn btn-primary">
                Nuevo barco
            </a>

            <c:if test="${DatosBarco == null}">
                <c:redirect url="barco?mostrar" />
            </c:if>

            <c:catch var="ex">
                <c:set var="listaBarcos" value="${DatosBarco}" />
            </c:catch>

            <c:if test="${ex != null}">
                <b>${ex}</b>
            </c:if>

            <br/>
            <br/>

            <form action="barco" method="post" class="ocean-search">

                <label for="buscarBarco">BUSCAR EMBARCACIÓN</label>

                <div class="input-group">

                    <span class="input-group-text">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>

                    <input type="text"
                           id="buscarBarco"
                           name="texto"
                           class="form-control"
                           placeholder="Ingrese el nombre del barco..."
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

                    <table id="tablaBarcos"
                           class="ocean-table ocean-table-striped ocean-table-hover"
                           style="width: 100%">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>NOMBRE</th>
                                <th>TIPO</th>
                                <th>PAÍS</th>
                                <th></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="b" items="${listaBarcos}">
                                <tr>
                                    <td>${b.id}</td>
                                    <td>${b.nombre}</td>
                                    <td>${b.tipo}</td>
                                    <td>${b.pais}</td>
                                    <td>
                                        <a href="FormBarco.jsp?id=${b.id}"
                                           class="btn btn-success">
                                            <i class="fa-solid fa-pen-to-square fa-1x"></i>
                                        </a>

                                        <a href="barco?eliminar&id=${b.id}"
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

            <c:if test="${empty listaBarcos}">
                <p class="mt-3" style="color: #94A3B8;">
                    No se encontraron barcos.
                </p>
            </c:if>

        </div>

        <script src="resources/js/bootstrap.bundle.min.js"></script>
        <script src="resources/js/pagination.js"></script>

        <script>
            iniciarPaginacion("tablaBarcos", 5);
        </script>

    </body>
</html>
