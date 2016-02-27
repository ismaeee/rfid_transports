package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.GCM.GCMClientManager;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 23/11/15.
 * Restablecer la contraseña
 */
public class RestablecerPass extends AppCompatActivity {

    private int id;
    private String code;
    private EditText pass1,pass2;
    private boolean enviar = true;
    //GCM
    private GCMClientManager gcmClientManager;
    String gcmIdDeviceRegistration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        //GCM
        gcmClientManager = new GCMClientManager(this, Util.PROJECT_NUMBER);
        gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                gcmIdDeviceRegistration = registrationId;
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            id = Integer.parseInt(uri.getQueryParameter("id"));
            code  = uri.getQueryParameter("code");

            //Toast.makeText(getApplicationContext(),"ID: "+id+" - code: "+code,Toast.LENGTH_SHORT).show();
        }


        pass1 = (EditText)findViewById(R.id.pass1);
        pass2 = (EditText)findViewById(R.id.pass2);
        pass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviaPass(null);
                    return true;
                }
                return false;
            }
        });

    }





    /**
     * Envía el contenido de formulario para cambiar contraseña
     */
    public void enviaPass(View view){
        enviar = true;

        validaExiste(pass1);
        validaExiste(pass2);

        if(enviar){
            validaPass(pass1);
            if(enviar){
                comparaPass(pass1,pass2);
                if(enviar){
                    new TaskNewPassword(pass1,id,code,getApplicationContext(),RestablecerPass.this, getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE),gcmIdDeviceRegistration).execute();
                }
            }
        }

        if(!enviar){
            Toast.makeText(getApplicationContext(),getText(R.string.revisaDatos),Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * Comprueba si el campo está vacío
     * @param editText
     * @return
     */
    public boolean validaExiste(EditText editText) {

        System.out.println(editText.getText().toString()+": "+editText.getText().toString().length());


        if (editText == null) {
            editText.setError(getText(R.string.campoObligatorio));
            enviar = false;
            return false;
        }else if(editText.getText().toString().length() == 0){
            editText.setError(getText(R.string.campoObligatorio));
            enviar = false;
            return false;
        }
        return true;
    }


    /**
     * Comprueba si la conrtaseña tiene como mínimo 4 caracteres
     * @param pass
     * @return
     */
    public boolean validaPass(EditText pass){

        if( pass.getText().toString().length() < 4 ){
            pass.setError(getText(R.string.passCorta));
            enviar = false;
            return false;
        }
        return true;
    }


    /**
     * Compara si ambas contraseñas son iguales
     * @param pass1
     * @param pass2
     * @return
     */
    public boolean comparaPass(EditText pass1, EditText pass2){
        String password1 = pass1.getText().toString();
        String password2 = pass2.getText().toString();

        if(!password1.contentEquals(password2)){
            pass2.setError(getText(R.string.passNoCoincide));

            enviar = false;
            return false;
        }
        return true;
    }


    /**
     * Task para enviar nueva contraseña
     */
    public class TaskNewPassword extends AsyncTask<Void, Void, String> {

        private EditText pass;
        private final String sPass;
        private Context context;
        private final String URL = Util.URL + "/restablecerPass";
        private final int id;
        private final String code;
        private Activity activity;
        private SharedPreferences sharedPreferences;
        private String gcm;

        public TaskNewPassword(EditText pass, int id, String code, Context context, Activity activity, SharedPreferences sharedPreferences, String gcm) {
            this.pass = pass;
            this.context = context;
            this.sPass = pass.getText().toString();
            this.id = id;
            this.code = code;
            this.activity = activity;
            this.sharedPreferences = sharedPreferences;
            this.gcm = gcm;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttp servidor = new OkHttp();
            String text = null;
            JSONObject json = new JSONObject();

            try {
                json.put("pass", sPass);
                json.put("id", id);
                json.put("code", code);
                json.put("gcm",gcm);
                text = servidor.post(URL, json.toJSONString());
                Log.d("TFG_Restablecer", text);

            } catch (IOException ce) {
                ce.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(final String results) {
            System.out.println(results);
            if (results.length() > 7 && !results.contains("<!DOCTYPE html><html>")) {

                try {
                    org.json.JSONObject jsonN = new org.json.JSONObject(results);

                    int error = jsonN.getInt("error");
                    if (error == 0) {
                        Usuario user = new Usuario(jsonN.getInt("id"), jsonN.getInt("rol"), jsonN.getString("user"), jsonN.getString("dni"), jsonN.getString("nombre"), jsonN.getString("apellidos"), jsonN.getString("email"), jsonN.getString("telefono"));

                        String token = "";
                        try {
                            token = jsonN.getString("token");
                        } catch (Exception e) {
                            Log.d("TFG_error", e.getMessage().toString());
                        }

                        sharedPreferences.edit().putInt("id", user.getId()).apply();
                        sharedPreferences.edit().putInt("rol", user.getRol()).apply();
                        sharedPreferences.edit().putString("user", user.getUser()).apply();
                        sharedPreferences.edit().putString("dni", user.getDni()).apply();
                        sharedPreferences.edit().putString("nombre", user.getNombre()).apply();
                        sharedPreferences.edit().putString("apellidos", user.getApellidos()).apply();
                        sharedPreferences.edit().putString("email", user.getEmail()).apply();
                        sharedPreferences.edit().putString("telefono", user.getTelefono()).apply();
                        sharedPreferences.edit().putString("token", token).apply();

                        Intent intentMain = new Intent(activity, MainActivity.class);
                        activity.startActivity(intentMain);


                    } else {
                        String mensaje = "";
                        switch (error) {
                            case -1:
                                mensaje = getString(R.string.caducado);
                                break;
                            default:
                                mensaje = getString(R.string.TaskErrorServer);
                                break;

                        }
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    System.out.print(e.toString());
                }


            } else if (results.contains("Refused")) {
                Toast.makeText(context, getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(context, getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();

            }


        }

    }




}
