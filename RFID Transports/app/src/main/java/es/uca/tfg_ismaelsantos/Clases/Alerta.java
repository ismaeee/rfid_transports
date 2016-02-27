package es.uca.tfg_ismaelsantos.Clases;

/**
 * Created by Ismael Santos Cabaña on 18/11/15.
 * Clase Alerta
 */
public class Alerta {

    int id, id_tarjeta,id_usuario;
    double saldo_bd,saldo_tarjeta;
    String lugar,sFecha_creacion;
    long fecha_creacion;
    String UID;

    /**
     * Constructor
     * @param id
     * @param id_tarjeta
     * @param id_usuario
     * @param saldo_bd
     * @param saldo_tarjeta
     * @param lugar
     * @param sFecha_creacion
     * @param fecha_creacion
     * @param UID
     */
    public Alerta(int id, int id_tarjeta, int id_usuario,double saldo_bd, double saldo_tarjeta, String lugar,String sFecha_creacion, long fecha_creacion , String UID) {
        this.id = id;
        this.id_tarjeta = id_tarjeta;
        this.id_usuario = id_usuario;
        this.saldo_bd = saldo_bd;
        this.saldo_tarjeta = saldo_tarjeta;
        this.lugar = lugar;
        this.sFecha_creacion = sFecha_creacion;
        this.fecha_creacion = fecha_creacion;
        this.UID = UID;

    }


    /**
     * getId, obteine id
     * @return
     */
    public int getId() { return id; }

    /**
     * setId, modifica id
     * @param id
     */
    public void setId(int id) { this.id = id; }

    /**
     * getId_tarjeta, obtiene id_tarjeta
     * @return
     */
    public int getId_tarjeta() {
        return id_tarjeta;
    }

    /**
     * setId_tarjeta, modifica id_tarjeta
     * @param id_tarjeta
     */
    public void setId_tarjeta(int id_tarjeta) {this.id_tarjeta = id_tarjeta;}

    /**
     * getId_usuario, obtiene el id_usuario
     * @return
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * setId_usuario, modifica el id_usuario
     * @param id_usuario
     */
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    /**
     * getSaldo_bd, otiene saldo_bd
     * @return
     */
    public double getSaldo_bd() {
        return saldo_bd;
    }

    /**
     * setSaldo_bd, modifica saldo_bd
     * @param saldo_bd
     */
    public void setSaldo_bd(double saldo_bd) {
        this.saldo_bd = saldo_bd;
    }

    /**
     * getSaldo_tarjeta, obtiene saldo_tarjeta
     * @return
     */
    public double getSaldo_tarjeta() {
        return saldo_tarjeta;
    }

    /**
     * setSaldo_tarjeta, modifica saldo_tarjeta
     * @param saldo_tarjeta
     */
    public void setSaldo_tarjeta(double saldo_tarjeta) {
        this.saldo_tarjeta = saldo_tarjeta;
    }

    /**
     * getLugar, obtiene lugar
     * @return
     */
    public String getLugar() {return lugar;}

    /**
     * setLugar, modifica lugar
     * @param lugar
     */
    public void setLugar(String lugar) {this.lugar = lugar;}

    /**
     * getFecha_creacion, obtiene fecha_creacion
     * @return
     */
    public long getFecha_creacion() {return fecha_creacion;}

    /**
     * setFecha_creacion, modifica fecha creación
     * @param fecha_creacion
     */
    public void setFecha_creacion(long fecha_creacion) {this.fecha_creacion = fecha_creacion;}

    /**
     * getUID, obtiene UID
     * @return
     */
    public String getUID() {return UID; }

    /**
     * setUID, modifica UID
     * @param UID
     */
    public void setUID(String UID) { this.UID = UID; }

    /**
     * getsFecha_creacion, obtiene sFecha_creacion
     * @return
     */
    public String getsFecha_creacion() {return sFecha_creacion;}

    /**
     * setsFecha_creacion, modifica sFecha_creacion
     * @param sFecha_creacion
     */
    public void setsFecha_creacion(String sFecha_creacion) {this.sFecha_creacion = sFecha_creacion;}

    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        return "Alerta{" +
                "id=" + id +
                ", id_tarjeta=" + id_tarjeta +
                ", id_usuario=" + id_usuario +
                ", saldo_bd=" + saldo_bd +
                ", saldo_tarjeta=" + saldo_tarjeta +
                ", lugar='" + lugar + '\'' +
                ", sFecha_creacion=" + sFecha_creacion +
                ", fecha_creacion=" + fecha_creacion +
                '}';
    }
}
