package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    // --- LÓGICA DE NEGOCIO ---

    /**
     * Calcula el precio total de la reserva.
     * Fórmula: (Fecha Fin - Fecha Inicio) * Precio Noche Inmueble
     * @return El precio total calculado.
     * @throws IllegalStateException Si faltan datos necesarios (fechas o inmueble).
     */
    public double calcularPrecioTotal() {
        if (fechaInicio == null || fechaFin == null || inmueble == null) {
            throw new IllegalStateException("Faltan datos para calcular el precio (fechas o inmueble)");
        }
        
        // Calculamos los días de diferencia
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        
        // Si el precio por noche es null, asumimos 0.0 para evitar NullPointerException
        double precioNoche = (inmueble.getPrecioNoche() != null) ? inmueble.getPrecioNoche() : 0.0;
        
        return dias * precioNoche;
    }

    /**
     * Valida que las fechas de la reserva sean coherentes.
     * La fecha de fin debe ser posterior a la de inicio.
     * @throws IllegalArgumentException Si la fecha de fin es anterior o igual a la de inicio.
     */
    public void validarFechas() {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    // --- GETTERS Y SETTERS ---

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