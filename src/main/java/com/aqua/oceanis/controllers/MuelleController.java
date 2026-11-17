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
/**
 * Controlador para gestionar los muelles.
 */
@WebServlet(name = "MuelleController", urlPatterns = {"/muelle"})
public class MuelleController extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // Inicializa los objetos y DAO
        Muelle muelle;
        Puerto puerto;
        MuelleDAO muelleDAO = new MuelleDAO();
        PuertoDAO puertoDAO = new PuertoDAO();
        RequestDispatcher req;

        // Muestra o busca los muelles registrados
        if(request.getParameter("mostrar") != null) {

            if(request.getParameter("texto") == null)
                request.setAttribute(
                    "DatosMuelle",
                    muelleDAO.MostrarMuelles()
                );
            else
                request.setAttribute(
                    "DatosMuelle",
                    muelleDAO.BuscarMuelles(
                        request.getParameter("texto")
                    )
                );
        }

        // Guarda un nuevo muelle
        if(request.getParameter("guardar") != null) {

            // CAMBIO: Recibe idpuerto desde FormMuelle.jsp
            int idpuerto = Integer.parseInt(
                request.getParameter("idpuerto")
            );

            // CAMBIO: Obtiene el puerto seleccionado
            puerto = puertoDAO.ObtenerPuertoPorId(idpuerto);

            // CAMBIO: Verifica que el puerto exista
            if(puerto == null) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El puerto seleccionado no existe."
                );
                return;
            }

            // Construye el nuevo muelle
            muelle = new Muelle(
                0,
                puerto,
                request.getParameter("nombre"),
                request.getParameter("estado")
            );

            // Inserta el registro
            int n = muelleDAO.InsertarMuelle(muelle);

            // CAMBIO: Comprueba si se guardó correctamente
            if(n > 0) {
                response.sendRedirect("muelle?mostrar");
            } else {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el muelle."
                );
            }
            return;
        }

        // Modifica un muelle existente
        if(request.getParameter("modificar") != null) {

            // CAMBIO: Recibe idpuerto desde FormMuelle.jsp
            int idpuerto = Integer.parseInt(
                request.getParameter("idpuerto")
            );

            // CAMBIO: Obtiene el puerto seleccionado
            puerto = puertoDAO.ObtenerPuertoPorId(idpuerto);

            // CAMBIO: Verifica que el puerto exista
            if(puerto == null) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El puerto seleccionado no existe."
                );
                return;
            }

            // Construye el muelle con los nuevos datos
            muelle = new Muelle(
                Integer.parseInt(request.getParameter("id")),
                puerto,
                request.getParameter("nombre"),
                request.getParameter("estado")
            );

            // Actualiza el registro
            int n = muelleDAO.ModificarMuelle(muelle);

            // CAMBIO: Comprueba si se modificó correctamente
            if(n > 0) {
                response.sendRedirect("muelle?mostrar");
            } else {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo modificar el muelle."
                );
            }
            return;
        }

        // Elimina un muelle mediante su ID
        if(request.getParameter("eliminar") != null) {

            muelle = new Muelle(
                Integer.parseInt(request.getParameter("id")),
                null, "", ""
            );

            muelleDAO.EliminarMuelle(muelle);

            // CAMBIO: Regresa al listado actualizado
            response.sendRedirect("muelle?mostrar");
            return;
        }

        // Envía los registros a la vista
        req = request.getRequestDispatcher("VistaMuelle.jsp");
        req.forward(request, response);
    }

    // Atiende las solicitudes GET
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    // Atiende las solicitudes POST
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    // Devuelve información del Servlet
    @Override
    public String getServletInfo() {
        return "Controlador de Muelles";
    }
}