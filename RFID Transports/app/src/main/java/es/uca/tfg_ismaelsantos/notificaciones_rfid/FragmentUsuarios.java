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

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.ListAdapter.UsuarioListAdapter;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.utils.Sesion;
import es.uca.tfg_ismaelsantos.utils.Util;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Ismael Santos Cabaña on 20/10/15.
 * FragmentUsuarios, fragment para el rol Admin, muestra todos los usuarios
 */
public class FragmentUsuarios extends Fragment implements MainActivity.OnBackPressedListenerUser {
    View v;

    private SmoothProgressBar smoothProgressBar;
    private boolean flag = true;
    private final int FRAGMENT_GROUP_ID =  111;
    private final int FRAGMENT_GROUP_ID_ROL =  1112;

    private final String TAG = "TFG_FragmentUsuario";
    private int id;
    private String token;
    private Toolbar toolbar;
    private MenuItem mSearchAction;
    public boolean isSearchOpened = false;
    private EditText edtSeach;
    private ListView lv;
    public FragmentUsuarios(){
        setHasOptionsMenu(true);
    }
    private List<Usuario> listUsuarios = new ArrayList<>();
    private UsuarioListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    private Handler handler = new Handler();
    private Context context;
    private  boolean primeraVez = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_usuarios,container,false);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        smoothProgressBar = (SmoothProgressBar)v.findViewById(R.id.smoothProgressBar);
        initSmoothProgressBar(smoothProgressBar);
        context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("id", 0);
        token = sharedPreferences.getString("token", "");

        lv = (ListView) v.findViewById(R.id.listUsuarios);
        listUsuarios = new ArrayList<>();

        adapter = new UsuarioListAdapter(getActivity(),listUsuarios);
        lv.setAdapter(adapter);



        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                isRefreshing = true;
                handler.post(refreshing);
                listUsuarios = new ArrayList<>();
                adapter = new UsuarioListAdapter(getActivity(),listUsuarios);
                TaskRecibirUsuariosLista task = new TaskRecibirUsuariosLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), 1);
                task.execute();
                lv.setAdapter(adapter);
                lv.setOnScrollListener(new EndlessScrollListener(swipeRefreshLayout) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        startSmoothProgressBar(smoothProgressBar);
                        new TaskRecibirUsuariosLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), page).execute();
                    }
                });

            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.rojoRFID));

        lv.setOnScrollListener(new EndlessScrollListener(swipeRefreshLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new TaskRecibirUsuariosLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), page).execute();
            }
        });



        new TaskRecibirUsuariosLista(id, token, adapter, getActivity(), getActivity().getApplicationContext(), 1).execute();




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

    /**
     * Init progressBar
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
     * Empieza porgressBar
     * @param smoothProgressBar
     */
    private void startSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    /**
     * Para progressBar
     * @param smoothProgressBar
     */
    private void stopSmoothProgressBar(SmoothProgressBar smoothProgressBar){
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        SpannableString sOpciones = new SpannableString(getText(R.string.opciones));
        SpannableString sEliminar = new SpannableString("  "+getText(R.string.eliminarUsuarioOp));
        SpannableString sRol = new SpannableString("  "+getText(R.string.cambiarRolOp));
        sOpciones.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sOpciones.length(), 0);
        sEliminar.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sEliminar.length(), 0);
        sRol.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sRol.length(), 0);

        menu.setHeaderTitle(sOpciones);
        menu.add(FRAGMENT_GROUP_ID, v.getId(), 0, "  ").setTitle(sEliminar);

        if(getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE).getInt("rol",0) == 3)
            menu.add(FRAGMENT_GROUP_ID_ROL, v.getId(), 0, "  ").setTitle(sRol);



    }

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getGroupId() == FRAGMENT_GROUP_ID){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            final int getId = listUsuarios.get(info.position).getId();
            final int infoPosition = info.position;
            final SharedPreferences preferences =  getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);

            if(getId == preferences.getInt("id",0)){
                Toast.makeText(getActivity(), getText(R.string.simismo), Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(getText(R.string.seguroEliminar))
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                startSmoothProgressBar(smoothProgressBar);
                                new EliminarUsuario(preferences.getInt("id",0),preferences.getString("token", ""),getId,infoPosition, getActivity().getApplicationContext()).execute();

                            }
                        })
                        .setTitle(getText(R.string.eliminarUsuarioOp));
                AlertDialog alert = builder.create();
                alert.show();

            }

            return true;

        }else if(item.getGroupId() == FRAGMENT_GROUP_ID_ROL){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            final int getId = listUsuarios.get(info.position).getId();
            final int infoPosition = info.position;
            final SharedPreferences preferences =  getActivity().getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);

            if(getId == preferences.getInt("id",0)){
                Toast.makeText(getActivity(), getText(R.string.simismoRol), Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final int rol = listUsuarios.get(info.position).getRol();
                if(rol == 2) {
                    builder.setMessage(getText(R.string.seguroRolAdmin));
                }else{
                    builder.setMessage(getText(R.string.seguroRolUser));
                }

                builder.setCancelable(true)
                        .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                startSmoothProgressBar(smoothProgressBar);
                                new CambiarRol(preferences.getInt("id",0),preferences.getString("token", ""),getId,rol, getActivity().getApplicationContext(),infoPosition).execute();

                            }
                        })
                        .setTitle(getText(R.string.cambiarRolOp));
                AlertDialog alert = builder.create();
                alert.show();

            }

            return true;

        }else{
            return false;
        }
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



    protected void handleMenuSearch(){
        Log.d(TAG,"Se ejecuta Handlemenusearch ");

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
                    if(edtSeach.getText().length() >= 3) {
                        ArrayList<Usuario> usuarios = new ArrayList<>();
                        ListView lv = (ListView) v.findViewById(R.id.listUsuarios);
                        UsuarioListAdapter adapter = new UsuarioListAdapter(getActivity(), usuarios);
                        lv.setAdapter(adapter);

                        new TaskRecibirUsuarios(edtSeach.getText().toString(),id,token,adapter).execute();
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
     * Salir de la aplciación con doble atrás
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

        ListView lista = (ListView) v.findViewById(R.id.listUsuarios);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), UserActivity.class);
                Bundle b = new Bundle();
                b.putInt("id", listUsuarios.get(position).getId());
                b.putInt("position", position);
                b.putString("name",listUsuarios.get(position).getUser());

                intent.putExtra("bundle", b);

                startActivity(intent);
            }
        });
        registerForContextMenu(lista);

    }















    /**
     * Created by Ismael Santos Cabaña on 20/10/15.
     * TaskRecibirUsuaruis, recibe usuarios coincidente con búsqueda
     */


    public class TaskRecibirUsuarios extends AsyncTask<Void, Void, String> {

        private final int id;
        private final String token;
        private final String texto;
        private final String TAG = "TFG_DescartarAlerta";
        private final String URLRecibirUsuario = Util.URL + "/recibirUsuarios";
        private List<Usuario> usuarios = new ArrayList<>();
        private final UsuarioListAdapter adapter;

        /**
         * Constructor
         * @param texto
         * @param id
         * @param token
         * @param adapter
         */
        public TaskRecibirUsuarios(String texto, int id, String token,UsuarioListAdapter adapter) {
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
                Log.d(TAG, "url: " + URLRecibirUsuario);
                json.put("id", id);
                json.put("token", token);
                json.put("texto", texto);

                text = servidor.post(URLRecibirUsuario, json.toJSONString());


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

                                Usuario usuario = new Usuario();
                                usuario.setId(json.getInt("id"));
                                usuario.setNombre(json.getString("nombre"));
                                usuario.setApellidos(json.getString("apellidos"));
                                usuario.setDni(json.getString("dni"));
                                usuario.setUser(json.getString("user"));
                                usuario.setRol(json.getInt("rol"));

                                this.usuarios.add(usuario);
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    showUsuarios();


                } else if (results.length() == 0) {
                    Log.d(TAG, "Error de token: " + results);

                    Activity activity = getActivity();
                    if(activity!= null){
                        Sesion sesion = new Sesion();
                        sesion.muestraAlertaSesion(context,activity);
                    }

                }else {
                    Log.d(TAG, "Error de conexión: " + results);

                }
            }

        }



        public void showUsuarios(){
            adapter.setData(usuarios);
            listUsuarios = usuarios;

        }

    }








    /**
     * Created by Ismael Santos Cabaña on 20/10/15.
     * TaskRecibirUsuariosLista, recibe usuarios por paginación
     */

    public class TaskRecibirUsuariosLista extends AsyncTask<Void, Void, String> {
        private final int id;
        private final String token;
        private final String TAG = "TFG_CargarMasUsuarios";
        private final String URLRecibirUsuario = Util.URL + "/recibirUsuariosLista";
        private List<Usuario> usuarios = new ArrayList<>();
        private final UsuarioListAdapter adapter;
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
        public TaskRecibirUsuariosLista(int id, String token,UsuarioListAdapter adapter,Activity activity,Context context, int page){
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

                text = servidor.post(URLRecibirUsuario, json.toJSONString());

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

                                Usuario usuario = new Usuario();
                                usuario.setId(json.getInt("id"));
                                usuario.setNombre(json.getString("nombre"));
                                usuario.setApellidos(json.getString("apellidos"));
                                usuario.setDni(json.getString("dni"));
                                usuario.setUser(json.getString("user"));
                                usuario.setRol(json.getInt("rol"));


                                this.usuarios.add(usuario);

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    adapter.setData(usuarios);


                } else if (results.contentEquals("[]")) {
                    Log.d(TAG,"Todos los usuarios cargados");

                }else {
                    Log.d(TAG, "Error de conexión: " + results);
                    if(!primeraVez) Toast.makeText(context, getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

                }
            }

            isRefreshing = false;
            stopSmoothProgressBar(smoothProgressBar);

        }


    }








    /**
     * Created by Ismael Santos Cabaña on 11/11/15.
     * EliminarUsuario, task para eliminar usuario por servicio REST
     */
    public class EliminarUsuario extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/eliminarUsuario";
        private final String token;
        private final int id;
        private final Context context;
        private final int idUsuario;
        private final int position;

        /**
         * Constructor
         * @param id
         * @param token
         * @param idUsuario
         * @param position
         * @param context
         */
        public EliminarUsuario(int id, String token, int idUsuario, int position, Context context) {
            this.context = context;
            this.token = token;
            this.id = id;
            this.idUsuario = idUsuario;
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
                json.put("idUsuario", idUsuario);

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
                    mensaje = getText(R.string.eliminado).toString();
                }else if(results.contentEquals("Admin")){

                    mensaje = getText(R.string.soloSuperAdmin).toString();
                }else if(results.contentEquals("Token")){
                    mensaje = getText(R.string.errorSesion).toString();
                    new Sesion().muestraAlertaSesion(context,getActivity());
                }
                else{
                    mensaje = getText(R.string.noEliminado).toString();
                    //Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(!primeraVez) mensaje = getText(R.string.noEliminado).toString();

            }
            Snackbar.make(v, mensaje, Snackbar.LENGTH_LONG).show();
            stopSmoothProgressBar(smoothProgressBar);

            primeraVez = false;
        }



    }








    /**
     * Created by Ismael Santos Cabaña on 23/11/15.
     * Task para cambiar el rol de un usuario (superadministrador)
     */
    public class CambiarRol extends AsyncTask<Void, Void, String> {

        private final String URL = Util.URL + "/cambiarRolMenu";
        private final String token;
        private final int id;
        private final Context context;
        private final int idUsuario;
        private final int rol;
        private int posicion;

        public CambiarRol(int id, String token, int idUsuario, int rol, Context context, int position) {
            this.context = context;
            this.token = token;
            this.id = id;
            this.idUsuario = idUsuario;
            this.posicion = position;

            if(rol == 1) {
                this.rol = 2;
            }else{
                this.rol = 1;
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
                json.put("idUsuario", idUsuario);
                json.put("rol", rol);


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
                if(results.contentEquals("ok")) {
                    mensaje = getText(R.string.rolCambiado).toString();
                    Usuario usuario = listUsuarios.get(posicion);
                    usuario.setRol(rol);
                    adapter.setData(usuario, posicion);
                }else if(results.contentEquals("token")){
                    mensaje = getText(R.string.errorSesion).toString();
                    new Sesion().muestraAlertaSesion(context,getActivity());
                }
                else{
                    mensaje = getText(R.string.errorCambiarRol).toString();
                }
            }else{
                mensaje = getText(R.string.TaskLoginErrorConexion).toString();
            }


            Snackbar.make(v, mensaje, Snackbar.LENGTH_LONG).show();
            stopSmoothProgressBar(smoothProgressBar);

        }
    }


}


