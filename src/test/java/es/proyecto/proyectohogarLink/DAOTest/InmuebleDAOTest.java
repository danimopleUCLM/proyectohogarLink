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
        String destino = "Madrid";
        Double precioMax = 100.0;
        String politica = "FLEXIBLE";
        List<Inmueble> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(Inmueble.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Inmueble> result = inmuebleDAO.buscarPorFiltros(destino, precioMax, politica);
        assertEquals(list, result);
        verify(query).setParameter("destino", "%Madrid%");
        verify(query).setParameter("precioMax", precioMax);
        verify(query).setParameter("politica", politica);
    }

    @Test
    public void testFindByPropietario() {
        int propietarioId = 1;
        List<Inmueble> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(Inmueble.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Inmueble> result = inmuebleDAO.findByPropietario(propietarioId);
        assertEquals(list, result);
        verify(query).setParameter("propietarioId", propietarioId);
    }
}
