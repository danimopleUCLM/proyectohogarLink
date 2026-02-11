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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/propietario")
public class GestorInmuebles {

    private static final String REDIRECT_MIS_INMUEBLES = "redirect:/propietario/mis-inmuebles";

    @PersistenceContext
    private EntityManager em;

    private InmuebleDAO inmuebleDAO;
    private DisponibilidadDAO disponibilidadDAO;

    private void initDaos() {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        if (disponibilidadDAO == null) disponibilidadDAO = new DisponibilidadDAO(em);
    }

    // Listar mis inmuebles
    @GetMapping("/mis-inmuebles")
    public String listarMisInmuebles(HttpSession session, Model model) {
        initDaos();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario != null && "Propietario".equalsIgnoreCase(usuario.getRol())) {
            List<Inmueble> misInmuebles = inmuebleDAO.findByPropietario(usuario.getId());
            model.addAttribute("misInmuebles", misInmuebles);
            return "panel_propietario";
        }
        return "redirect:/login";
    }

    // Mostrar formulario
    @GetMapping("/nuevo-inmueble")
    public String formNuevoInmueble(Model model) {
        model.addAttribute("inmueble", new Inmueble());
        return "form_inmueble";
    }

    // GUARDAR CON IMAGEN
    @PostMapping("/nuevo-inmueble")
    @Transactional 
    public String guardarInmueble(@ModelAttribute Inmueble inmueble,
                                  @RequestParam("archivoImagen") MultipartFile archivo,
                                  HttpSession session) {
        initDaos();
        Usuario usuarioSession = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioSession != null && "Propietario".equalsIgnoreCase(usuarioSession.getRol())) {
            
            // Recargamos propietario para evitar errores de sesión
            Propietario propietario = em.find(Propietario.class, usuarioSession.getId());
            
            if (propietario != null) {
                inmueble.setPropietario(propietario);
                
                // Convertir imagen a Base64
                if (!archivo.isEmpty()) {
                    try {
                        byte[] bytes = archivo.getBytes();
                        String base64Image = Base64.getEncoder().encodeToString(bytes);
                        inmueble.setImagenBase64(base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                inmuebleDAO.saveEntity(inmueble);
                // REDIRECCIÓN CORRECTA:
                return REDIRECT_MIS_INMUEBLES;
            }
        }
        return "redirect:/login";
    }
    
    // Método agregar disponibilidad (sin cambios)
    @PostMapping("/agregar-disponibilidad")
    @Transactional 
    public String agregarDisponibilidad(@ModelAttribute Disponibilidad disponibilidad, 
                                        @RequestParam Integer idInmueble,
                                        Model model) {
        initDaos();
        try {
            Inmueble inmueble = inmuebleDAO.selectEntity(idInmueble);
            disponibilidad.setInmueble(inmueble);
            if (disponibilidad.getFechaInicio().isAfter(disponibilidad.getFechaFin())) {
                return REDIRECT_MIS_INMUEBLES;
            }
            disponibilidadDAO.saveEntity(disponibilidad);
            return REDIRECT_MIS_INMUEBLES;
        } catch (Exception e) {
            return REDIRECT_MIS_INMUEBLES;
        }
    }
}