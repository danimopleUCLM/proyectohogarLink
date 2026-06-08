package es.proyecto.proyectohogarLink.dao;

import es.proyecto.proyectohogarLink.entity.SolicitudReserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SolicitudReservaDAO extends AbstractEntityDAO<SolicitudReserva> {

    public SolicitudReservaDAO(EntityManager em) {
        super(em, SolicitudReserva.class);
    }

    // Para el PROPIETARIO: Solo ver las que tiene que gestionar (Pendientes)
    public List<SolicitudReserva> buscarPendientesPorPropietario(int idPropietario) {
        String jpql = "SELECT s FROM SolicitudReserva s " +
                      "JOIN s.reserva r " +
                      "JOIN r.inmueble i " +
                      "WHERE i.propietario.id = :pid AND s.estado = 'PENDIENTE'";
        
        TypedQuery<SolicitudReserva> query = em.createQuery(jpql, SolicitudReserva.class);
        query.setParameter("pid", idPropietario);
        return query.getResultList();
    }
    
    // Para el INQUILINO: Ver su historial completo (Buzón de notificaciones)
    public List<SolicitudReserva> buscarTodasPorInquilino(int idInquilino) {
        String jpql = "SELECT s FROM SolicitudReserva s " +
                      "JOIN s.reserva r " +
                      "WHERE r.inquilino.id = :iid " +
                      "ORDER BY s.id DESC"; // Las más recientes primero
        
        TypedQuery<SolicitudReserva> query = em.createQuery(jpql, SolicitudReserva.class);
        query.setParameter("iid", idInquilino);
        return query.getResultList();
    }
}