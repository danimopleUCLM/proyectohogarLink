package es.proyecto.proyectohogarLink.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.dao.*;
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
import java.util.UUID; // Importante para generar la referencia del pago

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class GestorReservas {

    private static final Logger logger = LoggerFactory.getLogger(GestorReservas.class);
    private static final String USUARIO_LOGUEADO = "usuarioLogueado";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    
    // --- NUEVA CONSTANTE AÑADIDA PARA SOLUCIONAR EL CODE SMELL ---
    private static final String ERROR = "error";

    @PersistenceContext
    private EntityManager em;
    
    private final GestorPagos gestorPagos;

    @Autowired
    public GestorReservas(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }

    private ReservaDAO reservaDAO;
    private SolicitudReservaDAO solicitudDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (reservaDAO == null) reservaDAO = new ReservaDAO(em);
        if (solicitudDAO == null) solicitudDAO = new SolicitudReservaDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    // --- REALIZAR RESERVA (CORREGIDO) ---
    @PostMapping("/realizarReserva")
    @Transactional
    public String realizarReserva(
            @RequestParam Integer idInmueble,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam MetodoPago metodoPago, 
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) { 

        initDaos();
        Usuario usuarioSession = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        
        // Verificación de seguridad
        if (usuarioSession == null) {
            return REDIRECT_LOGIN;
        }
        
        // --- 2. NUEVA LÓGICA: SI ES PROPIETARIO -> ERROR EN LA MISMA PÁGINA ---
        if (usuarioSession instanceof Propietario) {
            // Guardamos el error para que se vea tras la redirección (Usando la constante)
            redirectAttributes.addFlashAttribute(ERROR, "Los propietarios no pueden realizar reservas. Debes entrar como Inquilino.");
            // Redirigimos de vuelta al detalle del inmueble
            return "redirect:/detalle/" + idInmueble;
        }
        
     // Añadir en realizarReserva() ANTES del try, tras la comprobación del Propietario:
        if (fechaInicio.isBefore(LocalDate.now())) {
            model.addAttribute(ERROR, "La fecha de inicio no puede ser pasada.");
            return ERROR;
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            model.addAttribute("error", "La fecha de fin debe ser posterior a la de inicio.");
            return ERROR;
        }

        try {
            // 1. Recargamos las entidades desde la BBDD para evitar errores de objetos desconectados
            Inquilino inquilino = em.find(Inquilino.class, usuarioSession.getId());
            Inmueble inmueble = em.find(Inmueble.class, idInmueble);
            
            if (inmueble == null) {
                // Usando la constante en lugar del literal
                model.addAttribute(ERROR, "El inmueble seleccionado no existe.");
                return ERROR; 
            }
            
            // 2. Lógica de disponibilidad
            boolean esInmediata = disponibilidadDAO.permiteReservaDirectaEnPeriodo(inmueble.getId(), fechaInicio, fechaFin);

            Reserva reserva = new Reserva();
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setInquilino(inquilino);
            reserva.setInmueble(inmueble);

            // --- CORRECCIÓN CRÍTICA AQUÍ ---
            reserva = reservaDAO.saveEntity(reserva);
            
            // Forzamos la sincronización con la BBDD
            em.flush(); 

            // 3. Crear el Pago
            Pago pago = new Pago();
            pago.setMetodoPago(metodoPago);
            pago.setReserva(reserva);
            
            String referenciaGenerada = UUID.randomUUID().toString();
            pago.setReferencia(referenciaGenerada);

            gestorPagos.procesarPagoInterno(pago);

            // 4. Gestionar Solicitud
            if (esInmediata) {
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setEstado("ACEPTADA");
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "¡Reserva Confirmada Inmediatamente!");
                model.addAttribute("referencia", referenciaGenerada);
                
                return "reserva_exito";
            } else {
                SolicitudReserva solicitud = new SolicitudReserva();
                solicitud.setReserva(reserva);
                solicitud.setEstado("PENDIENTE");
                solicitudDAO.saveEntity(solicitud);
                
                model.addAttribute("mensaje", "Solicitud enviada. Pendiente de aprobación.");
                return "reserva_pendiente";
            }

        } catch (Exception e) {
            logger.error("Error realizando la reserva", e);
            // Usando la constante en lugar del literal
            model.addAttribute(ERROR, "Error procesando la reserva: " + e.getMessage());
            return ERROR; 
        }
    }

    // --- VISTA PROPIETARIO ---
    @GetMapping("/propietario/solicitudes")
    public String verSolicitudesPropietario(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        if (usuario == null || !"Propietario".equals(usuario.getRol())) return REDIRECT_LOGIN;

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
                
                try {
                    Pago pago = solicitud.getReserva().getPago();
                    if(pago != null) {
                        logger.info("--- REEMBOLSO SIMULADO ---");
                        logger.info("Devolviendo referencia: {}", pago.getReferencia());
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo procesar reembolso o no hay pago asociado");
                }
            }
        }
        return "redirect:/propietario/solicitudes";
    }

    // --- VISTA INQUILINO ---
    @GetMapping("/inquilino/mis-reservas")
    public String verMisReservasInquilino(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        
        if (usuario != null && "Inquilino".equals(usuario.getRol())) {
             List<SolicitudReserva> misSolicitudes = solicitudDAO.buscarTodasPorInquilino(usuario.getId());
             model.addAttribute("misSolicitudes", misSolicitudes);
             return "mis_reservas_inquilino";
        }
        
        return REDIRECT_LOGIN;
    }
}