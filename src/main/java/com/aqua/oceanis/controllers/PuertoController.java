/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.aqua.oceanis.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.aqua.oceanis.models.*;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author a503o
 */
public class PuertoController extends HttpServlet {

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
            
        Puerto puerto;
        PuertoDAO puertoDAO = new PuertoDAO();
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

        // MOSTRAR PUERTOS

        if (request.getParameter("mostrar") != null) {

            if (request.getParameter("texto") == null) {

                request.setAttribute(
                        "DatosPuerto",
                        puertoDAO.MostrarPuertos()
                );

            } else {

                request.setAttribute(
                        "DatosPuerto",
                        puertoDAO.BuscarPuertos(
                                request.getParameter("texto")
                        )
                );
            }
        }

        // GUARDAR PUERTO

        if (request.getParameter("guardar") != null) {

            puerto = new Puerto(
                    0,
                    request.getParameter("nombre"),
                    request.getParameter("direccion")
            );

            if (puertoDAO.InsertarPuerto(puerto, idusuario, usuario)) {

                request.setAttribute(
                        "mensaje",
                        "Puerto registrado correctamente."
                );

            } else {

                request.setAttribute(
                        "error",
                        "No se pudo registrar el puerto."
                );
            }
        }

        // MODIFICAR PUERTO

        if (request.getParameter("modificar") != null) {

            puerto = new Puerto(
                    Integer.parseInt(request.getParameter("id")),
                    request.getParameter("nombre"),
                    request.getParameter("direccion")
            );

            if (puertoDAO.ModificarPuerto(puerto, idusuario, usuario)) {

                request.setAttribute(
                        "mensaje",
                        "Puerto modificado correctamente."
                );

            } else {

                request.setAttribute(
                        "error",
                        "No se pudo modificar el puerto."
                );
            }
        }

        // ELIMINAR PUERTO

        if (request.getParameter("eliminar") != null) {

            puerto = new Puerto(
                    Integer.parseInt(request.getParameter("id")),
                    "", ""
            );

            int resultado = puertoDAO.EliminarPuerto(
                    puerto, idusuario, usuario
            );

            if (resultado == 1) {

                request.setAttribute(
                        "mensaje",
                        "Puerto eliminado correctamente."
                );

            } else if (resultado == 0) {

                request.setAttribute(
                        "error",
                        "No se puede eliminar el puerto porque tiene muelles relacionados."
                );

            } else if (resultado == -2) {

                request.setAttribute(
                        "error",
                        "El puerto no existe o ya fue eliminado."
                );

            } else {

                request.setAttribute(
                        "error",
                        "Ocurrió un error al eliminar el puerto."
                );
            }
        }

        // ACTUALIZAR LISTADO DESPUÉS DE LAS OPERACIONES

        if (request.getParameter("guardar") != null
                || request.getParameter("modificar") != null
                || request.getParameter("eliminar") != null) {

            request.setAttribute(
                    "DatosPuerto",
                    puertoDAO.MostrarPuertos()
            );
        }

        req = request.getRequestDispatcher("VistaPuerto.jsp");
        req.forward(request, response);

 }
}// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
