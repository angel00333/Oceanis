
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="ocean-navbar">

    <div class="ocean-navbar-top">

        <a class="ocean-brand" href="bienvenido.jsp">

            <img src="${pageContext.request.contextPath}/resources/components/logo/oceanis.png"
                 alt="Oceanis System"
                 class="ocean-logo">

            <span>
                <strong>OCEANIS SYSTEM</strong>
                <small>PORT MANAGEMENT</small>
            </span>

        </a>

        <div class="ocean-navbar-user">

            <div class="dropdown">

                <button class="ocean-user-button dropdown-toggle"
                        type="button"
                        id="menuUsuario"
                        data-bs-toggle="dropdown"
                        aria-expanded="false">

                    <i class="fa-solid fa-circle-user"></i>

                    <span>
                        <c:out value="${sessionScope.sesion}"/>
                    </span>

                </button>

                <ul class="dropdown-menu dropdown-menu-end ocean-user-menu"
                    aria-labelledby="menuUsuario">

                    <li class="ocean-user-info">

                        <span>Usuario conectado</span>

                        <strong>
                            <c:out value="${sessionScope.sesion}"/>
                        </strong>

                    </li>

                    <li><hr class="dropdown-divider"></li>

                    <li>
                        <a class="dropdown-item ocean-logout"
                           href="usuario?cerrar">

                            <i class="fa-solid fa-right-from-bracket"></i>
                            Cerrar sesión

                        </a>
                    </li>

                </ul>

            </div>

        </div>

    </div>

    <div class="ocean-navbar-bottom">

        <a href="bienvenido.jsp">Inicio</a>

        <c:if test="${sessionScope.rol == 'administrador'}">
            <a href="dashboard.jsp">Dashboard</a>
            <a href="historial.jsp">Historial</a>
        </c:if>

        <a href="puerto?mostrar">Puertos</a>
        <a href="muelle?mostrar">Muelles</a>
        <a href="barco?mostrar">Barcos</a>
        <a href="cliente?mostrar">Clientes</a>
        <a href="atraque?mostrar">Atraques</a>
        <a href="carga?mostrar">Cargas</a>

    </div>

</nav>
