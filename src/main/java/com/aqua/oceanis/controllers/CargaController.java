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
import java.math.BigDecimal;
import jakarta.servlet.http.HttpSession;
/**
 *
 * @author a503o
 */
@WebServlet(name = "CargaController", urlPatterns = {"/carga"})
public class CargaController extends HttpServlet {

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
            
            Carga carga;
            Atraque atraque;
            Cliente cliente;

            CargaDAO cargaDAO = new CargaDAO();
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

            // MOSTRAR CARGAS

            if (request.getParameter("mostrar") != null) {

                if (request.getParameter("texto") == null) {

                    request.setAttribute(
                            "DatosCarga",
                            cargaDAO.MostrarCargas()
                    );

                } else {

                    request.setAttribute(
                            "DatosCarga",
                            cargaDAO.BuscarCargas(
                                    request.getParameter("texto")
                            )
                    );
                }
            }

            // GUARDAR CARGA

            if (request.getParameter("guardar") != null) {

                atraque = new Atraque(
                        Integer.parseInt(request.getParameter("atraque")),
                        null, null, null, null, ""
                );

                cliente = new Cliente(
                        Integer.parseInt(request.getParameter("cliente")),
                        "", ""
                );

                carga = new Carga(
                        0,
                        atraque,
                        cliente,
                        request.getParameter("descripcion"),
                        new BigDecimal(request.getParameter("peso")),
                        request.getParameter("tipo")
                );

                if (cargaDAO.InsertarCarga(carga, idusuario, usuario)) {

                    request.setAttribute(
                            "mensaje",
                            "Carga registrada correctamente."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "No se pudo registrar la carga. "
                            + "Verifique que el atraque y el cliente estén activos."
                    );
                }
            }

            // MODIFICAR CARGA

            if (request.getParameter("modificar") != null) {

                atraque = new Atraque(
                        Integer.parseInt(request.getParameter("atraque")),
                        null, null, null, null, ""
                );

                cliente = new Cliente(
                        Integer.parseInt(request.getParameter("cliente")),
                        "", ""
                );

                carga = new Carga(
                        Integer.parseInt(request.getParameter("id")),
                        atraque,
                        cliente,
                        request.getParameter("descripcion"),
                        new BigDecimal(request.getParameter("peso")),
                        request.getParameter("tipo")
                );

                if (cargaDAO.ModificarCarga(carga, idusuario, usuario)) {

                    request.setAttribute(
                            "mensaje",
                            "Carga modificada correctamente."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "No se pudo modificar la carga. "
                            + "Verifique el atraque, el cliente y el peso."
                    );
                }
            }

            // ELIMINAR CARGA

            if (request.getParameter("eliminar") != null) {

                carga = new Carga(
                        Integer.parseInt(request.getParameter("id")),
                        null,
                        null,
                        "",
                        BigDecimal.ZERO,
                        ""
                );

                int resultado = cargaDAO.EliminarCarga(
                        carga, idusuario, usuario
                );

                if (resultado == 1) {

                    request.setAttribute(
                            "mensaje",
                            "Carga eliminada correctamente."
                    );

                } else if (resultado == -2) {

                    request.setAttribute(
                            "error",
                            "La carga no existe o ya fue eliminada."
                    );

                } else {

                    request.setAttribute(
                            "error",
                            "Ocurrió un error al eliminar la carga."
                    );
                }
            }

            // ACTUALIZAR LISTADO

            if (request.getParameter("guardar") != null
                    || request.getParameter("modificar") != null
                    || request.getParameter("eliminar") != null) {

                request.setAttribute(
                        "DatosCarga",
                        cargaDAO.MostrarCargas()
                );
            }

            req = request.getRequestDispatcher("VistaCarga.jsp");
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
