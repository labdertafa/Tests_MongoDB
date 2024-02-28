package com.laboratorio.crudmongo.controller;

import com.laboratorio.crudmongo.model.Articulo;
import com.laboratorio.crudmongo.repository.MongoConnection;
import com.mongodb.client.MongoCollection;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafa
 */
@WebServlet(name = "PersonasController", urlPatterns = {"/PersonasController"})
public class PersonasController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger("**PersonasController**");
    
    @Inject
    private MongoConnection mongoConnection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        MongoCollection<Articulo> articulos;
        
        try {
            articulos = (MongoCollection<Articulo>)mongoConnection.getCollection("redsocial", "articulos", Articulo.class);
        } catch (Exception e) {
            log.error("Ha sido imposible conectarse a la colección articulos. Finaliza el programa!");
            log.error("Error: " + e.getMessage());
            return;
        }
        log.info("Me he conectado a la colección: articulos");
        
        List<Articulo> lista2 = new ArrayList<>();
        articulos.find().into(lista2);
        
        log.info("Listado de artículos registrados");
        for (Articulo art: lista2) {
            log.info(art.toString());
        }
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Página principal de personas</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PersonasController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
