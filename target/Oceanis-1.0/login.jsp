
<%-- 
    Document   : login
    Project    : Oceanis System
    Author     : a503o
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    HttpSession sesion = request.getSession(false);

    if("1".equals(request.getAttribute("resultado"))){

        sesion = request.getSession();
        request.changeSessionId();

        sesion.setAttribute("sesion", request.getAttribute("usr"));
        sesion.setAttribute("rol", request.getAttribute("rol"));
        sesion.setAttribute("idusuario", request.getAttribute("idusuario"));

        response.sendRedirect("bienvenido.jsp");
        return;
    }

    if(sesion != null && sesion.getAttribute("sesion") != null){
        response.sendRedirect("bienvenido.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Oceanis System - Iniciar sesión</title>

        <link href="resources/css/bootstrap.min.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/fontawesome/css/all.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/css/Oceanis_dark.css"
              rel="stylesheet" type="text/css"/>
    </head>

    <body class="ocean-login-page">

        <main class="login-container">

            <!-- PRESENTACIÓN DEL SISTEMA -->

            <section class="login-info">

                <div class="login-brand">

                    <img src="resources/components/logo/oceanis.png"
                         alt="Oceanis System">

                    <div class="brand-text">
                        <h2>OCEANIS SYSTEM</h2>
                        <span>PORT MANAGEMENT</span>
                    </div>

                </div>

                <div class="login-presentation">

                    <p class="login-label">
                        PLATAFORMA DE GESTIÓN PORTUARIA
                    </p>

                    <h1>
                        El control de tus puertos,
                        <span>en un solo lugar.</span>
                    </h1>

                    <p>
                        Administra embarcaciones, muelles,
                        atraques y mercancías desde una
                        plataforma centralizada, diseñada
                        para optimizar las operaciones portuarias.
                    </p>

                </div>

                <div class="login-footer">
                    OCEANIS SYSTEM © 2026
                </div>

            </section>

            <!-- FORMULARIO DE INICIO DE SESIÓN -->

            <section class="login-section">

                <div class="login-box">

                    <div class="login-icon">
                        <i class="fa-solid fa-user-lock"></i>
                    </div>

                    <h2>Bienvenido</h2>

                    <p class="login-description">
                        Ingresa tus credenciales para acceder al sistema.
                    </p>

                    <!-- MENSAJE DE ERROR -->

                    <%
                        if("0".equals(request.getAttribute("resultado"))){
                    %>

                    <div class="alert alert-danger" role="alert">
                        <i class="fa-solid fa-circle-exclamation"></i>
                        Usuario o contraseña incorrectos.
                    </div>

                    <%
                        }
                    %>

                    <!-- FORMULARIO -->

                    <form action="usuario" method="post" class="login-form">

                        <label for="nombre" class="form-label">
                            Usuario
                        </label>

                        <div class="input-group">

                            <span class="input-group-text">
                                <i class="fa-solid fa-user"></i>
                            </span>

                            <input type="text"
                                   id="nombre"
                                   name="nombre"
                                   class="form-control"
                                   placeholder="Ingrese su usuario"
                                   autocomplete="username"
                                   required>

                        </div>

                        <label for="contrasena" class="form-label">
                            Contraseña
                        </label>

                        <div class="input-group">

                            <span class="input-group-text">
                                <i class="fa-solid fa-lock"></i>
                            </span>

                            <input type="password"
                                   id="contrasena"
                                   name="contraseña"
                                   class="form-control"
                                   placeholder="Ingrese su contraseña"
                                   autocomplete="current-password"
                                   required>

                            <button type="button"
                                    class="btn btn-password"
                                    id="mostrarPassword"
                                    aria-label="Mostrar contraseña"
                                    aria-pressed="false">

                                <i class="fa-solid fa-eye"></i>

                            </button>

                        </div>

                        <button type="submit"
                                name="btn"
                                value="Ingresar"
                                class="btn btn-login">

                            Iniciar sesión

                            <i class="fa-solid fa-arrow-right ms-2"></i>

                        </button>

                    </form>

                    <div class="login-security">

                        <i class="fa-solid fa-shield-halved"></i>

                        Acceso exclusivo para usuarios autorizados

                    </div>

                    <div class="login-bottom">
                        Oceanis System · Port Management
                    </div>

                </div>

            </section>

        </main>

        <script src="resources/js/login.js"></script>
        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>

</html>
