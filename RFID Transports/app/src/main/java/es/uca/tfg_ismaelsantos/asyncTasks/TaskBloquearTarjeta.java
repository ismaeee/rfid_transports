package es.uca.tfg_ismaelsantos.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.net.ConnectException;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.ListAdapter.TarjetaListAdapter;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.BloqueadasActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 21/11/15.
 * Clase para realizar una tarea asíncrona para bloquaer tarjeta mediante servicio REST
 */
public class TaskBloquearTarjeta extends AsyncTask<Void, Void, String> {

    private final int id;
    private final int idTarjeta;
    private final String token;
    private final String TAG = "TFG_BloquearTarjeta";
    private final String URLBloquear = Util.URL + "/bloquearTarjeta";
    private final Context context;
    private SmoothProgressBar smoothProgressBar;
    private View v;
    private Activity activity;
    private TarjetaListAdapter adapter;
    private int position;
    private Tarjeta tarjeta;

    /**
     * Constructor de TaskBloquearTarjeta, es usado desde AlertaActivity
     * @param id
     * @param token
     * @param idTarjeta
     * @param context
     * @param activity
     * @param smoothProgressBar
     * @param v
     */
    public TaskBloquearTarjeta(int id, String token, int idTarjeta, Context context,Activity activity,SmoothProgressBar smoothProgressBar,View v) {
        this.id = id;
        this.token = token;
        this.idTarjeta = idTarjeta;
        this.context = context;
        this.smoothProgressBar = smoothProgressBar;
        this.v = v;
        this.activity = activity;

    }


    /**
     * Constructor de TaskBloquearTarjeta, es usado desde FragmentTarjetas
     * @param id
     * @param token
     * @param idTarjeta
     * @param context
     * @param activity
     * @param smoothProgressBar
     * @param v
     * @param adapter
     * @param position
     * @param tarjeta
     */
    public TaskBloquearTarjeta(int id, String token, int idTarjeta, Context context,Activity activity,SmoothProgressBar smoothProgressBar,View v,TarjetaListAdapter adapter, int position, Tarjeta tarjeta) {
        this.id = id;
        this.token = token;
        this.idTarjeta = idTarjeta;
        this.context = context;
        this.smoothProgressBar = smoothProgressBar;
        this.v = v;
        this.activity = activity;
        this.adapter = adapter;
        this.position = position;
        this.tarjeta = tarjeta;
    }



    @Override
    protected String doInBackground(Void... params) {
        String text;
        JSONObject json = new JSONObject();
        OkHttp servidor = new OkHttp();

        try {
            Log.d(TAG, "url: " + URLBloquear);
            json.put("id", id);
            json.put("token", token);
            json.put("idTarjeta", idTarjeta);


            text = servidor.post(URLBloquear, json.toJSONString());

            return text;

        } catch (ConnectException ce) {
            Log.d(TAG, ce.getCause().toString());
            Log.d(TAG, ce.getCause().getMessage());
            Log.d(TAG, ce.getCause().getLocalizedMessage());
            Log.d(TAG, "Error de conexion");

            ce.printStackTrace();
            return "Refused";//-1;
        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "Otro error");
            return "Fallo";//-1;
        }

    }

    @Override
    protected void onPostExecute(final String result) {

        Log.d(TAG, result);
        if (!result.contains("UNIQUE_ID")) { //Si no contiene error de unicidad
            if (result.contentEquals("ok")) { //si contiene "ok" como respuesta se considera que se ha bloqueado correctamente
                Log.d(TAG, "TarjetaBloqueada");
                if(adapter != null) adapter.setData(tarjeta,position);
                Snackbar.make(v, v.getResources().getText(R.string.TaskBloquearTarjetaOk), Snackbar.LENGTH_LONG).show();

            }else if(result.contentEquals("token")){ //Si se produce error de token (otro usuario ha logueado desde su cuenta, envía mensaje de alerta de sesión
                Log.d(TAG, "Error token");
                new Sesion().muestraAlertaSesion(context, activity);


            }else Toast.makeText(context, v.getResources().getText(R.string.TaskBloquearTarjetaError), Toast.LENGTH_SHORT).show(); //Otro tipo de error
        } else {
            Toast.makeText(context, v.getResources().getText(R.string.TaskBloquearTarjetaUnique), Toast.LENGTH_SHORT).show(); //Error de unicidad, indica que la tarjeta ya está bloqueada

        }


        BloqueadasActivity.servidor = true;

        //Se marca como invisible el progressbar y se para
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.INVISIBLE);
    }


}
