package es.proyecto.proyectohogarLink.DAO;

import jakarta.persistence.EntityManager;

/**
 * T representa la Entidad (Usuario, Inmueble, etc.)
 */
public abstract class AbstractEntityDAO<T> {

    protected EntityManager em;
    private Class<T> entityClass;

    // Constructor
    public AbstractEntityDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    /**
     * MÉTODO saveEntity (Create)
     * SOLO persist. La transacción la maneja Spring (@Transactional).
     */
// AbstractEntityDAO.java (SOLUCIÓN RECOMENDADA)
/**
 * MÉTODO saveEntity (Create y Update/Attach)
 */
    public T saveEntity(T entity) {
    if (entity != null) {
        // Usa merge() para manejar correctamente entidades Transitorias (nuevas) 
        // y Detached (existentes de la sesión o con ID asignado).
        // También maneja la cascada de persistencia correctamente.
        return em.merge(entity);
    }
    return null; // O lanza una excepción si entity es nulo
    }

    /**
     * MÉTODO selectEntity (Read)
     */
    public T selectEntity(int id) {
        return em.find(entityClass, id);
    }

    /**
     * MÉTODO updateEntity (Update)
     * SOLO merge.
     */
    public void updateEntity(T entity) {
        em.merge(entity);
    }

    /**
     * MÉTODO deleteEntity (Delete)
     * SOLO remove.
     */
    public void deleteEntity(int id) {
        T entity = em.find(entityClass, id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}