package es.proyecto.proyectohogarLink.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.test.util.ReflectionTestUtils;
import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import es.proyecto.proyectohogarLink.dao.ReservaDAO;
import es.proyecto.proyectohogarLink.dao.SolicitudReservaDAO;
import es.proyecto.proyectohogarLink.dao.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.entity.*;

class GestorReservasTest {

    @Mock private ReservaDAO reservaDAO;
    @Mock private SolicitudReservaDAO solicitudDAO;
    @Mock private DisponibilidadDAO disponibilidadDAO;
    @Mock private GestorPagos gestorPagos;
    @Mock private EntityManager em; 
    
    @Mock private HttpSession session;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;

    private GestorReservas gestorReservas;

    private Inmueble inmueble;
    private Inquilino inquilino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        gestorReservas = new GestorReservas(gestorPagos);

        ReflectionTestUtils.setField(gestorReservas, "em", em);
        ReflectionTestUtils.setField(gestorReservas, "reservaDAO", reservaDAO);
        ReflectionTestUtils.setField(gestorReservas, "solicitudDAO", solicitudDAO);
        ReflectionTestUtils.setField(gestorReservas, "disponibilidadDAO", disponibilidadDAO);

        inmueble = new Inmueble();
        inmueble.setId(1);
        inmueble.setPrecioNoche(100.0);

        inquilino = new Inquilino();
        inquilino.setId(10);
        inquilino.setNombre("Inquilino Test");

        fechaInicio = LocalDate.now().plusDays(1);
        fechaFin = LocalDate.now().plusDays(5);
    }

    @Test
    @DisplayName("Debe redirigir al login si no hay usuario en sesión")
    void testRealizarReserva_SinLogin() {
        when(session.getAttribute("usuarioLogueado")).thenReturn(null);
        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model, redirectAttributes);
        assertEquals("redirect:/login", vista);
    }

    @Test
    @DisplayName("CP1 - Reserva Exitosa")
    void testRealizarReserva() {
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        
        // CORRECCIÓN Fase Verde: any() en lugar de anyInt()
        when(em.find(eq(Inmueble.class), any())).thenReturn(inmueble);
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any())).thenReturn(true);

        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model, redirectAttributes);

        assertEquals("reserva_exito", vista);
    }

    @Test
    @DisplayName("CP6 - ID Inexistente")
    void testRealizarReserva_IdInexistente() {
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(Inmueble.class, -1)).thenReturn(null);

        String vista = gestorReservas.realizarReserva(-1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model, redirectAttributes);

        // CORRECCIÓN Fase Verde: vista genérica "error"
        assertEquals("error", vista);
    }

    @Test
    @DisplayName("Debe crear solicitud pendiente si no es reserva inmediata")
    void testRealizarReserva_Pendiente() {
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(Inmueble.class, 1)).thenReturn(inmueble);
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any())).thenReturn(false);

        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.PAYPAL, session, model, redirectAttributes);

        verify(solicitudDAO).saveEntity(argThat(solicitud -> 
            solicitud.getEstado().equals("PENDIENTE")
        ));

        assertEquals("reserva_pendiente", vista);
    }
    
    @Test
    @DisplayName("Propietario acepta solicitud: Debe cambiar estado a ACEPTADA")
    void testGestionarSolicitud_Aceptar() {
        SolicitudReserva solicitud = new SolicitudReserva();
        solicitud.setId(100);
        solicitud.setEstado("PENDIENTE");

        when(solicitudDAO.selectEntity(100)).thenReturn(solicitud);

        String vista = gestorReservas.gestionarSolicitud(100, true);

        assertEquals("ACEPTADA", solicitud.getEstado());
        verify(solicitudDAO).updateEntity(solicitud);
        assertEquals("redirect:/propietario/solicitudes", vista);
    }

    @Test
    @DisplayName("Propietario rechaza solicitud: Debe cambiar a RECHAZADA y reembolsar")
    void testGestionarSolicitud_Rechazar() {
        SolicitudReserva solicitud = new SolicitudReserva();
        solicitud.setId(200);
        solicitud.setEstado("PENDIENTE");

        Reserva reservaAsociada = new Reserva();
        Inquilino inquilinoAsociado = new Inquilino();
        inquilinoAsociado.setNombre("Inquilino Reembolso");
        reservaAsociada.setInquilino(inquilinoAsociado);
        
        Pago pagoOriginal = new Pago();
        pagoOriginal.setReferencia("REF-1234");
        pagoOriginal.setMetodoPago(MetodoPago.PAYPAL);
        reservaAsociada.setPago(pagoOriginal);
        
        solicitud.setReserva(reservaAsociada);

        when(solicitudDAO.selectEntity(200)).thenReturn(solicitud);

        String vista = gestorReservas.gestionarSolicitud(200, false);

        assertEquals("RECHAZADA", solicitud.getEstado());
        verify(solicitudDAO).updateEntity(solicitud);
        assertEquals("redirect:/propietario/solicitudes", vista);
    }

    @Test
    @DisplayName("Debe manejar excepciones y mostrar vista de error")
    void testRealizarReserva_Excepcion() {
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(Inmueble.class, 1)).thenReturn(inmueble);
        
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any()))
            .thenThrow(new RuntimeException("Error inesperado en BD"));

        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model, redirectAttributes);

        assertEquals("error", vista);
        verify(model).addAttribute(eq("error"), contains("Error inesperado en BD"));
    }
}