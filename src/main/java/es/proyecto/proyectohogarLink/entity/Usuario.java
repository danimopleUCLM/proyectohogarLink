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
    private String direccion;
    
    //Getters y setters
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
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getRol() {
	    return this.getClass().getSimpleName(); 
	}

	//To string
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", login=" + login + ", pass=" + pass + ", nombre=" + nombre + ", apellidos="
				+ apellidos + ", direccion=" + direccion + "]";
	}
	
    
}
