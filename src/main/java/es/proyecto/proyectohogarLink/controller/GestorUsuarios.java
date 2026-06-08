package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.dao.UsuarioDAO;
import es.proyecto.proyectohogarLink.entity.Inquilino;
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // IMPORTANTE
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GestorUsuarios {
    private static final String REGISTRO_INQUILINO = "registro_inquilino";
    private static final String REGISTRO_PROPIETARIO = "registro_propietario";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    @PersistenceContext
    private EntityManager em;
    
    private UsuarioDAO usuarioDAO;

    private void initDao() {
        if (usuarioDAO == null) usuarioDAO = new UsuarioDAO(em);
    }

    // --- LOGIN ---
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String login, 
                                @RequestParam String pass, 
                                HttpSession session,
                                Model model) {
        initDao();
        Usuario usuario = usuarioDAO.autenticar(login, pass);
        
        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/inicio";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return REDIRECT_LOGIN;
    }

    // --- REGISTRO INQUILINO ---
    @GetMapping("/registroInquilino")
    public String formRegistroInquilino(Model model) {
        model.addAttribute("inquilino", new Inquilino());
        return REGISTRO_INQUILINO;
    }

    @PostMapping("/registroInquilino")
    @Transactional // <--- AÑADIDO: Gestiona la transacción automáticamente
    public String registrarInquilino(@ModelAttribute Inquilino inquilino, Model model) {
        initDao();
        try {
            if (usuarioDAO.existeLogin(inquilino.getLogin())) {
                model.addAttribute("error", "El usuario ya existe");
                return REGISTRO_INQUILINO;
            }
            usuarioDAO.saveEntity(inquilino);
            return REDIRECT_LOGIN;
        } catch (Exception e) {
            model.addAttribute("error", "Error en registro: " + e.getMessage());
            return REGISTRO_INQUILINO;
        }
    }

    // --- REGISTRO PROPIETARIO ---
    @GetMapping("/registroPropietario")
    public String formRegistroPropietario(Model model) {
        model.addAttribute("propietario", new Propietario());
        return REGISTRO_PROPIETARIO;
    }

    @PostMapping("/registroPropietario")
    @Transactional // <--- AÑADIDO
    public String registrarPropietario(@ModelAttribute Propietario propietario, Model model) {
        initDao();
        try {
            if (usuarioDAO.existeLogin(propietario.getLogin())) {
                model.addAttribute("error", "El usuario ya existe");
                return REGISTRO_PROPIETARIO;
            }
            usuarioDAO.saveEntity(propietario);
            return REDIRECT_LOGIN;
        } catch (Exception e) {
            model.addAttribute("error", "Error en registro: " + e.getMessage());
            return REGISTRO_PROPIETARIO;
        }
    }
}