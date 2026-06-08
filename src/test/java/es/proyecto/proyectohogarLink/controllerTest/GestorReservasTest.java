package es.proyecto.proyectohogarLink.controllerTest;

import es.proyecto.proyectohogarLink.controller.GestorReservas;
import es.proyecto.proyectohogarLink.controller.GestorPagos;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.test.util.AopTestUtils; // <-- LA HERRAMIENTA MAGICA

import es.proyecto.proyectohogarLink.dao.*;
import es.proyecto.proyectohogarLink.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestorReservas.class)
@OverrideAutoConfiguration(enabled = true)
public class GestorReservasTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestorPagos gestorPagos;

    @MockBean
    private EntityManager em; 

    private ReservaDAO reservaDAOMock;
    private SolicitudReservaDAO solicitudDAOMock;
    private DisponibilidadDAO disponibilidadDAOMock;

    @Autowired
    private GestorReservas gestorReservasController;

    @BeforeEach
    void setUp() {
        reservaDAOMock = mock(ReservaDAO.class);
        solicitudDAOMock = mock(SolicitudReservaDAO.class);
        disponibilidadDAOMock = mock(DisponibilidadDAO.class);

        // --- SOLUCIÓN AL PROXY: Obtenemos la clase real saltándonos el @Transactional ---
        GestorReservas controladorReal = AopTestUtils.getTargetObject(gestorReservasController);

        // Inyectamos los mocks en la clase REAL
        ReflectionTestUtils.setField(controladorReal, "reservaDAO", reservaDAOMock);
        ReflectionTestUtils.setField(controladorReal, "solicitudDAO", solicitudDAOMock);
        ReflectionTestUtils.setField(controladorReal, "disponibilidadDAO", disponibilidadDAOMock);
        ReflectionTestUtils.setField(controladorReal, "em", em);
    }

    @ParameterizedTest(name = "CP{index}: Rol={0}, idInm={1}, F.Inicio={2}, F.Fin={3}, Pago={4}, DispBD={5} -> {6}")
    @CsvSource({
        "INQUILINO, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, reserva_exito",      // CP1
        "INQUILINO, 1, hoy, hoy+1, PAYPAL, FALSE, reserva_pendiente",                         // CP2
        "PROPIETARIO, 10, 2026-10-15, 2026-10-22, TARJETA_DEBITO, TRUE, redirect:/detalle/10", // CP3
        "NULL, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, redirect:/login",           // CP4
        "INQUILINO, 9999, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, error",              // CP5
        "INQUILINO, -1, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, error",                // CP6 
        "INQUILINO, NULL, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, bad_request",        // CP7
        "INQUILINO, letras, 2026-10-15, 2026-10-22, TARJETA_CREDITO, TRUE, bad_request",      // CP8
        "INQUILINO, 10, 2025-01-01, 2026-10-22, TARJETA_CREDITO, TRUE, error",                // CP9 
        "INQUILINO, 10, 2026-10-15, 2026-10-10, TARJETA_CREDITO, TRUE, error",                // CP10 
        "INQUILINO, 10, 2026-10-15, 2026-10-15, TARJETA_CREDITO, TRUE, error",                // CP11 
        "INQUILINO, 10, 2026-10-15, 2026-10-22, NULL, TRUE, bad_request",                     // CP12
        "INQUILINO, 10, 2026-10-15, 2026-10-22, BITCOIN, TRUE, bad_request",                  // CP13
        "INQUILINO, 10, 2026-10-15, 2026-10-22, TARJETA_CREDITO, EXCEPCION, error"            // CP14
    })
    @DisplayName("Tests parametrizados para realizarReserva")
    void testRealizarReserva(String rolStr, String idInmuebleStr, String fInicioStr, String fFinStr, 
                             String metodoPagoStr, String dispBDStr, String resultadoEsperado) throws Exception {

        LocalDate fInicio = fInicioStr.equals("hoy") ? LocalDate.now() : 
                            (fInicioStr.equals("2025-01-01") ? LocalDate.of(2025, 1, 1) : LocalDate.of(2026, 10, 15));
        LocalDate fFin = fFinStr.equals("hoy+1") ? LocalDate.now().plusDays(1) : 
                         (fFinStr.equals("2026-10-10") ? LocalDate.of(2026, 10, 10) : 
                         (fFinStr.equals("2026-10-15") ? LocalDate.of(2026, 10, 15) : LocalDate.of(2026, 10, 22)));

        MockHttpSession session = new MockHttpSession();
        if ("INQUILINO".equals(rolStr)) {
            Inquilino inq = new Inquilino();
            inq.setId(1);
            session.setAttribute("usuarioLogueado", inq);
            when(em.find(eq(Inquilino.class), eq(1))).thenReturn(inq); 
        } else if ("PROPIETARIO".equals(rolStr)) {
            Propietario prop = new Propietario();
            prop.setId(2);
            session.setAttribute("usuarioLogueado", prop);
        }

        if ("10".equals(idInmuebleStr) || "1".equals(idInmuebleStr)) {
            Inmueble inm = new Inmueble();
            int id = Integer.parseInt(idInmuebleStr);
            inm.setId(id);
            // ✅ Usa eq() con el ID concreto en vez de any()
            when(em.find(eq(Inmueble.class), eq(id))).thenReturn(inm);
        } else {
            when(em.find(eq(Inmueble.class), any())).thenReturn(null); 
        }

        if ("EXCEPCION".equals(dispBDStr)) {
            when(disponibilidadDAOMock.permiteReservaDirectaEnPeriodo(anyInt(), any(), any()))
                .thenThrow(new RuntimeException("Simulando caída BD"));
        } else {
            when(disponibilidadDAOMock.permiteReservaDirectaEnPeriodo(anyInt(), any(), any()))
                .thenReturn(Boolean.parseBoolean(dispBDStr));
        }
        
        when(reservaDAOMock.saveEntity(any(Reserva.class))).thenAnswer(i -> {
            Reserva r = i.getArgument(0);
            r.setId(999);
            return r;
        });

        var request = post("/realizarReserva")
                .session(session)
                .param("fechaInicio", fInicio.toString())
                .param("fechaFin", fFin.toString());
                
        if (!"NULL".equals(idInmuebleStr)) request.param("idInmueble", idInmuebleStr);
        if (!"NULL".equals(metodoPagoStr)) request.param("metodoPago", metodoPagoStr);

        if ("bad_request".equals(resultadoEsperado)) {
            mockMvc.perform(request).andExpect(status().isBadRequest());
        } else if (resultadoEsperado.startsWith("redirect:")) {
            mockMvc.perform(request)
                   .andExpect(status().is3xxRedirection())
                   .andExpect(redirectedUrl(resultadoEsperado.replace("redirect:", "")));
        } else {
            mockMvc.perform(request)
                   .andExpect(status().isOk())
                   .andExpect(view().name(resultadoEsperado));
        }
    }
    
    @ParameterizedTest(name = "CP{index} (gestionarSolicitud): idSolicitud={0}, Aceptar={1}")
    @CsvSource({
        "100, true",   
        "100, false"   
    })
    @DisplayName("Tests para gestionarSolicitud (CP15 y CP16)")
    void testGestionarSolicitud(Integer idSolicitud, boolean aceptar) throws Exception {
        
        MockHttpSession session = new MockHttpSession();
        Propietario prop = new Propietario();
        prop.setId(2);
        session.setAttribute("usuarioLogueado", prop);

        SolicitudReserva solicitudSimulada = new SolicitudReserva();
        solicitudSimulada.setId(idSolicitud);
        solicitudSimulada.setEstado("PENDIENTE");
        
        Reserva reservaSimulada = new Reserva();
        solicitudSimulada.setReserva(reservaSimulada); 
        
        when(solicitudDAOMock.selectEntity(idSolicitud)).thenReturn(solicitudSimulada);

        mockMvc.perform(post("/propietario/gestionarSolicitud")
                .session(session)
                .param("idSolicitud", idSolicitud.toString())
                .param("aceptar", String.valueOf(aceptar)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/propietario/solicitudes"));
    }
}