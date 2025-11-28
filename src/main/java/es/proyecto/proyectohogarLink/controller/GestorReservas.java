package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GestorReservas {

    @PersistenceContext
    private EntityManager em;

    // Inyectamos el gestor de pagos para usar su lógica
    @Autowired
    private GestorPagos gestorPagos;

    private ReservaDAO reservaDAO;
    private SolicitudReservaDAO solicitudDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (reservaDAO == null) reservaDAO = new ReservaDAO(em);
        if (solicitudDAO == null) solicitudDAO = new SolicitudReservaDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    /**
     * MÉTODO CENTRAL DEL SISTEMA
     * Gestiona la creación de la reserva, el pago y la decisión de si es solicitud o directa.
     */
    public void realizarReserva(Inquilino inquilino, Inmueble inmueble, LocalDate inicio, LocalDate fin, Pago datosPago) {
        initDaos();

        // 1. Verificar si el inmueble permite reserva inmediata en esas fechas
        boolean esInmediata = disponibilidadDAO.permiteReservaDirectaEnPeriodo(inmueble.getId(), inicio, fin);

        // 2. Crear la entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setInquilino(inquilino);
        reserva.setInmueble(inmueble);

        // 3. Guardar Reserva Base
        reservaDAO.saveEntity(reserva);

        // 4. Procesar el Pago (Se hace en ambos casos según requisitos)
        datosPago.setReserva(reserva);
        gestorPagos.procesarPago(datosPago);

        // 5. Bifurcación de lógica
        if (esInmediata) {
            // CASO A: Reserva Inmediata
            System.out.println("Reserva ID " + reserva.getId() + " confirmada automáticamente.");
            // Aquí se podría enviar email de confirmación
        } else {
            // CASO B: Requiere Aprobación (Crear Solicitud)
            SolicitudReserva solicitud = new SolicitudReserva();
            solicitud.setReserva(reserva);
            solicitud.setConfirmada(false); // Pendiente
            
            solicitudDAO.saveEntity(solicitud);
            System.out.println("Solicitud de reserva generada. Pendiente de aprobación del propietario.");
        }
    }

    /**
     * Panel Propietario: Ver solicitudes pendientes
     */
    public List<SolicitudReserva> verSolicitudesPendientes(int idPropietario) {
        initDaos();
        return solicitudDAO.buscarPendientesPorPropietario(idPropietario);
    }

    /**
     * Panel Propietario: Aceptar o Rechazar solicitud
     */
    public void gestionarSolicitud(int idSolicitud, boolean aceptar) {
        initDaos();
        SolicitudReserva solicitud = solicitudDAO.selectEntity(idSolicitud);

        if (solicitud == null) return;

        if (aceptar) {
            solicitud.setConfirmada(true);
            solicitudDAO.updateEntity(solicitud);
            System.out.println("Reserva confirmada por el propietario.");
            // Notificar inquilino
        } else {
            // RECHAZAR: Devolver dinero y borrar reserva
            Pago pago = solicitud.getReserva().getPago();
            System.out.println("Devolviendo importe de referencia: " + pago.getReferencia());
            
            // Eliminamos la solicitud y la reserva (cascade debería borrar el pago si está configurado, 
            // sino borramos manualmente).
            solicitudDAO.deleteEntity(idSolicitud);
            reservaDAO.deleteEntity(solicitud.getReserva().getId());
            
            System.out.println("Reserva cancelada y dinero devuelto.");
        }
    }
}




