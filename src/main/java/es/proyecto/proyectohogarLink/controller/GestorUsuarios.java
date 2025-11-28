package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.UsuarioDAO;
import es.proyecto.proyectohogarLink.entity.Inquilino;
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class GestorUsuarios {

    @PersistenceContext
    private EntityManager em;
    
    private UsuarioDAO usuarioDAO;

    // Inicializamos el DAO en el post-construct o al usarlo
    private UsuarioDAO getDao() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO(em);
        }
        return usuarioDAO;
    }

    public Usuario login(String login, String password) {
        return getDao().autenticar(login, password);
    }

    public void registrarInquilino(Inquilino inquilino) throws Exception {
        if (getDao().existeLogin(inquilino.getLogin())) {
            throw new Exception("El nombre de usuario ya existe");
        }
        getDao().saveEntity(inquilino);
    }

    public void registrarPropietario(Propietario propietario) throws Exception {
        if (getDao().existeLogin(propietario.getLogin())) {
            throw new Exception("El nombre de usuario ya existe");
        }
        getDao().saveEntity(propietario);
    }
}
