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

/**
 *
 * @author a503o
 */
@WebServlet(name = "AtraqueController", urlPatterns = {"/atraque"})
public class AtraqueController extends HttpServlet {

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
            Barco barco;
            Muelle muelle;
            Atraque atraque;
            BarcoDAO barcoDAO = new BarcoDAO();
            MuelleDAO muelleDAO = new MuelleDAO();
            AtraqueDAO atraqueDAO = new AtraqueDAO();
            RequestDispatcher req;

            if(request.getParameter("mostrar") != null) {
                if(request.getParameter("texto") == null)
                    request.setAttribute("DatosAtraque", atraqueDAO.MostrarAtraques());
                else
                    request.setAttribute("DatosAtraque", atraqueDAO.BuscarAtraques(request.getParameter("texto")));
            }

            if(request.getParameter("guardar") != null) {
                barco = new Barco(Integer.parseInt(request.getParameter("barco")), "", "", "");
                muelle = new Muelle(Integer.parseInt(request.getParameter("muelle")), null, "", "");

                atraque = new Atraque(1, barco, muelle,
                        LocalDateTime.parse(request.getParameter("fecha_llegada")),
                        request.getParameter("fecha_salida").isEmpty()
                            ? null : LocalDateTime.parse(request.getParameter("fecha_salida")),
                        request.getParameter("estado"));

                atraqueDAO.InsertarAtraque(atraque);
            }

            if(request.getParameter("modificar") != null) {
                barco = new Barco(Integer.parseInt(request.getParameter("barco")), "", "", "");
                muelle = new Muelle(Integer.parseInt(request.getParameter("muelle")), null, "", "");

                atraque = new Atraque(Integer.parseInt(request.getParameter("id")),
                        barco, muelle,
                        LocalDateTime.parse(request.getParameter("fecha_llegada")),
                        request.getParameter("fecha_salida").isEmpty()
                            ? null : LocalDateTime.parse(request.getParameter("fecha_salida")),
                        request.getParameter("estado"));

                atraqueDAO.ModificarAtraque(atraque);
            }

            if(request.getParameter("eliminar") != null) {
                atraque = new Atraque(Integer.parseInt(request.getParameter("id")),
                        null, null, null, null, "");
                atraqueDAO.EliminarAtraque(atraque);
            }

            req = request.getRequestDispatcher("VistaAtraque.jsp");
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
