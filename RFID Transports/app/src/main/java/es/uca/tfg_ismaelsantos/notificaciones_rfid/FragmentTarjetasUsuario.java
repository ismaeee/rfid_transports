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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.ListAdapter.TarjetaListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 20/10/15.
 * FragmentTarjetasUsuario, tarjetas de usuario para rol user
 */
public class FragmentTarjetasUsuario extends Fragment  {
    View v;

    final int FRAGMENT_GROUP_ID =  333;
    final private String TAG = "TFG_usuarios";
    private int id;
    private String token;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();

    boolean flag = true;
    boolean isRefreshing = false;

    public static boolean runonce = true;
    Toolbar toolbar;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private ListView lv;
    private TarjetaListAdapter adapter;
    private ArrayList<Tarjeta> listTarjeta = new ArrayList<>();
    public FragmentTarjetasUsuario(){
        setHasOptionsMenu(true);
    }
    private String nombre;
    private int rol;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tarjetas_usuario, container, false);
        setHasOptionsMenu(true);
        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("id", 0);
        token = sharedPreferences.getString("token", "");
        nombre = sharedPreferences.getString("nombre","")+" "+sharedPreferences.getString("apellidos","");
        rol = sharedPreferences.getInt("rol",0);
        lv = (ListView) v.findViewById(R.id.list);
        listTarjeta= new ArrayList<>();
        adapter = new TarjetaListAdapter(getActivity(),listTarjeta,rol);
        lv.setAdapter(adapter);

        if(runonce) new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext()).execute();
        else new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext()).showTarjetas();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                System.out.println("Se actualiza");

                isRefreshing = true;
                handler.post(refreshing);
                listTarjeta = new ArrayList<>();
                adapter = new TarjetaListAdapter(getActivity(),listTarjeta,rol);
                lv.setAdapter(adapter);
                new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext()).execute();

            }
        });
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
                    Log.d("TFG_FragmentAlertUser", "Entra");
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

        ListView lista = (ListView) v.findViewById(R.id.list);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intentMap = new Intent(getActivity(), TarjetaActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", listTarjeta.get(position).getId());
                b.putInt("position", position);


                //intentMap.putExtra("tarjeta", new Gson().toJson(listTarjeta.get(position)));

                intentMap.putExtra("bundle", b);

                startActivity(intentMap);

            }
        });
        registerForContextMenu(lista);

    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vacio, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }












    /**
     * Created by Ismael Santos Cabaña on 22/10/15.
     * TaskRecibirTarjetasLista para el rol de Usuario, recibe lista de tarjeta
     */
    public class TaskRecibirTarjetasLista extends AsyncTask<Void, Void, String> {
        private final int id;
        private final String token;
        private final String TAG = "TFG_GetTarjetasUser";
        private final String URL = Util.URL + "/getTarjetasUser";
        private List<Tarjeta> tarjetas= new ArrayList<>();
        private List<Tarjeta> listTarjetas = new ArrayList<>();
        private final TarjetaListAdapter adapter;
        private final Activity activity;
        private final TarjetaDBAdapter db;
        private Context context;

        /**
         * Constructor
         * @param id
         * @param token
         * @param adapter
         * @param activity
         * @param context
         */
        public TaskRecibirTarjetasLista(int id, String token,TarjetaListAdapter adapter,Activity activity,Context context){
            this.activity = activity;
            this.adapter = adapter;
            this.db = new TarjetaDBAdapter(context);
            this.token = token;
            this.id = id;
            this.context = context;

        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text;
            JSONObject json = new JSONObject();

            try {

                json.put("id", id);
                json.put("token", token);

                text = servidor.post(URL, json.toJSONString());

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
                                org.json.JSONObject json = jsonArray.getJSONObject(i);

                                String fechaActualizacion = Util.getFechaString(json.getLong("fecha_actualizacion"), Calendar.getInstance());
                                String fechaCreacion = Util.getFechaString(json.getLong("fecha_creacion"), Calendar.getInstance());

                                String fechaBloqueo = null;
                                long lFechaBloqueo = 0;

                                if(!json.isNull("fecha_bloqueo"))
                                {
                                    fechaBloqueo = Util.getFechaString(json.getLong("fecha_bloqueo"), Calendar.getInstance());
                                    lFechaBloqueo = json.getLong("fecha_bloqueo");
                                }

                                Tarjeta tarjeta = new Tarjeta(json.getInt("id"), json.getInt("id_usuario"),json.getString("uid"),json.getInt("saldo"),json.getInt("bloqueada"),fechaActualizacion, fechaCreacion, fechaBloqueo, json.getLong("fecha_actualizacion"),json.getLong("fecha_creacion"),lFechaBloqueo);
                                this.tarjetas.add(tarjeta);

                            }
                            runonce = false;

                            insertTarjetas();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showTarjetas();


                } else if(results.length()==0){
                    Log.d(TAG,"Error de token");
                    new Sesion().muestraAlertaSesion(context, activity);

                }else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                    showTarjetas();

                }
            }
            isRefreshing = false;

        }

        protected void insertTarjetas() {

            Log.d(TAG, "Inserta Tarjetas");
            db.open();
            db.deleteAllTarjetaUser();

            for (int i = 0; i < tarjetas.size(); i++) {
                db.createTarjetaUser(tarjetas.get(i));
            }

            db.close();

        }





        public void showTarjetas() {

            Log.d(TAG, "Muestra tarjetas");
            db.open();

            Cursor cursor = db.fetchAllTarjetaUser();

            for (int i = 0; i < cursor.getCount(); i++) {
                //KEY_UID_TARJETA,KEY_SALDO_TARJETA, KEY_FECHA_ACTUALIZACION_TARJETA,KEY_FECHA_CREACION_TARJETA
                Tarjeta tarjeta = new Tarjeta(cursor.getInt(cursor.getColumnIndex(db.KEY_ID_TARJETA)),
                        id,
                        cursor.getString(cursor.getColumnIndex(db.KEY_UID_TARJETA)),
                        cursor.getInt(cursor.getColumnIndex(db.KEY_SALDO_TARJETA)),
                        cursor.getInt(cursor.getColumnIndex(db.KEY_BLOQUEADA_TARJETA)),
                        cursor.getString(cursor.getColumnIndex(db.KEY_FECHA_ACTUALIZACION_TARJETA)),
                        cursor.getString(cursor.getColumnIndex(db.KEY_FECHA_CREACION_TARJETA)),
                        cursor.getString(cursor.getColumnIndex(db.KEY_FECHA_BLOQUEO_TARJETA)),
                        0,
                        0,
                        0);

                cursor.moveToNext();

                this.listTarjetas.add(tarjeta);
            }


            db.close();
            adapter.setData(listTarjetas);

        }


    }



}


