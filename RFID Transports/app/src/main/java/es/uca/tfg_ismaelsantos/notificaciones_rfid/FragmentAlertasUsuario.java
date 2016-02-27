package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import es.uca.tfg_ismaelsantos.ListAdapter.AlertListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 20/10/15.
 * Fragment de Alertas para el rol de Usuario
 */
public class FragmentAlertasUsuario extends Fragment {

    View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();

    boolean flag = true;
    boolean isRefreshing = false;
    final int FRAGMENT_GROUP_ID = 333;

    private ListView lv;
    private ArrayList<Alerta> alertas;
    private AlertListAdapter adapterAlerta;
    private AlertDialog alert;
    public FragmentAlertasUsuario() {
        setHasOptionsMenu(true);
    }

    public static boolean servidor = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_alertas_usuario, container, false);
        setHasOptionsMenu(true);
        lv = (ListView) v.findViewById(R.id.listAlertas);
        alertas = new ArrayList<>();
        adapterAlerta = new AlertListAdapter(getActivity(), alertas);
        lv.setAdapter(adapterAlerta);



        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                System.out.println("Se actualiza");

                isRefreshing = true;
                handler.post(refreshing);
                servidor = true;
                mostrarAlertas();


            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.rojoRFID));
        swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));



        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (lv != null && lv.getChildCount() > 0) {
                    Log.d("TFG_FragmentAlertUser","Entra");
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


        mostrarAlertas();

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
                muestraAlertaDetalles(alertas.get(position));
            }
        });
        registerForContextMenu(lista);

    }


    /**
     * Muestra detalles de alerta
     * @param alerta
     */
    private void muestraAlertaDetalles(Alerta alerta){
        Log.d("TFG_F_A_U","Muestra detalles de alerta id:"+alerta.getId());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.activity_alerta_usuario, null);

        DecimalFormat decimales = new DecimalFormat("0.00");

        TextView id,uid,saldoDB,saldoCard,fecha;


        id = (TextView)dialogLayout.findViewById(R.id.id);
        id.setText( " "+alerta.getId());

        uid = (TextView)dialogLayout.findViewById(R.id.uid);
        uid.setText(" "+alerta.getUID());

        saldoDB = (TextView)dialogLayout.findViewById(R.id.saldoDB);
        saldoDB.setText(" "+decimales.format(alerta.getSaldo_bd()/200.0)+"€");

        saldoCard = (TextView)dialogLayout.findViewById(R.id.saldoCard);
        saldoCard.setText(" "+decimales.format(alerta.getSaldo_tarjeta()/200.0)+"€");

        fecha = (TextView)dialogLayout.findViewById(R.id.fecha);
        fecha.setText(" " + alerta.getsFecha_creacion());


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogLayout);
        alert = builder.create();
        alert.show();
    }

    private void mostrarAlertas() {

        SharedPreferences sharedpreferences = v.getContext().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        alertas = new ArrayList<>();
        adapterAlerta = new AlertListAdapter(getActivity(), alertas);

        Log.d("TFG_F_A_U", "Ejecuta \"mostrarAlertas()\" con servidor? " + servidor);
        if (servidor) {
            Log.d("TFG_F_A_U","Consulta al servidor");

            //AlertaDBAdapter adapter, Context context, Activity activity, SharedPreferences sharedpreferences
            new TaskAlertas(adapterAlerta, v.getContext(), getActivity(), sharedpreferences).execute();
            servidor = false;
        } else {
            Log.d("TFG_F_A_U","Consulta al la base de datos");
            new TaskAlertas(adapterAlerta, v.getContext(), getActivity(), sharedpreferences).showAlertas();
        }

        lv.setAdapter(adapterAlerta);


    }




    /**
     * Created by Ismael Santos Cabaña on 27/11/15.
     * TaskAlertas, obtiene las tarjetas
     */


    public class TaskAlertas extends AsyncTask<Void, Void, String> {

        private final String URLAlertas = Util.URL + "/getAlertasUser";
        private final SharedPreferences sharedpreferences;
        private final String TAG = "TFG_AlertasUsuario";
        private final Context context;
        private final Activity activity;
        private final AlertListAdapter adapter;
        private final AlertaDBAdapter db;
        private final UserDBAdapter dbUser;
        private List<Alerta> alertas = new ArrayList<>();
        private List<Alerta> listAlertas = new ArrayList<>();
        private final int id;

        /**
         * Constructor
         * @param adapter
         * @param context
         * @param activity
         * @param sharedpreferences
         */
        public TaskAlertas(AlertListAdapter adapter, Context context, Activity activity, SharedPreferences sharedpreferences) {
            this.sharedpreferences = sharedpreferences;
            this.id = sharedpreferences.getInt("id",0);
            this.context = context;
            this.activity = activity;
            this.adapter = adapter;
            this.db = new AlertaDBAdapter(context);
            this.dbUser = new UserDBAdapter(context);
        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", sharedpreferences.getInt("id", 0));
                json.put("token", sharedpreferences.getString("token", ""));

                text = servidor.post(URLAlertas, json.toJSONString());

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

                                Alerta alerta = new Alerta(json.getInt("id"), json.getInt("id_tarjeta"), id, json.getDouble("saldo_bd"), json.getDouble("saldo_tarjeta"), json.getString("lugar"), sFechaCreacion, fecha_creacion, json.getString("uid"));


                                this.alertas.add(alerta);

                            }

                            insertAlertas();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showAlertas();


                } else if (results.contentEquals("[]")) {
                    if(isRefreshing) Toast.makeText(context, context.getText(R.string.noAlertas), Toast.LENGTH_SHORT).show();
                    isRefreshing = false;
                    //mProgressBar.progressiveStop();

                }else if(results.length()==0){
                    Log.d(TAG, "Error de token");
                    new Sesion().muestraAlertaSesion(context, activity);

                }
            }else {
                Log.d(TAG, "Error de conexión: " + results);
                Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();
                showAlertas();
                isRefreshing = false;
                //mProgressBar.progressiveStop();
            }

        }

        protected void insertAlertas() {

            Log.d(TAG, "Inserta Alertas");
            db.open();

            int eliminadas = db.deleteAllAlert();

            Log.d("TFG_F_A_U",eliminadas+" Eliminadas");
            if(alertas != null)
                Log.d("TFG_F_A_U","Insertadas nuevas: "+alertas.size());


            for (int i = 0; i < alertas.size(); i++) {
                db.createAlerta(alertas.get(i));
            }

            db.close();

        }



        public void showAlertas() {

            Log.d(TAG, "Muestra alertas");
            db.open();

            Cursor cursor = db.fetchAllAlert();

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

            if(listAlertas != null)
                Log.d("TFG_F_A_U", "Va a insertar x alertas: " + listAlertas.size());


            adapter.setData(listAlertas);

            isRefreshing = false; //Termina el Task y para el icono de actualziar

        }





    }

}
