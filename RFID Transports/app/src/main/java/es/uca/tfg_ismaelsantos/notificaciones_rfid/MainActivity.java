package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.uca.tfg_ismaelsantos.dbAdapter.AlertaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.dbAdapter.UserDBAdapter;
import es.uca.tfg_ismaelsantos.tab.SlidingTabLayout;
import es.uca.tfg_ismaelsantos.tab.ViewPagerAdapter;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 20/9/15.
 * MainActivity, activity principal del sistema, contiene panel, fragment etc.
 */
public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar mToolbar;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int rol;
    String user,nombre,apellidos,email;
    private final int FRAGMENT_GROUP_ID_USER = 111;
    private final int FRAGMENT_GROUP_ID_TARJETA = 333;
    private SharedPreferences sharedPreferences;
    private final String TAG = "TFG_Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkSession()){ //Comprueba si tiene alguna sesión iniciada antes de hacer nada más
            setContentView(R.layout.activity_main);

            initToolbar();
            setupNavigationView();

            Bundle bundle = this.getIntent().getExtras();


            int posicionFragment;


            rol = getSharedPreferences(Util.MyPREFERENCES,MODE_PRIVATE).getInt("rol",1);

            if((rol==1)||(rol==3)) { //Si es admin o superadmin

                posicionFragment = 1;
                if(bundle != null) {
                    posicionFragment = bundle.getInt("fragment");
                }
                int[] imageResIdAdmins = {
                        R.mipmap.ic_people_white_24dp,
                        R.mipmap.ic_notifications_white_24dp,
                        R.mipmap.ic_credit_card_white_24dp
                };

                //Se crea el ViewPagerAdapter y el Viepager y los parámetros de configuracion de las preferencias. Todo_ ello para generar los tab
                adapter =  new ViewPagerAdapter(getSupportFragmentManager(),imageResIdAdmins,getApplicationContext(),rol);
                pager = (ViewPager) findViewById(R.id.pager);
                pager.setAdapter(adapter);
                pager.setCurrentItem(posicionFragment);



            }else{
                posicionFragment = 0;
                if(bundle != null) {
                    posicionFragment = bundle.getInt("fragment");
                }

                int[] imageResIdUsers = {
                        R.mipmap.ic_credit_card_white_24dp,
                        R.mipmap.ic_notifications_white_24dp

                };


                //Se crea el ViewPagerAdapter y el Viepager y los parámetros de configuracion de las preferencias. Todo_ ello para generar los tab
                adapter =  new ViewPagerAdapter(getSupportFragmentManager(),imageResIdUsers,getApplicationContext(),rol);
                pager = (ViewPager) findViewById(R.id.pager);
                pager.setAdapter(adapter);
                pager.setCurrentItem(posicionFragment);
            }

            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true);
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.red_transparente);
                }
            });
            tabs.setCustomTabView(R.layout.custom_tab, 0);
            tabs.setViewPager(pager);





        }else{
            startActivity(new Intent(MainActivity.this,LoginActivity.class));

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch(id) {

            case android.R.id.home:
            {
                if(drawerLayout != null)
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    /**
     * Método para cerrar sesión, para ello se elimina las preferencias compartidas y tablas en la base de datos y posteriormente envía a la activity de TaskLogin/registro
     */
    private void cerrarSesion(){
        getSharedPreferences(Util.MyPREFERENCES,MODE_PRIVATE).edit().clear().apply();
        AlertaDBAdapter dbAlerta = new AlertaDBAdapter(getApplicationContext());
        dbAlerta.open();
        dbAlerta.deleteAllAlert();
        dbAlerta.close();

        TarjetaDBAdapter dbTarjeta = new TarjetaDBAdapter(getApplicationContext());
        dbTarjeta.open();
        dbTarjeta.deleteAllTarjeta();
        dbTarjeta.deleteAllTarjetaUser();
        dbTarjeta.deleteAllTarjetaBloqueada();
        dbTarjeta.close();

        UserDBAdapter dbUser = new UserDBAdapter(getApplicationContext());
        dbUser.open();
        dbUser.deleteAllUserBusqueda();
        dbUser.deleteAllUser();
        dbUser.close();

        FragmentAlertas.servidor = true;
        FragmentAlertasUsuario.servidor = true;

        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }

    /**
     * Método para comprobar si hay una sesión abierta, para ello comprueba si tiene algún elemento almacenado en las preferencias compartidas,
     * como por ejemplo el Token, que será necesario para todas las interacciones que se hagan con el servidor
     */
    private boolean checkSession(){
        Log.d(TAG, "Se cierra sesión");
        if(getSharedPreferences(Util.MyPREFERENCES, MODE_PRIVATE).getString("token", "").length() > 0) return true;
        else return false;
    }


    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.myToolbar);
        mToolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(mToolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

    }




    private void setupNavigationView(){
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation);
        if(navigationView != null){
            setupDrawerContent(navigationView);
        }

    }


    private void setupDrawerContent(NavigationView navigationView) {
        SharedPreferences sharedPreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        rol = sharedPreferences.getInt("rol", 2);
        user = sharedPreferences.getString("user", "-");
        nombre = sharedPreferences.getString("nombre", "-");
        apellidos = sharedPreferences.getString("apellidos","-");
        email = sharedPreferences.getString("email","-");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        if(drawerLayout != null){
            if(rol != 3) {//Superadministrador
                MenuItem item = navigationView.getMenu().getItem(2);
                item.setVisible(false);
            }else{
                MenuItem item = navigationView.getMenu().getItem(2);
                item.setVisible(true);
            }
            if((rol != 1) && ( rol != 3)){
                MenuItem item = navigationView.getMenu().getItem(1);
                item.setVisible(false);
            }else{
                MenuItem item = navigationView.getMenu().getItem(1);
                item.setVisible(true);
            }

            TextView nombre_nav = (TextView) findViewById(R.id.name_nav_header);
            nombre_nav.setText(nombre+" "+apellidos);

            TextView username_nav = (TextView) findViewById(R.id.username_nav_header);
            username_nav.setText(user);

            TextView email_nav = (TextView) findViewById(R.id.email_nav_header);
            email_nav.setText(email);

            ImageView imageview = (ImageView) findViewById(R.id.circle_image);

            switch (rol){
                case 1: imageview.setImageResource(R.mipmap.ic_admin);break;
                case 2: imageview.setImageResource(R.mipmap.ic_user);break;
                case 3: imageview.setImageResource(R.mipmap.ic_super_admin);break;
            }
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {

                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            selectItem(id);
                            return true;
                        }
                    }
            );

        }
    }


    private void selectItem(int id) {
        switch (id){
            case R.id.navigation_item_1: nuevaTarjeta(); break;
            case R.id.navigation_item_2: startActivity(new Intent(MainActivity.this,BloqueadasActivity.class)); break;
            case R.id.navigation_item_3: startActivity(new Intent(MainActivity.this, SolicitudesAdminActivity.class)); break;
            case R.id.nav_log_out: cerrarSesion(); break;
            case R.id.about:
                //Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                showAbout(); break;


        }

    }


    private  void nuevaTarjeta(){
        SharedPreferences sharedPreferences = getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        rol = sharedPreferences.getInt("rol", 2);
        if(rol==2){
            startActivity(new Intent(MainActivity.this,NewTarjetaParaUsuariosActivity.class));
        }else{
            startActivity(new Intent(MainActivity.this,NewTarjetaActivity.class));
        }

    }


    public void showAbout(){
        LayoutInflater inflater = getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.about, null);
        /*
        TextView contenidoAbout = (TextView)dialogLayout.findViewById(R.id.contenidoAbout);

        contenidoAbout.setText("Realizado por: Ismael Santos Cabaña\n" +
                "ismael.santosca@alum.uca.es\n\n" +
                "TFG presentado por: Ismael Santos Cabaña\n" +
                "Profesor director del proyecto: Juan Boubeta Puig\n\n" +
                "Universidad de Cádiz\nEscuela Superior de Ingeniería\n2015 - 2016");
        */

        TextView ok = (TextView) dialogLayout.findViewById(R.id.okAbout);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        final AlertDialog alert = builder.create();
        alert.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
    }


    protected OnBackPressedListenerUser onBackPressedListenerUser;
    protected OnBackPressedListenerTarjeta onBackPressedListenerTarjeta;
    protected OnBackPressedListenerAlerta onBackPressedListenerAlerta;

    public interface OnBackPressedListenerUser {
        void doBack();
    }

    public interface OnBackPressedListenerTarjeta {
        void doBack();
    }

    public interface OnBackPressedListenerAlerta {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListenerUser onBackPressedListener) {
        this.onBackPressedListenerUser = onBackPressedListener;
    }

    public void setOnBackPressedListener(OnBackPressedListenerTarjeta onBackPressedListener) {
        this.onBackPressedListenerTarjeta = onBackPressedListener;
    }

    public void setOnBackPressedListener(OnBackPressedListenerAlerta onBackPressedListener) {
        this.onBackPressedListenerAlerta = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if((onBackPressedListenerAlerta != null)||(onBackPressedListenerTarjeta != null)||(onBackPressedListenerUser != null)) {

            if (onBackPressedListenerUser != null) {
                onBackPressedListenerUser.doBack();
            }

            if (onBackPressedListenerTarjeta != null) {
                onBackPressedListenerTarjeta.doBack();
            }

            if (onBackPressedListenerAlerta != null) {
                onBackPressedListenerAlerta.doBack();
            }
        }else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        onBackPressedListenerUser = null;
        onBackPressedListenerAlerta = null;
        onBackPressedListenerTarjeta = null;
        super.onDestroy();
    }





}