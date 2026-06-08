package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.dao.InmuebleDAO;
import es.proyecto.proyectohogarLink.dao.ListaDeseosDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import es.proyecto.proyectohogarLink.entity.ListaDeseos;
import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest; // <--- IMPORTAR
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <--- IMPORTAR

@Controller
@RequestMapping("/wishlist")
public class GestorWishlist {
    private static final String USUARIO_LOGUEADO = "usuarioLogueado";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    @PersistenceContext
    private EntityManager em;

    private ListaDeseosDAO listaDAO;
    private InmuebleDAO inmuebleDAO;

    private void initDaos() {
        if (listaDAO == null) listaDAO = new ListaDeseosDAO(em);
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
    }

    @PostMapping("/add/{id}")
    @Transactional
    public String add(@PathVariable("id") Integer inmuebleId, 
                      HttpSession session, 
                      HttpServletRequest request, // Para saber de dónde venimos
                      RedirectAttributes redirectAttributes) { // Para mostrar mensaje
        initDaos();
        Usuario user = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        if (user == null) return REDIRECT_LOGIN;

        Inmueble inmueble = inmuebleDAO.selectEntity(inmuebleId);

        // Verificamos si ya existe para no duplicar
        if (listaDAO.find(user.getId(), inmuebleId) == null) {
            listaDAO.saveEntity(new ListaDeseos(user, inmueble));
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", "❤️ ¡Guardado en favoritos!");
        } else {
            // Mensaje informativo si ya estaba
            redirectAttributes.addFlashAttribute("mensajeInfo", "Ya tienes este piso en tu lista.");
        }

        // --- LÓGICA DE REDIRECCIÓN ---
        // Obtenemos la URL desde la que se hizo la petición
        String referer = request.getHeader("Referer");
        
        // Si existe, volvemos a ella. Si no, vamos a /buscar por defecto.
        return "redirect:" + (referer != null ? referer : "/buscar");
    }

    // ... Resto de métodos (delete, lista) siguen igual ...
    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Integer inmuebleId, HttpSession session) {
        initDaos();
        Usuario user = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        if (user == null) return REDIRECT_LOGIN;

        ListaDeseos existente = listaDAO.find(user.getId(), inmuebleId);
        if (existente != null) {
            em.remove(existente);
        }

        return "redirect:/wishlist/mis-deseos";
    }

    @GetMapping("/mis-deseos")
    @Transactional(readOnly = true)
    public String lista(Model model, HttpSession session) {
        initDaos();
        Usuario user = (Usuario) session.getAttribute(USUARIO_LOGUEADO);
        if (user == null) return REDIRECT_LOGIN;

        model.addAttribute("listaDeseos", listaDAO.findByUsuario(user.getId()));
        return "wishlist";
    }
}