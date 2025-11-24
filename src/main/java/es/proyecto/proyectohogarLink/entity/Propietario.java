package es.proyecto.proyectohogarLink.entity;
import jakarta.persistence.*;
import java.util.*;

@Entity
@DiscriminatorValue("PROPIETARIO")
public class Propietario extends Usuario {
	
	@OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Inmueble> propiedades;

	public List<Inmueble> getPropiedades() {
		return propiedades;
	}

	public void setPropiedades(List<Inmueble> propiedades) {
		this.propiedades = propiedades;
	}
	
	

}
