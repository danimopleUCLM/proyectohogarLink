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
import es.proyecto.proyectohogarLink.dao.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.entity.Disponibilidad;

public class DisponibilidadDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Disponibilidad> query;
    
    @Mock
    private TypedQuery<Long> queryLong;

    private DisponibilidadDAO disponibilidadDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        disponibilidadDAO = new DisponibilidadDAO(em);
    }

    @Test
    public void testFindByInmuebleId() {
        int inmuebleId = 1;
        List<Disponibilidad> list = new ArrayList<>();
        
        when(em.createQuery(anyString(), eq(Disponibilidad.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Disponibilidad> result = disponibilidadDAO.findByInmuebleId(inmuebleId);
        assertEquals(list, result);
        verify(query).setParameter("inmuebleId", inmuebleId);
    }

    @Test
    public void testPermiteReservaDirectaEnPeriodo() {
        int inmuebleId = 1;
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);
        
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);
        
        boolean result = disponibilidadDAO.permiteReservaDirectaEnPeriodo(inmuebleId, start, end);
        assertTrue(result);
        verify(queryLong).setParameter("inmuebleId", inmuebleId);
        verify(queryLong).setParameter("fechaInicio", start);
        verify(queryLong).setParameter("fechaFin", end);
    }
}
