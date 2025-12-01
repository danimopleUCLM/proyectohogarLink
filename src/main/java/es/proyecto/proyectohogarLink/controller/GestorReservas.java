package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    // Inyectamos el otro Controller para usar su lógica de pago
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

    // --- REALIZAR RESERVA (Acción del Inquilino) ---
    @PostMapping("/realizarReserva")
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
            // Recuperamos entidades
            Inquilino inquilino = (Inquilino) usuario; // Viene de sesión
            // Nota: aquí deberíamos buscar el inmueble por ID, asumo que el DAO genérico lo permite
            // O usamos el EM directamente para no crear otro DAO en este ejemplo
            Inmueble inmueble = em.find(Inmueble.class, idInmueble);

            // 1. Verificar disponibilidad
            boolean esInmediata = disponibilidadDAO.permiteReservaDirectaEnPeriodo(inmueble.getId(), fechaInicio, fechaFin);

            // 2. Crear Reserva Base
            Reserva reserva = new Reserva();
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setInquilino(inquilino);
            reserva.setInmueble(inmueble);

            reservaDAO.saveEntity(reserva);

            // 3. Procesar Pago (Usando GestorPagos)
            Pago pago = new Pago();
            pago.setMetodoPago(metodoPago);
            pago.setReserva(reserva);
            gestorPagos.procesarPagoInterno(pago); // Llamada al otro controller

            // 4. Lógica bifurcada
            if (esInmediata) {
                model.addAttribute("mensaje", "¡Reserva Confirmada Inmediatamente!");
                return "reserva_exito"; // Espera reserva_exito.html
            } else {
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setConfirmada(false);
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "Solicitud enviada. Pendiente de aprobación.");
                return "reserva_pendiente"; // Espera reserva_pendiente.html
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error procesando reserva: " + e.getMessage());
            return "error";
        }
    }

    // --- GESTIÓN SOLICITUDES (Acción del Propietario) ---
    
    @GetMapping("/propietario/solicitudes")
    public String verSolicitudes(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(usuario instanceof Propietario)) return "redirect:/login";

        List<SolicitudReserva> pendientes = solicitudDAO.buscarPendientesPorPropietario(usuario.getId());
        model.addAttribute("solicitudes", pendientes);
        return "lista_solicitudes"; // Espera lista_solicitudes.html
    }

    @PostMapping("/propietario/gestionarSolicitud")
    public String gestionarSolicitud(@RequestParam Integer idSolicitud, 
                                     @RequestParam boolean aceptar) {
        initDaos();
        SolicitudReserva solicitud = solicitudDAO.selectEntity(idSolicitud);

        if (solicitud != null) {
            if (aceptar) {
                solicitud.setConfirmada(true);
                solicitudDAO.updateEntity(solicitud);
            } else {
                // Rechazo: Devolución de dinero y borrado
                // Aquí podrías llamar a gestorPagos.devolverDinero() si lo implementaras
                solicitudDAO.deleteEntity(idSolicitud);
                reservaDAO.deleteEntity(solicitud.getReserva().getId());
            }
        }
        return "redirect:/propietario/solicitudes";
    }
}

