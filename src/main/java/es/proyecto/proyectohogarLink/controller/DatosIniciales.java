package es.proyecto.proyectohogarLink.controller;

import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DatosIniciales implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);
        InmuebleDAO inmuebleDAO = new InmuebleDAO(em);
        DisponibilidadDAO disponibilidadDAO = new DisponibilidadDAO(em);

        // 1. CREAR PROPIETARIO DE PRUEBA (si no existe)
        if (!usuarioDAO.existeLogin("propietario1")) {
            Propietario prop = new Propietario();
            prop.setLogin("propietario1");
            prop.setPass("1234");
            prop.setNombre("Carlos Dueño");
            prop.setApellidos("Sánchez");
            prop.setDireccion("Oficina Central");
            usuarioDAO.saveEntity(prop);

            System.out.println(">>> DATOS: Propietario creado (propietario1 / 1234)");

            // 2. CREAR UN PISO EN EL CENTRO
            Inmueble piso1 = new Inmueble();
            piso1.setDireccion("Plaza Mayor 15, Madrid");
            piso1.setPrecioNoche(120.0);
            piso1.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE);
            piso1.setPropietario(prop);
            inmuebleDAO.saveEntity(piso1);

            // Disponibilidad para este piso (Próximo mes, Inmediata)
            Disponibilidad disp1 = new Disponibilidad();
            disp1.setFechaInicio(LocalDate.now().plusDays(1));
            disp1.setFechaFin(LocalDate.now().plusDays(30));
            disp1.setPrecio(110.0); // Oferta
            disp1.setDirecta(true); // ¡Reserva Inmediata!
            disp1.setInmueble(piso1);
            disponibilidadDAO.saveEntity(disp1);

            // 3. CREAR UN CHALET EN LA PLAYA
            Inmueble piso2 = new Inmueble();
            piso2.setDireccion("Av. del Mar 22, Valencia");
            piso2.setPrecioNoche(250.0);
            piso2.setPoliticaCancelacion(PoliticaCancelacion.NO_REEMBOLSABLE);
            piso2.setPropietario(prop);
            inmuebleDAO.saveEntity(piso2);

            // Disponibilidad (Requiere solicitud)
            Disponibilidad disp2 = new Disponibilidad();
            disp2.setFechaInicio(LocalDate.now().plusDays(5));
            disp2.setFechaFin(LocalDate.now().plusDays(15));
            disp2.setPrecio(250.0);
            disp2.setDirecta(false); // Requiere aprobación
            disp2.setInmueble(piso2);
            disponibilidadDAO.saveEntity(disp2);
        }

        // 4. CREAR INQUILINO DE PRUEBA
        if (!usuarioDAO.existeLogin("inquilino1")) {
            Inquilino inq = new Inquilino();
            inq.setLogin("inquilino1");
            inq.setPass("1234");
            inq.setNombre("Ana Inquilina");
            usuarioDAO.saveEntity(inq);
            System.out.println(">>> DATOS: Inquilino creado (inquilino1 / 1234)");
        }
    }
}