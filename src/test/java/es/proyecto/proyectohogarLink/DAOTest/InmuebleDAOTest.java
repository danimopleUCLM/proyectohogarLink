package es.proyecto.proyectohogarLink.DAOTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import es.proyecto.proyectohogarLink.dao.InmuebleDAO;
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
        // 1. Configuramos los parámetros que espera el DAO
        String destino = "Madrid";
        LocalDate fechaLlegada = LocalDate.of(2026, 7, 1);
        LocalDate fechaSalida = LocalDate.of(2026, 7, 10);
        Integer viajeros = 2;
        List<Inmueble> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(Inmueble.class))).thenReturn(query);
        // Configuramos el encadenamiento de setParameter para evitar errores
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        // 2. Llamamos al método
        List<Inmueble> result = inmuebleDAO.buscarPorFiltros(destino, fechaLlegada, fechaSalida, viajeros);
        
        // 3. Verificaciones
        assertEquals(list, result);
        verify(query).setParameter("destino", "%" + destino + "%");
        verify(query).setParameter("fechaLlegada", fechaLlegada);
        verify(query).setParameter("fechaSalida", fechaSalida);
        verify(query).setParameter("viajeros", viajeros);
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
        // Buscamos un ID que seguro no existe
        Inmueble resultado = inmuebleDAO.selectEntity(-999);
        
        // Verificamos que devuelve null y no explota
        assertNull(resultado, "Debe devolver null si el inmueble no existe");
    }
}