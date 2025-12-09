package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.DAO.ListaDeseosDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import es.proyecto.proyectohogarLink.entity.ListaDeseos;
import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wishlist")
public class GestorWishlist {

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
    public String add(@PathVariable("id") Integer inmuebleId, HttpSession session) {
        initDaos();
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        Inmueble inmueble = inmuebleDAO.selectEntity(inmuebleId);

        if (listaDAO.find(user.getId(), inmuebleId) == null) {
            listaDAO.saveEntity(new ListaDeseos(user, inmueble));
        }

        return "redirect:/wishlist/mis-deseos";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Integer inmuebleId, HttpSession session) {
        initDaos();
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

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
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        model.addAttribute("listaDeseos", listaDAO.findByUsuario(user.getId()));
        return "wishlist";
    }
}
