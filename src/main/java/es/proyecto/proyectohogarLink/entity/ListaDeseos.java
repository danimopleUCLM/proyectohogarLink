package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ListaDeseos")
public class ListaDeseos {

    @EmbeddedId
    private ListaDeseosId id;

    @ManyToOne
    @MapsId("inquilino_id")
    @JoinColumn(name = "inquilino_id")
    private Usuario inquilino;

    @ManyToOne
    @MapsId("inmueble_id")
    @JoinColumn(name = "inmueble_id")
    private Inmueble inmueble;

    public ListaDeseos() {}

    public ListaDeseos(Usuario inquilino, Inmueble inmueble) {
        this.inquilino = inquilino;
        this.inmueble = inmueble;

        this.id = new ListaDeseosId(
                inquilino.getId(),
                inmueble.getId()
        );
    }

    public ListaDeseosId getId() { return id; }
    public Usuario getInquilino() { return inquilino; }
    public Inmueble getInmueble() { return inmueble; }
}
