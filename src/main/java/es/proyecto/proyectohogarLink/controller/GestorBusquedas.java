package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GestorBusquedas {

    @PersistenceContext
    private EntityManager em;
    private InmuebleDAO inmuebleDAO;

    public List<Inmueble> buscarInmuebles(String destino, Double precioMax, String politicaCancelacion) {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        
        return inmuebleDAO.buscarPorFiltros(destino, precioMax, politicaCancelacion);
    }
    
    public Inmueble obtenerDetalleInmueble(int id) {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        return inmuebleDAO.selectEntity(id);
    }
}




