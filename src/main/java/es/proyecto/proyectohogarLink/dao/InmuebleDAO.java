package es.proyecto.proyectohogarLink.dao;

import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class InmuebleDAO extends AbstractEntityDAO<Inmueble> {

    public InmuebleDAO(EntityManager em) {
        super(em, Inmueble.class);
    }

    public List<Inmueble> buscarPorFiltros(String destino, LocalDate fechaLlegada, LocalDate fechaSalida, Integer viajeros) {
        
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT i FROM Inmueble i WHERE 1=1");
        
        // 1. Filtrar por Destino
        if (destino != null && !destino.trim().isEmpty()) {
            jpql.append(" AND LOWER(i.direccion) LIKE LOWER(:destino)");
        }
        
        // 2. Filtrar por Fechas
        if (fechaLlegada != null && fechaSalida != null) {
            jpql.append(" AND EXISTS (SELECT d FROM Disponibilidad d WHERE d.inmueble = i ")
                .append(" AND d.fechaInicio <= :fechaLlegada AND d.fechaFin >= :fechaSalida)");
        }

        // 3. Filtrar por Número de Viajeros (Ya activado)
        if (viajeros != null && viajeros > 0) {
            jpql.append(" AND i.capacidad >= :viajeros");
        }
        
        TypedQuery<Inmueble> query = em.createQuery(jpql.toString(), Inmueble.class);
        
        // Asignar parámetros
        if (destino != null && !destino.trim().isEmpty()) {
            query.setParameter("destino", "%" + destino + "%");
        }
        
        if (fechaLlegada != null && fechaSalida != null) {
            query.setParameter("fechaLlegada", fechaLlegada);
            query.setParameter("fechaSalida", fechaSalida);
        }

        if (viajeros != null && viajeros > 0) {
            query.setParameter("viajeros", viajeros);
        }

        return query.getResultList();
    }
    
    public List<Inmueble> findByPropietario(int propietarioId) {
        TypedQuery<Inmueble> query = em.createQuery(
            "SELECT i FROM Inmueble i WHERE i.propietario.id = :propietarioId", 
            Inmueble.class);
        
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }
}