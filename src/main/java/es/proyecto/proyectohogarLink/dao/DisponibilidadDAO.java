package es.proyecto.proyectohogarLink.dao;

import es.proyecto.proyectohogarLink.entity.Disponibilidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class DisponibilidadDAO extends AbstractEntityDAO<Disponibilidad> {

    public DisponibilidadDAO(EntityManager em) {
        // Inicializa el padre: AbstractEntityDAO<Disponibilidad>
        super(em, Disponibilidad.class);
    }

    // --- MÉTODOS ESPECÍFICOS DEL NEGOCIO ---

    /**
     * Recupera todos los periodos de disponibilidad dados de alta para un inmueble.
     * Útil para el panel de gestión del propietario.
     * @param inmuebleId El ID del inmueble.
     * @return Lista de periodos de Disponibilidad ordenados por fecha.
     */
    public List<Disponibilidad> findByInmuebleId(int inmuebleId) {
        TypedQuery<Disponibilidad> query = em.createQuery(
            "SELECT d FROM Disponibilidad d WHERE d.inmueble.id = :inmuebleId " +
            "ORDER BY d.fechaInicio", Disponibilidad.class);
        
        query.setParameter("inmuebleId", inmuebleId);
        return query.getResultList();
    }

    /**
     * Verifica si existe un periodo de disponibilidad activo (no solapado)
     * para el rango de fechas solicitado y si tiene el flag 'directa' activo.
     * * NOTA: Esta consulta garantiza que se encuentre UNA disponibilidad que contenga
     * o coincida con las fechas solicitadas Y que esté marcada como directa.
     * * @param inmuebleId El ID del inmueble.
     * @param fechaInicio Solicitada.
     * @param fechaFin Solicitada.
     * @return true si existe al menos una disponibilidad DIRECTA que cubra el periodo.
     */
    public boolean permiteReservaDirectaEnPeriodo(int inmuebleId, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            // Buscamos cuántas disponibilidades DIRECTAS cubren el rango solicitado
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(d) FROM Disponibilidad d " +
                "WHERE d.inmueble.id = :inmuebleId " +
                "AND d.directa = TRUE " + // Debe ser reserva directa
                "AND d.fechaInicio <= :fechaInicio " + // El inicio debe ser antes o igual al inicio solicitado
                "AND d.fechaFin >= :fechaFin", Long.class); // El fin debe ser después o igual al fin solicitado

            query.setParameter("inmuebleId", inmuebleId);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);

            return query.getSingleResult() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al verificar disponibilidad directa: " + e.getMessage());
            return false;
        }
    }
}