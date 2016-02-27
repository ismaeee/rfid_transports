package es.uca.tfg_ismaelsantos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertas;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertasUsuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.LoginActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;


/**
 * Created by Ismael Santos Cabaña on 21/09/15.
 */
public class Util {
    public static String MyPREFERENCES = "MyPrefs";

    /**
     * Dirección IP del servicio REST
     */
    //public static String ip = "192.168.1.4"; //macbook
    //public static String ip = "10.0.2.2"; //emulator
    //public static String ip = "10.182.103.204";//ESI
    public static String ip = "51.254.114.142"; //Servidor ovh de Ismael Santos

    public static String URL = "http://"+ip+":8080/TFG-RFID/notifications/principal";


    //DB
    public static final String DATABASE_NAME = "RFID";
    public static final String SQLITE_TABLE_ALERTA = "Alertas";
    public static final String SQLITE_TABLE_USER = "Usuarios";
    public static final String SQLITE_TABLE_USER_BUSQUEDA = "UsuariosBusqueda";
    public static final String SQLITE_TABLE_TARJETA_BUSQUEDA = "TarjetasBusqueda";
    public static final String SQLITE_TABLE_TARJETA = "Tarjetas";

    public static final int DATABASE_VERSION = 11;

    public static final String PROJECT_NUMBER = "448096938190";


    /**
     * Obtener fecha en formato deseado
     * @param fecha
     * @param calendar
     * @return
     */
    public static String getFechaString(long fecha, Calendar calendar) {
        calendar.setTimeInMillis(fecha);
        String dia, mes, hora, minuto;
        dia = String.valueOf(calendar.get(Calendar.DATE));
        if (dia.length() < 2) {
            dia = 0 + dia;
        }
        mes = String.valueOf((calendar.get(Calendar.MONTH) + 1));
        if (mes.length() < 2) {
            mes = 0 + mes;
        }

        hora = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        if (hora.length() < 2) {
            hora = 0 + hora;
        }

        minuto = String.valueOf(calendar.get(Calendar.MINUTE));
        if (minuto.length() < 2) {
            minuto = 0 + minuto;
        }


        return hora + ":" + minuto + " " + dia + "-" + mes + "-" + calendar.get(Calendar.YEAR);
    }


    /**
     * Obtener fecha en formato deseado (con segundos)
     * @param fecha
     * @param calendar
     * @return
     */
    public static String getFechaStringWithSecond(long fecha, Calendar calendar) {
        calendar.setTimeInMillis(fecha);
        String dia, mes, hora, minuto,segundo;
        dia = String.valueOf(calendar.get(Calendar.DATE));
        if (dia.length() < 2) {
            dia = 0 + dia;
        }
        mes = String.valueOf((calendar.get(Calendar.MONTH) + 1));
        if (mes.length() < 2) {
            mes = 0 + mes;
        }

        hora = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        if (hora.length() < 2) {
            hora = 0 + hora;
        }

        minuto = String.valueOf(calendar.get(Calendar.MINUTE));
        if (minuto.length() < 2) {
            minuto = 0 + minuto;
        }
        segundo = String.valueOf(calendar.get(Calendar.SECOND));
        if (segundo.length() < 2) {
            segundo = 0 + segundo;
        }

        return hora + ":" + minuto+":" + segundo + " " + dia + "-" + mes + "-" + calendar.get(Calendar.YEAR);
    }




}
