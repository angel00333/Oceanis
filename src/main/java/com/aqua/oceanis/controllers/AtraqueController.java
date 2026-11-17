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

            // Muestra o busca los atraques
            if(request.getParameter("mostrar") != null) {
                if(request.getParameter("texto") == null)
                    request.setAttribute("DatosAtraque", atraqueDAO.MostrarAtraques());
                else
                    request.setAttribute("DatosAtraque", atraqueDAO.BuscarAtraques(request.getParameter("texto")));
            }

            // Guarda un atraque con barco y muelle asociados
            if(request.getParameter("guardar") != null) {
                // CAMBIO: Obtiene los objetos maestros según los ID recibidos
                barco = barcoDAO.ObtenerBarcoPorId(Integer.parseInt(request.getParameter("idbarco")));
                muelle = muelleDAO.ObtenerMuellePorId(Integer.parseInt(request.getParameter("idmuelle")));

                // CAMBIO: Valida que existan ambos registros
                if(barco == null || muelle == null) {
                    response.sendError(400, "El barco o muelle seleccionado no existe.");
                    return;
                }

                atraque = new Atraque(0, barco, muelle,
                        LocalDateTime.parse(request.getParameter("fecha_llegada")),
                        request.getParameter("fecha_salida").isEmpty()
                            ? null : LocalDateTime.parse(request.getParameter("fecha_salida")),
                        request.getParameter("estado"));

                atraqueDAO.InsertarAtraque(atraque);
                // CAMBIO: Regresa al listado actualizado
                response.sendRedirect("atraque?mostrar");
                return;
            }

            // Modifica un atraque existente
            if(request.getParameter("modificar") != null) {
                // CAMBIO: Obtiene los objetos maestros según los ID recibidos
                barco = barcoDAO.ObtenerBarcoPorId(Integer.parseInt(request.getParameter("idbarco")));
                muelle = muelleDAO.ObtenerMuellePorId(Integer.parseInt(request.getParameter("idmuelle")));

                if(barco == null || muelle == null) {
                    response.sendError(400, "El barco o muelle seleccionado no existe.");
                    return;
                }

                atraque = new Atraque(Integer.parseInt(request.getParameter("id")),
                        barco, muelle,
                        LocalDateTime.parse(request.getParameter("fecha_llegada")),
                        request.getParameter("fecha_salida").isEmpty()
                            ? null : LocalDateTime.parse(request.getParameter("fecha_salida")),
                        request.getParameter("estado"));

                atraqueDAO.ModificarAtraque(atraque);
                // CAMBIO: Regresa al listado actualizado
                response.sendRedirect("atraque?mostrar");
                return;
            }

            // Elimina un atraque
            if(request.getParameter("eliminar") != null) {
                atraque = new Atraque(Integer.parseInt(request.getParameter("id")),
                        null, null, null, null, "");
                atraqueDAO.EliminarAtraque(atraque);

                // CAMBIO: Actualiza el listado
                response.sendRedirect("atraque?mostrar");
                return;
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