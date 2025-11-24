package es.proyecto.proyectohogarLink.entity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("PROPIETARIO")
public class Propietario extends Usuario {
	
	@OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Inmueble> propiedades;
	

}
