package es.proyecto.proyectohogarLink.DAO;

import es.proyecto.proyectohogarLink.entity.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class PagoDAO extends AbstractEntityDAO<Pago> {

    public PagoDAO(EntityManager em) {
        super(em, Pago.class);
    }

    /**
     * Busca un pago por su referencia única (UUID o código de transacción).
     */
    public Pago buscarPorReferencia(String referencia) {
        try {
            TypedQuery<Pago> query = em.createQuery(
                "SELECT p FROM Pago p WHERE p.referencia = :ref", 
                Pago.class);
            query.setParameter("ref", referencia);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // No existe pago con esa referencia
        }
    }
}