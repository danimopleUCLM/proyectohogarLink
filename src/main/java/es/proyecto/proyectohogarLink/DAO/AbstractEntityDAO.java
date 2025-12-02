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
    public void saveEntity(T entity) {
        em.persist(entity);
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