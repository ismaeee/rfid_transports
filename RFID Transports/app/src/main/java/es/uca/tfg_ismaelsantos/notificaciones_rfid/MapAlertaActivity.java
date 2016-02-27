package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ismael Santos Cabaña on 21/11/15.
 * Muestra en mapa de Google Map la localización de la alerta
 */

public class MapAlertaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private GoogleMap googleMap;
    private LatLng location;
    private int id;
    private String fecha,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        initToolbar();

        if(checkPlayServices()){
            Bundle bundle = this.getIntent().getExtras();

            if(bundle != null){
                location = new LatLng(bundle.getDouble("latitud"),bundle.getDouble("longitud"));
                id = bundle.getInt("id");
                fecha = bundle.getString("fecha");
                uid = bundle.getString("uid");
            }
            setUpMapIfNeeded();






        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            // Check if we were successful in obtaining the map.
            if (googleMap!= null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        MarkerOptions markerOptions = new MarkerOptions().position(location).title(getText(R.string.labelAlerta)+" "+id).snippet(getText(R.string.adFecha)+" "+fecha+"\n"+getText(R.string.adUID)+" "+uid);
        googleMap.addMarker(markerOptions);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


    }


    /**
     * Inicia Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        toolbar.setLogo(R.mipmap.logo_inverso_mini_x2);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();

            }
            return false;
        }
        return true;
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
            default: startActivity(new Intent(MapAlertaActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
