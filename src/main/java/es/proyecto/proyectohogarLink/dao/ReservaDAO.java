package es.proyecto.proyectohogarLink.dao;

import es.proyecto.proyectohogarLink.entity.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ReservaDAO extends AbstractEntityDAO<Reserva> {

    public ReservaDAO(EntityManager em) {
        super(em, Reserva.class);
    }

    /**
     * Devuelve todas las reservas hechas por un inquilino específico.
     */
    public List<Reserva> buscarPorInquilino(int idInquilino) {
        String jpql = "SELECT r FROM Reserva r WHERE r.inquilino.id = :id";
        TypedQuery<Reserva> query = em.createQuery(jpql, Reserva.class);
        query.setParameter("id", idInquilino);
        return query.getResultList();
    }

    /**
     * Devuelve todas las reservas asociadas a un inmueble concreto.
     */
    public List<Reserva> buscarPorInmueble(int idInmueble) {
        String jpql = "SELECT r FROM Reserva r WHERE r.inmueble.id = :id";
        TypedQuery<Reserva> query = em.createQuery(jpql, Reserva.class);
        query.setParameter("id", idInmueble);
        return query.getResultList();
    }
}