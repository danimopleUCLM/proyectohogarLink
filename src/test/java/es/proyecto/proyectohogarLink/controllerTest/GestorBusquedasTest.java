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
import java.time.LocalDate;
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
        // 1. Datos simulados
        String destino = "Madrid";
        LocalDate fechaInicio = LocalDate.of(2026, 6, 10);
        LocalDate fechaFin = LocalDate.of(2026, 6, 15);
        Integer huespedes = 2;
        
        // 2. Comportamiento del Mock (OJO a los 4 parámetros)
        when(inmuebleDAO.buscarPorFiltros(eq(destino), eq(fechaInicio), eq(fechaFin), eq(huespedes))).thenReturn(new ArrayList<>());
        
        // 3. Ejecutar la acción en el controlador (ahora con 5 parámetros)
        String view = gestorBusquedas.buscarInmuebles(destino, fechaInicio, fechaFin, huespedes, model);
        
        // 4. Verificaciones
        assertEquals("lista_inmuebles", view);
        verify(inmuebleDAO).buscarPorFiltros(eq(destino), eq(fechaInicio), eq(fechaFin), eq(huespedes));
        verify(model).addAttribute(eq("inmuebles"), anyList());
        verify(model).addAttribute(eq("destinoBuscado"), eq(destino));
        verify(model).addAttribute(eq("huespedes"), eq(huespedes));
    }
}
