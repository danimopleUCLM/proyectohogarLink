package es.proyecto.proyectohogarLink.entityTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import es.proyecto.proyectohogarLink.entity.User;

public class UserTest {

    @Test
    public void testConstructor() {
        User user = new User("name", "email@test.com");
        assertEquals("name", user.getName());
        assertEquals("email@test.com", user.getEmail());
    }
}
