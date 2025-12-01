package es.proyecto.proyectohogarLink.DAO;

import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

// Hereda de AbstractEntityDAO, pasándole la clase Inmueble
public class InmuebleDAO extends AbstractEntityDAO<Inmueble> {

    // El DAO necesita saber con qué entidad trabajar
    public InmuebleDAO(EntityManager em) {
        // Llama al constructor del padre: AbstractEntityDAO(em, Inmueble.class)
        super(em, Inmueble.class);
    }

    // --- MÉTODOS ESPECÍFICOS DEL NEGOCIO ---
    
    /**
     * Permite buscar inmuebles por dirección (destino) y filtrar por precio máximo
     * y la política de cancelación. 
     * @param destino Parte de la dirección a buscar.
     * @param precioMax Precio máximo por noche.
     * @param politicaCancelacion Filtro opcional por tipo de política.
     * @return Lista de Inmuebles que cumplen los criterios.
     */
    public List<Inmueble> buscarPorFiltros(String destino, Double precioMax, String politicaCancelacion) {
        
        StringBuilder jpql = new StringBuilder("SELECT i FROM Inmueble i WHERE 1=1");
        
        // 1. Filtrar por Destino (Dirección)
        if (destino != null && !destino.trim().isEmpty()) {
            jpql.append(" AND i.direccion LIKE :destino");
        }
        
        // 2. Filtrar por Precio
        if (precioMax != null && precioMax > 0) {
            jpql.append(" AND i.precioNoche <= :precioMax");
        }
        
        // 3. Filtrar por Política de Cancelación
        if (politicaCancelacion != null && !politicaCancelacion.trim().isEmpty()) {
            jpql.append(" AND i.politicaCancelacion = :politica");
        }
        
        TypedQuery<Inmueble> query = em.createQuery(jpql.toString(), Inmueble.class);
        
        // Asignar parámetros
        if (destino != null && !destino.trim().isEmpty()) {
            query.setParameter("destino", "%" + destino + "%");
        }
        if (precioMax != null && precioMax > 0) {
            query.setParameter("precioMax", precioMax);
        }
        if (politicaCancelacion != null && !politicaCancelacion.trim().isEmpty()) {
            // Aseguramos que el String coincida con el Enum (case-sensitive)
            query.setParameter("politica", politicaCancelacion); 
        }

        return query.getResultList();
    }
    
    /**
     * Recupera todos los inmuebles que pertenecen a un propietario específico.
     * Útil para el panel de gestión del Propietario.
     * @param propietarioId ID del Propietario.
     * @return Lista de Inmuebles.
     */
    public List<Inmueble> findByPropietario(int propietarioId) {
        TypedQuery<Inmueble> query = em.createQuery(
            "SELECT i FROM Inmueble i WHERE i.propietario.id = :propietarioId", 
            Inmueble.class);
        
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }
}