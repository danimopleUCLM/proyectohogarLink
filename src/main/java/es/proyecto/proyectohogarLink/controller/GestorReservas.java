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
import java.util.List;

@Controller
public class GestorReservas {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private GestorPagos gestorPagos; // Necesario para simular reembolso

    private ReservaDAO reservaDAO;
    private SolicitudReservaDAO solicitudDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (reservaDAO == null) reservaDAO = new ReservaDAO(em);
        if (solicitudDAO == null) solicitudDAO = new SolicitudReservaDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    // --- REALIZAR RESERVA (Igual que antes, pero usando estado) ---
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
            
            // ... lógica de disponibilidad ... 
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
                // Si es inmediata, no creamos solicitud pendiente, o creamos una directamente ACEPTADA
                // Para simplificar tu modelo, creamos solicitud ACEPTADA
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setEstado("ACEPTADA");
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "¡Reserva Confirmada Inmediatamente!");
                return "reserva_exito";
            } else {
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setEstado("PENDIENTE"); // Se queda esperando al propietario
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "Solicitud enviada. Pendiente de aprobación.");
                return "reserva_pendiente";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            return "error";
        }
    }

    // --- VISTA PROPIETARIO (Gestionar) ---
    @GetMapping("/propietario/solicitudes")
    public String verSolicitudesPropietario(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(usuario instanceof Propietario)) return "redirect:/login";

        List<SolicitudReserva> pendientes = solicitudDAO.buscarPendientesPorPropietario(usuario.getId());
        model.addAttribute("solicitudes", pendientes);
        return "lista_solicitudes";
    }

    @PostMapping("/propietario/gestionarSolicitud")
    @Transactional 
    public String gestionarSolicitud(@RequestParam Integer idSolicitud, 
                                     @RequestParam boolean aceptar) {
        initDaos();
        SolicitudReserva solicitud = solicitudDAO.selectEntity(idSolicitud);

        if (solicitud != null) {
            if (aceptar) {
                solicitud.setEstado("ACEPTADA");
                solicitudDAO.updateEntity(solicitud);
            } else {
                solicitud.setEstado("RECHAZADA");
                solicitudDAO.updateEntity(solicitud);
                
                // Lógica de Reembolso
                Pago pago = solicitud.getReserva().getPago();
                if(pago != null) {
                    System.out.println("--- REEMBOLSO REALIZADO ---");
                    System.out.println("Devolviendo dinero a: " + solicitud.getReserva().getInquilino().getNombre());
                    System.out.println("Referencia de pago original: " + pago.getReferencia());
                    System.out.println("Monto devuelto al método: " + pago.getMetodoPago());
                    System.out.println("---------------------------");
                }
            }
        }
        return "redirect:/propietario/solicitudes";
    }

    // --- NUEVO: VISTA INQUILINO (Buzón de notificaciones) ---
    @GetMapping("/inquilino/mis-reservas")
    public String verMisReservasInquilino(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        // Verificamos que sea inquilino
        if (usuario != null && "Inquilino".equals(usuario.getRol())) {
             List<SolicitudReserva> misSolicitudes = solicitudDAO.buscarTodasPorInquilino(usuario.getId());
             model.addAttribute("misSolicitudes", misSolicitudes);
             return "mis_reservas_inquilino";
        }
        
        return "redirect:/login";
    }
}