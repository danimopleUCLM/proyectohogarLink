package es.proyecto.proyectohogarLink.DAOTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import es.proyecto.proyectohogarLink.DAO.UsuarioDAO;
import es.proyecto.proyectohogarLink.entity.Usuario;

public class UsuarioDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Usuario> queryUsuario;
    
    @Mock
    private TypedQuery<Long> queryLong;

    private UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDAO = new UsuarioDAO(em);
    }

    @Test
    public void testSaveEntity() {
        Usuario usuario = mock(Usuario.class);
        when(em.merge(usuario)).thenReturn(usuario);
        
        Usuario saved = usuarioDAO.saveEntity(usuario);
        
        assertNotNull(saved);
        verify(em).merge(usuario);
    }

    @Test
    public void testAutenticarExito() {
        String login = "user";
        String pass = "pass";
        Usuario usuario = mock(Usuario.class);
        
        when(em.createQuery(anyString(), eq(Usuario.class))).thenReturn(queryUsuario);
        when(queryUsuario.getSingleResult()).thenReturn(usuario);

        Usuario result = usuarioDAO.autenticar(login, pass);
        
        assertNotNull(result);
        verify(queryUsuario).setParameter("login", login);
        verify(queryUsuario).setParameter("pass", pass);
    }

    @Test
    public void testAutenticarFallo() {
         String login = "user";
        String pass = "pass";
        
        when(em.createQuery(anyString(), eq(Usuario.class))).thenReturn(queryUsuario);
        when(queryUsuario.getSingleResult()).thenThrow(new NoResultException());

        Usuario result = usuarioDAO.autenticar(login, pass);
        
        assertNull(result);
    }

    @Test
    public void testExisteLoginTrue() {
        String login = "user";
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(1L);

        assertTrue(usuarioDAO.existeLogin(login));
    }
    
    @Test
    public void testExisteLoginFalse() {
        String login = "user";
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(0L);

        assertFalse(usuarioDAO.existeLogin(login));
    }
}
