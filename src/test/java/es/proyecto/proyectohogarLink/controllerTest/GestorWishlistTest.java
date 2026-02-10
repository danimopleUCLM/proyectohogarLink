package es.proyecto.proyectohogarLink.controllerTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.ArrayList;

import es.proyecto.proyectohogarLink.controller.GestorWishlist;
import es.proyecto.proyectohogarLink.DAO.ListaDeseosDAO;
import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.*;

public class GestorWishlistTest {

    @InjectMocks
    private GestorWishlist gestorWishlist;

    @Mock private ListaDeseosDAO listaDAO;
    @Mock private InmuebleDAO inmuebleDAO;
    @Mock private HttpSession session;
    @Mock private Model model;
    @Mock private HttpServletRequest request;
    @Mock private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        Field lDao = GestorWishlist.class.getDeclaredField("listaDAO");
        lDao.setAccessible(true);
        lDao.set(gestorWishlist, listaDAO);
        
        Field iDao = GestorWishlist.class.getDeclaredField("inmuebleDAO");
        iDao.setAccessible(true);
        iDao.set(gestorWishlist, inmuebleDAO);
    }

    @Test
    public void testAdd() {
        Usuario user = new Usuario() { public Integer getId() { return 1; } };
        Inmueble inmueble = new Inmueble();
        
        when(session.getAttribute("usuarioLogueado")).thenReturn(user);
        when(inmuebleDAO.selectEntity(anyInt())).thenReturn(inmueble);
        when(listaDAO.find(anyInt(), anyInt())).thenReturn(null);
        
        String view = gestorWishlist.add(1, session, request, redirectAttributes);
        
        assertEquals("redirect:/buscar", view);
        verify(listaDAO).saveEntity(any(ListaDeseos.class));
    }
}
