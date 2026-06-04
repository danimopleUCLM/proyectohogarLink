package es.proyecto.proyectohogarLink.controllerTest;
import org.springframework.context.annotation.Import;
import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import es.proyecto.proyectohogarLink.DAO.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GestorReservas.class)
@OverrideAutoConfiguration(enabled = true)
public class GestorReservasTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestorPagos gestorPagos;

    @MockBean
    private EntityManager em; // Para simular la búsqueda de entidades

    // Mocks de los DAOs que inyectaremos manualmente
    private ReservaDAO reservaDAOMock;
    private SolicitudReservaDAO solicitudDAOMock;
    private DisponibilidadDAO disponibilidadDAOMock;

    @Autowired
    private GestorReservas gestorReservasController;

    @BeforeEach
    void setUp() {
        // Inicializamos los mocks de los DAOs
        reservaDAOMock = mock(ReservaDAO.class);
        solicitudDAOMock = mock(SolicitudReservaDAO.class);
        disponibilidadDAOMock = mock(DisponibilidadDAO.class);

        // Inyectamos los mocks directamente en el controlador para saltarnos el initDaos()
        ReflectionTestUtils.setField(gestorReservasController, "reservaDAO", reservaDAOMock);
        ReflectionTestUtils.setField(gestorReservasController, "solicitudDAO", solicitudDAOMock);
        ReflectionTestUtils.setField(gestorReservasController, "disponibilidadDAO", disponibilidadDAOMock);
    }

    @ParameterizedTest(name = "CP{index}: Rol={0}, idInm={1}, F.Inicio={2}, F.Fin={3}, Pago={4}, DispBD={5} -> {6}")
    @CsvSource({
        // Rol, idInmueble, fechaInicio, fechaFin, metodoPago, isDisponibleBD, ResultadoEsperado (Vista/Redirección)
        "INQUILINO, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, reserva_exito",      // CP1
        "INQUILINO, 1, hoy, hoy+1, PAYPAL, FALSE, reserva_pendiente",                         // CP2
        "PROPIETARIO, 10, 2026-10-15, 2026-10-22, TARJETA_DEBITO, TRUE, redirect:/detalle/10", // CP3
        "NULL, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, redirect:/login",           // CP4
        "INQUILINO, 9999, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, error",              // CP5 (Inmueble no existe)
        "INQUILINO, -1, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, bad_request",          // CP6
        "INQUILINO, NULL, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, bad_request",        // CP7
        "INQUILINO, letras, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, bad_request",      // CP8
        "INQUILINO, 10, 2025-01-01, 2026-10-22, TARJETA_CREDITO, TRUE, error",                // CP9 (Pasado)
        "INQUILINO, 10, 2026-10-15, 2026-10-10, TARJETA_CREDITO, TRUE, error",                // CP10 (Fin < Inicio)
        "INQUILINO, 10, 2026-10-15, 2026-10-15, TARJETA_CREDITO, TRUE, error",                // CP11 (Mismo día)
        "INQUILINO, 10, 2026-10-15, 2026-10-22, NULL, TRUE, bad_request",                     // CP12
        "INQUILINO, 10, 2026-10-15, 2026-10-22, BITCOIN, TRUE, bad_request",                  // CP13
        "INQUILINO, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, EXCEPCION, error"            // CP14
    })
    @DisplayName("Tests parametrizados para realizarReserva")
    void testRealizarReserva(String rolStr, String idInmuebleStr, String fInicioStr, String fFinStr, 
                             String metodoPagoStr, String dispBDStr, String resultadoEsperado) throws Exception {

        // 1. Configurar fechas dinámicas
        LocalDate fInicio = fInicioStr.equals("hoy") ? LocalDate.now() : 
                            (fInicioStr.equals("2025-01-01") ? LocalDate.of(2025, 1, 1) : LocalDate.of(2026, 10, 15));
        LocalDate fFin = fFinStr.equals("hoy+1") ? LocalDate.now().plusDays(1) : 
                         (fFinStr.equals("2026-10-10") ? LocalDate.of(2026, 10, 10) : 
                         (fFinStr.equals("2026-10-15") ? LocalDate.of(2026, 10, 15) : LocalDate.of(2026, 10, 22)));

        // 2. Configurar la Sesión simulada
        MockHttpSession session = new MockHttpSession();
        if ("INQUILINO".equals(rolStr)) {
            Inquilino inq = new Inquilino();
            inq.setId(1);
            session.setAttribute("usuarioLogueado", inq);
            when(em.find(eq(Inquilino.class), anyInt())).thenReturn(inq);
        } else if ("PROPIETARIO".equals(rolStr)) {
            Propietario prop = new Propietario();
            prop.setId(2);
            session.setAttribute("usuarioLogueado", prop);
        }

        // 3. Configurar el Mock de Inmueble y BBDD
        if ("10".equals(idInmuebleStr) || "1".equals(idInmuebleStr)) {
            Inmueble inm = new Inmueble();
            inm.setId(Integer.parseInt(idInmuebleStr));
            when(em.find(eq(Inmueble.class), anyInt())).thenReturn(inm);
        } else {
            when(em.find(eq(Inmueble.class), anyInt())).thenReturn(null); // CP5 (9999)
        }

        // Simular disponibilidad y excepciones de BD
        if ("EXCEPCION".equals(dispBDStr)) {
            when(disponibilidadDAOMock.permiteReservaDirectaEnPeriodo(anyInt(), any(), any()))
                .thenThrow(new RuntimeException("Simulando caída BD"));
        } else {
            when(disponibilidadDAOMock.permiteReservaDirectaEnPeriodo(anyInt(), any(), any()))
                .thenReturn(Boolean.parseBoolean(dispBDStr));
        }
        
        // Simular el guardado de la reserva (devuelve un mock de reserva con ID)
        when(reservaDAOMock.saveEntity(any(Reserva.class))).thenAnswer(i -> {
            Reserva r = i.getArgument(0);
            r.setId(999);
            return r;
        });

        // 4. Ejecutar petición HTTP
        var request = post("/realizarReserva")
                .session(session)
                .param("fechaInicio", fInicio.toString())
                .param("fechaFin", fFin.toString());
                
        // Añadir parámetros opcionales
        if (!"NULL".equals(idInmuebleStr)) request.param("idInmueble", idInmuebleStr);
        if (!"NULL".equals(metodoPagoStr)) request.param("metodoPago", metodoPagoStr);

        // 5. Validar Resultados
        if ("bad_request".equals(resultadoEsperado)) {
            // Maneja CP6, CP7, CP8, CP12, CP13 (Spring lanza TypeMismatch antes de llegar al controlador)
            mockMvc.perform(request).andExpect(status().isBadRequest());
        } else if (resultadoEsperado.startsWith("redirect:")) {
            // Maneja CP3 y CP4
            mockMvc.perform(request)
                   .andExpect(status().is3xxRedirection())
                   .andExpect(redirectedUrl(resultadoEsperado.replace("redirect:", "")));
        } else {
            // Maneja CP1, CP2, CP5, CP9, CP10, CP11, CP14 (Devuelven una vista HTML)
            mockMvc.perform(request)
                   .andExpect(status().isOk())
                   .andExpect(view().name(resultadoEsperado));
        }
    }
    
    @ParameterizedTest(name = "CP{index} (gestionarSolicitud): idSolicitud={0}, Aceptar={1}")
    @CsvSource({
        "100, true",   // CP15: Propietario acepta -> Éxito
        "100, false"   // CP16: Propietario rechaza -> Falla reembolso (simulado)
    })
    @DisplayName("Tests para gestionarSolicitud (CP15 y CP16)")
    void testGestionarSolicitud(Integer idSolicitud, boolean aceptar) throws Exception {
        
        // 1. Simular la sesión del Propietario
        MockHttpSession session = new MockHttpSession();
        Propietario prop = new Propietario();
        prop.setId(2);
        session.setAttribute("usuarioLogueado", prop);

        // 2. Simular la Base de Datos (Solicitud y Reserva asociada)
        SolicitudReserva solicitudSimulada = new SolicitudReserva();
        solicitudSimulada.setId(idSolicitud);
        solicitudSimulada.setEstado("PENDIENTE");
        
        Reserva reservaSimulada = new Reserva();
        // Al no setearle un Pago a la reserva, forzamos que al rechazar salte el NullPointerException 
        // y entre en el catch simulando el fallo del GestorPagos (CP16)
        solicitudSimulada.setReserva(reservaSimulada); 
        
        when(solicitudDAOMock.selectEntity(idSolicitud)).thenReturn(solicitudSimulada);

        // 3. Ejecutar petición HTTP POST
        mockMvc.perform(post("/propietario/gestionarSolicitud")
                .session(session)
                .param("idSolicitud", idSolicitud.toString())
                .param("aceptar", String.valueOf(aceptar)))
        // 4. Comprobar que en ambos casos redirige a la lista de solicitudes
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/propietario/solicitudes"));
    }
}