package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.PagoDAO;
import es.proyecto.proyectohogarLink.entity.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GestorPagos {

    @PersistenceContext
    private EntityManager em;
    private PagoDAO pagoDAO;

    public Pago procesarPago(Pago datosPago) {
        if (pagoDAO == null) pagoDAO = new PagoDAO(em);

        // Simulamos conexión con banco...
        // Generamos referencia única
        datosPago.setReferencia(UUID.randomUUID().toString());
        
        // Guardamos el pago
        pagoDAO.saveEntity(datosPago);
        
        System.out.println("Pago realizado con éxito. Referencia: " + datosPago.getReferencia());
        return datosPago;
    }
}




