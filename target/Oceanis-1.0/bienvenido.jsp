<%-- 
    Document   : bienvenido
    Created on : 18 nov 2026, 11:18:34 a. m.
    Author     : a503o
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    HttpSession sesion = request.getSession(false);

    if(sesion == null || sesion.getAttribute("sesion") == null){
        response.sendRedirect("login.jsp");
        return;
    }

    String usr = sesion.getAttribute("sesion").toString();
%>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Oceanis System - Bienvenido</title>

        <link href="resources/css/bootstrap.min.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/fontawesome/css/all.css"
              rel="stylesheet" type="text/css"/>

        <link href="resources/css/Oceanis_dark.css"
              rel="stylesheet" type="text/css"/>
    </head>

    <body class="ocean-welcome-page">

        <!-- NAVBAR -->

        <jsp:include page="resources/components/navbar.jsp" />

        <!-- CONTENIDO PRINCIPAL -->

        <main class="ocean-welcome-container">

            <section class="ocean-welcome-header">

                <p class="ocean-welcome-label">
                    OCEANIS SYSTEM · PORT MANAGEMENT
                </p>

                <h1>
                    Bienvenido al Sistema de
                    <span>Gestión de Puertos</span>
                </h1>

                <p class="ocean-welcome-description">
                    Bienvenido, <strong><%=usr%></strong>.
                    Oceanis System es una plataforma diseñada
                    para facilitar la administración de puertos,
                    muelles, embarcaciones, atraques y cargas,
                    centralizando las principales operaciones
                    portuarias en un solo lugar.
                </p>

            </section>

            <!-- IMAGEN DEL PUERTO -->

            <section class="ocean-welcome-banner">

                <img src="resources/components/img/puerto.png"
                     alt="Puerto comercial de El Salvador"
                     class="ocean-welcome-image">

                <div class="ocean-welcome-overlay">

                    <div class="ocean-welcome-overlay-content">

                        <p>GESTIÓN PORTUARIA INTEGRAL</p>

                        <h2>
                            Conectando operaciones,
                            optimizando resultados.
                        </h2>

                    </div>

                </div>

            </section>

            <!-- PIE DE PÁGINA -->

            <footer class="ocean-welcome-footer">

                <span>
                    <i class="fa-solid fa-anchor"></i>
                    OCEANIS SYSTEM
                </span>

                <span>
                    Sistema de Gestión Portuaria © 2026
                </span>

            </footer>

        </main>

        <script src="resources/js/bootstrap.bundle.min.js"></script>

    </body>

</html>
