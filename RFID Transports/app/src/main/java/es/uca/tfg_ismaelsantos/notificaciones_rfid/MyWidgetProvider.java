package es.uca.tfg_ismaelsantos.notificaciones_rfid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.asyncTasks.OkHttp;
import es.uca.tfg_ismaelsantos.dbAdapter.TarjetaDBAdapter;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 2/12/15.
 * Widget para consultar saldo
 */
public class MyWidgetProvider extends AppWidgetProvider {
    public static int saldoWidget;
    public static String uidWidget;
    public static String mensajeSaldo = "";
    public static String mensajeUid = "";
    public static String mensajeFecha = "";
    public static final String TAG = "TFG_Widget";
    private static boolean firstTime = true;
    private static final String ACTION_CLICK = "ACTION_CLICK";
    private Handler handler = new Handler();
    private static int cont = 0;
    static private List<Tarjeta> tarjetasWidget  = new ArrayList<>();
    private static boolean continua = false;
    private static final String MyOnClick = "myOnClickTag";
    private AppWidgetManager appWidgetManager;
    private int[]appWidgetIds;
    private String fecha;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.MyPREFERENCES, Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String token = sharedPreferences.getString("token", "");

        Log.d(TAG,"mensajeSaldo: "+saldoWidget);
        Log.d(TAG,"mensajeUID: "+uidWidget);
        Log.d(TAG,"mensajeFecha: "+mensajeFecha);

        if(firstTime){

            mensajeUid = context.getText(R.string.presiona).toString();
            firstTime = false;
        }

        if(id!=0){
            //if(firstTime){
            new getTarjetasUser(id,token,context).execute();
            //    firstTime = false;
        }

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            // Set the text
            remoteViews.setTextViewText(R.id.update, mensajeUid);
            remoteViews.setTextViewText(R.id.update2, mensajeSaldo);
            remoteViews.setTextViewText(R.id.update3, mensajeFecha);


            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.update2, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }


    }








    /**
     * getTarjetasUser
     */


    public class getTarjetasUser extends AsyncTask<Void, Void, String> {
        private final int id;
        private final String token;
        private final String URL = Util.URL + "/getTarjetasUser";
        private List<Tarjeta> listTarjetas= new ArrayList<>();
        private final TarjetaDBAdapter db;
        private Context context;

        public getTarjetasUser(int id, String token,Context context){
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

                                this.listTarjetas.add(tarjeta);

                            }

                            obtenerDatos();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (results.contentEquals("[]")) {
                    obtenerDatos();

                } else {
                    Log.d(TAG, "Error de conexión: " + results);
                    Toast.makeText(context, context.getText(R.string.TaskLoginErrorConexion), Toast.LENGTH_SHORT).show();

                }
            }

        }

        protected void obtenerDatos() {

            Log.d(TAG,"ObtenerDatos");
            String fecha = "";
            cont++;
            tarjetasWidget = listTarjetas;
            if(listTarjetas.size()>0){
                saldoWidget = listTarjetas.get(cont%listTarjetas.size()).getSaldo();
                uidWidget = listTarjetas.get(cont%listTarjetas.size()).getUid();
                fecha = Util.getFechaStringWithSecond(Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
                mensajeSaldo = saldoWidget/200.0+"€";
                mensajeUid = uidWidget;
                mensajeFecha = fecha;
            }else{
                mensajeSaldo = "";
                mensajeUid = context.getText(R.string.noTarjetas).toString();
                mensajeFecha = "";
            }


        }



    }





}