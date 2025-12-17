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
import es.proyecto.proyectohogarLink.DAO.ReservaDAO;
import es.proyecto.proyectohogarLink.entity.Reserva;

public class ReservaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Reserva> query;

    private ReservaDAO reservaDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reservaDAO = new ReservaDAO(em);
    }

    @Test
    public void testBuscarPorInquilino() {
        int idInquilino = 1;
        List<Reserva> list = new ArrayList<>();
        when(em.createQuery(anyString(), eq(Reserva.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Reserva> result = reservaDAO.buscarPorInquilino(idInquilino);
        assertEquals(list, result);
        verify(query).setParameter("id", idInquilino);
    }

    @Test
    public void testBuscarPorInmueble() {
        int idInmueble = 1;
        List<Reserva> list = new ArrayList<>();
        when(em.createQuery(anyString(), eq(Reserva.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);
        
        List<Reserva> result = reservaDAO.buscarPorInmueble(idInmueble);
        assertEquals(list, result);
        verify(query).setParameter("id", idInmueble);
    }
}
