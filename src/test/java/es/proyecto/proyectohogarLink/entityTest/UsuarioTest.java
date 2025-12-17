package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import es.proyecto.proyectohogarLink.entity.Inquilino;
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.entity.Usuario;

public class UsuarioTest {

    @Test
    public void testGetRolInquilino() {
        Usuario usuario = new Inquilino();
        assertEquals("Inquilino", usuario.getRol());
    }

    @Test
    public void testGetRolPropietario() {
        Usuario usuario = new Propietario();
        assertEquals("Propietario", usuario.getRol());
    }
}
