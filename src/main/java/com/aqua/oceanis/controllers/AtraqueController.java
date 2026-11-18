/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.aqua.oceanis.controllers;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.aqua.oceanis.models.*;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author a503o
 */
@WebServlet(name = "AtraqueController", urlPatterns = {"/atraque"})
public class AtraqueController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            Atraque atraque;
            Barco barco;
            Muelle muelle;

            AtraqueDAO atraqueDAO = new AtraqueDAO();
            RequestDispatcher req;

            HttpSession sesion = request.getSession(false);

            if (sesion == null
                    || sesion.getAttribute("sesion") == null
                    || !(sesion.getAttribute("idusuario") instanceof Integer)) {

                response.sendRedirect("login.jsp");
                return;
            }

            int idusuario = (Integer) sesion.getAttribute("idusuario");
            String usuario = sesion.getAttribute("sesion").toString();

            // MOSTRAR ATRAQUES

            if (request.getParameter("mostrar") != null) {

                if (request.getParameter("texto") == null) {

                    request.setAttribute(
                            "DatosAtraque",
                            atraqueDAO.MostrarAtraques()
                    );

                } else {

                    request.setAttribute(
                            "DatosAtraque",
                            atraqueDAO.BuscarAtraques(
                                    request.getParameter("texto")
                            )
                    );
                }
            }

            // GUARDAR ATRAQUE

            if (request.getParameter("guardar") != null) {

                try {

                    barco = new Barco(
                            Integer.parseInt(request.getParameter("barco")),
                            "", "", ""
                    );

                    muelle = new Muelle(
                            Integer.parseInt(request.getParameter("muelle")),
                            null, "", ""
                    );

                    String fechaSalida = request.getParameter("fecha_salida");

                    atraque = new Atraque(
                            0,
                            barco,
                            muelle,
                            LocalDateTime.parse(
                                    request.getParameter("fecha_llegada")
                            ),
                            fechaSalida == null || fechaSalida.isEmpty()
                                    ? null : LocalDateTime.parse(fechaSalida),
                            request.getParameter("estado")
                    );

                    if (atraqueDAO.InsertarAtraque(
                            atraque, idusuario, usuario)) {

                        request.setAttribute(
                                "mensaje",
                                "Atraque registrado correctamente."
                        );

                    } else {

                        request.setAttribute(
                                "error",
                                "No se pudo registrar el atraque. "
                                + "Verifique que el barco y el muelle estén "
                                + "activos y que las fechas sean correctas."
                        );
                    }

                } catch (Exception ex) {

                    request.setAttribute(
                            "error",
                            "Los datos del atraque no son válidos."
                    );
                }
            }

            // MODIFICAR ATRAQUE

            if (request.getParameter("modificar") != null) {

                try {

                    barco = new Barco(
                            Integer.parseInt(request.getParameter("barco")),
                            "", "", ""
                    );

                    muelle = new Muelle(
                            Integer.parseInt(request.getParameter("muelle")),
                            null, "", ""
                    );

                    String fechaSalida = request.getParameter("fecha_salida");

                    atraque = new Atraque(
                            Integer.parseInt(request.getParameter("id")),
                            barco,
                            muelle,
                            LocalDateTime.parse(
                                    request.getParameter("fecha_llegada")
                            ),
                            fechaSalida == null || fechaSalida.isEmpty()
                                    ? null : LocalDateTime.parse(fechaSalida),
                            request.getParameter("estado")
                    );

                    if (atraqueDAO.ModificarAtraque(
                            atraque, idusuario, usuario)) {

                        request.setAttribute(
                                "mensaje",
                                "Atraque modificado correctamente."
                        );

                    } else {

                        request.setAttribute(
                                "error",
                                "No se pudo modificar el atraque. "
                                + "Verifique las fechas, las relaciones "
                                + "y los registros seleccionados."
                        );
                    }

                } catch (Exception ex) {

                    request.setAttribute(
                            "error",
                            "Los datos del atraque no son válidos."
                    );
                }
            }

            // ELIMINAR ATRAQUE

            if (request.getParameter("eliminar") != null) {

                atraque = new Atraque(
                        Integer.parseInt(request.getParameter("id")),
                        null, null, null, null, ""
                );

                int resultado = atraqueDAO.EliminarAtraque(
                        atraque, idusuario, usuario
                );

                if (resultado == 1) {

                    request.setAttribute(
                            "mensaje",
                            "Atraque eliminado correctamente."
                    );

                } else if (resultado == 0) {

                    request.setAttribute(
                            "error",
                            "No se puede eliminar el atraque porque "
                            + "tiene cargas relacionadas."
                    );

                } else if (resultado == -2) {

                    request.setAttribute(
                            "error",
                            "El atraque no existe o ya fue eliminado."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "Ocurrió un error al eliminar el atraque."
                    );
                }
            }

            // ACTUALIZAR LISTADO

            if (request.getParameter("guardar") != null
                    || request.getParameter("modificar") != null
                    || request.getParameter("eliminar") != null) {

                request.setAttribute(
                        "DatosAtraque",
                        atraqueDAO.MostrarAtraques()
                );
            }

            req = request.getRequestDispatcher("VistaAtraque.jsp");
            req.forward(request, response);

        }
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}