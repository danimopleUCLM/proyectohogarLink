package es.proyecto.proyectohogarLink.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.ArrayList;

import es.proyecto.proyectohogarLink.controller.GestorInmuebles;
import es.proyecto.proyectohogarLink.dao.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.dao.InmuebleDAO;
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
    private EntityManager em; // <--- NECESARIO: Ahora el controlador usa em.find()

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private MultipartFile multipartFile; // <--- NECESARIO: Para la subida de imagen

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Inyectar InmuebleDAO
        Field daoField = GestorInmuebles.class.getDeclaredField("inmuebleDAO");
        daoField.setAccessible(true);
        daoField.set(gestorInmuebles, inmuebleDAO);
        
        // Inyectar DisponibilidadDAO
        Field dispField = GestorInmuebles.class.getDeclaredField("disponibilidadDAO");
        dispField.setAccessible(true);
        dispField.set(gestorInmuebles, disponibilidadDAO);

        // Inyectar EntityManager (NUEVO)
        Field emField = GestorInmuebles.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(gestorInmuebles, em);
    }

    @Test
    public void testListarMisInmuebles() {
        Usuario usuario = new Propietario();
        usuario.setId(1);
        usuario.setTipoUsuario("Propietario"); // Aseguramos el rol para equalsIgnoreCase

        when(session.getAttribute("usuarioLogueado")).thenReturn(usuario);
        when(inmuebleDAO.findByPropietario(1)).thenReturn(new ArrayList<>());

        String view = gestorInmuebles.listarMisInmuebles(session, model);
        
        assertEquals("panel_propietario", view);
        verify(inmuebleDAO).findByPropietario(1);
    }

    @Test
    public void testGuardarInmueble() {
        // 1. Preparar datos
        Propietario propietario = new Propietario();
        propietario.setId(1);
        propietario.setTipoUsuario("Propietario");

        Inmueble inmueble = new Inmueble();

        // 2. Simular comportamientos (Mocks)
        when(session.getAttribute("usuarioLogueado")).thenReturn(propietario);
        
        // IMPORTANTE: Simular que la BBDD encuentra al propietario cuando hacemos em.find()
        when(em.find(Propietario.class, 1)).thenReturn(propietario);
        
        // IMPORTANTE: Simular que el archivo no está vacío (o sí, para este test simple)
        // Si ponemos true, no entra en el bloque de leer bytes, simplificando el test
        when(multipartFile.isEmpty()).thenReturn(true); 

        // 3. Ejecutar método
        String view = gestorInmuebles.guardarInmueble(inmueble, multipartFile, session);
        
        // 4. Verificaciones
        assertEquals("redirect:/propietario/mis-inmuebles", view);
        verify(inmuebleDAO).saveEntity(inmueble); // Verificar que se llamó a guardar
        verify(em).find(Propietario.class, 1);    // Verificar que se recargó el usuario
    }
}