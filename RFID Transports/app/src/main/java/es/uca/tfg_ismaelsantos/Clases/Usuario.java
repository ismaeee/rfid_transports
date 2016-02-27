package es.uca.tfg_ismaelsantos.Clases;

/**
 * Created by Ismael Santos Cabaña on 21/10/15.
 */
public class Usuario {

    private int id,rol;
    private String user,dni,nombre,apellidos,email,telefono;

    /**
     * Constructor
     * @param id
     * @param rol
     * @param user
     * @param dni
     * @param nombre
     * @param apellidos
     * @param email
     * @param telefono
     */
    public Usuario(int id, int rol, String user, String dni, String nombre, String apellidos, String email,String telefono) {
        this.id = id;
        this.rol = rol;
        this.user = user;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    /**
     * Constructor nulo
     */
    public Usuario (){}


    /**
     * getId, obtiene id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * setId, modifica id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getRol, obtiene rol
     * @return
     */
    public int getRol() {
        return rol;
    }

    /**
     * setRol, modifica rol
     * @param rol
     */
    public void setRol(int rol) {
        this.rol = rol;
    }

    /**
     * getUser, obtiene user
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * setUser, modifica user
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * getDni, obtiene dni
     * @return
     */
    public String getDni() {
        return dni;
    }

    /**
     * setDni, modifica dni
     * @param dni
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * getNombre, obtiene nombre
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * setNombre, modifica nombre
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * getApellidos, obtiene apellidos
     * @return
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * setApellidos, modifica apellidos
     * @param apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * getEmail, obteine email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * setEmail, modifica email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getTelefono, obtiene telefono
     * @return
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * setTelefono, modifica telefono
     * @param telefono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", rol=" + rol +
                ", user='" + user + '\'' +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
