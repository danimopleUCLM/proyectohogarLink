package es.proyecto.proyectohogarLink.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "Usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String login;
    
    @Column(nullable = false)
    private String pass;
    
    private String nombre;
    private String apellidos;
    
    // --- NUEVOS CAMPOS DE DIRECCIÓN ---
    private String calle;
    private String numero;
    private String localidad;
    private String provincia;
    private String codigoPostal;

    @Column(name = "tipo_usuario", insertable = false, updatable = false)
    private String tipoUsuario;

    // --- GETTER Y SETTER PARA EL TIPO DE USUARIO ---
    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    // -----------------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
    // --- GETTERS Y SETTERS DE DIRECCIÓN ---
    public String getCalle() {
        return calle;
    }
    public void setCalle(String calle) {
        this.calle = calle;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getCodigoPostal() {
        return codigoPostal;
    }
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    
    // Este método es útil para saber el rol dinámicamente
    public String getRol() {
        return this.getClass().getSimpleName(); 
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", login=" + login + ", pass=" + pass + ", nombre=" + nombre + ", apellidos="
                + apellidos + ", calle=" + calle + ", numero=" + numero + ", localidad=" + localidad 
                + ", provincia=" + provincia + ", codigoPostal=" + codigoPostal + ", tipo=" + tipoUsuario + "]";
    }
}