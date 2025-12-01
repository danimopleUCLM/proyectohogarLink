package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Greeting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String name;
    private String email;

    // Constructor vacío
    public Greeting() {}
    
    public Greeting(String name, String email) {
    	super();
    	this.name = name;
    	this.email = email;
    	}


    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
    return String.format("Greeting [id=%s, name=%s, email=%s]", id, name, email);
    }
}

