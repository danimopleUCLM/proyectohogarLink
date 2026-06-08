package es.proyecto.proyectohogarLink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import es.proyecto.proyectohogarLink.entity.Greeting;
import es.proyecto.proyectohogarLink.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProyectohogarLinkApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testUserEntity() {
		// Probamos los constructores y setters de User
		User user = new User("Daniel", "dani@ejemplo.com");
		user.setId(1L);
		user.setName("Carlos");
		
		// Comprobamos que los getters devuelven lo correcto
		assertEquals("Carlos", user.getName());
		assertEquals("dani@ejemplo.com", user.getEmail());
		assertEquals(1L, user.getId());
	}

	@Test
	void testGreetingEntity() {
		// Probamos los constructores y setters de Greeting
		Greeting greeting = new Greeting("Ana", "ana@ejemplo.com");
		greeting.setName("María");
		greeting.setEmail("maria@ejemplo.com");
		
		// Comprobamos los getters
		assertEquals("María", greeting.getName());
		assertEquals("maria@ejemplo.com", greeting.getEmail());
		
		// Probamos el método toString
		assertNotNull(greeting.toString());
	}
}