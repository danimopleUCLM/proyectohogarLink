package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "inquilino_id", nullable = false)
    private Inquilino inquilino;

    @ManyToOne
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    // Relación 1 a 1 con SolicitudReserva (Opcional, solo si no es directa)
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private SolicitudReserva solicitud;

    // Relación 1 a 1 con Pagos
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Pago pago;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Inquilino getInquilino() {
        return inquilino;
    }

    public void setInquilino(Inquilino inquilino) {
        this.inquilino = inquilino;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public SolicitudReserva getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudReserva solicitud) {
        this.solicitud = solicitud;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}