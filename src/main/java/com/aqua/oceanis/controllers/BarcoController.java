/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.aqua.oceanis.controllers;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.aqua.oceanis.models.*;

/**
 *
 * @author a503o
 */
public class BarcoController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            Barco barco;
            BarcoDAO barcoDAO = new BarcoDAO();
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

            // MOSTRAR BARCOS

            if (request.getParameter("mostrar") != null) {

                if (request.getParameter("texto") == null) {

                    request.setAttribute(
                            "DatosBarco",
                            barcoDAO.MostrarBarcos()
                    );

                } else {

                    request.setAttribute(
                            "DatosBarco",
                            barcoDAO.BuscarBarcos(
                                    request.getParameter("texto")
                            )
                    );
                }
            }

            // GUARDAR BARCO

            if (request.getParameter("guardar") != null) {

                barco = new Barco(
                        0,
                        request.getParameter("nombre"),
                        request.getParameter("tipo"),
                        request.getParameter("pais")
                );

                if (barcoDAO.InsertarBarco(barco, idusuario, usuario)) {

                    request.setAttribute(
                            "mensaje",
                            "Barco registrado correctamente."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "No se pudo registrar el barco."
                    );
                }
            }

            // MODIFICAR BARCO

            if (request.getParameter("modificar") != null) {

                barco = new Barco(
                        Integer.parseInt(request.getParameter("id")),
                        request.getParameter("nombre"),
                        request.getParameter("tipo"),
                        request.getParameter("pais")
                );

                if (barcoDAO.ModificarBarco(barco, idusuario, usuario)) {

                    request.setAttribute(
                            "mensaje",
                            "Barco modificado correctamente."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "No se pudo modificar el barco."
                    );
                }
            }

            // ELIMINAR BARCO

            if (request.getParameter("eliminar") != null) {

                barco = new Barco(
                        Integer.parseInt(request.getParameter("id")),
                        "", "", ""
                );

                int resultado = barcoDAO.EliminarBarco(
                        barco, idusuario, usuario
                );

                if (resultado == 1) {

                    request.setAttribute(
                            "mensaje",
                            "Barco eliminado correctamente."
                    );

                } else if (resultado == 0) {

                    request.setAttribute(
                            "error",
                            "No se puede eliminar el barco porque tiene atraques relacionados."
                    );

                } else if (resultado == -2) {

                    request.setAttribute(
                            "error",
                            "El barco no existe o ya fue eliminado."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "Ocurrió un error al eliminar el barco."
                    );
                }
            }

            // ACTUALIZAR LOS REGISTROS

            if (request.getParameter("guardar") != null
                    || request.getParameter("modificar") != null
                    || request.getParameter("eliminar") != null) {

                request.setAttribute(
                        "DatosBarco",
                        barcoDAO.MostrarBarcos()
                );
            }

            req = request.getRequestDispatcher("VistaBarco.jsp");
            req.forward(request, response);

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
