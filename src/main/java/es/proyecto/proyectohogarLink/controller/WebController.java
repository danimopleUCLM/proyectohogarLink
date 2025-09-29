package es.proyecto.proyectohogarLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import es.proyecto.proyectohogarLink.entity.*;

@Controller
public class WebController {

    @GetMapping("/")
    public String verPaginaDeInicio() {
        // Esto le dice a Spring que busque un archivo llamado "inicio.html"
        // en la carpeta de plantillas (templates).
        return "inicio";
    }
    @GetMapping("/greeting")
    public String verPaginaGreeting(Model model) {
    	model.addAttribute("greeting", new Greeting());
        return "greeting";
    }
    
    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
    model.addAttribute("greeting", greeting);
    return "result";
    }
}