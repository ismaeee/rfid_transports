package es.uca.tfg_ismaelsantos.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.net.ConnectException;

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.MainActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 21/10/15.
 * Clase para realizar una tarea asíncrona para enviar el Login de usuario mediante servicio REST
 */
public class TaskLogin extends AsyncTask<Void, Void, String> {

    private final String user;
    private final String pass;
    private final Context context;
    private final String URLLogin = Util.URL + "/login";
    private final SharedPreferences sharedpreferences;
    private final Activity activity;
    private final String TAG = "TFG_Login";
    private final String gcm;

    /**
     * Constructor
     * @param user
     * @param pass
     * @param activity
     * @param context
     * @param sharedpreferences
     * @param gcm
     */
    public TaskLogin(String user, String pass, Activity activity, Context context, SharedPreferences sharedpreferences,String gcm) {
        this.user = user.replaceAll(" ","");
        this.pass = pass.replaceAll(" ","");
        this.context = context;
        this.sharedpreferences = sharedpreferences;
        this.activity = activity;
        this.gcm = gcm;
    }



    @Override
    protected void onPreExecute() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected String doInBackground(Void... params) {
        OkHttp servidor = new OkHttp();
        String text = null;
        JSONObject json = new JSONObject();

        try {
            Log.d(TAG, "url: " + URLLogin);
            json.put("user", user);
            json.put("pass", pass);
            json.put("gcm",gcm);

            text = servidor.post(URLLogin, json.toJSONString());

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

        Log.d(TAG, "Resultado"+result);
        if (result.length() > 7 && !result.contains("<!DOCTYPE html><html>")) { //Operación correcta

            try {
                org.json.JSONObject jsonN = new org.json.JSONObject(result);

                Usuario user = new Usuario(jsonN.getInt("id"), jsonN.getInt("rol"), jsonN.getString("user"), jsonN.getString("dni"), jsonN.getString("nombre"), jsonN.getString("apellidos"), jsonN.getString("email"), jsonN.getString("telefono"));

                String token = "";
                try {
                    token = jsonN.getString("token");
                } catch (Exception e) {
                    System.out.println(e.getMessage().toString());
                }

                // Se almacena en las preferencias de la aplicación los datos de usuario para tenerlos siempre disponibles
                sharedpreferences.edit().putInt("id", user.getId()).apply();
                sharedpreferences.edit().putInt("rol", user.getRol()).apply();
                sharedpreferences.edit().putString("user", user.getUser()).apply();
                sharedpreferences.edit().putString("dni", user.getDni()).apply();
                sharedpreferences.edit().putString("nombre", user.getNombre()).apply();
                sharedpreferences.edit().putString("apellidos", user.getApellidos()).apply();
                sharedpreferences.edit().putString("email", user.getEmail()).apply();
                sharedpreferences.edit().putString("telefono", user.getTelefono()).apply();
                sharedpreferences.edit().putString("token", token).apply();


            } catch (Exception e) {
                System.out.print(e.toString());
            }

            Intent intentMain = new Intent(activity, MainActivity.class);
            activity.startActivity(intentMain);

        } else if(result.contentEquals("")){ //Error en la contraseña
            Toast.makeText(context, context.getText(R.string.TaskLoginPassword), Toast.LENGTH_SHORT).show();
        }
        else if (result.contains("Refused")) { //Conexión rechazada
            Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

        }
        else { //Error en el servidor
            Toast.makeText(context, context.getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();

        }

    }


}
