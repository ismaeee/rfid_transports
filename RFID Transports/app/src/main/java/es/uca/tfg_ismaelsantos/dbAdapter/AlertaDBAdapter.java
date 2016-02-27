package es.uca.tfg_ismaelsantos.dbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.uca.tfg_ismaelsantos.Clases.Alerta;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 18/11/15.
 * Adapter para obtener datos de alerta de la base de datos interna
 */
public class AlertaDBAdapter {

    // Nombre de columnas de tabla alerta
    public static final String KEY_ROWID = "id";
    public static final String KEY_IDTARJETA = "id_tarjeta";
    public static final String KEY_SALDODB = "saldo_bd";
    public static final String KEY_SALDOTARJETA = "saldo_tarjeta";
    public static final String KEY_LUGAR = "lugar";
    public static final String KEY_FECHASTRING = "sFecha_creacion";
    public static final String KEY_FECHA = "fecha_creacion";
    public static final String KEY_UID = "uid";
    public static final String KEY_ID_ALERTUSER = "id_usuario";

    // Nombre de columnas de tabla tarjeta
    public static final String KEY_ID_TARJETA = "id";
    public static final String KEY_NOMBRE_TARJETA = "nombreapellidos";
    public static final String KEY_UID_TARJETA = "UID";
    public static final String KEY_FECHA_BLOQUEO_TARJETA = "fechaBloqueo";
    public static final String KEY_BLOQUEADA_TARJETA = "bloqueada";


    public static final String KEY_FECHA_ACTUALIZACION_TARJETA="fechaActualizacion";
    public static final String KEY_FECHA_CREACION_TARJETA="fechaCreacion";
    public static final String KEY_FECHA_ACTUALIZACION_TARJETA_LONG="fechaActualizacionLong";
    public static final String KEY_FECHA_CREACION_TARJETA_LONG="fechaCreacionLong";
    public static final String KEY_FECHA_BLOQUEO_TARJETA_LONG="fechaBloqueoLong";
    public static final String KEY_ID_USUARIO_TARJETA ="idUsuario";
    public static final String KEY_SALDO_TARJETA="saldo";


    // Nombre de columnas de tabla user
    public static final String KEY_ID_USER = "id";
    public static final String KEY_USER = "user";
    public static final String KEY_ROL_USER = "rol";
    public static final String KEY_DNI_USER = "dni";
    public static final String KEY_NOMBRE_USER = "nombre";
    public static final String KEY_APELLIDOS_USER = "apellidos";
    public static final String KEY_EMAIL_USER = "email";
    public static final String KEY_TELEFONO_USER = "telefono";

    private static final String TAG = "AlertaDbAdapter";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;



    private final Context context;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + Util.SQLITE_TABLE_ALERTA + " (" +
                    KEY_ROWID + " integer PRIMARY KEY," +
                    KEY_IDTARJETA + "," +
                    KEY_SALDODB + "," +
                    KEY_SALDOTARJETA + "," +
                    KEY_LUGAR + "," +
                    KEY_FECHASTRING+ ","+
                    KEY_FECHA+","+
                    KEY_ID_ALERTUSER+","+
                    KEY_UID+ ");";



    private static final String DATABASE_CREATE2 =
            "CREATE TABLE if not exists " + Util.SQLITE_TABLE_USER + " (" +
                    KEY_ID_USER + " integer PRIMARY KEY," +
                    KEY_USER + "," +
                    KEY_ROL_USER + "," +
                    KEY_DNI_USER + "," +
                    KEY_NOMBRE_USER + "," +
                    KEY_APELLIDOS_USER + "," +
                    KEY_EMAIL_USER+ "," +
                    KEY_TELEFONO_USER + "," +
                    " UNIQUE (" + KEY_USER +"));";


    private static final String DATABASE_CREATE_USER_BUSQUEDA =
            "CREATE TABLE if not exists " + Util.SQLITE_TABLE_USER_BUSQUEDA + " (" +
                    KEY_ID_USER + " integer PRIMARY KEY," +
                    KEY_USER + "," +
                    KEY_DNI_USER + "," +
                    KEY_NOMBRE_USER + "," +
                    KEY_APELLIDOS_USER + "," +
                    " UNIQUE (" + KEY_USER +"));";

    private static final String DATABASE_CREATE_TARJETA_BUSQUEDA =
            "CREATE TABLE if not exists " + Util.SQLITE_TABLE_TARJETA_BUSQUEDA + " (" +
                    KEY_ID_TARJETA + " integer PRIMARY KEY," +
                    KEY_UID_TARJETA + "," +
                    KEY_NOMBRE_TARJETA + "," +
                    KEY_BLOQUEADA_TARJETA + ","+
                    KEY_FECHA_BLOQUEO_TARJETA + ", "+
                    " UNIQUE (" + KEY_UID +"));";



    private static final String DATABASE_CREATE_TARJETA =
            "CREATE TABLE if not exists " + Util.SQLITE_TABLE_TARJETA + " (" +
                    KEY_ID_TARJETA + " integer PRIMARY KEY," +
                    KEY_UID_TARJETA + "," +
                    KEY_FECHA_ACTUALIZACION_TARJETA + ", "+
                    KEY_FECHA_CREACION_TARJETA + ", "+
                    KEY_FECHA_ACTUALIZACION_TARJETA_LONG + ", "+
                    KEY_FECHA_CREACION_TARJETA_LONG + ", "+
                    KEY_ID_USUARIO_TARJETA + ", "+
                    KEY_SALDO_TARJETA + ", "+
                    " UNIQUE (" + KEY_UID +"));";


