package es.proyecto.proyectohogarLink.controllerTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;

import es.proyecto.proyectohogarLink.controller.GestorPagos;
import es.proyecto.proyectohogarLink.DAO.PagoDAO;
import es.proyecto.proyectohogarLink.entity.Pago;

public class GestorPagosTest {

    @InjectMocks
    private GestorPagos gestorPagos;

    @Mock
    private PagoDAO pagoDAO;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        Field daoField = GestorPagos.class.getDeclaredField("pagoDAO");
        daoField.setAccessible(true);
        daoField.set(gestorPagos, pagoDAO);
    }

    @Test
    public void testProcesarPagoInterno() {
        Pago pago = new Pago();
        
        Pago result = gestorPagos.procesarPagoInterno(pago);
        
        assertNotNull(result.getReferencia());
        verify(pagoDAO).saveEntity(pago);
    }
}
