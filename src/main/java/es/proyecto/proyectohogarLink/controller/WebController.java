package es.proyecto.proyectohogarLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String verPaginaDeInicio() {
        // Esto le dice a Spring que busque un archivo llamado "inicio.html"
        // en la carpeta de plantillas (templates).
        return "inicio";
    }
}