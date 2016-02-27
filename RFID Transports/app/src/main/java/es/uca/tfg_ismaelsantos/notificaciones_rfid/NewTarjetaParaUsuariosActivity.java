package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 28/11/15.
 * Añadir nueva tarjeta (usuario)
 */
public class NewTarjetaParaUsuariosActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private EditText uidEditText;
    private TextView enviar;
    private String token;
    private int id;
    private SmoothProgressBar smoothProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_tarjeta_usuario);

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
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    enviarDatos();
                    return true;
                }
                return false;
            }
        });

        enviar = (TextView)findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatos();
            }
        });


    }


    private void enviarDatos(){
        if(uidEditText.getText().length()>0) {
            if (uidEditText.getText().length() < 8){
                uidEditText.setError(getText(R.string.malUid));
            }else{
                empezarSmoothProgressBar(smoothProgressBar);
                new TaskAddTarjetaParaUsuario(uidEditText.getText().toString(),id,token,getApplicationContext()).execute();
            }

        }else{
            uidEditText.setError(getText(R.string.campoObligatorio));
        }
    }

    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
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
            default: startActivity(new Intent(NewTarjetaParaUsuariosActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Task para añadir nueva tarjeta (usuario)
     */

    public class TaskAddTarjetaParaUsuario extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/addTarjetaParaUsuario";
        private final String uid;
        private final String token;
        private final int id;
        private final Context context;


        public TaskAddTarjetaParaUsuario(String uid,  int id, String token, Context context) {
            this.context = context;
            this.token = token;
            this.id = id;
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
            if(results.length() > 0){
                if(results.contentEquals("Ok")) {
                    mensaje = getText(R.string.addCardOk).toString().toUpperCase();
                    uidEditText.setText("");
                }
                else if(results.contentEquals("Error")){
                    mensaje = getText(R.string.error).toString().toUpperCase();

                }else if(results.contains("Duplicate entry")){
                    mensaje = getText(R.string.UIDDuplicado).toString().toUpperCase();
                    uidEditText.setError(getText(R.string.UIDDuplicado));
                }else if(results.contentEquals("token")) {
                    Log.d("TFG_NewTarjeta","Error de token");
                    mensaje = getText(R.string.errorSesion).toString().toUpperCase();
                    new Sesion().muestraAlertaSesion(context, NewTarjetaParaUsuariosActivity.this);
                }else{
                    mensaje = getText(R.string.TaskErrorServer).toString().toUpperCase();
                }
            }else{
                mensaje = getText(R.string.TaskErrorServer).toString().toUpperCase();
            }


            Snackbar.make(findViewById(android.R.id.content),mensaje, Snackbar.LENGTH_LONG)
                    .setAction(getText(R.string.back).toString().toUpperCase(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentTarjetasUsuario.runonce = true;
                            startActivity(new Intent(NewTarjetaParaUsuariosActivity.this, MainActivity.class));
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.red))
                    .show();

            pararSmoothProgressBar(smoothProgressBar);

        }

    }

}
