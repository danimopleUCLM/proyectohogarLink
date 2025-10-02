package es.proyecto.proyectohogarLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.proyecto.proyectohogarLink.entity.Greeting;
import es.proyecto.proyectohogarLink.entity.User;
import es.proyecto.proyectohogarLink.repository.UserRepository;

import java.util.List;

@Controller
public class WebController {

    private final UserRepository userRepository;

    public WebController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- EXISTENTE ---
    @GetMapping("/")
    public String verPaginaDeInicio() {
        return "inicio";
    }

    @GetMapping("/greeting")
    public String verPaginaGreeting(Model model) {
        model.addAttribute("greeting", new Greeting());   // para el form Greeting
        model.addAttribute("user", new User());           // para el form User
        List<User> users = userRepository.findAll();      // lista desde Mongo
        model.addAttribute("users", users);
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("greeting", greeting);
        return "result";
    }

    // --- NUEVO: manejar formulario User en greeting.html ---
    @PostMapping("/users")
    public String crearUsuario(@ModelAttribute User user, Model model) {
        userRepository.save(user);

        // recargar la vista greeting con lista actualizada
        model.addAttribute("greeting", new Greeting());
        model.addAttribute("user", new User());
        model.addAttribute("users", userRepository.findAll());

        return "greeting";
    }
}

