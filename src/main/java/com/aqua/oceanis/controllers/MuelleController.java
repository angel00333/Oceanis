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

/**
 *
 * @author a503o
 */
@WebServlet(name = "MuelleController", urlPatterns = {"/muelle"})
public class MuelleController extends HttpServlet {

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
            Muelle muelle;
            Puerto puerto;
            MuelleDAO muelleDAO = new MuelleDAO();
            PuertoDAO puertoDAO = new PuertoDAO();
            RequestDispatcher req;

            if(request.getParameter("mostrar") != null) {
                if(request.getParameter("texto") == null)
                    request.setAttribute("DatosMuelle", muelleDAO.MostrarMuelles());
                else
                    request.setAttribute("DatosMuelle", muelleDAO.BuscarMuelles(request.getParameter("texto")));
            }

            if(request.getParameter("guardar") != null) {
                puerto = new Puerto(Integer.parseInt(request.getParameter("puerto")), "", "");
                muelle = new Muelle(1, puerto, request.getParameter("nombre"),
                        request.getParameter("estado"));
                muelleDAO.InsertarMuelle(muelle);
            }

            if(request.getParameter("modificar") != null) {
                puerto = new Puerto(Integer.parseInt(request.getParameter("puerto")), "", "");
                muelle = new Muelle(Integer.parseInt(request.getParameter("id")),
                        puerto, request.getParameter("nombre"),
                        request.getParameter("estado"));
                muelleDAO.ModificarMuelle(muelle);
            }

            if(request.getParameter("eliminar") != null) {
                muelle = new Muelle(Integer.parseInt(request.getParameter("id")),
                        null, "", "");
                muelleDAO.EliminarMuelle(muelle);
            }

            req = request.getRequestDispatcher("VistaMuelle.jsp");
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
