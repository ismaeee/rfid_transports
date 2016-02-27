package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 28/11/15.
 * Añadir nueva tarjeta (admin)
 */
public class NewTarjetaActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private EditText saldoEditText,uidEditText;
    private AutoCompleteTextView dniEditText;
    private TextView enviar;
    private String token;
    private int id;
    private SmoothProgressBar smoothProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tarjeta);

        initToolbar();
        smoothProgressBar = (SmoothProgressBar)findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);


        token = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", "");
        id = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getInt("id", 0);

        uidEditText =  (EditText)findViewById(R.id.uid);
        uidEditText.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        uidEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (uidEditText.getText().length() < 8){
                        uidEditText.setError(getText(R.string.malUid));
                    }
                    saldoEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        saldoEditText = (EditText)findViewById(R.id.saldo);
        saldoEditText.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        saldoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviaDatos();
                    return true;
                }
                return false;
            }
        });

        dniEditText = (AutoCompleteTextView) findViewById(R.id.dni);
        dniEditText.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        dniEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(dniEditText.getText().length() >= 2) {
                    new RecibirDNI(dniEditText,id,token,NewTarjetaActivity.this).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 8){
                    dniEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else if(s.length() == 9){
                    dniEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });

        enviar = (TextView)findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviaDatos();
            }
        });


    }


    public void enviaDatos(){
        if(validaExisten()){
            if (uidEditText.getText().length() < 8){
                uidEditText.setError(getText(R.string.malUid));
            }else{
                empezarSmoothProgressBar(smoothProgressBar);
                new TaskAddTarjeta(dniEditText.getText().toString(),uidEditText.getText().toString(),Integer.parseInt(saldoEditText.getText().toString()),id,token,getApplicationContext()).execute();
            }
        }
    }

    public boolean validaExisten(){
        boolean enviar = true;
        if(dniEditText.getText().length()==0) {dniEditText.setError(getText(R.string.campoObligatorio)); enviar = false;}
        if(uidEditText.getText().length()==0) {uidEditText.setError(getText(R.string.campoObligatorio)); enviar = false;}
        if(saldoEditText.getText().length()==0) {saldoEditText.setError(getText(R.string.campoObligatorio)); enviar = false;}

        return enviar;
    }

    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(getText(R.string.addCard));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }


    private void initSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.setVisibility(View.INVISIBLE);
        smoothProgressBar.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.rojoRFID));
        smoothProgressBar.setSmoothProgressDrawableUseGradients(false);
        smoothProgressBar.setSmoothProgressDrawableSeparatorLength(0);
        smoothProgressBar.setSmoothProgressDrawableSpeed(2);
        smoothProgressBar.setSmoothProgressDrawableInterpolator(new AccelerateDecelerateInterpolator());
        smoothProgressBar.setSmoothProgressDrawableSectionsCount(2);
        smoothProgressBar.setSmoothProgressDrawableProgressiveStartSpeed(2);
        smoothProgressBar.setSmoothProgressDrawableProgressiveStopSpeed(2);

    }


    private void empezarSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    private void pararSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case -1 : break;
            default: startActivity(new Intent(NewTarjetaActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Task para recibir DNI de usuarios coincidentes
     */
    public class RecibirDNI extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/getDNI";
        private final AutoCompleteTextView dni;
        private final String token;
        private final int id;
        private final Context context;
        private final String sDni;

        public RecibirDNI(AutoCompleteTextView dni, int id, String token, Context context) {
            this.context = context;
            this.token = token;
            this.id = id;
            this.dni = dni;
            this.sDni = dni.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", id);
                json.put("token", token);
                json.put("dni", sDni);

                text = servidor.post(URL, json.toJSONString());

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {
            Log.d("TFG_RecibirDNI", "Results: " + results);
            if(results.length() > 0){
                if(!results.contains("java.lang.IllegalArgumentException")) {
                    String[] arrayDni = results.split(",");
                    Log.d("TFG_RecibirDNI","arrayDni T: "+arrayDni.length);
                    dni.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, arrayDni));
                }
            }

        }

    }


    /**
     * Task para añadir tarjeta (admin)
     */
    public class TaskAddTarjeta extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/addTarjeta";
        private final String dni;
        private final String uid;
        private final int saldo;
        private final String token;
        private final int id;
        private final Context context;


        public TaskAddTarjeta(String dni, String uid, int saldo, int id, String token, Context context) {
            this.context = context;
            this.token = token;
            this.id = id;
            this.dni = dni;
            this.saldo = saldo;
            this.uid = uid.toLowerCase();
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
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", id);
                json.put("token", token);
                json.put("dni", dni);
                json.put("saldo",saldo);
                json.put("uid",uid);

                text = servidor.post(URL, json.toJSONString());
                Log.d("TFG_N",text);
            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {
            Log.d("TFG_NEWTarjeta", results);
            String mensaje= "";
            int color = Color.RED;
            if(results.length() > 0){
                if(results.contentEquals("Ok")) {
                    mensaje = getText(R.string.addCardOk).toString().toUpperCase();
                    color = Color.GREEN;
                    dniEditText.setText("");
                    uidEditText.setText("");
                    saldoEditText.setText("");
                    //Toast.makeText(context, "Tarjeta añadida", Toast.LENGTH_SHORT).show();
                }
                else if(results.contentEquals("Error")){
                    //Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    mensaje = getText(R.string.error).toString().toUpperCase();

                }else if(results.contentEquals("token")){
                    //Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    mensaje = getText(R.string.errorSesion).toString().toUpperCase();
                    Log.d("TFG_NewTarjetaActivity", "Error token");
                    new Sesion().muestraAlertaSesion(context, NewTarjetaActivity.this);

                }else if(results.contains("Duplicate entry")){
                    //Toast.makeText(context, "Tarjeta Duplicada", Toast.LENGTH_SHORT).show();
                    mensaje = getText(R.string.UIDDuplicado).toString().toUpperCase();
                    uidEditText.setError(getText(R.string.UIDDuplicado));
                }else{
                    //Toast.makeText(context, "Error en el servidor", Toast.LENGTH_SHORT).show();
                    mensaje = getText(R.string.TaskErrorServer).toString().toUpperCase();
                }
            }else{
                //Toast.makeText(context, "Error en el servidor", Toast.LENGTH_SHORT).show();
                mensaje = getText(R.string.TaskErrorServer).toString().toUpperCase();
            }


            Snackbar.make(findViewById(android.R.id.content),mensaje, Snackbar.LENGTH_LONG)
                    .setAction(getText(R.string.back).toString().toUpperCase(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentAlertas.noshow = true;
                            startActivity(new Intent(NewTarjetaActivity.this, MainActivity.class));
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.red))
                    .show();

            pararSmoothProgressBar(smoothProgressBar);

        }

    }

}
