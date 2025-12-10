package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import es.proyecto.proyectohogarLink.entity.ListaDeseosId;

public class ListaDeseosIdTest {

    @Test
    public void testEqualsAndHashCode() {
        ListaDeseosId id1 = new ListaDeseosId(1, 2);
        ListaDeseosId id2 = new ListaDeseosId(1, 2);
        ListaDeseosId id3 = new ListaDeseosId(2, 3);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
    }
}
