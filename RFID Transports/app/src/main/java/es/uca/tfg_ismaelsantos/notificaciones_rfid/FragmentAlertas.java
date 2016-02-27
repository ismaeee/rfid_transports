package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Alerta;
import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.ListAdapter.AlertListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by ismaeee on 20/10/15.
 */
public class FragmentAlertas extends Fragment implements MainActivity.OnBackPressedListenerAlerta {

    View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();

    boolean flag = true;
    boolean isRefreshing = false;
    final int FRAGMENT_GROUP_ID = 333;

    private ListView lv;
    private ArrayList<Alerta> alertas;
    private AlertListAdapter adapterAlerta;
    public static boolean noshow = false;
    public FragmentAlertas() {
        setHasOptionsMenu(true);
    }

    public static boolean servidor = true;
    private SharedPreferences sharedpreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_alertas, container, false);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

        lv = (ListView) v.findViewById(R.id.listAlertas);

        alertas = new ArrayList<>();
        adapterAlerta = new AlertListAdapter(getActivity(), alertas);

        lv.setAdapter(adapterAlerta);

        sharedpreferences = v.getContext().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);


        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                System.out.println("Se actualiza");
                servidor = true;
                isRefreshing = true;
                handler.post(refreshing);
                alertas = new ArrayList<>();
                adapterAlerta = new AlertListAdapter(getActivity(),alertas);
                lv.setAdapter(adapterAlerta);
                new TaskAlertas(adapterAlerta, v.getContext(), getActivity(), sharedpreferences).execute();


            }
        });
        // sets the colors used in the refresh animation
        //swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.rojoRFID));

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (lv != null && lv.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lv.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                    swipeRefreshLayout.setEnabled(enable);

                }
            }
        });

        if(servidor) {
            new TaskAlertas(adapterAlerta, v.getContext(), getActivity(), sharedpreferences).execute();
        }else{
            new TaskAlertas(adapterAlerta, v.getContext(), getActivity(), sharedpreferences).showAlertas();
        }




        return v;
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


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences = v.getContext().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);

        ListView lista = (ListView) v.findViewById(R.id.listAlertas);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), AlertaActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", alertas.get(position).getId());
                b.putInt("position", position);

                intent.putExtra("bundle", b);
                intent.setAction("fragment");
                startActivity(intent);
            }
        });
        registerForContextMenu(lista);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alertas, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

        }

        return super.onOptionsItemSelected(item);
    }



    long period = 1000;
    long now = 0;
    long lastTime = 0;

    @Override
    public void doBack() {
        //BackPressed in activity will call this;
        Log.d("TFG_Alertas","Presiona atraaaassss");
        now = Calendar.getInstance().getTimeInMillis();
        if((now -lastTime)<period){
            Activity activity = getActivity();
            if(activity!=null){
                activity.finishAffinity();
            }
        }else{
            lastTime = now;
        }
    }






    /**
     * TaskAlertas
     */


    public class TaskAlertas extends AsyncTask<Void, Void, String> {

        private final String URLAlertas = Util.URL + "/getAlertas";
        private final SharedPreferences sharedpreferences;
        private final String TAG = "TFG_Alertas";
        private final Context context;
        private final Activity activity;
        private final AlertListAdapter adapter;
        private final AlertaDBAdapter db;
        private final UserDBAdapter dbUser;
        private List<Alerta> alertas = new ArrayList<>();
        private List<Alerta> listAlertas = new ArrayList<>();


        public TaskAlertas(AlertListAdapter adapter, Context context, Activity activity, SharedPreferences sharedpreferences) {
            this.sharedpreferences = sharedpreferences;
            this.context = context;
            this.activity = activity;
            this.adapter = adapter;
            this.db = new AlertaDBAdapter(context);
            this.dbUser = new UserDBAdapter(context);
            Log.d(TAG, "Se ejecuta taskalertas");
        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text ;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", sharedpreferences.getInt("id", 0));
                json.put("token", sharedpreferences.getString("token", ""));

                text = servidor.post(URLAlertas, json.toJSONString());
                Log.d(TAG,"Text:"+text);

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {


            Log.d(TAG,"Resultado: " + results);
            if (results != null) {
                if (results.contains("[{")) {

                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(results);

                        if (jsonArray != null) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);

                                //Alerta
                                long fecha_creacion = json.getLong("fecha_creacion");
                                String sFechaCreacion = Util.getFechaString(fecha_creacion, Calendar.getInstance());

                                JSONObject jsonUsuario = json.getJSONObject("usuario");
                                Usuario usuario = new Usuario(jsonUsuario.getInt("id"), jsonUsuario.getInt("rol"), jsonUsuario.getString("user"), jsonUsuario.getString("dni"), jsonUsuario.getString("nombre"), jsonUsuario.getString("apellidos"), jsonUsuario.getString("email"), jsonUsuario.getString("telefono"));

                                Alerta alerta = new Alerta(json.getInt("id"), json.getInt("id_tarjeta"), jsonUsuario.getInt("id"), json.getDouble("saldo_bd"), json.getDouble("saldo_tarjeta"), json.getString("lugar"), sFechaCreacion, fecha_creacion, json.getString("uid"));

                                insertUser(usuario);

                                this.alertas.add(alerta);

                            }

                            insertAlertas();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Numero de alertas: " + alertas.size());

                    showAlertas();
                    servidor = false;

                } else if (results.contentEquals("[]")) {

                    if (!noshow)
                        Toast.makeText(context, context.getText(R.string.noAlertas), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;
                    //mProgressBar.progressiveStop();

                } else if (results.length() == 0) {
                    Log.d(TAG, "Error de token: " + results);
                    //Toast.makeText(context, "Error de token", Toast.LENGTH_SHORT).show();
                    Activity activity = getActivity();
                    if(activity!= null){
                        Sesion sesion = new Sesion();
                        sesion.muestraAlertaSesion(context,activity);
                    }

                    showAlertas();
                } else {
                    Log.d(TAG, "Error de conexion: " + results);
                    Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                    showAlertas();
                    //mProgressBar.progressiveStop();

                }


            }else {
                Log.d(TAG, "Error de conexion: " + results);
                Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                showAlertas();
            }

            if(noshow){ noshow = false;}
            isRefreshing = false;


        }

        protected void insertAlertas() {

            Log.d(TAG, "Inserta Alertas");
            db.open();

            db.deleteAllAlert();

            for (int i = 0; i < alertas.size(); i++) {
                db.createAlerta(alertas.get(i));
            }

            db.close();

        }


        protected void insertUser(Usuario usuario) {
            dbUser.open();
            dbUser.createUser(usuario);
            dbUser.close();
        }


        public void showAlertas() {

            db.open();

            Cursor cursor = db.fetchAllAlert();
            Log.d(TAG, "Muestra alertas: "+cursor.getCount());

            for (int i = 0; i < cursor.getCount(); i++) {

                int id = cursor.getInt(cursor.getColumnIndex(db.KEY_ROWID));
                int id_tarjeta = cursor.getInt(cursor.getColumnIndex(db.KEY_IDTARJETA));
                int id_usuario = cursor.getInt(cursor.getColumnIndex(db.KEY_ID_ALERTUSER));
                double saldo_db = cursor.getDouble(cursor.getColumnIndex(db.KEY_SALDODB));
                double saldo_tarjeta = cursor.getDouble(cursor.getColumnIndex(db.KEY_SALDOTARJETA));
                String lugar = cursor.getString(cursor.getColumnIndex(db.KEY_LUGAR));
                String sFecha_creacion = cursor.getString(cursor.getColumnIndex(db.KEY_FECHASTRING));
                long fecha_creacion = cursor.getLong(cursor.getColumnIndex(db.KEY_FECHA));
                String uid = cursor.getString(cursor.getColumnIndex(db.KEY_UID));


                cursor.moveToNext();
                this.listAlertas.add(new Alerta(id, id_tarjeta, id_usuario, saldo_db, saldo_tarjeta, lugar, sFecha_creacion, fecha_creacion, uid));
            }


            db.close();
            adapter.setData(listAlertas);
            Log.d(TAG, "ListAlertas :" + listAlertas.size());

            isRefreshing = false; //Termina el Task y para el icono de actualziar

            //mProgressBar.progressiveStop();


        }





    }

}
