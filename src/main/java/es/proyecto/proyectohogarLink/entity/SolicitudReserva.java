package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SolicitudesReserva")
public class SolicitudReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @Column(nullable = false)
    private Boolean confirmada = false; // Por defecto false (0)

    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    // Getters y Setters
}