package es.proyecto.proyectohogarLink.DAOTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import es.proyecto.proyectohogarLink.dao.PagoDAO;
import es.proyecto.proyectohogarLink.entity.Pago;

public class PagoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Pago> query;

    private PagoDAO pagoDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pagoDAO = new PagoDAO(em);
    }

    @Test
    public void testBuscarPorReferenciaExito() {
        String ref = "REF123";
        Pago pago = mock(Pago.class);
        
        when(em.createQuery(anyString(), eq(Pago.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(pago);
        
        Pago result = pagoDAO.buscarPorReferencia(ref);
        assertEquals(pago, result);
        verify(query).setParameter("ref", ref);
    }
    
    @Test
    public void testBuscarPorReferenciaFallo() {
        String ref = "REF123";
        
        when(em.createQuery(anyString(), eq(Pago.class))).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        
        Pago result = pagoDAO.buscarPorReferencia(ref);
        assertNull(result);
    }
}
