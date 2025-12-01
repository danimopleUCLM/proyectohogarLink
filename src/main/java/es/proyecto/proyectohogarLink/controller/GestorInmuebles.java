package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.DisponibilidadDAO;
import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Disponibilidad;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import es.proyecto.proyectohogarLink.entity.Propietario;
import es.proyecto.proyectohogarLink.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTANTE
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/propietario")
public class GestorInmuebles {

    @PersistenceContext
    private EntityManager em;

    private InmuebleDAO inmuebleDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    @GetMapping("/mis-inmuebles")
    public String listarMisInmuebles(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario instanceof Propietario) {
            List<Inmueble> misInmuebles = inmuebleDAO.findByPropietario(usuario.getId());
            model.addAttribute("misInmuebles", misInmuebles);
            return "panel_propietario";
        }
        return "redirect:/login";
    }

    @GetMapping("/nuevo-inmueble")
    public String formNuevoInmueble(Model model) {
        model.addAttribute("inmueble", new Inmueble());
        return "form_inmueble";
    }

    @PostMapping("/nuevo-inmueble")
    @Transactional // <--- NUEVO
    public String guardarInmueble(@ModelAttribute Inmueble inmueble, HttpSession session) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario instanceof Propietario) {
            inmueble.setPropietario((Propietario) usuario);
            inmuebleDAO.saveEntity(inmueble);
            return "redirect:/propietario/mis-inmuebles";
        }
        return "redirect:/login";
    }
    
    @PostMapping("/agregar-disponibilidad")
    @Transactional // <--- NUEVO
    public String agregarDisponibilidad(@ModelAttribute Disponibilidad disponibilidad, 
                                        @RequestParam Integer idInmueble,
                                        Model model) {
        initDaos();
        try {
            Inmueble inmueble = inmuebleDAO.selectEntity(idInmueble);
            disponibilidad.setInmueble(inmueble);
            
            if (disponibilidad.getFechaInicio().isAfter(disponibilidad.getFechaFin())) {
                model.addAttribute("error", "Fechas incorrectas");
                return "redirect:/propietario/mis-inmuebles";
            }
            
            disponibilidadDAO.saveEntity(disponibilidad);
            return "redirect:/propietario/mis-inmuebles";
            
        } catch (Exception e) {
            return "error";
        }
    }
}