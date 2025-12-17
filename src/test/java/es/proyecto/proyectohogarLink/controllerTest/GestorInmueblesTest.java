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
import java.lang.reflect.Field;
import java.util.ArrayList;

import es.proyecto.proyectohogarLink.controller.GestorInmuebles;
import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.DAO.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.entity.Usuario;

public class GestorInmueblesTest {

    @InjectMocks
    private GestorInmuebles gestorInmuebles;

    @Mock
    private InmuebleDAO inmuebleDAO;
    
    @Mock
    private DisponibilidadDAO disponibilidadDAO;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        Field daoField = GestorInmuebles.class.getDeclaredField("inmuebleDAO");
        daoField.setAccessible(true);
        daoField.set(gestorInmuebles, inmuebleDAO);
        
        Field dispField = GestorInmuebles.class.getDeclaredField("disponibilidadDAO");
        dispField.setAccessible(true);
        dispField.set(gestorInmuebles, disponibilidadDAO);
    }

    @Test
    public void testListarMisInmuebles() {
        Usuario usuario = new Propietario();
        usuario.setId(1);
        when(session.getAttribute("usuarioLogueado")).thenReturn(usuario);
        when(inmuebleDAO.findByPropietario(1)).thenReturn(new ArrayList<>());

        String view = gestorInmuebles.listarMisInmuebles(session, model);
        
        assertEquals("panel_propietario", view);
        verify(inmuebleDAO).findByPropietario(1);
    }

    @Test
    public void testGuardarInmueble() {
        Usuario usuario = new Propietario();
        when(session.getAttribute("usuarioLogueado")).thenReturn(usuario);
        Inmueble inmueble = new Inmueble();

        String view = gestorInmuebles.guardarInmueble(inmueble, session);
        
        assertEquals("redirect:/propietario/mis-inmuebles", view);
        verify(inmuebleDAO).saveEntity(inmueble);
    }
}