    /**
     * Constructor
     * @param context
     */
    public AlertaDBAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open, para "abrir" la base de datos y poder realizar las demás operaciones
     * @return
     * @throws SQLException
     */
    public AlertaDBAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close, para "cerrar" la base de datos
     */
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }


    /**
     * createAlerta, para almacenar una alerta en la base de datos mediante objeto Alerta
     * @param alerta
     * @return
     */
    public long createAlerta(Alerta alerta) {
        int id = alerta.getId();
        int id_tarjeta = alerta.getId_tarjeta();
        int id_usuario = alerta.getId_usuario();
        double saldo_bd = alerta.getSaldo_bd();
        double saldo_tarjeta = alerta.getSaldo_tarjeta();
        String lugar = alerta.getLugar();
        String sFecha_creacion = alerta.getsFecha_creacion();
        long fecha_creacion = alerta.getFecha_creacion();
        String uid = alerta.getUID();

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_IDTARJETA, id_tarjeta);
        initialValues.put(KEY_ID_ALERTUSER,id_usuario);
        initialValues.put(KEY_SALDODB, saldo_bd);
        initialValues.put(KEY_SALDOTARJETA, saldo_tarjeta);
        initialValues.put(KEY_LUGAR, lugar);
        initialValues.put(KEY_FECHASTRING,sFecha_creacion);
        initialValues.put(KEY_FECHA, fecha_creacion);
        initialValues.put(KEY_UID, uid);

        return db.insert(Util.SQLITE_TABLE_ALERTA, null, initialValues);

    }


    /**
     * Extraer alerta (por cursor) mediante id
     * @param id
     * @return
     * @throws SQLException
     */
    public Cursor fetchAlertById(int id) throws SQLException {
        Log.w(TAG, "Alert id: " + id);
        Cursor mCursor = null;

        mCursor = db.query(true, Util.SQLITE_TABLE_ALERTA, new String[]{KEY_ROWID,KEY_IDTARJETA,KEY_ID_ALERTUSER,KEY_SALDODB,KEY_SALDOTARJETA,KEY_LUGAR,KEY_FECHASTRING,KEY_FECHA,KEY_UID},
                KEY_ROWID + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * devolverAlerta, obtiene alerta mediante id
     * @param id
     * @return
     * @throws SQLException
     */
    public Alerta devolverAlerta(int id) throws SQLException {
        Log.w(TAG, "Alert id: " + id);
        Cursor mCursor = null;

        mCursor = db.query(true, Util.SQLITE_TABLE_ALERTA, new String[]{KEY_ROWID,KEY_IDTARJETA,KEY_ID_ALERTUSER,KEY_SALDODB,KEY_SALDOTARJETA,KEY_LUGAR,KEY_FECHASTRING,KEY_FECHA,KEY_UID},
                KEY_ROWID + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return new Alerta(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)),mCursor.getInt(mCursor.getColumnIndex(KEY_IDTARJETA)),mCursor.getInt(mCursor.getColumnIndex(KEY_ID_ALERTUSER)),mCursor.getDouble(mCursor.getColumnIndex(KEY_SALDODB)),
                mCursor.getDouble(mCursor.getColumnIndex(KEY_SALDOTARJETA)),mCursor.getString(mCursor.getColumnIndex(KEY_LUGAR)),mCursor.getString(mCursor.getColumnIndex(KEY_FECHASTRING)),mCursor.getLong(mCursor.getColumnIndex(KEY_FECHA)),
                mCursor.getString(mCursor.getColumnIndex(KEY_UID)));
    }


    /**
     * Extraer todas las alertas por cursor
     * @return
     */
    public Cursor fetchAllAlert() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_ALERTA, new String[] {KEY_ROWID,KEY_IDTARJETA,KEY_ID_ALERTUSER,KEY_SALDODB,KEY_SALDOTARJETA,KEY_LUGAR,KEY_FECHASTRING,KEY_FECHA,KEY_UID},
                null, null, null, null, KEY_FECHA+" DESC",null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Eliminar alerta por id
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean deleteAlert(int id) throws SQLException {
        Log.w(TAG, "Delete Alert id: " + id);

        return db.delete(Util.SQLITE_TABLE_ALERTA, KEY_ROWID + "=" + id, null) > 0;

    }


    /**
     * Eliminar todas las alertas
     * @return
     */
    public int deleteAllAlert() {
        int count = db.delete(Util.SQLITE_TABLE_ALERTA, null , null);
        Log.w(TAG, Integer.toString(count));
        return count ;

    }


    /**
     * DatabaseHelper
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            crearTablaUsuarios(db);
            crearTablaUsuariosBusqueda(db);
            crearTablaTarjetasBusqueda(db);
            crearTablaTarjetas(db);
        }

        private void crearTablaUsuarios(SQLiteDatabase db){db.execSQL(DATABASE_CREATE2);}
        private void crearTablaUsuariosBusqueda(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_USER_BUSQUEDA);}
        private void crearTablaTarjetasBusqueda(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_TARJETA_BUSQUEDA);}
        private void crearTablaTarjetas(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_TARJETA);}



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_ALERTA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER_BUSQUEDA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA_BUSQUEDA);

            onCreate(db);
        }

    }




}


