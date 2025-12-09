package es.proyecto.proyectohogarLink.DAO;

import es.proyecto.proyectohogarLink.entity.ListaDeseos;
import es.proyecto.proyectohogarLink.entity.ListaDeseosId;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ListaDeseosDAO extends AbstractEntityDAO<ListaDeseos> {

    public ListaDeseosDAO(EntityManager em) {
        super(em, ListaDeseos.class);
    }

    public ListaDeseos find(Integer inquilinoId, Integer inmuebleId) {
        ListaDeseosId id = new ListaDeseosId(inquilinoId, inmuebleId);
        return em.find(ListaDeseos.class, id);
    }

    public List<ListaDeseos> findByUsuario(Integer userId) {
        return em.createQuery(
                "SELECT l FROM ListaDeseos l WHERE l.inquilino.id = :id",
                ListaDeseos.class
        ).setParameter("id", userId).getResultList();
    }
}
