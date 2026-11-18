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
import jakarta.servlet.http.HttpSession;

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

        // MOSTRAR MUELLES

        if (request.getParameter("mostrar") != null) {

            if (request.getParameter("texto") == null) {

                request.setAttribute(
                        "DatosMuelle",
                        muelleDAO.MostrarMuelles()
                );

            } else {

                request.setAttribute(
                        "DatosMuelle",
                        muelleDAO.BuscarMuelles(
                                request.getParameter("texto")
                        )
                );
            }
        }

        // GUARDAR MUELLE

        if (request.getParameter("guardar") != null) {

            puerto = new Puerto(
                    Integer.parseInt(request.getParameter("puerto")),
                    "", ""
            );

            muelle = new Muelle(
                    0,
                    puerto,
                    request.getParameter("nombre"),
                    request.getParameter("estado")
            );

            if (muelleDAO.InsertarMuelle(muelle, idusuario, usuario)) {

                request.setAttribute(
                        "mensaje",
                        "Muelle registrado correctamente."
                );

            } else {

                request.setAttribute(
                        "error",
                        "No se pudo registrar el muelle. "
                        + "Verifique que el puerto esté activo."
                );
            }
        }

        // MODIFICAR MUELLE

        if (request.getParameter("modificar") != null) {

            puerto = new Puerto(
                    Integer.parseInt(request.getParameter("puerto")),
                    "", ""
            );

            muelle = new Muelle(
                    Integer.parseInt(request.getParameter("id")),
                    puerto,
                    request.getParameter("nombre"),
                    request.getParameter("estado")
            );

            if (muelleDAO.ModificarMuelle(muelle, idusuario, usuario)) {

                request.setAttribute(
                        "mensaje",
                        "Muelle modificado correctamente."
                );

            } else {

                request.setAttribute(
                        "error",
                        "No se pudo modificar el muelle. "
                        + "Verifique el puerto seleccionado o "
                        + "si existen atraques relacionados."
                );
            }
        }

        // ELIMINAR MUELLE

        if (request.getParameter("eliminar") != null) {

            muelle = new Muelle(
                    Integer.parseInt(request.getParameter("id")),
                    null, "", ""
            );

            int resultado = muelleDAO.EliminarMuelle(
                    muelle, idusuario, usuario
            );

            if (resultado == 1) {

                request.setAttribute(
                        "mensaje",
                        "Muelle eliminado correctamente."
                );

            } else if (resultado == 0) {

                request.setAttribute(
                        "error",
                        "No se puede eliminar el muelle porque "
                        + "tiene atraques relacionados."
                );

            } else if (resultado == -2) {

                request.setAttribute(
                        "error",
                        "El muelle no existe o ya fue eliminado."
                );

            } else {

                request.setAttribute(
                        "error",
                        "Ocurrió un error al eliminar el muelle."
                );
            }
        }

        // ACTUALIZAR LISTADO DESPUÉS DE LAS OPERACIONES

        if (request.getParameter("guardar") != null
                || request.getParameter("modificar") != null
                || request.getParameter("eliminar") != null) {

            request.setAttribute(
                    "DatosMuelle",
                    muelleDAO.MostrarMuelles()
            );
        }

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