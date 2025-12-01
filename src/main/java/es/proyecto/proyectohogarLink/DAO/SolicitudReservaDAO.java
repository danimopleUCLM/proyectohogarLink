package es.proyecto.proyectohogarLink.DAO;

import es.proyecto.proyectohogarLink.entity.SolicitudReserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SolicitudReservaDAO extends AbstractEntityDAO<SolicitudReserva> {

    public SolicitudReservaDAO(EntityManager em) {
        super(em, SolicitudReserva.class);
    }

    /**
     * Método CRÍTICO para el panel del Propietario.
     * Busca solicitudes NO confirmadas (pendientes) de inmuebles que pertenecen al propietario logueado.
     * Requiere navegar: Solicitud -> Reserva -> Inmueble -> Propietario
     */
    public List<SolicitudReserva> buscarPendientesPorPropietario(int idPropietario) {
        String jpql = "SELECT s FROM SolicitudReserva s " +
                      "JOIN s.reserva r " +
                      "JOIN r.inmueble i " +
                      "WHERE i.propietario.id = :pid AND s.confirmada = false";
        
        TypedQuery<SolicitudReserva> query = em.createQuery(jpql, SolicitudReserva.class);
        query.setParameter("pid", idPropietario);
        
        return query.getResultList();
    }
    
    /**
     * Busca solicitudes confirmadas (Historial)
     */
    public List<SolicitudReserva> buscarConfirmadasPorPropietario(int idPropietario) {
        String jpql = "SELECT s FROM SolicitudReserva s " +
                      "JOIN s.reserva r " +
                      "JOIN r.inmueble i " +
                      "WHERE i.propietario.id = :pid AND s.confirmada = true";
        
        TypedQuery<SolicitudReserva> query = em.createQuery(jpql, SolicitudReserva.class);
        query.setParameter("pid", idPropietario);
        
        return query.getResultList();
    }
}