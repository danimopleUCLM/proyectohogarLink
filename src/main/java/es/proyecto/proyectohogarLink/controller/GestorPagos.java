package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.PagoDAO;
import es.proyecto.proyectohogarLink.entity.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTANTE

import java.util.UUID;

@Controller
public class GestorPagos {

    @PersistenceContext
    private EntityManager em;
    private PagoDAO pagoDAO;

    @Transactional // <--- NUEVO
    public Pago procesarPagoInterno(Pago datosPago) {
        if (pagoDAO == null) pagoDAO = new PagoDAO(em);

        datosPago.setReferencia(UUID.randomUUID().toString());
        pagoDAO.saveEntity(datosPago);
        
        System.out.println("Pago realizado con éxito. Referencia: " + datosPago.getReferencia());
        return datosPago;
    }
}