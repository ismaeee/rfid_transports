package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.ListAdapter.TarjetaListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.asyncTasks.TaskBloquearTarjeta;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 20/10/15.
 * Fragment de tarjetas para admin
 */
public class FragmentTarjetas extends Fragment implements MainActivity.OnBackPressedListenerTarjeta {
    View v;

    private SmoothProgressBar mProgressBar;
    private boolean flag = true;
    final int FRAGMENT_GROUP_ID =  333;
    final int FRAGMENT_GROUP_ID_ELIMINAR =  3331;
    final int FRAGMENT_GROUP_ID_BLOQUEAR =  3332;
    final private String TAG = "TFG_TARJETAS";
    private int id;
    private String token;

    private static boolean runonce = true;
    Toolbar toolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private ListView lv;
    private TarjetaListAdapter adapter;
    private List<Tarjeta> listTarjeta = new ArrayList<>();
    public FragmentTarjetas(){
        setHasOptionsMenu(true);
    }
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    private Handler handler = new Handler();
    private SmoothProgressBar smoothProgressBar;
    private boolean primeravez = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tarjetas,container,false);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

        smoothProgressBar = (SmoothProgressBar)v.findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);


        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("id", 0);
        token = sharedPreferences.getString("token", "");

        lv = (ListView) v.findViewById(R.id.listTarjetas);
        listTarjeta= new ArrayList<>();

        adapter = new TarjetaListAdapter(getActivity(),listTarjeta);
        lv.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                isRefreshing = true;
                handler.post(refreshing);
                listTarjeta= new ArrayList<>();
                adapter = new TarjetaListAdapter(getActivity(),listTarjeta);
                TaskRecibirTarjetasLista task = new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), 1);
                task.execute();
                lv.setAdapter(adapter);
                lv.setOnScrollListener(new EndlessScrollListener(swipeRefreshLayout) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        startSmoothProgressBar(smoothProgressBar);
                        new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), page).execute();
                    }
                });

            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.rojoRFID));


        lv.setOnScrollListener(new EndlessScrollListener(swipeRefreshLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), page).execute();
            }
        });

        new TaskRecibirTarjetasLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), 1).execute();


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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchAction =  menu.findItem(R.id.action_search);
        super.onPrepareOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case -1:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
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


    protected void handleMenuSearch(){
        //ActionBar action = ((ActionBarActivity) getActivity()).getSupportActionBar(); //get the actionbar
        final ActionBar action  =((AppCompatActivity)getActivity()).getSupportActionBar();

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar
            Log.d(TAG, "Muestra titulo de nuevo");

            //hides the keyboard

            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            //add the search icon in the action bar
            //mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white_48dp));
            mSearchAction.setIcon(R.mipmap.ic_search_white_48dp);

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            final Activity activity = getActivity();

            //this is a listener to do a search when the user clicks on search button

            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
                        action.setDisplayShowTitleEnabled(true); //show the title in the action bar
                        Log.d(TAG, "Muestra titulo de nuevo");

                        //hides the keyboard

                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        //add the search icon in the action bar
                        //mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white_48dp));
                        mSearchAction.setIcon(R.mipmap.ic_search_white_48dp);

                        isSearchOpened = false;

                        Toast.makeText(getActivity(), getText(R.string.busqueda)+": "+edtSeach.getText().toString(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(R.mipmap.ic_action);

            isSearchOpened = true;

            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(edtSeach.getText().length() >= 2) {
                        ArrayList<Tarjeta> tarjetas = new ArrayList<>();
                        ListView lv = (ListView) v.findViewById(R.id.listTarjetas);
                        TarjetaListAdapter adapter = new TarjetaListAdapter(getActivity(),tarjetas);
                        lv.setAdapter(adapter);

                        new TaskRecibirTarjetas(edtSeach.getText().toString(),id,token,adapter).execute();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    long period = 1000;
    long now = 0;
    long lastTime = 0;

    /**
     * Control de doble click en botón atrás
     */
    @Override
    public void doBack() {
        //BackPressed in activity will call this;
        Log.d(TAG,"Presiona atraaaassss");

        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }else{
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
    }



    @Override
    public void onResume() {
        super.onResume();

        ListView lista = (ListView) v.findViewById(R.id.listTarjetas);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), TarjetaActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", listTarjeta.get(position).getId());
                b.putInt("position", position);

                intent.putExtra("bundle", b);

                startActivity(intent);
            }
        });
        registerForContextMenu(lista);

    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        SpannableString sOpciones = new SpannableString(getText(R.string.opciones));
        SpannableString sBloquear = new SpannableString("  "+getText(R.string.bloquearTarjetaOp));
        SpannableString sEliminar = new SpannableString("  "+getText(R.string.eliminarTarjetaOp));
        sOpciones.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sOpciones.length(), 0);
        sEliminar.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sEliminar.length(), 0);
        sBloquear.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sBloquear.length(), 0);


        menu.setHeaderTitle(sOpciones);
        menu.add(FRAGMENT_GROUP_ID_BLOQUEAR, v.getId(), 0, "").setTitle(sBloquear);
        menu.add(FRAGMENT_GROUP_ID_ELIMINAR, v.getId(), 1, "").setTitle(sEliminar);
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getGroupId() == FRAGMENT_GROUP_ID_BLOQUEAR) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int getId = listTarjeta.get(info.position).getId();
            final int infoPosition = info.position;
            final SharedPreferences preferences = getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);


            if(listTarjeta.get(info.position).getBloqueada() == 1){
                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.TaskBloquearTarjetaUnique), Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final Tarjeta tarjeta = listTarjeta.get(info.position);
                tarjeta.setBloqueada(1);
                builder.setMessage(getText(R.string.seguroBloquear))
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                startSmoothProgressBar(smoothProgressBar);
                                new TaskBloquearTarjeta(preferences.getInt("id", 0),preferences.getString("token", ""),getId,getActivity().getApplicationContext(),getActivity(),smoothProgressBar,v,adapter,infoPosition,tarjeta).execute();

                            }
                        })
                        .setTitle((getText(R.string.bloquearTarjeta)));
                AlertDialog alert = builder.create();
                alert.show();
            }


            return true;

        }else if(item.getGroupId() == FRAGMENT_GROUP_ID_ELIMINAR){

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int getId = listTarjeta.get(info.position).getId();
            final int infoPosition = info.position;
            final SharedPreferences preferences = getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getText(R.string.seguroTarjeta))
                    .setCancelable(true)
                    .setNegativeButton((getText(R.string.cancelar)), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton((getText(R.string.si)), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            startSmoothProgressBar(smoothProgressBar);
                            new EliminarTarjeta(preferences.getInt("id", 0), preferences.getString("token", ""), getId, infoPosition, getActivity().getApplicationContext()).execute();

                        }
                    })
                    .setTitle(getText(R.string.eliminarTarjeta));
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }else{
            return false;
        }


    }




    /**
     * Created by Ismael Santos Cabaña on 20/10/15.
     * Fragment de TaskRecibirTarjetas para el rol de administrador
     * recibe tarjetas según el texto enviado por búsqueda
     */


    public class TaskRecibirTarjetas extends AsyncTask<Void, Void, String> {

        private final int id;
        private final String token;
        private final String texto;
        private final String TAG = "TFG_RecibirTarjetas";
        private final String URLRecibirTarjeta = Util.URL + "/recibirTarjetas";
        private List<Tarjeta> tarjetas = new ArrayList<>();
        private final TarjetaListAdapter adapter;


        /**
         * Constructor
         * @param texto
         * @param id
         * @param token
         * @param adapter
         */
        public TaskRecibirTarjetas(String texto, int id, String token,TarjetaListAdapter adapter) {
            this.id = id;
            this.texto = texto;
            this.token = token;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttp servidor = new OkHttp();
            String text = null;
            JSONObject json = new JSONObject();

            try {
                Log.d(TAG, "url: " + URLRecibirTarjeta);
                json.put("id", id);
                json.put("token", token);
                json.put("texto", texto);

                text = servidor.post(URLRecibirTarjeta, json.toJSONString());
                Log.d(TAG,text);

            } catch (IOException ce) {
                ce.printStackTrace();
            }
            return text;
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
                                Tarjeta tarjeta = new Tarjeta(json.getInt("id"),json.getString("uid"),json.getString("nombreUsuario"), json.getInt("bloqueada"));
                                this.tarjetas.add(tarjeta);
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showTarjetas();


                } else if (results.length() == 0) {
                    new Sesion().muestraAlertaSesion(getActivity().getApplicationContext(), getActivity());

                } else {
                    Log.d(TAG, "Error de conexión: " + results);

                }
            }

        }



        public void showTarjetas(){
            adapter.setData(tarjetas);
            listTarjeta = tarjetas;
        }

    }



    /**
     * Created by Ismael Santos Cabaña on 20/10/15.
     * Fragment de TaskRecibirTarjetasLista para el rol de admin
     * Recibe tarjetas por paginación
     */
    public class TaskRecibirTarjetasLista extends AsyncTask<Void, Void, String> {
        private final int id;
        private final String token;
        private final String TAG = "TFG_CargarMasTarjetas";
        private final String URLRecibirTarjetas = Util.URL + "/recibirTarjetaLista";
        private List<Tarjeta> tarjetas= new ArrayList<>();
        private final TarjetaListAdapter adapter;
        private final int page;
        private final Activity activity;
        private Context context;

        /**
         * Constructor
         * @param id
         * @param token
         * @param adapter
         * @param activity
         * @param context
         * @param page
         */
        public TaskRecibirTarjetasLista(int id, String token,TarjetaListAdapter adapter,Activity activity,Context context, int page){
            this.activity = activity;
            this.adapter = adapter;
            this.page = page;
            this.token = token;
            this.id = id;
            this.context = context;
        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttp servidor = new OkHttp();
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();

            try {

                json.put("id", id);
                json.put("token", token);
                json.put("page",page);

                text = servidor.post(URLRecibirTarjetas, json.toJSONString());

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {


            Log.d(TAG, "Resultado: " + results);
            if (results != null) {
                if (results.contains("[{")) {

                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(results);

                        if (jsonArray != null) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                org.json.JSONObject json = jsonArray.getJSONObject(i);

                                Tarjeta tarjeta = new Tarjeta(json.getInt("id"),json.getString("uid"),json.getString("nombreUsuario"),json.getInt("bloqueada"));

                                this.tarjetas.add(tarjeta);

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    adapter.setData(tarjetas);


                }else if(results.length()==0){
                    Log.d(TAG, "Error de token");
                    if(!primeravez)
                        Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

                }else if(results.contentEquals("[]")){
                    Log.d(TAG, "No hay más tarjetas para cargar: " + results);
                } else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

                }
            }
            isRefreshing = false;
            stopSmoothProgressBar(smoothProgressBar);
        }






    }








    /**
     * Created by Ismael Santos Cabaña on 20/10/15.
     * Eliminar tarjeta
     */
    public class EliminarTarjeta extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/eliminarTarjeta";
        private final String token;
        private final int id;
        private final Context context;
        private final int idTarjeta;
        private final int position;

        /**
         * Constructor
         * @param id
         * @param token
         * @param idTarjeta
         * @param position
         * @param context
         */
        public EliminarTarjeta(int id, String token, int idTarjeta, int position, Context context) {
            this.context = context;
            this.token = token;
            this.id = id;
            this.idTarjeta= idTarjeta;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttp servidor = new OkHttp();
            String text;
            org.json.simple.JSONObject json = new org.json.simple.JSONObject();
            try {
                json.put("id", id);
                json.put("token", token);
                json.put("idTarjeta", idTarjeta);

                text = servidor.post(URL, json.toJSONString());

            } catch (Exception e) {
                return e.toString();
            }

            return text;
        }


        @Override
        protected void onPostExecute(String results) {
            Log.d("TFG_Eliminar", "Results: " + results);
            String mensaje = "";
            if(results.length() > 0){
                if(results.contentEquals("Ok")) {
                    adapter.remove(position);
                    mensaje = context.getText(R.string.eliminado).toString();
                }else if(results.contentEquals("token")){
                    mensaje = context.getText(R.string.errorSesion).toString();
                    new Sesion().muestraAlertaSesion(context, getActivity());
                }else{
                    mensaje = context.getText(R.string.noEliminado).toString();
                }
            }else{
                mensaje = context.getText(R.string.noEliminado).toString();
            }
            Snackbar.make(v, mensaje, Snackbar.LENGTH_LONG).show();
            stopSmoothProgressBar(smoothProgressBar);
        }
    }



}


