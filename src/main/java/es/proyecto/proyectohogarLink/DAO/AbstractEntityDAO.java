package es.proyecto.proyectohogarLink.DAO;

import jakarta.persistence.EntityManager;


public abstract class AbstractEntityDAO<T> {

    protected EntityManager em;
    private Class<T> entityClass;

    public AbstractEntityDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    /**
     * SAVE (CREATE)
     * Delegamos la transacción a Spring (@Transactional en el Controller)
     */
    public void saveEntity(T entity) {
        em.persist(entity);
    }

    /**
     * SELECT (READ)
     */
    public T selectEntity(int id) {
        return em.find(entityClass, id);
    }

    /**
     * UPDATE
     */
    public void updateEntity(T entity) {
        em.merge(entity);
    }

    /**
     * DELETE
     */
    public void deleteEntity(int id) {
        T entity = em.find(entityClass, id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}