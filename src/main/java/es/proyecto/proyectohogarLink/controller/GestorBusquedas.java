package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // IMPORTANTE
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GestorBusquedas {

    @PersistenceContext
    private EntityManager em;
    private InmuebleDAO inmuebleDAO;

    @GetMapping({"/", "/inicio"})
    public String mostrarInicio() {
        return "inicio";
    }

    @GetMapping("/buscar")
    @Transactional(readOnly = true) // <--- AÑADIDO (Recomendado)
    public String buscarInmuebles(@RequestParam(required = false) String destino,
                                  @RequestParam(required = false) Double precioMax,
                                  @RequestParam(required = false) String politica,
                                  Model model) {
        
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        
        List<Inmueble> resultados = inmuebleDAO.buscarPorFiltros(destino, precioMax, politica);
        
        model.addAttribute("listaInmuebles", resultados);
        return "lista_inmuebles";
    }
    
    @GetMapping("/detalle/{id}")
    @Transactional(readOnly = true) // <--- AÑADIDO (Recomendado)
    public String verDetalle(@PathVariable Integer id, Model model) {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        
        Inmueble inmueble = inmuebleDAO.selectEntity(id);
        model.addAttribute("inmueble", inmueble);
        
        return "detalle_inmueble";
    }
}