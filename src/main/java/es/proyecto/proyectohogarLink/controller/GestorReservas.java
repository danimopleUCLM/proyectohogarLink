package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTANTE
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GestorReservas {

    @PersistenceContext
    private EntityManager em;

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

    @PostMapping("/realizarReserva")
    @Transactional // <--- NUEVO: Todo esto ocurre en una sola transacción
    public String realizarReserva(
            @RequestParam Integer idInmueble,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam MetodoPago metodoPago,
            HttpSession session,
            Model model) {

        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(usuario instanceof Inquilino)) return "redirect:/login";

        try {
            Inquilino inquilino = (Inquilino) usuario;
            Inmueble inmueble = em.find(Inmueble.class, idInmueble);

            boolean esInmediata = disponibilidadDAO.permiteReservaDirectaEnPeriodo(inmueble.getId(), fechaInicio, fechaFin);

            Reserva reserva = new Reserva();
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setInquilino(inquilino);
            reserva.setInmueble(inmueble);

            reservaDAO.saveEntity(reserva);

            Pago pago = new Pago();
            pago.setMetodoPago(metodoPago);
            pago.setReserva(reserva);
            gestorPagos.procesarPagoInterno(pago);

            if (esInmediata) {
                model.addAttribute("mensaje", "¡Reserva Confirmada Inmediatamente!");
                return "reserva_exito";
            } else {
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setConfirmada(false);
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "Solicitud enviada. Pendiente de aprobación.");
                return "reserva_pendiente";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error procesando reserva: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/propietario/solicitudes")
    public String verSolicitudes(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(usuario instanceof Propietario)) return "redirect:/login";

        List<SolicitudReserva> pendientes = solicitudDAO.buscarPendientesPorPropietario(usuario.getId());
        model.addAttribute("solicitudes", pendientes);
        return "lista_solicitudes";
    }

    @PostMapping("/propietario/gestionarSolicitud")
    @Transactional // <--- NUEVO
    public String gestionarSolicitud(@RequestParam Integer idSolicitud, 
                                     @RequestParam boolean aceptar) {
        initDaos();
        SolicitudReserva solicitud = solicitudDAO.selectEntity(idSolicitud);

        if (solicitud != null) {
            if (aceptar) {
                solicitud.setConfirmada(true);
                solicitudDAO.updateEntity(solicitud);
            } else {
                solicitudDAO.deleteEntity(idSolicitud);
                reservaDAO.deleteEntity(solicitud.getReserva().getId());
            }
        }
        return "redirect:/propietario/solicitudes";
    }
}