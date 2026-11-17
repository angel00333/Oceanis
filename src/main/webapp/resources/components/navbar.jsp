
<%@page pageEncoding="UTF-8"%>

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
            <i class="fa-solid fa-bell"></i>
            <i class="fa-solid fa-circle-user"></i>
        </div>

    </div>

    <div class="ocean-navbar-bottom">

        <a href="bienvenido.jsp">Inicio</a>
        <a href="puerto?mostrar">Puertos</a>
        <a href="muelle?mostrar">Muelles</a>
        <a href="barco?mostrar">Barcos</a>
        <a href="cliente?mostrar">Clientes</a>
        <a href="atraque?mostrar">Atraques</a>
        <a href="carga?mostrar">Cargas</a>

    </div>

</nav>
