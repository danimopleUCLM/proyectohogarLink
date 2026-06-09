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
import es.proyecto.proyectohogarLink.dao.InmuebleDAO;

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
        Integer viajeros = 2; 
        
        // 2. Comportamiento del Mock
        // Usamos any() para las fechas para simplificar el test, 
        // tal como tenías en tu versión corregida
        when(inmuebleDAO.buscarPorFiltros(eq(destino), any(), any(), eq(viajeros))).thenReturn(new ArrayList<>());
        
        // 3. Ejecutar la acción en el controlador
        gestorBusquedas.buscarInmuebles(destino, null, null, viajeros, model);
        
        // 4. Verificaciones
        // Esta es la parte crítica que querías corregir para que el test pase (Fase Verde)
        verify(model).addAttribute(eq("listaInmuebles"), any());
        
        // Si necesitas verificar el destino o viajeros también, puedes añadirlos así:
        verify(model).addAttribute("destinoBuscado", destino);
        verify(model).addAttribute("huespedes", viajeros);
    }
}