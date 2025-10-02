package es.proyecto.proyectohogarLink.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import es.proyecto.proyectohogarLink.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByNombre(String nombre);
}
