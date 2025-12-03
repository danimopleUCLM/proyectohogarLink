package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList; // Importante para listas vacías
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

    // --- REALIZAR RESERVA ---
    @PostMapping("/realizarReserva")
    @Transactional
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
                
                System.out.println(">>> DEBUG: Solicitud creada con ID Reserva: " + reserva.getId());
                
                model.addAttribute("mensaje", "Solicitud enviada. Pendiente de aprobación.");
                return "reserva_pendiente";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error procesando reserva: " + e.getMessage());
            return "error";
        }
    }

    // --- GESTIÓN SOLICITUDES (CORREGIDO CON LOGS) ---
    @GetMapping("/propietario/solicitudes")
    public String verSolicitudes(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(usuario instanceof Propietario)) return "redirect:/login";

        List<SolicitudReserva> pendientes = solicitudDAO.buscarPendientesPorPropietario(usuario.getId());
        
        // --- BLOQUE DE DEPURACIÓN (MIRA LA CONSOLA AL ENTRAR AQUÍ) ---
        if (pendientes == null) {
            System.out.println(">>> DEBUG: La lista de pendientes es NULL");
            pendientes = new ArrayList<>();
        } else {
            System.out.println(">>> DEBUG: Tamaño de lista recuperada: " + pendientes.size());
            for (int i = 0; i < pendientes.size(); i++) {
                SolicitudReserva s = pendientes.get(i);
                if (s == null) {
                    System.out.println(">>> DEBUG: Elemento [" + i + "] es NULL (Error de base de datos)");
                } else {
                    System.out.println(">>> DEBUG: Elemento [" + i + "] ID Solicitud: " + s.getId());
                    System.out.println(">>> DEBUG:    -> Reserva asociada: " + (s.getReserva() != null ? "ID " + s.getReserva().getId() : "NULL"));
                }
            }
        }
        // -------------------------------------------------------------

        model.addAttribute("solicitudes", pendientes);
        return "lista_solicitudes";
    }

    @PostMapping("/propietario/gestionarSolicitud")
    @Transactional
    public String gestionarSolicitud(@RequestParam Integer idSolicitud, 
                                     @RequestParam boolean aceptar) {
        initDaos();
        // Añadida validación extra por si idSolicitud llega nulo
        if(idSolicitud == null) return "redirect:/propietario/solicitudes";

        SolicitudReserva solicitud = solicitudDAO.selectEntity(idSolicitud);

        if (solicitud != null) {
            if (aceptar) {
                System.out.println(">>> DEBUG: Aceptando solicitud " + idSolicitud);
                solicitud.setConfirmada(true);
                solicitudDAO.updateEntity(solicitud);
            } else {
                System.out.println(">>> DEBUG: Rechazando solicitud " + idSolicitud);
                // Al borrar, aseguramos borrar la reserva también si es necesario
                Reserva reserva = solicitud.getReserva();
                solicitudDAO.deleteEntity(idSolicitud);
                if (reserva != null) {
                    reservaDAO.deleteEntity(reserva.getId());
                }
            }
        } else {
            System.out.println(">>> DEBUG: No se encontró la solicitud con ID " + idSolicitud);
        }
        return "redirect:/propietario/solicitudes";
    }
}