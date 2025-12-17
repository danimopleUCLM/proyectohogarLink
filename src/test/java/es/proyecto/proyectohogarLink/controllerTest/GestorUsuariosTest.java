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

import es.proyecto.proyectohogarLink.controller.GestorUsuarios;
import es.proyecto.proyectohogarLink.DAO.UsuarioDAO;
import es.proyecto.proyectohogarLink.entity.Usuario;
import es.proyecto.proyectohogarLink.entity.Inquilino;

public class GestorUsuariosTest {

    @InjectMocks
    private GestorUsuarios gestorUsuarios;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Inject mock manually
        Field daoField = GestorUsuarios.class.getDeclaredField("usuarioDAO");
        daoField.setAccessible(true);
        daoField.set(gestorUsuarios, usuarioDAO);
    }

    @Test
    public void testProcesarLoginExito() {
        String login = "user";
        String pass = "pass";
        Usuario usuario = new Inquilino();
        when(usuarioDAO.autenticar(login, pass)).thenReturn(usuario);

        String view = gestorUsuarios.procesarLogin(login, pass, session, model);
        
        assertEquals("redirect:/inicio", view);
        verify(session).setAttribute("usuarioLogueado", usuario);
    }

    @Test
    public void testProcesarLoginFallo() {
        String login = "user";
        String pass = "pass";
        when(usuarioDAO.autenticar(login, pass)).thenReturn(null);

        String view = gestorUsuarios.procesarLogin(login, pass, session, model);
        
        assertEquals("login", view);
        verify(model).addAttribute(eq("error"), anyString());
    }

    @Test
    public void testRegistrarInquilinoExito() {
        Inquilino inquilino = new Inquilino();
        inquilino.setLogin("newuser");
        when(usuarioDAO.existeLogin("newuser")).thenReturn(false);

        String view = gestorUsuarios.registrarInquilino(inquilino, model);
        
        assertEquals("redirect:/login", view);
        verify(usuarioDAO).saveEntity(inquilino);
    }
}
