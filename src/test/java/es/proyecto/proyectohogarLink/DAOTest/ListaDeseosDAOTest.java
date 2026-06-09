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
import es.proyecto.proyectohogarLink.dao.ListaDeseosDAO;
import es.proyecto.proyectohogarLink.entity.ListaDeseos;
import es.proyecto.proyectohogarLink.entity.ListaDeseosId;

public class ListaDeseosDAOTest {

    @Mock
    private EntityManager em;
    
    @Mock
    private TypedQuery<ListaDeseos> query;

    private ListaDeseosDAO listaDeseosDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        listaDeseosDAO = new ListaDeseosDAO(em);
    }

    @Test
    public void testFind() {
        Integer inquilinoId = 1;
        Integer inmuebleId = 2;
        ListaDeseos lista = mock(ListaDeseos.class);
        ListaDeseosId id = new ListaDeseosId(inquilinoId, inmuebleId);
        
        when(em.find(eq(ListaDeseos.class), eq(id))).thenReturn(lista);
        
        ListaDeseos result = listaDeseosDAO.find(inquilinoId, inmuebleId);
        assertEquals(lista, result);
    }

    @Test
    public void testFindByUsuario() {
        Integer userId = 1;
        List<ListaDeseos> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(ListaDeseos.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<ListaDeseos> result = listaDeseosDAO.findByUsuario(userId);
        assertEquals(list, result);
        verify(query).setParameter("id", userId);
    }
}
