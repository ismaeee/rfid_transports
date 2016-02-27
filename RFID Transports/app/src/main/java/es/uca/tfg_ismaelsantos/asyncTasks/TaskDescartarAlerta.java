package es.uca.tfg_ismaelsantos.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.MainActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 21/11/15.
 * Clase para realizar una tarea asíncrona para descartar una alerta mediante servicio REST
 */
public class TaskDescartarAlerta extends AsyncTask<Void, Void, String>{

    private final int id;
    private final int idAlerta;
    private final String token;
    private final String TAG = "TFG_DescartarAlerta";
    private final String URLAlerta = Util.URL + "/descartarAlerta";
    private final Context context;
    private final int position;
    private SmoothProgressBar smoothProgressBar;
    private Activity activity;

    /**
     * Constructor
     * @param id
     * @param token
     * @param idAlerta
     * @param context
     * @param activity
     * @param position
     * @param smoothProgressBar
     */
    public TaskDescartarAlerta(int id, String token, int idAlerta,Context context,Activity activity,int position,SmoothProgressBar smoothProgressBar) {
        this.id = id;
        this.token = token;
        this.idAlerta = idAlerta;
        this.context = context;
        this.position = position;
        this.smoothProgressBar = smoothProgressBar;
        this.activity  = activity;
    }


    @Override
    protected String doInBackground(Void... params) {
        JSONObject json = new JSONObject();
        String response = "";
        OkHttp servidor = new OkHttp();
        try {
            json.put("id", id);
            json.put("token", token);
            json.put("idAlerta", idAlerta);

            response = servidor.post(URLAlerta, json.toJSONString());
            Log.d(TAG,"TENEMOS ESTO CON EL NUEVO: "+response);
        }catch (IOException ioe){
            ioe.getMessage();
        }
        return response;
    }


    @Override
    protected void onPostExecute(final String result) {

        Log.d(TAG, result);
        if (!result.contains("<!DOCTYPE html><html>")) {
            if(result.contentEquals("ok"))  { //Si se ha realziado correctamente

                //Eliminamos alerta de la base de datos e indicamos que la lista se ha modificado
                AlertaDBAdapter dbAdapter = new AlertaDBAdapter(context);
                dbAdapter.open();
                dbAdapter.deleteAlert(idAlerta);
                dbAdapter.close();

                //Lanzamos el intent para que vuelva a la pantalla principal
                Intent a = new Intent(context, MainActivity.class);
                a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(a);
                Toast.makeText(context, context.getText(R.string.TaskDescartarTarjetaOk), Toast.LENGTH_LONG).show();
            }else if(result.contentEquals("token")){ //Si se produce error de token (otro usuario ha logueado desde su cuenta, envía mensaje de alerta de sesión
                Log.d(TAG, "Error token");
                new Sesion().muestraAlertaSesion(context, activity);
            }else {
                Toast.makeText(context,context.getText(R.string.TaskDescartarTarjetaError),Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,context.getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();
        }

        smoothProgressBar.progressiveStop();
    }


}
