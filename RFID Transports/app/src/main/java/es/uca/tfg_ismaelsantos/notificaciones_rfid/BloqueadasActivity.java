package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.ListAdapter.TarjetasBloqueadasListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 26/11/15.
 * Activity para las tarjetas bloquedas
 */
public class BloqueadasActivity extends AppCompatActivity {

    private Toolbar toolbar;


    private final String TAG = "TFG_BLOQUEADAS";


    private String tokenUser;
    private int idUser;
    public static boolean servidor = true;
    private boolean isRefreshing = false;
    private SmoothProgressBar smoothProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private ArrayList<Tarjeta> tarjetas;
    private TarjetasBloqueadasListAdapter adapter;
    private ListView lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueadas);
        initToolbar();


        smoothProgressBar = (SmoothProgressBar)findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);

        tokenUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", "");
        idUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getInt("id", 0);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                System.out.println("Se actualiza");

                servidor = true;
                isRefreshing = true;
                handler.post(refreshing);
                mostrarBloqueadas();


            }
        });
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.rojoRFID));

        lista = (ListView) findViewById(R.id.list);
        lista.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (lista != null && lista.getChildCount() > 0) {
                    Log.d("TFG_FragmentAlertUser", "Entra");
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lista.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lista.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                    swipeRefreshLayout.setEnabled(enable);

                }

            }
        });

        empezarSmoothProgressBar(smoothProgressBar);
        mostrarBloqueadas();

    }


    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                // TODO : isRefreshing should be attached to your data request status
                if (isRefreshing) {
                    // re run the verification after 1 second
                    handler.postDelayed(this, 500);
                } else {
                    // stop the animation after the data is fully loaded
                    swipeRefreshLayout.setRefreshing(false);
                    // TODO : update your list with the new data
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(getString(R.string.tarjetasBloqueadas));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }


    /**
     * Inicia SmoothProgressBar
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


    /**
     * Comienza a mostrar progressBar
     * @param smoothProgressBar
     */
    private void empezarSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    /**
     * Para progressBar
     * @param smoothProgressBar
     */
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
            default: startActivity(new Intent(BloqueadasActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int posicion = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(BloqueadasActivity.this);

                builder.setMessage(getText(R.string.seguroDesbloquear))
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        })

                        .setPositiveButton(getText(R.string.desbloquear), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                empezarSmoothProgressBar(smoothProgressBar);

                                new TaskDesbloquearTarjeta(idUser, tokenUser, tarjetas.get(posicion).getId(), getApplicationContext(), BloqueadasActivity.this, smoothProgressBar, posicion).execute();

                            }
                        })

                        .setTitle(getText(R.string.desbloquearTarjeta));
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
        registerForContextMenu(lista);

    }


    /**
     * Muestra las tarjetas bloquedas
     */
    private void mostrarBloqueadas() {
        ListView lv = (ListView) findViewById(R.id.list);

        tarjetas= new ArrayList<>();
        adapter = new TarjetasBloqueadasListAdapter(this, tarjetas);

        lv.setAdapter(adapter);

        if (servidor) {
            Log.d(TAG,"EJECUTA EN EL SERVIDOR");

            new TaskRecibirBloqueadas(idUser,adapter, tokenUser, getApplicationContext(),BloqueadasActivity.this,smoothProgressBar).execute();
            servidor = false;

        } else {
            empezarSmoothProgressBar(smoothProgressBar);
            Log.d(TAG,"MUESTRA DESDE DB");
            new TaskRecibirBloqueadas(idUser,adapter, tokenUser, getApplicationContext(),BloqueadasActivity.this,smoothProgressBar).showBloqueadas();
        }
    }




    /**
     * Created by Ismael Santos Cabaña on 21/11/15.
     */
    public class TaskRecibirBloqueadas extends AsyncTask<Void, Void, String> {

        private final int id;
        private final String token;
        private final String TAG = "TFG_RecibirBloqueadas";
        private final String URLBloqueadas = Util.URL + "/recibirBloqueadas";
        private final Context context;
        private SmoothProgressBar smoothProgressBar;
        private ArrayList<Tarjeta> tarjetas = new ArrayList<>();
        private ArrayList<Tarjeta> listTarjetas = new ArrayList<>();
        private TarjetaDBAdapter db;
        private TarjetasBloqueadasListAdapter adapter ;
        private Activity activity;

        /**
         * Constructor
         * @param id
         * @param adapter
         * @param token
         * @param context
         * @param activity
         * @param smoothProgressBar
         */
        public TaskRecibirBloqueadas(int id,TarjetasBloqueadasListAdapter adapter, String token, Context context,Activity activity, SmoothProgressBar smoothProgressBar) {
            this.id = id;
            this.adapter = adapter;
            this.token = token;
            this.context = context;
            this.db = new TarjetaDBAdapter(context);
            this.smoothProgressBar = smoothProgressBar;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            String response = "";
            OkHttp servidor = new OkHttp();
            try {
                json.put("id", id);
                json.put("token", token);

                response = servidor.post(URLBloqueadas, json.toJSONString());
                Log.d(TAG, "Response: " + response);

            }catch (IOException ioe){
                ioe.getMessage();
            }
            return response;
        }


        @Override
        protected void onPostExecute(final String results) {
            if (results != null) {
                if (results.contains("[{")) {

                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(results);

                        if (jsonArray != null) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                org.json.JSONObject json = jsonArray.getJSONObject(i);



                                String fechaBloqueada = null;

                                if(!json.isNull("fechaBloqueada"))
                                {
                                    fechaBloqueada = Util.getFechaString(json.getLong("fechaBloqueada"), Calendar.getInstance());
                                }




                                Tarjeta tarjeta = new Tarjeta(json.getInt("id"),json.getString("uid"),json.getString("nombreUsuario"),json.getInt("bloqueada"));
                                tarjeta.setFechaBloqueo(fechaBloqueada);

                                this.tarjetas.add(tarjeta);

                            }

                            insertBloqueadas();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showBloqueadas();


                } else if (results.contentEquals("[]")) {

                    Toast.makeText(context, context.getText(R.string.nobloqueadas), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;

                }else if(results.contentEquals("token")){
                    Log.d(TAG, "Error token");
                    new Sesion().muestraAlertaSesion(context, activity);
                }
                else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                    showBloqueadas();
                    isRefreshing = false;
                }

            }
            pararSmoothProgressBar(smoothProgressBar);

        }

        protected void insertBloqueadas() {

            Log.d(TAG, "Inserta Bloqueadas");
            db.open();

            db.deleteAllTarjetaBloqueada();

            for (int i = 0; i < tarjetas.size(); i++) {
                db.createTarjetaBloqueo(tarjetas.get(i));
            }

            db.close();

        }

        public void showBloqueadas() {

            Log.d(TAG, "Muestra bloqueadas");
            db.open();

            Cursor cursor = db.fetchAllTarjetaBloqueo();

            for (int i = 0; i < cursor.getCount(); i++) {

                Tarjeta tarjeta = new Tarjeta(cursor.getInt(cursor.getColumnIndex(db.KEY_ID_TARJETA)),cursor.getString(cursor.getColumnIndex(db.KEY_UID_TARJETA)),cursor.getString(cursor.getColumnIndex(db.KEY_NOMBRE_TARJETA)),cursor.getInt(cursor.getColumnIndex(db.KEY_BLOQUEADA_TARJETA)));
                tarjeta.setFechaBloqueo(cursor.getString(cursor.getColumnIndex(db.KEY_FECHA_BLOQUEO_TARJETA)));
                cursor.moveToNext();

                this.listTarjetas.add(tarjeta);
            }


            db.close();

            adapter.setData(listTarjetas);

            isRefreshing = false; //Termina el Task y para el icono de actualziar

            pararSmoothProgressBar(smoothProgressBar);


        }

    }











    /**
     * Created by Ismael Santos Cabaña on 27/11/15.
     * Desbloquear tarjeta bloqueada
     */
    public class TaskDesbloquearTarjeta extends AsyncTask<Void, Void, String> {

        private final int id, idTarjeta;
        private final String token;
        private final String TAG = "TFG_DesbloquearTarjeta";
        private final String URL = Util.URL + "/desbloquearTarjeta";
        private final Context context;
        private SmoothProgressBar smoothProgressBar;
        private final int posicion;
        private Activity activity;

        /**
         * Constructor
         * @param id
         * @param token
         * @param idTarjeta
         * @param context
         * @param activity
         * @param smoothProgressBar
         * @param posicion
         */
        public TaskDesbloquearTarjeta(int id, String token, int idTarjeta, Context context,Activity activity, SmoothProgressBar smoothProgressBar,int posicion) {
            this.id = id;
            this.idTarjeta = idTarjeta;
            this.token = token;
            this.context = context;
            this.smoothProgressBar = smoothProgressBar;
            this.posicion = posicion;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            String response = "";
            OkHttp servidor = new OkHttp();
            try {
                json.put("id", id);
                json.put("token", token);
                json.put("idTarjeta", idTarjeta);

                response = servidor.post(URL, json.toJSONString());
                Log.d(TAG, "Response: " + response);

            } catch (IOException ioe) {
                ioe.getMessage();
            }
            return response;
        }


        @Override
        protected void onPostExecute(final String results) {
            if (results != null) {
                if (results.contentEquals("Ok")) {
                    Toast.makeText(context, context.getText(R.string.desbloqueada), Toast.LENGTH_SHORT).show();
                    adapter.remove(posicion);
                }else if(results.contentEquals("token")) {
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, activity);
                }else {
                    Toast.makeText(context, context.getText(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getText(R.string.error), Toast.LENGTH_SHORT).show();
            }
            pararSmoothProgressBar(smoothProgressBar);
        }

    }


}
