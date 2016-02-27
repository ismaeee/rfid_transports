package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Alerta;
import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.ListAdapter.AlertListAdapter;
import es.uca.tfg_ismaelsantos.ListAdapter.TarjetasDeUsuarioListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskBloquearTarjeta;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskDescartarAlerta;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskEmail;
import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


/**
 * Created by Ismael Santos Cabaña on 19/11/15.
 * Muestra información de usuario
 */

public class UserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int idConsulta;
    private final String TAG = "TFG_UserActivity";
    private SharedPreferences sharedPreferences;
    ArrayList<Tarjeta> tarjetas = new ArrayList<>();
    TarjetasDeUsuarioListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isRefreshing = false;
    private Handler handler = new Handler();
    private String nombreUsuario;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initToolbar();

        Bundle bundle = getIntent().getBundleExtra("bundle");   //<< get Bundle from Intent

        if(bundle != null){
            idConsulta = bundle.getInt("id",0);
            nombreUsuario = bundle.getString("name", "");
        }

        sharedPreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);


        lv = (ListView) findViewById(R.id.list);
        tarjetas  = new ArrayList<>();
        adapter = new TarjetasDeUsuarioListAdapter(UserActivity.this,tarjetas);
        lv.setAdapter(adapter);

        new TaskUser(idConsulta, adapter, getApplicationContext(), UserActivity.this, sharedPreferences).execute();

    }


    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(nombreUsuario);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }



    private void showUsuario(Usuario inserUsuario){

        TextView idUsuario,user,nombre,apellidos,dni,email,telefono;
        ImageView imageView = (ImageView)findViewById(R.id.imagenUsuario);


        idUsuario = (TextView)findViewById(R.id.idUsuario);
        idUsuario.setText(" "+inserUsuario.getId());

        user = (TextView)findViewById(R.id.user);
        user.setText(" "+inserUsuario.getUser());

        nombre = (TextView)findViewById(R.id.nombre);
        nombre.setText(" "+inserUsuario.getNombre());

        apellidos = (TextView)findViewById(R.id.apellidos);
        apellidos.setText(" " + inserUsuario.getApellidos());

        email = (TextView)findViewById(R.id.email);
        email.setText(" "+inserUsuario.getEmail());

        dni = (TextView)findViewById(R.id.dni);
        dni.setText(" " + inserUsuario.getDni());

        telefono = (TextView)findViewById(R.id.telefono);
        telefono.setText(" " + inserUsuario.getTelefono());


        int rol = inserUsuario.getRol();
        switch (rol){
            case 1: {
                imageView.setImageResource(R.mipmap.ic_admin);
                imageView.setBackgroundResource(R.drawable.border_admin);
            }break;
            case 2: {
                imageView.setImageResource(R.mipmap.ic_user);
                imageView.setBackgroundResource(R.drawable.border_user);
            }break;
            case 3: {
                imageView.setImageResource(R.mipmap.ic_super_admin);
                imageView.setBackgroundResource(R.drawable.border_super_admin);
            }break;
        }

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
            default: startActivity(new Intent(UserActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }














    /**
     * TaskUser para recibir información de usuario
     */


    public class TaskUser extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/getUserConTarjetas";
        private final SharedPreferences sharedpreferences;
        private final String TAG = "TFG_User";
        private final Context context;
        private final Activity activity;
        private Usuario u;
        private final int idUser;
        private TarjetasDeUsuarioListAdapter adapter;
        private ArrayList<Tarjeta> tarjetas = new ArrayList<>();

        public TaskUser(int idUser, TarjetasDeUsuarioListAdapter adapter, Context context, Activity activity, SharedPreferences sharedpreferences) {
            this.sharedpreferences = sharedpreferences;
            this.context = context;
            this.activity = activity;
            this.adapter = adapter;
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
                json.put("idUsuario", idUser);

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
                if (results.contains("{\"usuario\":")) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(results);

                        if (jsonObject != null) {

                            JSONObject usuario = (JSONObject) jsonObject.get("usuario");
                            u = new Usuario(usuario.getInt("id"), usuario.getInt("rol"),usuario.getString("user"), usuario.getString("dni"), usuario.getString("nombre"), usuario.getString("apellidos"), usuario.getString("email"),usuario.getString("telefono"));

                            showUsuario(u);

                            JSONArray jsonTarjetas = (JSONArray) jsonObject.get("tarjetas");

                            for(int i=0;i<jsonTarjetas.length();i++){
                                JSONObject json = jsonTarjetas.getJSONObject(i);
                                String fechaCreacion = Util.getFechaString(json.getLong("fecha_creacion"), Calendar.getInstance());
                                String fechaActualizacion = Util.getFechaString(json.getLong("fecha_actualizacion"), Calendar.getInstance());
                                String fechaBloqueo = null;
                                long lFechaBloqueo = 0;

                                if(!json.isNull("fecha_bloqueo"))
                                {
                                    fechaBloqueo = Util.getFechaString(json.getLong("fecha_bloqueo"), Calendar.getInstance());
                                    lFechaBloqueo = json.getLong("fecha_bloqueo");
                                }

                                tarjetas.add(new Tarjeta(json.getInt("id"), json.getInt("id_usuario"),json.getString("uid"),json.getInt("saldo"),json.getInt("bloqueada"),fechaActualizacion, fechaCreacion, fechaBloqueo, json.getLong("fecha_actualizacion"),json.getLong("fecha_creacion"),lFechaBloqueo));

                            }


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showTarjetas();


                } else if (results.contentEquals("[]")) {

                    Toast.makeText(context, getText(R.string.noUserData), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;

                }else if(results.length()==0){
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, activity);

                }

                else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;
                }
            }

        }


        protected void showTarjetas() {
            for(int i=0;i<tarjetas.size();i++){
                Log.d("TFG_MUESTRA ALERTAS",tarjetas.get(i).toString());
            }

            adapter.setData(tarjetas);
            isRefreshing = false;
        }


    }

}
