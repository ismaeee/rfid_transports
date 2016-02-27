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

import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 21/11/15.
 * Clase para realizar una tarea asíncrona para enviar email al usuario mediante servicio REST
 */
public class TaskEmail extends AsyncTask<Void, Void, String> {

    private final int id;
    private final String token;
    private final String to;
    private final String contenido;
    private final String TAG = "TFG_Email";
    private final String URLEmail = Util.URL + "/enviarEmail";
    private final Context context;
    private SmoothProgressBar smoothProgressBar;
    private Activity activity;
    private View view;


    /**
     * Constructor
     * @param id
     * @param token
     * @param to
     * @param contenido
     * @param context
     * @param activity
     * @param smoothProgressBar
     * @param view
     */
    public TaskEmail(int id, String token, String to, String contenido, Context context,Activity activity, SmoothProgressBar smoothProgressBar,View view) {
        this.id = id;
        this.token = token;
        this.to = to;
        this.contenido = contenido;
        this.context = context;
        this.smoothProgressBar = smoothProgressBar;
        this.activity = activity;
        this.view = view;
    }


    @Override
    protected String doInBackground(Void... params) {
        OkHttp servidor = new OkHttp();
        String text = null;
        JSONObject json = new JSONObject();

        try {
            Log.d(TAG, "url: " + URLEmail);
            json.put("id", id);
            json.put("token", token);
            json.put("to", to);
            json.put("contenido", contenido);

            text = servidor.post(URLEmail, json.toJSONString());
            return text;

        } catch (ConnectException ce) {
            Log.d(TAG, ce.getCause().toString());
            Log.d(TAG, ce.getCause().getMessage());
            Log.d(TAG, ce.getCause().getLocalizedMessage());
            Log.d(TAG, "Error de conexion");

            ce.printStackTrace();
            return "Refused";
        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "Otro error");
            return "Fallo";
        }

    }

    @Override
    protected void onPostExecute(final String result) {

        Log.d(TAG, result);
        if (!result.contains("<!DOCTYPE html><html>")) {
            if (result.contentEquals("ok")) {
                //Toast.makeText(context, "Enviado corractamente", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, context.getText(R.string.TaskEnviarEmailOk), Snackbar.LENGTH_LONG).show();
            }else if(result.contentEquals("token")) {
                Log.d(TAG, "Error token");
                new Sesion().muestraAlertaSesion(context, activity);
            }else {
                Toast.makeText(context, context.getText(R.string.TaskEnviarEmailError), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();
        }

        smoothProgressBar.progressiveStop();

    }
}

