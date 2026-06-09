package es.proyecto.proyectohogarLink.dao;

import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UsuarioDAO extends AbstractEntityDAO<Usuario> {

    /**
     * Constructor: Pasa el EntityManager y la clase Usuario al padre
     * para que funcionen los métodos genéricos (save, update, delete, find).
     */
    public UsuarioDAO(EntityManager em) {
        super(em, Usuario.class);
    }

    /**
     * Método específico para el LOGIN.
     * Busca un usuario (sea Propietario o Inquilino) por su login y contraseña.
     * * @param login Nombre de usuario
     * @param pass Contraseña (en texto plano según tu entidad actual)
     * @return El objeto Usuario si existe, o null si las credenciales son incorrectas.
     */
    public Usuario autenticar(String login, String pass) {
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.login = :login AND u.pass = :pass";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            
            query.setParameter("login", login);
            query.setParameter("pass", pass);

            return query.getSingleResult();

        } catch (NoResultException e) {
            // Si no encuentra coincidencia, devuelve null
            return null;
        }
    }

    /**
     * Método auxiliar para validar registro.
     * Comprueba si un nombre de usuario ya existe en la base de datos.
     */
    public boolean existeLogin(String login) {
        String jpql = "SELECT COUNT(u) FROM Usuario u WHERE u.login = :login";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("login", login);
        
        // Si el conteo es mayor a 0, significa que ya existe
        return query.getSingleResult() > 0;
    }
}
