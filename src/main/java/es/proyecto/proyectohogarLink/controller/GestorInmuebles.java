package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Disponibilidad;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestorInmuebles {

    @PersistenceContext
    private EntityManager em;

    private InmuebleDAO inmuebleDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    public void darAltaInmueble(Inmueble inmueble) {
        initDaos();
        inmuebleDAO.saveEntity(inmueble);
    }

    public void agregarDisponibilidad(Disponibilidad disponibilidad) {
        initDaos();
        // Validar lógica de fechas (opcional: que inicio sea antes que fin)
        if (disponibilidad.getFechaInicio().isAfter(disponibilidad.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la de fin");
        }
        disponibilidadDAO.saveEntity(disponibilidad);
    }

    public List<Inmueble> obtenerInmueblesDePropietario(int idPropietario) {
        initDaos();
        return inmuebleDAO.findByPropietario(idPropietario);
    }
}




