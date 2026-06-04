package es.proyecto.proyectohogarLink.DAOTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate; // ¡Importación nueva necesaria para las fechas!

import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;

public class InmuebleDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Inmueble> query;

    private InmuebleDAO inmuebleDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        inmuebleDAO = new InmuebleDAO(em);
    }

    @Test
    public void testBuscarPorFiltros() {
        // 1. Preparamos los DATOS NUEVOS que exige el método ahora
        String destino = "Madrid";
        LocalDate fechaInicio = LocalDate.of(2026, 6, 10);
        LocalDate fechaFin = LocalDate.of(2026, 6, 15);
        Integer huespedes = 2;
        List<Inmueble> list = new ArrayList<>();
        
        // 2. Comportamiento simulado (Mocks)
        when(em.createQuery(anyString(), eq(Inmueble.class))).thenReturn(query);
        // Añadimos esto para evitar fallos si el código real encadena los setParameter
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        // 3. Ejecutamos el método con la NUEVA firma
        List<Inmueble> result = inmuebleDAO.buscarPorFiltros(destino, fechaInicio, fechaFin, huespedes);
        
        // 4. Verificaciones
        assertEquals(list, result);
        
        // Verificamos que se pasan los parámetros correctos a la consulta con los nombres EXACTOS del DAO
        verify(query).setParameter("destino", "%" + destino + "%");
        verify(query).setParameter("fechaLlegada", fechaInicio);
        verify(query).setParameter("fechaSalida", fechaFin);
        verify(query).setParameter("viajeros", huespedes);
    }

    @Test
    public void testFindByPropietario() {
        int propietarioId = 1;
        List<Inmueble> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(Inmueble.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Inmueble> result = inmuebleDAO.findByPropietario(propietarioId);
        assertEquals(list, result);
        verify(query).setParameter("propietarioId", propietarioId);
    }
    
    @Test
    void testBuscarInmuebleNoExistente() {
        // Buscamos un ID que seguro no existe (ej. negativo)
        Inmueble resultado = inmuebleDAO.selectEntity(-999);
        
        // Verificamos que devuelve null y no explota
        assertNull(resultado, "Debe devolver null si el inmueble no existe");
    }
}