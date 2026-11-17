
function iniciarPaginacion(idTabla, registrosPorPagina = 5) {

    const tabla = document.getElementById(idTabla);
    if (!tabla) return;

    const contenedor = tabla.closest(".ocean-listado");
    if (!contenedor) return;

    const filas = Array.from(tabla.querySelectorAll("tbody tr"));
    const paginacion = contenedor.querySelector(".ocean-pagination");
    const informacion = contenedor.querySelector(".ocean-page-info");

    if (!paginacion || !informacion) return;

    let paginaActual = 1;

    function mostrarPagina(pagina) {

        const totalPaginas = Math.ceil(
            filas.length / registrosPorPagina
        );

        paginaActual = totalPaginas === 0
            ? 1
            : Math.max(1, Math.min(pagina, totalPaginas));

        const inicio = (paginaActual - 1) * registrosPorPagina;
        const fin = inicio + registrosPorPagina;

        filas.forEach(function(fila, indice) {
            fila.style.display =
                indice >= inicio && indice < fin ? "" : "none";
        });

        informacion.textContent = filas.length === 0
            ? "No hay registros"
            : "Mostrando " + (inicio + 1) + " - " +
              Math.min(fin, filas.length) +
              " de " + filas.length + " registros";

        paginacion.innerHTML = "";

        function agregarBoton(texto, pagina, activo, deshabilitado) {

            const li = document.createElement("li");
            li.className = "page-item";

            if (activo) li.classList.add("active");
            if (deshabilitado) li.classList.add("disabled");

            const boton = document.createElement("button");
            boton.type = "button";
            boton.className = "page-link";
            boton.textContent = texto;
            boton.disabled = deshabilitado;

            if (activo) {
                boton.setAttribute("aria-current", "page");
            }

            boton.addEventListener("click", function() {
                mostrarPagina(pagina);
            });

            li.appendChild(boton);
            paginacion.appendChild(li);
        }

        agregarBoton("«", paginaActual - 1,
                     false, paginaActual === 1);

        for (let i = 1; i <= totalPaginas; i++) {
            agregarBoton(i, i, i === paginaActual, false);
        }

        agregarBoton("»", paginaActual + 1,
                     false, paginaActual === totalPaginas);
    }

    mostrarPagina(1);
}
