package es.uca.tfg_ismaelsantos.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertas;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertasUsuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.LoginActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;


/**
 * Created by Ismael Santos Cabaña on 30/10/15.
 */

public class Sesion{

    public  static boolean exist = false;

    /**
     * Método para mostrar alerta de sesión
     * @param context
     * @param activity
     */
    public synchronized void muestraAlertaSesion(final Context context, final Activity activity){

        exist = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(context.getString(R.string.tituloSesion));
        alertDialog.setMessage(context.getString(R.string.mensajeSesion));

        alertDialog.setPositiveButton(context.getText(R.string.salir), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                logOut(context,activity);
            }
        });


        alertDialog.setNegativeButton(context.getText(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();


    }

    /**
     * Método para cerrar sesión
     * @param context
     * @param activity
     */
    private void logOut(Context context,Activity activity){

        activity.getSharedPreferences(Util.MyPREFERENCES,Context.MODE_PRIVATE).edit().clear().apply();
        AlertaDBAdapter dbAlerta = new AlertaDBAdapter(context);
        dbAlerta.open();
        dbAlerta.deleteAllAlert();
        dbAlerta.close();

        TarjetaDBAdapter dbTarjeta = new TarjetaDBAdapter(context);
        dbTarjeta.open();
        dbTarjeta.deleteAllTarjeta();
        dbTarjeta.deleteAllTarjetaBloqueada();
        dbTarjeta.close();

        UserDBAdapter dbUser = new UserDBAdapter(context);
        dbUser.open();
        dbUser.deleteAllUserBusqueda();
        dbUser.deleteAllUser();
        dbUser.close();

        FragmentAlertas.noshow = true;
        FragmentAlertasUsuario.servidor = true;

        activity.startActivity(new Intent(activity, LoginActivity.class));

    }
}