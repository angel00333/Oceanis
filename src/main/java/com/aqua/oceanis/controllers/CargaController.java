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
            AtraqueDAO atraqueDAO = new AtraqueDAO();
            ClienteDAO clienteDAO = new ClienteDAO();
            RequestDispatcher req;

            if(request.getParameter("mostrar") != null) {
                if(request.getParameter("texto") == null)
                    request.setAttribute("DatosCarga", cargaDAO.MostrarCargas());
                else
                    request.setAttribute("DatosCarga", cargaDAO.BuscarCargas(request.getParameter("texto")));
            }

             // Guarda la carga con sus relaciones maestro-detalle
            if(request.getParameter("guardar") != null) {
                // CAMBIO: Obtiene los objetos maestros seleccionados
                atraque = atraqueDAO.ObtenerAtraquePorId(Integer.parseInt(request.getParameter("idatraque")));
                cliente = clienteDAO.ObtenerClientePorId(Integer.parseInt(request.getParameter("idcliente")));

                // CAMBIO: Verifica que existan los registros
                if(atraque == null || cliente == null) {
                    response.sendError(400, "El atraque o cliente seleccionado no existe.");
                    return;
                }

                carga = new Carga(0, atraque, cliente,
                        request.getParameter("descripcion"),
                        new BigDecimal(request.getParameter("peso")),
                        request.getParameter("tipo"));

                // CAMBIO: Verifica que se haya guardado
                if(cargaDAO.InsertarCarga(carga) > 0)
                    response.sendRedirect("carga?mostrar");
                else
                    response.sendError(500, "No se pudo guardar la carga.");
                return;
            }

           
            // Modifica una carga existente
            if(request.getParameter("modificar") != null) {
                // CAMBIO: Obtiene los objetos maestros seleccionados
                atraque = atraqueDAO.ObtenerAtraquePorId(Integer.parseInt(request.getParameter("idatraque")));
                cliente = clienteDAO.ObtenerClientePorId(Integer.parseInt(request.getParameter("idcliente")));

                if(atraque == null || cliente == null) {
                    response.sendError(400, "El atraque o cliente seleccionado no existe.");
                    return;
                }

                carga = new Carga(Integer.parseInt(request.getParameter("id")),
                        atraque, cliente,
                        request.getParameter("descripcion"),
                        new BigDecimal(request.getParameter("peso")),
                        request.getParameter("tipo"));

                // CAMBIO: Verifica que se haya modificado
                if(cargaDAO.ModificarCarga(carga) > 0)
                    response.sendRedirect("carga?mostrar");
                else
                    response.sendError(500, "No se pudo modificar la carga.");
                return;
            }

                        if(request.getParameter("eliminar") != null) {
                carga = new Carga(Integer.parseInt(request.getParameter("id")),
                        null, null, "", BigDecimal.ZERO, "");

                // CAMBIO: Elimina y verifica el resultado
                if(cargaDAO.EliminarCarga(carga) > 0)
                    response.sendRedirect("carga?mostrar");
                else
                    response.sendError(500, "No se pudo eliminar la carga.");
                return;
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
