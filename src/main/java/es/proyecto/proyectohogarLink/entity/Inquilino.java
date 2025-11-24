package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("INQUILINO")
public class Inquilino extends Usuario {

    @ManyToMany
    @JoinTable(
        name = "ListaDeseos",
        joinColumns = @JoinColumn(name = "inquilino_id"),
        inverseJoinColumns = @JoinColumn(name = "inmueble_id")
    )
    
    private List<Inmueble> listaDeseos;

    @OneToMany(mappedBy = "inquilino")
    private List<Reserva> reservas;

 // Getters y Setters
	public List<Inmueble> getListaDeseos() {
		return listaDeseos;
	}

	public void setListaDeseos(List<Inmueble> listaDeseos) {
		this.listaDeseos = listaDeseos;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}
    
}