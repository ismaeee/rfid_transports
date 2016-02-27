package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


/**
 * Created by Ismael Santos Cabaña on 19/11/15.
 * Muestra contenido de una tarjeta
 */

public class TarjetaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int idConsulta;
    private final String TAG = "TFG_TarjetaActivity";
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isRefreshing = false;
    private Handler handler = new Handler();
    private String nombreUsuario;
    private ListView lv;
    private int idUsuario;
    private int rol;
    private Tarjeta tarjeta;
    private SmoothProgressBar smoothProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta);
        initToolbar();
        smoothProgressBar = (SmoothProgressBar)findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);

        Bundle bundle = getIntent().getBundleExtra("bundle");   //<< get Bundle from Intent

        if(bundle != null){
            idConsulta = bundle.getInt("id", 0);

        }

        sharedPreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        rol = sharedPreferences.getInt("rol",0);
        if(rol==2){

            TextView textViewIdTarjeta = (TextView)findViewById(R.id.idTarjetaTab);
            if(textViewIdTarjeta!=null) textViewIdTarjeta.setVisibility(View.INVISIBLE);
            TextView textViewIdUsuario = (TextView)findViewById(R.id.usuarioTab);
            if(textViewIdUsuario!=null)textViewIdUsuario.setVisibility(View.INVISIBLE);

        }

        startSmoothProgressBar(smoothProgressBar);
        new TaskTarjeta(idConsulta, getApplicationContext(), TarjetaActivity.this, sharedPreferences,rol).execute();


    }




    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(getText(R.string.tarjetasBloqueadas));
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


    private void startSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    private void stopSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.INVISIBLE);
    }


    private void showTarjeta(Tarjeta tarjeta,int rol){
        TextView idTarjeta,uid,saldo,fechaActualizacion,fechaCreacion,usuario,fecha;
        if((rol == 1)||(rol == 3)){//user, solo muetra algunos datos
            idTarjeta = (TextView)findViewById(R.id.idTarjeta);
            idTarjeta.setText(" "+tarjeta.getId());

            usuario = (TextView)findViewById(R.id.usuario);
            usuario.setText(Html.fromHtml("<u>" + tarjeta.getId_usuario() + "</u>"));
            usuario.setTextColor(getResources().getColor(R.color.holo_blue_dark));
            idUsuario = tarjeta.getId_usuario();

        }

        if(tarjeta.getBloqueada() == 1){
            ImageView imageView = (ImageView)findViewById(R.id.imagenTarjeta);
            imageView.setImageResource(R.mipmap.tarjeta_bloqueada);
        }

        uid = (TextView)findViewById(R.id.uid);
        uid.setText(" "+tarjeta.getUid());

        saldo = (TextView)findViewById(R.id.saldo);
        saldo.setText(" "+tarjeta.getSaldo()/200.0+"€");

        fechaActualizacion = (TextView)findViewById(R.id.fechaActualizacion);
        fechaActualizacion.setText(" " + tarjeta.getFecha_actualizacion());

        fechaCreacion = (TextView)findViewById(R.id.fechaCreacion);
        fechaCreacion.setText(" " + tarjeta.getFecha_creacion());

        fecha = (TextView)findViewById(R.id.fecha);
        fecha.setText(" " + Util.getFechaString(Calendar.getInstance().getTimeInMillis(),Calendar.getInstance()));

    }


    public void update(){
        startSmoothProgressBar(smoothProgressBar);
        new TaskTarjeta(idConsulta, getApplicationContext(), TarjetaActivity.this, sharedPreferences,rol).execute();
    }

    public void onClick(View v){
        Intent intent = new Intent(TarjetaActivity.this, UserActivity.class);
        Bundle b = new Bundle();
        b.putInt("id",idUsuario);
        intent.putExtra("bundle", b);

        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.update: update(); break;
            default: startActivity(new Intent(TarjetaActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }














    /**
     * TaskTarjeta , recibe información de la tarjeta
     */


    public class TaskTarjeta extends AsyncTask<Void, Void, String> {
        private final String URL;

        private final SharedPreferences sharedpreferences;
        private final String TAG = "TFG_Task";
        private final Context context;
        private final Activity activity;

        private final int idUser;



        public TaskTarjeta(int idUser, Context context, Activity activity, SharedPreferences sharedpreferences,int rol) {
            if(rol == 2) {
                URL =  Util.URL + "/getTarjetaUser";
            }else{
                URL =  Util.URL + "/getTarjeta";
            }


            this.sharedpreferences = sharedpreferences;
            this.context = context;
            this.activity = activity;
            this.idUser = idUser;
            Log.d(TAG, "Se ejecuta taskUser");
        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", sharedpreferences.getInt("id", 0));
                json.put("token", sharedpreferences.getString("token", ""));
                json.put("idTarjeta", idUser);


                text = servidor.post(URL, json.toJSONString());

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {


            Log.d(TAG, "Resultado: " + results);
            if (results != null) {
                if (results.contains("{\"id\":")) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(results);

                        if (jsonObject != null) {





                            String fechaActualizacion = Util.getFechaString(jsonObject.getLong("fecha_actualizacion"),Calendar.getInstance());
                            String fechaCreacion = Util.getFechaString(jsonObject.getLong("fecha_creacion"), Calendar.getInstance());

                            String fechaBloqueo = null;
                            long lFechaBloqueo = 0;

                            if(!jsonObject.isNull("fecha_bloqueo"))
                            {
                                fechaBloqueo = Util.getFechaString(jsonObject.getLong("fecha_bloqueo"), Calendar.getInstance());
                                lFechaBloqueo = jsonObject.getLong("fecha_bloqueo");
                            }


                            tarjeta = new Tarjeta(jsonObject.getInt("id"), jsonObject.getInt("id_usuario"),jsonObject.getString("uid"),jsonObject.getInt("saldo"),jsonObject.getInt("bloqueada"),fechaActualizacion, fechaCreacion, fechaBloqueo, jsonObject.getLong("fecha_actualizacion"),jsonObject.getLong("fecha_creacion"),lFechaBloqueo);

                            showTarjeta(tarjeta,rol);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
                else if(results.length()==0){
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, activity);

                }else {
                    Log.d(TAG, "Error: " + results);
                    Toast.makeText(context, getText(R.string.error), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;
                }
            }

            stopSmoothProgressBar(smoothProgressBar);

        }


    }

}
