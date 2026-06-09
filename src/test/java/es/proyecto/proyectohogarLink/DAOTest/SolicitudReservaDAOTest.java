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
import es.proyecto.proyectohogarLink.dao.SolicitudReservaDAO;
import es.proyecto.proyectohogarLink.entity.SolicitudReserva;

public class SolicitudReservaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<SolicitudReserva> query;

    private SolicitudReservaDAO solicitudReservaDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        solicitudReservaDAO = new SolicitudReservaDAO(em);
    }

    @Test
    public void testBuscarPendientesPorPropietario() {
        int pid = 1;
        List<SolicitudReserva> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(SolicitudReserva.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<SolicitudReserva> result = solicitudReservaDAO.buscarPendientesPorPropietario(pid);
        assertEquals(list, result);
        verify(query).setParameter("pid", pid);
    }

    @Test
    public void testBuscarTodasPorInquilino() {
        int iid = 1;
        List<SolicitudReserva> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(SolicitudReserva.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<SolicitudReserva> result = solicitudReservaDAO.buscarTodasPorInquilino(iid);
        assertEquals(list, result);
        verify(query).setParameter("iid", iid);
    }
}
