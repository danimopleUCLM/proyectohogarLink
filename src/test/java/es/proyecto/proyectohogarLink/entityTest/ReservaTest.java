package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;

import es.proyecto.proyectohogarLink.entity.Reserva;
import es.proyecto.proyectohogarLink.entity.Inmueble;

class ReservaTest {

    @Test
    @DisplayName("Debe calcular el precio total basado en días * precioNoche")
    void testCalcularPrecioTotal() {
        // 1. ARRANGE (Preparar)
        // Simulamos un inmueble que cuesta 100€ la noche
        Inmueble casa = new Inmueble();
        casa.setPrecioNoche(100.0);

        Reserva reserva = new Reserva();
        reserva.setInmueble(casa);
        // Reserva de 5 días (del 1 al 6 de Enero)
        reserva.setFechaInicio(LocalDate.of(2025, 1, 1));
        reserva.setFechaFin(LocalDate.of(2025, 1, 6)); 

        // 2. ACT (Actuar)
        // Intentamos llamar a un método que AÚN NO EXISTE
        double precioTotal = reserva.calcularPrecioTotal();

        // 3. ASSERT (Verificar)
        // 5 noches * 100€ = 500€
        assertEquals(500.0, precioTotal, 0.01, "El precio total debería ser 500.0");
    }

    @Test
    @DisplayName("Debe lanzar excepción si las fechas son incoherentes")
    void testValidarFechas() {
        Reserva reserva = new Reserva();
        // Fin antes que inicio
        reserva.setFechaInicio(LocalDate.of(2025, 1, 10));
        reserva.setFechaFin(LocalDate.of(2025, 1, 5));

        // Verificamos que al validar lance una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            reserva.validarFechas();
        }, "Debe lanzar excepción si la fecha fin es anterior a la de inicio");
    }
}