package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import es.uca.tfg_ismaelsantos.GCM.GCMClientManager;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskLogin;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskRegistro;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 20/9/15.
 * Login (y todo_ lo relacionado) en el sistema
 */
public class LoginActivity extends AppCompatActivity {

    //TextView bienvenida;
    LinearLayout seleccion;
    RelativeLayout loginLayout,registroLayout,recuperarLayout;
    EditText user,pass,user2,pass1,pass2,dni,nombre,apellidos,email,telefono;
    EditText emailRecuperar;
    boolean enviar = false;
    String TAG = "TFG_Login";
    SharedPreferences sharedpreferences;
    int position = 1; //Position  1 Admin 2 User
    private RadioGroup radioGroup;
    private RadioButton rUser,rAdmin;
    boolean doubleBackToExitPressedOnce = false;

    //GCM
    private GCMClientManager gcmClientManager;
    String gcmIdDeviceRegistration = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);

        //GCM
        gcmClientManager = new GCMClientManager(this, Util.PROJECT_NUMBER);
        gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler(){
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                gcmIdDeviceRegistration = registrationId;
            }
            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });

        //Principal
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        registroLayout = (RelativeLayout) findViewById(R.id.registroLayout);
        recuperarLayout = (RelativeLayout) findViewById(R.id.recuperarLayout);
        seleccion = (LinearLayout) findViewById(R.id.seleccion);

        //Login
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviaLogin(null);
                    return true;
                }
                return false;
            }
        });

        //Registro
        user2 = (EditText) findViewById(R.id.user2);
        user2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validaExiste(user2);
                    pass1.requestFocus();
                    return true;
                }
                return false;
            }
        });

        pass1 = (EditText) findViewById(R.id.pass1);
        pass1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(validaExiste(pass1)){validaPass(pass1);}
                    pass2.requestFocus();
                    return true;
                }
                return false;
            }
        });

        pass2 = (EditText) findViewById(R.id.pass2);
        pass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(validaExiste(pass2)){
                        if(validaPass(pass2)){
                            comparaPass(pass1,pass2);
                        }

                    }
                    dni.requestFocus();
                    return true;
                }
                return false;
            }
        });
        dni = (EditText) findViewById(R.id.dni);
        dni.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() == 8){
                    dni.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else if(s.length() == 9){
                    dni.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        dni.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(validaExiste(dni)){validaDNI(dni);}
                    nombre.requestFocus();
                    return true;
                }
                return false;
            }
        });


        nombre = (EditText) findViewById(R.id.nombre);
        nombre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validaExiste(nombre);
                    apellidos.requestFocus();
                    return true;
                }
                return false;
            }
        });
        apellidos = (EditText) findViewById(R.id.apellidos);
        apellidos.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validaExiste(apellidos);
                    email.requestFocus();
                    return true;
                }
                return false;
            }
        });

        email = (EditText) findViewById(R.id.email);
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (validaExiste(email))
                        validaEmail(email);
                    telefono.requestFocus();
                    return true;
                }
                return false;
            }
        });
        telefono = (EditText) findViewById(R.id.telefono);
        telefono.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviaRegistro(null);
                    return true;
                }
                return false;
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        rAdmin = (RadioButton) findViewById(R.id.rAdmin);
        rUser = (RadioButton) findViewById(R.id.rUser);

        //recuperar
        emailRecuperar = (EditText) findViewById(R.id.emailRecuperar);
        emailRecuperar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviaRecuperar(null);
                    return true;
                }
                return false;
            }
        });
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Si está en la pantalla de Selección y presiona el botón Atrás, cerrará la aplicación
            if(seleccion.getVisibility() == View.VISIBLE){
                this.finishAffinity();

            }else if(recuperarLayout.getVisibility() == View.VISIBLE){
                seleccion.setVisibility(View.INVISIBLE);
                registroLayout.setVisibility(View.INVISIBLE);
                recuperarLayout.setVisibility(View.INVISIBLE);
                loginLayout.setVisibility(View.VISIBLE);

                loginLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
            }else{
                seleccion.setVisibility(View.VISIBLE);
                registroLayout.setVisibility(View.INVISIBLE);
                recuperarLayout.setVisibility(View.INVISIBLE);
                loginLayout.setVisibility(View.INVISIBLE);

                seleccion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }







    /**
     * Muestra el formulario de login
     * @param v
     */
    public void onClickLogin(View v){  //Inicia Login
        seleccion.setVisibility(View.INVISIBLE);
        registroLayout.setVisibility(View.INVISIBLE);
        recuperarLayout.setVisibility(View.INVISIBLE);
        loginLayout.setVisibility(View.VISIBLE);
        loginLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));

    }


    /**
     * Muestra el formulario de registro
     * @param v
     */
    public void onClickRegistro(View v){  //Inicia Registro
        seleccion.setVisibility(View.INVISIBLE);
        loginLayout.setVisibility(View.INVISIBLE);
        recuperarLayout.setVisibility(View.INVISIBLE);
        registroLayout.setVisibility(View.VISIBLE);
        registroLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
    }



    /**
     * Muestra el contenido del formulario para recuperar contraseña
     */
    public void onClickRecuperar(View view){
        seleccion.setVisibility(View.INVISIBLE);
        loginLayout.setVisibility(View.INVISIBLE);
        registroLayout.setVisibility(View.INVISIBLE);
        recuperarLayout.setVisibility(View.VISIBLE);
        recuperarLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
    }


    /**
     * Envía el contenido de formulario para iniciar sesión
     */
    public void enviaLogin(View view){
        if(validadoresLogin()){
            Log.d(TAG,"Validación previa pasada correctamente");
            TaskLogin taskLoginTask = new TaskLogin(user.getText().toString(),pass.getText().toString(),LoginActivity.this,getApplicationContext(),sharedpreferences,gcmIdDeviceRegistration);
            taskLoginTask.execute();
            //Toast.makeText(getApplicationContext(),"Inicia sesión",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),getText(R.string.revisaDatos),Toast.LENGTH_SHORT).show();
        }
    }





    /**
     * Envía el contenido de formulario para realizar registro
     */
    public void enviaRegistro(View view){
        if(validadoresRegistro()){

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if(selectedId == rUser.getId()) {
                position = 2;
            } else {
                position = 1;
            }

            Log.d(TAG, "User: " + user.getText().toString());
            TaskRegistro taskRegistroTask = new TaskRegistro(user2,pass1,dni,nombre,apellidos,email,telefono,position,LoginActivity.this,getApplicationContext(),sharedpreferences,gcmIdDeviceRegistration);
            taskRegistroTask.execute();

        }else{
            Toast.makeText(getApplicationContext(),getText(R.string.revisaDatos),Toast.LENGTH_SHORT).show();
        }
    }







    /**
     * Envía el contenido de formulario para recuperar acceso
     */
    public void enviaRecuperar(View view){
        if(validaEmail(emailRecuperar)){
            Log.d(TAG,"Validación previa pasada correctamente");
            new TaskRecuperar(emailRecuperar, LoginActivity.this,getApplicationContext()).execute();

        }else{
            Toast.makeText(getApplicationContext(),getText(R.string.revisaDatos),Toast.LENGTH_SHORT).show();
        }
    }







    /**
     * Valida el formulario del login, comprueba si los dos campos introducidos son correctos
     * @return
     */
    public boolean validadoresLogin(){
        enviar = true;

        validaExiste(user);
        validaExiste(pass);

        if(enviar){
            validaPass(pass);
        }
        Log.d(TAG,"enviar: "+enviar);

        return enviar;
    }


    /**
     * Valida el formulario del registro, comprueba si los datos introducidos son correctos
     * @return
     */
    public boolean validadoresRegistro (){
        enviar = true;

        validaExiste(user2);
        validaExiste(pass1);
        validaExiste(pass2);
        validaExiste(dni);
        validaExiste(nombre);
        validaExiste(apellidos);
        validaExiste(email);


        if(enviar == true){
            validaEmail(email);
            comparaPass(pass1, pass2);
            validaDNI(dni);

        }


        Log.d(TAG, "enviar: " + enviar);
        return enviar;
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
     * Comprueba si el email tiene el formato correcto
     * @param email
     * @return
     */
    public boolean validaEmail(EditText email){
        String em = email.getText().toString().replaceAll(" ", "");

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()){
            email.setError(getText(R.string.emailIncorrecto));
            enviar = false;
        }

        return true;
    }


    public boolean validaDNI(EditText dni){
        String TAG = "TFG_VALIDADNI";
        String textoDni = dni.getText().toString().toLowerCase();
        Log.d(TAG,"introducido: "+textoDni);

        try{
            if(textoDni.length()==9){
                Log.d(TAG,">9");
                int numeros = Integer.parseInt(textoDni.substring(0, textoDni.length() - 1));
                Log.d(TAG,"numeros: "+numeros);

                char letra = textoDni.charAt(textoDni.length()-1);

                Log.d(TAG,"letra :"+letra);

                char letraReal = letraDNI(numeros);
                Log.d(TAG,"letraReal:"+letraReal);

                if(letraReal == letra){

                    Log.d(TAG,"OK");
                    return true;
                }else{
                    Log.d(TAG,"DNI Incorrecto");
                }
            }

            enviar = false;
            dni.setError(getText(R.string.dniIncorrecto));
            return false;
        }catch (Exception e){
            Log.d(TAG,"ERROR");
            e.printStackTrace();
            dni.setError(getText(R.string.dniIncorrecto));
            enviar = false;
            return false;
        }


    }

    public static final String STRINGDNI = "trwagmyfpdxbnjzsqvhlcke";

    public static char letraDNI(int dni) {
        return STRINGDNI.charAt(dni % 23);
    }


    /**
     * Created by Ismael Santos Cabaña on 17/11/15.
     * Task para restaurar contraseña
     */
    public class TaskRecuperar extends AsyncTask<Void, Void, String>{

        private EditText emailRecuperar;
        private final String sEmail;
        private Activity activity;
        private Context context;
        private final String URL = Util.URL+"/recuperarPass";

        public TaskRecuperar(EditText emailRecuperar, Activity activity, Context context) {
            this.emailRecuperar = emailRecuperar;
            this.context = context;
            this.activity = activity;
            this.sEmail = emailRecuperar.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }


        @Override
        protected String doInBackground(Void... params) {
            OkHttp servidor = new OkHttp();
            String text = null;
            JSONObject json = new JSONObject();

            try {
                json.put("email",sEmail);
                text = servidor.post(URL, json.toJSONString());
                Log.d(TAG,text);

            } catch (IOException ce) {
                ce.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(final String results) {
            if (results != null) {
                if (results.contentEquals("Ok")) {

                    Snackbar.make(findViewById(android.R.id.content),getText(R.string.peticionAceptadaEmail), Snackbar.LENGTH_LONG)
                            .setAction(getText(R.string.salir), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.finishAffinity();

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.red))
                            .show();
                    emailRecuperar.setText("");
                }else if(results.contentEquals("NoExiste")){
                    Toast.makeText(context, getText(R.string.noExisteEmail), Toast.LENGTH_SHORT).show();
                    emailRecuperar.setError(getText(R.string.noExisteEmail));
                }
            }else{
                Toast.makeText(context, getText(R.string.error), Toast.LENGTH_SHORT).show();
            }


        }

    }

}
