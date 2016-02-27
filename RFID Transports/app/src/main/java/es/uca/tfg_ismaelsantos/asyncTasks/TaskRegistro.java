package es.uca.tfg_ismaelsantos.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.net.ConnectException;

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.MainActivity;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 21/10/15.
 * Clase para realizar una tarea asíncrona para registrar un usuario mediante servicio REST
 */
public class TaskRegistro extends AsyncTask<Void, Void, String> {

    private final String sUser,sPass,sDni,sNombre,sApellidos,sEmail,sTelefono;
    private final EditText user,pass,dni,nombre,apellidos,email,telefono;
    private final int rol;
    private final Context context;
    private String URLRegistro = Util.URL+"/registro";
    private final SharedPreferences sharedpreferences;
    private final Activity activity;
    private final String TAG = "TFG_Login";
    private final String gcm ;


    /**
     * Constructor
     * @param user
     * @param pass
     * @param dni
     * @param nombre
     * @param apellidos
     * @param email
     * @param telefono
     * @param rol
     * @param activity
     * @param context
     * @param sharedpreferences
     * @param gcm
     */
    public TaskRegistro(EditText user, EditText pass, EditText dni, EditText nombre, EditText apellidos, EditText email,EditText telefono, int rol, Activity activity, Context context, SharedPreferences sharedpreferences,String gcm) {
        this.user = user;
        this.sUser = user.getText().toString();

        this.pass = pass;
        this.sPass = pass.getText().toString();

        this.dni = dni;
        this.sDni = dni.getText().toString();

        this.nombre = nombre;
        this.sNombre = nombre.getText().toString();

        this.apellidos = apellidos;
        this.sApellidos = apellidos.getText().toString();

        this.email = email;
        this.sEmail = email.getText().toString();

        this.telefono = telefono;
        this.sTelefono = telefono.getText().toString();

        this.rol = rol;
        this.context = context;
        this.sharedpreferences = sharedpreferences;
        this.activity = activity;
        this.gcm = gcm;
    }



    @Override
    protected String doInBackground(Void... params)  {

        OkHttp servidor = new OkHttp();
        String text = null;
        JSONObject json = new JSONObject();
        Log.d(TAG,"rol: "+rol);
        try {

            json.put("user", sUser);
            json.put("pass", sPass);
            json.put("dni",sDni);
            json.put("rol",rol);
            json.put("nombre",sNombre);
            json.put("apellidos",sApellidos);
            json.put("email", sEmail);
            json.put("telefono",sTelefono);
            json.put("gcm",gcm);

            text = servidor.post(URLRegistro,json.toJSONString());

            return text;

        }catch (ConnectException ce){
            Log.d(TAG,ce.getCause().toString());
            Log.d(TAG,ce.getCause().getMessage());
            Log.d(TAG,ce.getCause().getLocalizedMessage());
            Log.d(TAG,"Error de conexion");

            ce.printStackTrace();
            return "Refused";
        }
        catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            Log.d(TAG, "Otro error");
            return "Fallo";
        }

    }

    @Override
    protected void onPostExecute(final String result) {

        System.out.println(result);
        if(result.length() > 7 && !result.contains("<!DOCTYPE html><html>")) { //Operación correcta

            try {
                org.json.JSONObject jsonN = new org.json.JSONObject(result);

                int error = jsonN.getInt("error");
                if(error == 0){
                    Usuario user = new Usuario(jsonN.getInt("id"),jsonN.getInt("rol"),jsonN.getString("user"),jsonN.getString("dni"),jsonN.getString("nombre"),jsonN.getString("apellidos"),jsonN.getString("email"),jsonN.getString("telefono"));

                    String token = "";
                    try{
                        token = jsonN.getString("token");
                    }catch (Exception e){System.out.println(e.getMessage().toString());}

                    // Se almacena en las preferencias de la aplicación los datos de usuario para tenerlos siempre disponibles

                    sharedpreferences.edit().putInt("id", user.getId()).apply();
                    sharedpreferences.edit().putInt("rol", user.getRol()).apply();
                    sharedpreferences.edit().putString("user", user.getUser()).apply();
                    sharedpreferences.edit().putString("dni", user.getDni()).apply();
                    sharedpreferences.edit().putString("nombre",user.getNombre()).apply();
                    sharedpreferences.edit().putString("apellidos",user.getApellidos()).apply();
                    sharedpreferences.edit().putString("email",user.getEmail()).apply();
                    sharedpreferences.edit().putString("telefono",user.getTelefono()).apply();
                    sharedpreferences.edit().putString("token",token).apply();

                    Intent intentMain = new Intent(activity, MainActivity.class);
                    activity.startActivity(intentMain);


                }else{ //Se informa del error producido
                    switch (error){
                        case -1: user.setError(context.getText(R.string.TaskRegistroUser)); break;
                        case -2: dni.setError(context.getText(R.string.TaskRegistroDNI)); break;
                        case -3: email.setError(context.getText(R.string.TaskRegistroEmail)); break;
                        case -4: user.setError(context.getText(R.string.TaskRegistroUser)); dni.setError(context.getText(R.string.TaskRegistroDNI)); break;
                        case -5: user.setError(context.getText(R.string.TaskRegistroUser)); email.setError(context.getText(R.string.TaskRegistroEmail)); break;
                        case -6: dni.setError(context.getText(R.string.TaskRegistroDNI)); email.setError(context.getText(R.string.TaskRegistroEmail)); break;
                        case -7: user.setError(context.getText(R.string.TaskRegistroUser));dni.setError(context.getText(R.string.TaskRegistroDNI)); email.setError(context.getText(R.string.TaskRegistroEmail)); break;
                    }
                }

            }catch (Exception e){ System.out.print(e.toString()); }


        }else if (result.contains("Refused")){ //Conexión rechazada
            Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

        }else if (result.contains("Fallo")){ //fallo
            Toast.makeText(context, context.getText(R.string.TaskLoginPassword), Toast.LENGTH_SHORT).show();

        }else{//Error del servidor
            Toast.makeText(context, context.getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();

        }

    }


}
