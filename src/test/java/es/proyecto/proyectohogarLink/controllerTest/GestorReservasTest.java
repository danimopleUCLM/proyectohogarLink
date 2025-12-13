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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import es.proyecto.proyectohogarLink.DAO.ReservaDAO;
import es.proyecto.proyectohogarLink.DAO.SolicitudReservaDAO;
import es.proyecto.proyectohogarLink.DAO.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.entity.*;

class GestorReservasTest {

    // 1. Mocks de las dependencias del controlador
    @Mock private ReservaDAO reservaDAO;
    @Mock private SolicitudReservaDAO solicitudDAO;
    @Mock private DisponibilidadDAO disponibilidadDAO;
    @Mock private GestorPagos gestorPagos;
    @Mock private EntityManager em; // Necesario porque usas em.find()
    
    // Mocks de objetos Web
    @Mock private HttpSession session;
    @Mock private Model model;

    @InjectMocks
    private GestorReservas gestorReservas;

    // Datos de prueba
    private Inmueble inmueble;
    private Inquilino inquilino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Preparamos datos básicos
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

        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model);

        assertEquals("redirect:/login", vista);
    }

    @Test
    @DisplayName("Debe realizar reserva exitosa (inmediata)")
    void testRealizarReserva_Exito_Inmediata() {
        // 1. Arrange (Preparar comportamiento de los mocks)
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(Inmueble.class, 1)).thenReturn(inmueble);
        
        // Simulamos que hay disponibilidad inmediata
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any())).thenReturn(true);

        // 2. Act (Ejecutar el método del controlador)
        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.TARJETA_CREDITO, session, model);

        // 3. Assert (Verificaciones)
        
        // Verificamos que se llamó a guardar la reserva
        verify(reservaDAO).saveEntity(any(Reserva.class));
        
        // Verificamos que se procesó el pago
        verify(gestorPagos).procesarPagoInterno(any(Pago.class));
        
        // Verificamos que se guardó una solicitud ACEPTADA (porque era inmediata)
        verify(solicitudDAO).saveEntity(argThat(solicitud -> 
            solicitud.getEstado().equals("ACEPTADA")
        ));

        // Verificamos la vista de retorno
        assertEquals("reserva_exito", vista);
    }

    @Test
    @DisplayName("Debe crear solicitud pendiente si no es reserva inmediata")
    void testRealizarReserva_Pendiente() {
        // 1. Arrange
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(Inmueble.class, 1)).thenReturn(inmueble);
        
        // Simulamos que NO es inmediata (requiere aprobación)
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any())).thenReturn(false);

        // 2. Act
        String vista = gestorReservas.realizarReserva(1, fechaInicio, fechaFin, MetodoPago.PAYPAL, session, model);

        // 3. Assert
        // Verificamos que se guardó una solicitud PENDIENTE
        verify(solicitudDAO).saveEntity(argThat(solicitud -> 
            solicitud.getEstado().equals("PENDIENTE")
        ));

        assertEquals("reserva_pendiente", vista);
    }
}