package es.uca.tfg_ismaelsantos.GCM;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import es.uca.tfg_ismaelsantos.notificaciones_rfid.AlertaActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertasUsuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.MainActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.SolicitudesAdminActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.TarjetaActivity;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 5/12/15.
 * Manejador de notificaciones de GCM
 */
public class GcmMessageHandler extends IntentService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    private static String TAG = "TFG_Notificacion";
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"OnHandleIntent");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG,messageType);

        String title = extras.getString("title");
        String body = extras.getString("body");
        String action = extras.getString("action");
        Log.i("TFG_GCM", "Acción a ejecutar: " + action);

        int idUsuario = Integer.parseInt(extras.getString("idUsuario", "0"));
        int idAlert = Integer.parseInt(extras.getString("idAccion", "0"));

        Log.d(TAG,"idUsuario+ :"+extras.getString("idUsuario"));
        Log.d(TAG,"idAccion+ :"+extras.getString("idAccion"));


        Log.d("TFG_AlertaActivity","IdAlerta: "+idAlert+" IdUsuario: "+idUsuario);

        SharedPreferences sharedPreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        int idPreferences = sharedPreferences.getInt("id",0);
        if( idPreferences != 0){ // Si hay iniciada alguna sesión
            if(action.contentEquals("alerta_fraude")){
                createNotificationAlerta(title, body, sharedPreferences, idAlert);
                GcmBroadcastReceiver.completeWakefulIntent(intent);
            }else if(action.contentEquals("alerta_saldo")) {
                if((idPreferences == idUsuario)){
                    createNotificationSaldo(title, body, sharedPreferences, idAlert);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                }
            }else if(action.contentEquals("alerta_bloqueo")) {
                if((idPreferences == idUsuario)) {
                    createNotificationBloqueo(title, body, sharedPreferences, idAlert);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                }
            }else if(action.contentEquals("alerta_admin")) {
                if((sharedPreferences.getInt("rol",0) == 3) && (idPreferences == idUsuario)){
                    createNotificationAdminPeticion(title,body,sharedPreferences,idAlert);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                }
            }else if(action.contentEquals("alerta_admin_aceptada")) {
                if((sharedPreferences.getInt("id",0) == idAlert) && (idPreferences == idUsuario)){
                    createNotificationAdminPeticionAceptada(title, body, sharedPreferences, idAlert);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                }
            }else if(action.contentEquals("alerta_rol_user")) {
                if((sharedPreferences.getInt("id",0) == idAlert) && (idPreferences == idUsuario)){
                    createNotificationRolUser(title, body, sharedPreferences, idAlert);
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                }
            }
        }


    }


    /**
     * Método para crear notificaciones, es genérico, todos los demás metodos lo usan
     * @param resultIntent
     * @param color
     * @param title
     * @param body
     * @param info
     */
    @TargetApi(21)
    private void createNotificationGeneral(Intent resultIntent, int color, String title, String body,String info){

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;


        Context context = getBaseContext();
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_notificaciones_mini)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo(info)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setLights(Color.rgb(255, 143, 53), 1000, 1000);

        if(currentapiVersion >= 21){
            mBuilder.setColor(color);

        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());

    }


    /**
     * método para crear notificación de alerta
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationAlerta(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada");
        Intent  resultIntent  = null;


        int rol = sharedPreferences.getInt("rol", 0);

        if((rol == 1)||(rol==3)){
            resultIntent = new Intent(this, AlertaActivity.class);
            resultIntent.setAction("notificacion");
            resultIntent.putExtra("idAccion", idAlert);
        }else{
            resultIntent = new Intent(this,MainActivity.class);
            FragmentAlertasUsuario.servidor = true;
            resultIntent.putExtra("fragment",1);
        }

        createNotificationGeneral(resultIntent, 0xffcc0000, title, body, "Fraud");
    }


    /**
     * crear Notificación de bloqueo
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationBloqueo(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada");

        Intent  resultIntent = new Intent(this, TarjetaActivity.class);

        Bundle b = new Bundle();
        b.putInt("id", idAlert);
        resultIntent.putExtra("bundle", b);

        createNotificationGeneral(resultIntent,0xffcc0000,title,body,"Block");

    }


    /**
     * Crear notificación de petición de administrador
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationAdminPeticion(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada");
        Intent  resultIntent  = new Intent(this, SolicitudesAdminActivity.class);
        createNotificationGeneral(resultIntent, 0xffcc0000, title, body, "Admin");

    }

    /**
     * Crear notificación de amdinistración de petición aceptada
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationAdminPeticionAceptada(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada");
        sharedPreferences.edit().putInt("rol",1).apply(); //Indicamos que tiene rol de admin

        Intent  resultIntent  = new Intent(this, MainActivity.class);
        createNotificationGeneral(resultIntent, 0xffcc0000, title, body, "Admin");

    }

    /**
     * Crear notificación de cambio de rol
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationRolUser(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada");
        sharedPreferences.edit().putInt("rol",2).apply(); //Indicamos que tiene rol de admin

        Intent  resultIntent  = new Intent(this, MainActivity.class);
        createNotificationGeneral(resultIntent, 0xffcc0000, title, body, "User");

    }


    /**
     * Crea notificación de poco saldo
     * @param title
     * @param body
     * @param sharedPreferences
     * @param idAlert
     */
    private void createNotificationSaldo(String title, String body, SharedPreferences sharedPreferences, int idAlert) {

        Log.d(TAG, "Notificación creada, idAlert: "+idAlert);


        Intent  resultIntent  = new Intent(this, TarjetaActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", idAlert);
        resultIntent.putExtra("bundle", b);

        createNotificationGeneral(resultIntent, 0xffcc0000, title, body, "Balance");

    }

}
