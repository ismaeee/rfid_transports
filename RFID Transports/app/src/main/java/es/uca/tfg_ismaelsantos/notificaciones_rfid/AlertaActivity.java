package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

import es.uca.tfg_ismaelsantos.Clases.Alerta;
import es.uca.tfg_ismaelsantos.Clases.Usuario;
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
 * Activity para los detalles de la alerta
 */

public class AlertaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private AlertaDBAdapter dbAlerta;
    private UserDBAdapter dbUsuario;
    private int id;

    private final String TAG = "TFG_AlertaActivity";

    private Alerta alerta;
    private Usuario usuario;

    private String tokenUser;
    private int idUser;
    private int position;

    private SmoothProgressBar smoothProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);
        initToolbar();
        String action = getIntent().getAction();
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);

        tokenUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", "");
        idUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getInt("id", 0);


        if(action != null) {
            Log.d(TAG,action);
            if (action.contentEquals("notificacion")) {

                Bundle bundle = this.getIntent().getExtras();

                Log.d(TAG,"Antes");
                int idAlerta = bundle.getInt("idAccion");
                Log.d(TAG,"idAlerta: "+idAlerta);
                startProgresBar();
                GetAlerta getAlerta = new GetAlerta(idAlerta,getApplicationContext(),AlertaActivity.this,tokenUser,idUser);
                getAlerta.execute();

                FragmentAlertas.servidor = true; // Cambiamos la variable servidor a true para indicar que se debe actualizar con el servidor con los nuevos datos

            } else {

                Bundle bundle = getIntent().getBundleExtra("bundle");   //<< get Bundle from Intent
                if (bundle != null) {

                    id = bundle.getInt("id", 0);
                    position = bundle.getInt("position", -1);
                    obtenerDatos(id);
                    Log.d(TAG, "" + usuario.toString());
                    Log.d(TAG, "" + alerta.toString());

                }

            }
        }
    }






    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(getString(R.string.labelAlerta));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }


    /**
     * Inicia el progressBar
     * @param smoothProgressBar
     */
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





    public void onClickBloquear(View v) {
        //Toast.makeText(getApplicationContext(), "Click en Bloquear", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getText(R.string.seguroBloquear))
                .setCancelable(true)
                .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        smoothProgressBar.progressiveStart();
                        smoothProgressBar.setVisibility(View.VISIBLE);
                        TaskBloquearTarjeta taskBloquearTarjeta = new TaskBloquearTarjeta(idUser,tokenUser,alerta.getId_tarjeta(),getApplicationContext(),AlertaActivity.this, smoothProgressBar,findViewById(android.R.id.content));
                        taskBloquearTarjeta.execute();
                    }
                })
                .setTitle(getText(R.string.bloquearTarjeta));
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Méotod que se ejecuta cuando se presiona el botón de localizar
     * @param v
     */
    public void onClickLocalizar(View v) {
        String lugar = alerta.getLugar();
        if(lugar != null){


            Bundle bundle = new Bundle();
            bundle.putInt("id", alerta.getId());
            bundle.putString("uid", alerta.getUID());
            bundle.putString("fecha", alerta.getsFecha_creacion());


            String[] vLugar = lugar.split(","); //= new String[2]; vLugar =

            bundle.putDouble("latitud", Double.parseDouble(vLugar[0]));
            bundle.putDouble("longitud", Double.parseDouble(vLugar[1]));

            startActivity(new Intent(AlertaActivity.this, MapAlertaActivity.class).putExtras(bundle));
        }else{
            Toast.makeText(getApplicationContext(),getText(R.string.noLocalizacion), Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * Método que se ejecuta cuando se presiona el botón de descartar
     * @param v
     */
    public void onClickDescartar(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String token = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token","");

        builder.setMessage(getText(R.string.siDescarta))
                .setCancelable(true)
                .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        smoothProgressBar.progressiveStart();
                        smoothProgressBar.setVisibility(View.VISIBLE);
                        TaskDescartarAlerta taskDescartarAlerta = new TaskDescartarAlerta(idUser,tokenUser,alerta.getId(),getApplicationContext(),AlertaActivity.this,position,smoothProgressBar);
                        taskDescartarAlerta.execute();
                    }
                })
                .setTitle(getText(R.string.descartarAlerta));
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Método que se ejecuta cuando se presiona el botón de llamar
     * @param v
     */
    public void onClickLlamar(View v){
        Toast.makeText(getApplicationContext(),getText(R.string.llamar),Toast.LENGTH_SHORT).show();
        System.out.println("TELFONO:                   " + usuario.getTelefono());

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", usuario.getTelefono(), null));
        startActivity(intent);
    }

    /**
     * Método que se ejecuta cuando se presina en el botón de enviar email
     * @param v
     */
    public void onClickEmail(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String token = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", "");

        builder.setMessage(getText(R.string.quieresenviar))
                .setCancelable(true)
                .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getText(R.string.enviar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enviarEmail(null);
                        //mProgressBar.progressiveStart();
                        //mProgressBar.setVisibility(View.VISIBLE);

                    }
                })
                .setTitle(getText(R.string.notifyEmail));
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Obtiene datos de la alerta de la base de datos
     * @param _id
     */
    private void obtenerDatos(int _id){
        dbAlerta = new AlertaDBAdapter(getApplicationContext());
        dbUsuario = new UserDBAdapter(getApplicationContext());

        dbAlerta.open();
        alerta = dbAlerta.devolverAlerta(_id);
        dbAlerta.close();
        dbUsuario.open();
        usuario = dbUsuario.devolverUsuario(alerta.getId_usuario());
        dbUsuario.close();

        introducirContenido(alerta, usuario);
    }


    /**
     * Introduce conteindo en los elementos del layout
     * @param alerta
     * @param usuario
     */
    private void introducirContenido(Alerta alerta, Usuario usuario){


        DecimalFormat decimales = new DecimalFormat("0.00");

        TextView id,uid,saldoDB,saldoCard,fecha,idUsuario,user,nombre,apellidos,dni,email,telefono;

        id = (TextView)findViewById(R.id.id);
        id.setText( " "+alerta.getId());

        uid = (TextView)findViewById(R.id.uid);
        uid.setText(" "+alerta.getUID());

        saldoDB = (TextView)findViewById(R.id.saldoDB);
        saldoDB.setText(" "+decimales.format(alerta.getSaldo_bd()/200.0)+"€");

        saldoCard = (TextView)findViewById(R.id.saldoCard);
        saldoCard.setText(" "+decimales.format(alerta.getSaldo_tarjeta()/200.0)+"€");

        fecha = (TextView)findViewById(R.id.fecha);
        fecha.setText(" "+alerta.getsFecha_creacion());

        idUsuario = (TextView)findViewById(R.id.idUsuario);
        idUsuario.setText(" "+usuario.getId());

        user = (TextView)findViewById(R.id.user);
        user.setText(" "+usuario.getUser());

        nombre = (TextView)findViewById(R.id.nombre);
        nombre.setText(" "+usuario.getNombre());

        apellidos = (TextView)findViewById(R.id.apellidos);
        apellidos.setText(" "+usuario.getApellidos());

        email = (TextView)findViewById(R.id.email);
        email.setText(" "+usuario.getEmail());

        dni = (TextView)findViewById(R.id.dni);
        dni.setText(" "+usuario.getDni());

        telefono = (TextView)findViewById(R.id.telefono);
        telefono.setText(" " + usuario.getTelefono());
    }


    /**
     * Enviar email
     * @param contenido
     */
    public void enviarEmail(String contenido){

        if(contenido == null){
            contenido =   "<h3 style=\"color: red\">Detected possible fraud in your card</h3>"
                    + "<br/><b>Id alert: </b>"+ alerta.getId() +"<br/>"
                    + "<b>UID: </b>"+ alerta.getUID() +"<br/>"
                    + "<b>Money System: </b>"+ alerta.getSaldo_bd()/200.0 +" &#8364;<br/>"
                    + "<b>Money Card: </b>"+alerta.getSaldo_tarjeta()/200.0+" &#8364;<br/>"
                    + "<b>Location: </b>("+alerta.getLugar()+") <br/>"
                    + "<br/><br/><br/>Please, contact our staff to solve the problem.";
        }


        startProgresBar();
        TaskEmail taskEmail = new TaskEmail(idUser,tokenUser,usuario.getEmail(),contenido,getApplicationContext(),AlertaActivity.this,smoothProgressBar,findViewById(android.R.id.content));
        taskEmail.execute();
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
            default: startActivity(new Intent(AlertaActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Parar progressBar
     */
    public void stopProgressbar(){
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Empezar progressBar
     */
    public void startProgresBar(){
        smoothProgressBar.progressiveStart();
        smoothProgressBar.setVisibility(View.VISIBLE);
    }


    /**
     * Obtener Alerta mediante el servicio REST
     */
    public class GetAlerta extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/getAlerta";
        private final String token;
        private final String TAG = "TFG_Alerta";
        private final Context context;
        private final Activity activity;
        private final int idAlerta;
        private final int idUser;


        /**
         * Constructor
         * @param idAlerta
         * @param context
         * @param activity
         * @param token
         * @param idUser
         */
        public GetAlerta(int idAlerta,Context context, Activity activity, String token, int idUser) {

            this.context = context;
            this.activity = activity;
            this.idAlerta = idAlerta;
            this.idUser = idUser;
            this.token = token;

            Log.d(TAG,"GetAlerta");
        }

        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text ;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", idUser);
                json.put("token", token);
                json.put("idAlerta",idAlerta);

                text = servidor.post(URL, json.toJSONString());
                Log.d(TAG,"Text:"+text);

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {


            Log.d(TAG, "Resultado: " + results);
            if (results.length() > 0) {
                if (results.contains("id_tarjeta")) {

                    try {
                        JSONObject json = new JSONObject(results);
                        JSONObject jsonUsuario = json.getJSONObject("usuario");

                        //Usuario
                        usuario = new Usuario(jsonUsuario.getInt("id"), jsonUsuario.getInt("rol"), jsonUsuario.getString("user"), jsonUsuario.getString("dni"), jsonUsuario.getString("nombre"), jsonUsuario.getString("apellidos"), jsonUsuario.getString("email"), jsonUsuario.getString("telefono"));

                        //Alerta
                        long fecha_creacion = json.getLong("fecha_creacion");
                        String sFechaCreacion = Util.getFechaString(fecha_creacion, Calendar.getInstance());
                        alerta = new Alerta(json.getInt("id"), json.getInt("id_tarjeta"), jsonUsuario.getInt("id"), json.getDouble("saldo_bd"), json.getDouble("saldo_tarjeta"), json.getString("lugar"), sFechaCreacion, fecha_creacion, json.getString("uid"));


                        introducirContenido(alerta,usuario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Error de conexion: " + results);
                    Toast.makeText(context, getText(R.string.TaskErrorServer), Toast.LENGTH_SHORT).show();
                }


            }else {
                Log.d(TAG, "Error de sesion: " + results);
                Sesion sesion = new Sesion();
                sesion.muestraAlertaSesion(context, activity);

            }

            stopProgressbar();

        }


    }


}
