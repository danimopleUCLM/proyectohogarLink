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
    
    // --- CAMPO NUEVO: CAPACIDAD ---
    @Column(name = "capacidad")
    private Integer capacidad = 1;

    // Enum corregido
    @Enumerated(EnumType.STRING)
    @Column(name = "politica_cancelacion")
    private PoliticaCancelacion politicaCancelacion;

    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    // --- CAMPO NUEVO PARA FOTO ---
    @Lob
    @Column(name = "imagen_base64", columnDefinition = "MEDIUMTEXT")
    private String imagenBase64;

    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> disponibilidades;

    @ManyToMany(mappedBy = "listaDeseos")
    private List<Inquilino> inquilinosInteresados;

    // --- GETTERS Y SETTERS ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public Double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(Double precioNoche) { this.precioNoche = precioNoche; }
    
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) { this.politicaCancelacion = politicaCancelacion; }
    
    public Propietario getPropietario() { return propietario; }
    public void setPropietario(Propietario propietario) { this.propietario = propietario; }
    
    public String getImagenBase64() { return imagenBase64; }
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    
    public List<Disponibilidad> getDisponibilidades() { return disponibilidades; }
    public void setDisponibilidades(List<Disponibilidad> disponibilidades) { this.disponibilidades = disponibilidades; }
    
    public List<Inquilino> getInquilinosInteresados() { return inquilinosInteresados; }
    public void setInquilinosInteresados(List<Inquilino> inquilinosInteresados) { this.inquilinosInteresados = inquilinosInteresados; }
}