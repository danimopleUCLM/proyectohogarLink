package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.InmuebleDAO;
import es.proyecto.proyectohogarLink.entity.Inmueble;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
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

    // Ruta: /buscar?destino=Madrid&precioMax=100
    @GetMapping("/buscar")
    public String buscarInmuebles(@RequestParam(required = false) String destino,
                                  @RequestParam(required = false) Double precioMax,
                                  @RequestParam(required = false) String politica,
                                  Model model) {
        
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        
        List<Inmueble> resultados = inmuebleDAO.buscarPorFiltros(destino, precioMax, politica);
        
        model.addAttribute("listaInmuebles", resultados);
        return "lista_inmuebles"; // Espera lista_inmuebles.html
    }
    
    // Ver detalle de un piso antes de reservar
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        if (inmuebleDAO == null) inmuebleDAO = new InmuebleDAO(em);
        
        Inmueble inmueble = inmuebleDAO.selectEntity(id);
        model.addAttribute("inmueble", inmueble);
        
        return "detalle_inmueble"; // Espera detalle_inmueble.html
    }
}