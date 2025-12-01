package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "Inmuebles")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmueble")
    private Integer id;

    @Column(nullable = false)
    private String direccion;

    @Column(name = "precio_noche", nullable = false)
    private Double precioNoche;

    @Enumerated(EnumType.STRING)
    @Column(name = "politica_cancelacion")
    private PoliticaCancelacion politicaCancelacion;

    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    // Relación con Disponibilidades
    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> disponibilidades;

    // Relación inversa para Lista de Deseos
    @ManyToMany(mappedBy = "listaDeseos")
    private List<Inquilino> inquilinosInteresados;
    
    // Getters y Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Double getPrecioNoche() {
		return precioNoche;
	}

	public void setPrecioNoche(Double precioNoche) {
		this.precioNoche = precioNoche;
	}

	public PoliticaCancelacion getPoliticaCancelacion() {
		return politicaCancelacion;
	}

	public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
		this.politicaCancelacion = politicaCancelacion;
	}

	public Propietario getPropietario() {
		return propietario;
	}

	public void setPropietario(Propietario propietario) {
		this.propietario = propietario;
	}

	public List<Disponibilidad> getDisponibilidades() {
		return disponibilidades;
	}

	public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
		this.disponibilidades = disponibilidades;
	}

	public List<Inquilino> getInquilinosInteresados() {
		return inquilinosInteresados;
	}

	public void setInquilinosInteresados(List<Inquilino> inquilinosInteresados) {
		this.inquilinosInteresados = inquilinosInteresados;
	}

	@Override
	public String toString() {
		return "Inmueble [id=" + id + ", direccion=" + direccion + ", precioNoche=" + precioNoche + ", propietario="
				+ propietario + ", inquilinosInteresados=" + inquilinosInteresados + "]";
	}
	
 
    
}
