package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.PagoDAO;
import es.proyecto.proyectohogarLink.entity.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class GestorPagos {

    @PersistenceContext
    private EntityManager em;
    private PagoDAO pagoDAO;

    /**
     * Este método NO es un endpoint web (@PostMapping), sino un método de lógica 
     * que será llamado desde GestorReservas. Cumple el requisito de ser Controller 
     * pero actúa como ayudante.
     */
    public Pago procesarPagoInterno(Pago datosPago) {
        if (pagoDAO == null) pagoDAO = new PagoDAO(em);

        // Lógica de negocio de pago (Simulación)
        // Generamos referencia única
        datosPago.setReferencia(UUID.randomUUID().toString());
        
        // Guardamos el pago en BD
        pagoDAO.saveEntity(datosPago);
        
        System.out.println("Pago realizado con éxito. Referencia: " + datosPago.getReferencia());
        return datosPago;
    }
}



