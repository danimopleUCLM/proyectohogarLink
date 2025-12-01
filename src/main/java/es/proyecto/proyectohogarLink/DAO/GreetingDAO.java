package es.proyecto.proyectohogarLink.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.proyecto.proyectohogarLink.entity.*;
@Repository
public interface GreetingDAO extends JpaRepository<Greeting, Long> {

}
