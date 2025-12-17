package es.proyecto.proyectohogarLink.controllerTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import java.lang.reflect.Field;
import java.util.ArrayList;

import es.proyecto.proyectohogarLink.controller.GestorBusquedas;
import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;

public class GestorBusquedasTest {

    @InjectMocks
    private GestorBusquedas gestorBusquedas;

    @Mock
    private InmuebleDAO inmuebleDAO;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        Field daoField = GestorBusquedas.class.getDeclaredField("inmuebleDAO");
        daoField.setAccessible(true);
        daoField.set(gestorBusquedas, inmuebleDAO);
    }

    @Test
    public void testBuscarInmuebles() {
        String destino = "Madrid";
        when(inmuebleDAO.buscarPorFiltros(eq(destino), any(), any())).thenReturn(new ArrayList<>());
        
        String view = gestorBusquedas.buscarInmuebles(destino, null, null, model);
        
        assertEquals("lista_inmuebles", view);
        verify(inmuebleDAO).buscarPorFiltros(eq(destino), any(), any());
    }
}
