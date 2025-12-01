package es.proyecto.proyectohogarLink.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * Clase genérica para operaciones CRUD básicas.
 * T representa la Entidad (Usuario, Inmueble, etc.)
 */
public abstract class AbstractEntityDAO<T> {

    protected EntityManager em;
    private Class<T> entityClass;

    // Constructor: Necesitamos el EntityManager y la Clase específica para saber qué buscar
    public AbstractEntityDAO(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    /**
     * MÉTODO saveEntity (Create)
     * Guarda un nuevo objeto en la base de datos.
     */
    public void saveEntity(T entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity); // 'persist' es el comando JPA para INSERT
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar entidad", e);
        }
    }

    /**
     * MÉTODO selectEntity (Read)
     * Busca un objeto por su ID.
     */
    public T selectEntity(int id) {
        // 'find' es el comando JPA para SELECT * FROM tabla WHERE id = ?
        return em.find(entityClass, id);
    }

    /**
     * MÉTODO updateEntity (Update)
     * Actualiza un objeto existente.
     */
    public void updateEntity(T entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity); // 'merge' es el comando JPA para UPDATE
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar entidad", e);
        }
    }

    /**
     * MÉTODO deleteEntity (Delete)
     * Borra un objeto por su ID.
     */
    public void deleteEntity(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity); // 'remove' es el comando JPA para DELETE
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al borrar entidad", e);
        }
    }
}