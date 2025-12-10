package es.proyecto.proyectohogarLink.controllerTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;

import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;

public class GestorReservasTest {

    @InjectMocks
    private GestorReservas gestorReservas;

    @Mock
    private ReservaDAO reservaDAO;
    @Mock
    private SolicitudReservaDAO solicitudDAO;
    @Mock
    private DisponibilidadDAO disponibilidadDAO;
    @Mock
    private GestorPagos gestorPagos;
    @Mock
    private EntityManager em;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        Field resDao = GestorReservas.class.getDeclaredField("reservaDAO");
        resDao.setAccessible(true);
        resDao.set(gestorReservas, reservaDAO);
        
        Field solDao = GestorReservas.class.getDeclaredField("solicitudDAO");
        solDao.setAccessible(true);
        solDao.set(gestorReservas, solicitudDAO);
        
        Field dispDao = GestorReservas.class.getDeclaredField("disponibilidadDAO");
        dispDao.setAccessible(true);
        dispDao.set(gestorReservas, disponibilidadDAO);
    }

    @Test
    public void testRealizarReservaInmediata() {
        Inquilino inquilino = new Inquilino();
        Inmueble inmueble = new Inmueble();
        inmueble.setId(1);
        
        when(session.getAttribute("usuarioLogueado")).thenReturn(inquilino);
        when(em.find(eq(Inmueble.class), eq(1))).thenReturn(inmueble);
        when(disponibilidadDAO.permiteReservaDirectaEnPeriodo(anyInt(), any(), any())).thenReturn(true);

        String view = gestorReservas.realizarReserva(1, LocalDate.now(), LocalDate.now().plusDays(1), MetodoPago.TARJETA_CREDITO, session, model);
        
        assertEquals("reserva_exito", view);
        verify(reservaDAO).saveEntity(any(Reserva.class));
        verify(solicitudDAO).saveEntity(any(SolicitudReserva.class)); // Aceptada
    }
}
