package es.uca.tfg_ismaelsantos.Clases;

/**
 * Created by Ismael Santos Cabaña on 21/10/15.
 * Clase Tarjeta
 */
public class Tarjeta {

    int id,id_usuario;
    String uid;
    int saldo;
    String fecha_actualizacion,fecha_creacion, fecha_bloqueo;
    String nombreUsuario;
    String fechaBloqueo;
    long lFechaCreacion,lFechaActualizacion,lFechaBloqueo;
    int bloqueada;


    /**
     * Constructor
     * @param id
     * @param id_usuario
     * @param uid
     * @param saldo
     * @param bloqueada
     * @param fecha_actualizacion
     * @param fecha_creacion
     * @param fecha_bloqueo
     * @param lFechaActualizacion
     * @param lFechaCreacion
     * @param lFechaBloqueo
     */
    public Tarjeta(int id, int id_usuario, String uid, int saldo, int bloqueada, String fecha_actualizacion, String fecha_creacion , String fecha_bloqueo, long lFechaActualizacion,long lFechaCreacion,long lFechaBloqueo) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.uid = uid;
        this.saldo = saldo;
        this.fecha_actualizacion = fecha_actualizacion;
        this.fecha_creacion = fecha_creacion;
        this.fecha_bloqueo = fecha_bloqueo;
        this.lFechaActualizacion = lFechaActualizacion;
        this.lFechaCreacion = lFechaCreacion;
        this.lFechaBloqueo = lFechaBloqueo;
        this.bloqueada = bloqueada;
    }


    /**
     * Constructor
     * @param id
     * @param uid
     * @param nombreUsuario
     * @param bloqueada
     */
    public Tarjeta(int id, String uid, String nombreUsuario, int bloqueada) {
        this.id = id;
        this.uid = uid;
        this.nombreUsuario = nombreUsuario;
        this.bloqueada = bloqueada;
    }


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
     * getId_usuario, obtiene id_usuario
     * @return
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * setId_usuario, modifica id_usuario
     * @param id_usuario
     */
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    /**
     * getUid, obtiene uid
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * setUid, modifica uid
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * getSaldo, obtiene saldo
     * @return
     */
    public int getSaldo() {
        return saldo;
    }

    /**
     * setSaldo,modifica saldo
     * @param saldo
     */
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    /**
     * getFecha_actualizacion, obtiene fecha_actualizacion
     * @return
     */
    public String getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    /**
     * setFecha_actualizacion, modifica fecha_actualizacion
     * @param fecha_actualizacion
     */
    public void setFecha_actualizacion(String fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }

    /**
     * getFecha_creacion, obtiene fecha_creacion
     * @return
     */
    public String getFecha_creacion() {
        return fecha_creacion;
    }

    /**
     * setFecha_creacion, modifica fecha_creacion
     * @param fecha_creacion
     */
    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    /**
     * getNombreUsuario, obtiene nombreUsuario
     * @return
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * setNombreUsuario, modfiica NombreUsuario
     * @param nombreUsuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * getFecha_bloqueo, obtiene fecha_bloqueo
     * @return
     */
    public String getFecha_bloqueo() {
        return fecha_bloqueo;
    }

    /**
     * setFecha_bloqueo, modifica fecha_bloqueo
     * @param fecha_bloqueo
     */
    public void setFecha_bloqueo(String fecha_bloqueo) {
        this.fecha_bloqueo = fecha_bloqueo;
    }

    /**
     * getlFechaBloqueo, obtiene lFechaBloqueo
     * @return
     */
    public long getlFechaBloqueo() {
        return lFechaBloqueo;
    }

    /**
     * setlFechaBloqueo, modifica lFechaBloqueo
     * @param lFechaBloqueo
     */
    public void setlFechaBloqueo(long lFechaBloqueo) {
        this.lFechaBloqueo = lFechaBloqueo;
    }

    /**
     * getBloqueada, obtiene bloqueada
     * @return
     */
    public int getBloqueada() {
        return bloqueada;
    }

    /**
     * setBloqueada, modifica bloqueada
     * @param bloqueada
     */
    public void setBloqueada(int bloqueada) {
        this.bloqueada = bloqueada;
    }

    /**
     * getlFechaCreacion, obtiene lFechaCreacion
     * @return
     */
    public long getlFechaCreacion() {
        return lFechaCreacion;
    }

    /**
     * setlFechaCreacion, modifica lFechaCreacion
     * @param lFechaCreacion
     */
    public void setlFechaCreacion(long lFechaCreacion) {
        this.lFechaCreacion = lFechaCreacion;
    }

    /**
     * getlFechaActualizacion, obtiene lFechaActualizacion
     * @return
     */
    public long getlFechaActualizacion() {
        return lFechaActualizacion;
    }

    /**
     * setlFechaActualizacion, modifica lFechaActualizacion
     * @param lFechaActualizacion
     */
    public void setlFechaActualizacion(long lFechaActualizacion) {
        this.lFechaActualizacion = lFechaActualizacion;
    }

    /**
     * getFechaBloqueo, obtiene fechaBloqueo
     * @return
     */
    public String getFechaBloqueo() {
        return fechaBloqueo;
    }

    /**
     * setFechaBloqueo, modifica fechaBloqueo
     * @param fechaBloqueo
     */
    public void setFechaBloqueo(String fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        return "Tarjeta{" +
                "id=" + id +
                ", id_usuario=" + id_usuario +
                ", uid='" + uid + '\'' +
                ", saldo=" + saldo +
                ", fecha_actualizacion='" + fecha_actualizacion + '\'' +
                ", fecha_creacion='" + fecha_creacion + '\'' +
                ", fecha_bloqueo='" + fecha_bloqueo + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", fechaBloqueo='" + fechaBloqueo + '\'' +
                ", lFechaCreacion=" + lFechaCreacion +
                ", lFechaActualizacion=" + lFechaActualizacion +
                ", lFechaBloqueo=" + lFechaBloqueo +
                ", bloqueada=" + bloqueada +
                '}';
    }
}
