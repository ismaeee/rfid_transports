package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.ListAdapter.UsuarioListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 26/11/15.
 * Solicitudes de administrador
 */
public class SolicitudesAdminActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private final String TAG = "TFG_SolicitudesAdmin";
    private String tokenUser;
    private int idUser;
    private int position;
    private boolean isRefreshing = false;
    private SmoothProgressBar smoothProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private SharedPreferences sharedpreferences;
    private ListView lista;
    private  ArrayList<Usuario> usuarios;
    private UsuarioListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_admin);
        initToolbar();

        sharedpreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        tokenUser = sharedpreferences.getString("token", "");
        idUser = sharedpreferences.getInt("id",0);

        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);

        tokenUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", "");
        idUser = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getInt("id", 0);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                System.out.println("Se actualiza");

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
                    Log.d("TFG_FragmentAlertUser","Entra");
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
        toolbar.setTitle(getString(R.string.solicitudesAdmin));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }


    private void initSmoothProgressBar(SmoothProgressBar smoothProgressBar) {
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



    @Override
    public void onResume() {
        super.onResume();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int posicion = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(SolicitudesAdminActivity.this);

                builder.setMessage(getText(R.string.queDesea))
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.descartar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                smoothProgressBar.progressiveStart();
                                smoothProgressBar.setVisibility(View.VISIBLE);

                                new TaskAceptarSolicitud(idUser, tokenUser, usuarios.get(posicion).getId(), 1, getApplicationContext(), smoothProgressBar, posicion).execute();

                            }
                        })

                        .setNeutralButton(getText(R.string.salir), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }

                        })

                        .setPositiveButton(getText(R.string.aceptar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                smoothProgressBar.progressiveStart();
                                smoothProgressBar.setVisibility(View.VISIBLE);

                                new TaskAceptarSolicitud(idUser, tokenUser, usuarios.get(posicion).getId(), 0, getApplicationContext(), smoothProgressBar, posicion).execute();

                            }
                        })

                        .setTitle(getText(R.string.solicitudAdmin));
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
        registerForContextMenu(lista);

    }





    private void mostrarBloqueadas() {
        ListView lv = (ListView) findViewById(R.id.list);

        usuarios = new ArrayList<>();
        adapter = new UsuarioListAdapter(this, usuarios);

        lv.setAdapter(adapter);

        Log.d(TAG, "EJECUTA");


        new TaskRecibirSolicitudes(idUser, adapter, tokenUser, getApplicationContext()).execute();

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
            default: startActivity(new Intent(SolicitudesAdminActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }








    /**
     * Created by Ismael Santos Cabaña on 27/11/15.
     * Recibir y mostrar solicitudes
     */
    public class TaskRecibirSolicitudes extends AsyncTask<Void, Void, String> {

        private final int id;
        private final String token;
        private final String TAG = "TFG_RecibirSolicitudes";
        private final String URLBloqueadas = Util.URL + "/recibirSolicitudes";
        private final Context context;
        private ArrayList<Usuario> usuarios = new ArrayList<>();
        private UsuarioListAdapter adapter;

        public TaskRecibirSolicitudes(int id, UsuarioListAdapter adapter, String token, Context context) {
            this.id = id;
            this.adapter = adapter;
            this.token = token;
            this.context = context;
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

            } catch (IOException ioe) {
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
                                Usuario usuario = new Usuario();
                                usuario.setId(json.getInt("id"));
                                usuario.setNombre(json.getString("nombre"));
                                usuario.setApellidos(json.getString("apellidos"));
                                usuario.setDni(json.getString("dni"));
                                usuario.setUser(json.getString("user"));

                                this.usuarios.add(usuario);

                            }


                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showSolicitudes();


                } else if (results.contentEquals("[]")) {

                    Toast.makeText(context, getText(R.string.noSolicitudes), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;

                }else if(results.length() == 0){
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, SolicitudesAdminActivity.this);
                }else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                    showSolicitudes();
                    isRefreshing = false;


                }
            }

        }


        public void showSolicitudes() {

            adapter.setData(usuarios);
            isRefreshing = false; //Termina el Task y para el icono de actualziar


        }


    }


    /**
     * Created by Ismael Santos cabaña on 27/11/15.
     * Aceptar solicitud
     */
    public class TaskAceptarSolicitud extends AsyncTask<Void, Void, String> {

        private final int id, idSolicitud,operacion;
        private final String token;
        private final String TAG = "TFG_AceptarSolicitud";
        private final String URL = Util.URL + "/aceptarSolicitud";
        private final Context context;
        private SmoothProgressBar smoothProgressBar;
        private final int posicion;

        public TaskAceptarSolicitud(int id, String token, int idSolicitud,int operacion, Context context, SmoothProgressBar smoothProgressBar,int posicion) {
            this.id = id;
            this.idSolicitud = idSolicitud;
            this.operacion = operacion;
            this.token = token;
            this.context = context;
            this.smoothProgressBar = smoothProgressBar;
            this.posicion = posicion;
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            String response = "";
            OkHttp servidor = new OkHttp();
            try {
                json.put("id", id);
                json.put("token", token);
                json.put("idSolicitud", idSolicitud);
                json.put("operacion",operacion);

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
                    Toast.makeText(context, getText(R.string.solicitudAceptada), Toast.LENGTH_SHORT).show();
                    adapter.remove(posicion);
                } else if (results.contentEquals("Cancelado")) {
                    Toast.makeText(context, getText(R.string.solicitudDescartada), Toast.LENGTH_SHORT).show();
                    adapter.remove(posicion);
                }else if(results.contentEquals("token")) {
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, SolicitudesAdminActivity.this);
                } else {
                    Toast.makeText(context,getText(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, getText(R.string.error), Toast.LENGTH_SHORT).show();
            }
            smoothProgressBar.progressiveStop();
            smoothProgressBar.setVisibility(View.INVISIBLE);
        }

    }


}
