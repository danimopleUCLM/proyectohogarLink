package es.proyecto.proyectohogarLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

import es.proyecto.proyectohogarLink.entity.User;
import es.proyecto.proyectohogarLink.repository.UserRepository;
import es.proyecto.proyectohogarLink.entity.Greeting;

@Controller
public class WebController {

    // --- Inyectamos el Repositorio ---
    // Spring crea automáticamente un objeto que implementa UsuarioRepository
    // y nos lo da para que podamos usar la base de datos.
    @Autowired
    private UserRepository usuarioRepository;

    @GetMapping("/")
    public String verPaginaDeInicio(Model model) {
        // --- Ejemplo de cómo usar la BD ---
        // 1. Pedimos todos los usuarios a la base de datos
        List<User> listaDeUsuarios = usuarioRepository.findAll();
        
        // 2. Los añadimos al 'model' para que la plantilla HTML los pueda usar
        model.addAttribute("usuarios", listaDeUsuarios);

        // 3. AÑADIMOS ESTO: Creamos un objeto Greeting vacío para el formulario
        model.addAttribute("greeting", new Greeting());

        // 4. Devolvemos la plantilla
        return "inicio"; // Esto busca inicio.html
    }
    
    @GetMapping("/greeting")
    public String verPaginaGreeting(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }
    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting) {
        // Guardar en la BD
        User nuevoUsuario = new User();
        nuevoUsuario.setName(greeting.getName());
        nuevoUsuario.setEmail(greeting.getEmail());

        usuarioRepository.save(nuevoUsuario);

        // Redirigir a /inicio
        return "redirect:/";
    }

}

