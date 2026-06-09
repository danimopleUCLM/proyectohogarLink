package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import es.proyecto.proyectohogarLink.entity.Inquilino;
import es.proyecto.proyectohogarLink.entity.ListaDeseos;

public class ListaDeseosTest {

    @Test
    public void testConstructor() {
        Inquilino inquilino = new Inquilino();
        inquilino.setId(10);
        
        Inmueble inmueble = new Inmueble();
        inmueble.setId(20);
        
        ListaDeseos lista = new ListaDeseos(inquilino, inmueble);
        
        assertNotNull(lista.getId());
        assertEquals(10, lista.getId().getInquilino_id());
        assertEquals(20, lista.getId().getInmueble_id());
        assertEquals(inquilino, lista.getInquilino());
        assertEquals(inmueble, lista.getInmueble());
    }
}
