package es.proyecto.proyectohogarLink.repository;

import es.proyecto.proyectohogarLink.entity.SolicitudReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SolicitudReservaRepository extends JpaRepository<SolicitudReserva, Integer> {

    // Esta consulta viaja: Solicitud -> Reserva -> Inmueble -> Propietario
    @Query("SELECT s FROM SolicitudReserva s WHERE s.reserva.inmueble.propietario.id = :propietarioId")
    List<SolicitudReserva> findByPropietarioId(@Param("propietarioId") Integer propietarioId);
}