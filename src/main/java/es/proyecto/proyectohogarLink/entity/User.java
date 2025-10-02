package es.proyecto.proyectohogarLink.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")  
public class User {
    @Id
    private String id;
    private String nombre;
    private int edad;

    public User() {}

    public User(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
}
