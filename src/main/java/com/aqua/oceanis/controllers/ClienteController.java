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
import com.aqua.oceanis.models.*;

/**
 *
 * @author a503o
 */
public class ClienteController extends HttpServlet {

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
        Cliente cliente;
        ClienteDAO clienteDAO = new ClienteDAO();
        RequestDispatcher req;

        if(request.getParameter("mostrar") != null) {
            if(request.getParameter("texto") == null)
                request.setAttribute("DatosCliente", clienteDAO.MostrarClientes());
            else
                request.setAttribute("DatosCliente", clienteDAO.BuscarClientes(request.getParameter("texto")));
        }

        if(request.getParameter("guardar") != null) {
            cliente = new Cliente(1, request.getParameter("nombre"), request.getParameter("telefono"));
            clienteDAO.InsertarCliente(cliente);
        }

        if(request.getParameter("eliminar") != null) {
            cliente = new Cliente(Integer.parseInt(request.getParameter("id")), "", "");
            clienteDAO.EliminarCliente(cliente);
        }

        if(request.getParameter("modificar") != null) {
            cliente = new Cliente(Integer.parseInt(request.getParameter("id")), request.getParameter("nombre"), request.getParameter("telefono"));
            clienteDAO.ModificarCliente(cliente);
        }

        req = request.getRequestDispatcher("VistaCliente.jsp");
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
