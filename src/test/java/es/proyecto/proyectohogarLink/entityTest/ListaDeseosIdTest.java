package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.proyecto.proyectohogarLink.entity.ListaDeseosId;

class ListaDeseosIdTest {

    @Test
    @DisplayName("Verificar contrato de Equals y HashCode")
    void testEqualsAndHashCode() {
        // 1. Arrange (Preparación)
        ListaDeseosId id1 = new ListaDeseosId(1, 2);
        ListaDeseosId id2 = new ListaDeseosId(1, 2); 
        ListaDeseosId id3 = new ListaDeseosId(2, 3); 
        ListaDeseosId id4 = new ListaDeseosId(1, 3); 

        // 2. Assert (Verificaciones)
        
        
        assertEquals(id1, id2, "Dos instancias con los mismos valores deben ser iguales");
        assertEquals(id1.hashCode(), id2.hashCode(), "El HashCode debe coincidir si los objetos son iguales");
        
        
        assertNotEquals(id1, id3, "Deben ser diferentes si cambian todos los valores");
        assertNotEquals(id1, id4, "Deben ser diferentes si cambia al menos un valor");

        // --- Casos Límite (Edge Cases) ---
        
        assertEquals(id1, id1, "Reflexividad: El objeto debe ser igual a sí mismo");
        
        
        assertNotEquals(id1, null, "El objeto no debe ser igual a null");
        
        
        assertNotEquals(id1, new Object(), "El objeto no debe ser igual a una instancia de otra clase");
    }
}