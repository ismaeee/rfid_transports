package es.uca.tfg_ismaelsantos.dbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 18/11/15.
 * Adapter para obtener datos de tarjeta de la base de datos interna
 */
public class TarjetaDBAdapter {

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



    // Nombre de columnas de tabla usuario
    public static final String KEY_ID_USER = "id";
    public static final String KEY_USER = "user";
    public static final String KEY_ROL_USER = "rol";
    public static final String KEY_DNI_USER = "dni";
    public static final String KEY_NOMBRE_USER = "nombre";
    public static final String KEY_APELLIDOS_USER = "apellidos";
    public static final String KEY_EMAIL_USER = "email";
    public static final String KEY_TELEFONO_USER = "telefono";


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
                    KEY_BLOQUEADA_TARJETA + ","+
                    KEY_FECHA_ACTUALIZACION_TARJETA + ", "+
                    KEY_FECHA_CREACION_TARJETA + ", "+
                    KEY_FECHA_BLOQUEO_TARJETA + ", "+
                    KEY_FECHA_BLOQUEO_TARJETA_LONG +", "+
                    KEY_FECHA_ACTUALIZACION_TARJETA_LONG + ", "+
                    KEY_FECHA_CREACION_TARJETA_LONG + ", "+
                    KEY_ID_USUARIO_TARJETA + ", "+
                    KEY_SALDO_TARJETA + ", "+
                    " UNIQUE (" + KEY_UID +"));";


    /**
     * Constructor
     * @param context
     */
    public TarjetaDBAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open, para "abrir" la base de datos y poder realizar las demás operaciones
     * @return
     * @throws SQLException
     */
    public TarjetaDBAdapter open() throws SQLException {
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
     * createTarjetaUser, crea Tarjeta para usuario mediante objeto Tarjeta
     * @param tarjeta
     * @return
     */
    public long createTarjetaUser(Tarjeta tarjeta) {
        int id = tarjeta.getId();
        String uid = tarjeta.getUid();
        int idUsuario = tarjeta.getId_usuario();
        int saldo = tarjeta.getSaldo();
        String fechaCreacion = tarjeta.getFecha_creacion();
        String fechaActualizacion= tarjeta.getFecha_actualizacion();
        String fechaBloqueo = tarjeta.getFecha_bloqueo();
        long lFechaCreacion = tarjeta.getlFechaCreacion();
        long lFechaActualizacion = tarjeta.getlFechaActualizacion();
        long lFechaBloqueo = tarjeta.getlFechaBloqueo();
        int bloqueada = tarjeta.getBloqueada();

        ContentValues initialValues = new ContentValues();


        initialValues.put(KEY_ID_TARJETA, id);
        initialValues.put(KEY_UID_TARJETA, uid);
        initialValues.put(KEY_SALDO_TARJETA, saldo);
        initialValues.put(KEY_ID_USUARIO_TARJETA, idUsuario);
        initialValues.put(KEY_FECHA_ACTUALIZACION_TARJETA, fechaActualizacion);
        initialValues.put(KEY_FECHA_CREACION_TARJETA, fechaCreacion);
        initialValues.put(KEY_FECHA_ACTUALIZACION_TARJETA_LONG, lFechaActualizacion);
        initialValues.put(KEY_FECHA_CREACION_TARJETA_LONG, lFechaCreacion);
        initialValues.put(KEY_FECHA_BLOQUEO_TARJETA, fechaBloqueo);
        initialValues.put(KEY_FECHA_BLOQUEO_TARJETA_LONG, lFechaBloqueo);
        initialValues.put(KEY_BLOQUEADA_TARJETA, bloqueada);

        return db.insert(Util.SQLITE_TABLE_TARJETA, null, initialValues);
    }


    /**
     * createTarjetaBusqueda mediante obtjeto Tarjeta
     * @param tarjeta
     * @return
     */
    public long createTarjetaBusqueda(Tarjeta tarjeta) {
        int id = tarjeta.getId();
        String nombre = tarjeta.getNombreUsuario();
        String uid = tarjeta.getUid();

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_ID_TARJETA, id);
        initialValues.put(KEY_UID_TARJETA, uid);
        initialValues.put(KEY_NOMBRE_TARJETA, nombre);

        return db.insertWithOnConflict(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * createTarjetaBloqueo, almacena mediante objeto Tarjeta
     * @param tarjeta
     * @return
     */
    public long createTarjetaBloqueo(Tarjeta tarjeta) {
        int id = tarjeta.getId();
        String nombre = tarjeta.getNombreUsuario();
        String uid = tarjeta.getUid();
        String fechaBloqueo = tarjeta.getFechaBloqueo();
        ContentValues initialValues = new ContentValues();
        int bloqueada = tarjeta.getBloqueada();

        initialValues.put(KEY_ID_TARJETA, id);
        initialValues.put(KEY_UID_TARJETA, uid);
        initialValues.put(KEY_NOMBRE_TARJETA, nombre);
        initialValues.put(KEY_BLOQUEADA_TARJETA, bloqueada);
        initialValues.put(KEY_FECHA_BLOQUEO_TARJETA, fechaBloqueo);

        return db.insertWithOnConflict(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Extrae todas las tarjetas bloquedas por cursor
     * @return
     */
    public Cursor fetchAllTarjetaBloqueo() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, new String[]{KEY_ID_TARJETA, KEY_UID_TARJETA, KEY_NOMBRE_TARJETA, KEY_FECHA_BLOQUEO_TARJETA,KEY_BLOQUEADA_TARJETA},
                KEY_FECHA_BLOQUEO_TARJETA + " IS NOT NULL", null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Extrae todas las tarjetasUser por cursor
     * @return
     */
    public Cursor fetchAllTarjetaUser() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_TARJETA, new String[]{KEY_ID_TARJETA, KEY_UID_TARJETA,KEY_SALDO_TARJETA, KEY_FECHA_ACTUALIZACION_TARJETA,KEY_FECHA_CREACION_TARJETA,KEY_BLOQUEADA_TARJETA,KEY_FECHA_BLOQUEO_TARJETA},
                null, null, null, null, KEY_FECHA_ACTUALIZACION_TARJETA+" DESC", null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Extrae por cursor una tarjeta  de usuario mediante el id
     * @param id
     * @return
     */
    public Cursor fetchTarjetaByIdTarjeta_User(int id) {

        Cursor mCursor = db.query(true, Util.SQLITE_TABLE_TARJETA, new String[]{KEY_ID_TARJETA, KEY_UID_TARJETA,KEY_SALDO_TARJETA ,KEY_FECHA_ACTUALIZACION_TARJETA,KEY_FECHA_CREACION_TARJETA},
                KEY_ID_TARJETA + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Extrae por cursor tarjeta por id
     * @param id
     * @return
     * @throws SQLException
     */
    public Cursor fetchTarjetaById(int id) throws SQLException {
        Log.w(TAG, "TARJETA id: " + id);
        Cursor mCursor = null;

        mCursor = db.query(true, Util.SQLITE_TABLE_TARJETA_BUSQUEDA, new String[]{KEY_ID_TARJETA,KEY_UID_TARJETA,KEY_NOMBRE_TARJETA},
                KEY_ID_TARJETA + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Extrae todas las tarjetas por curosr
     * @return
     */
    public Cursor fetchAllTarjeta() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, new String[]{KEY_ID_TARJETA,KEY_UID_TARJETA,KEY_NOMBRE_TARJETA},
                KEY_FECHA_BLOQUEO_TARJETA + " IS NULL", null, null, null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Elimina todas las tarjetas
     * @return
     */
    public boolean deleteAll(){
        int count = db.delete(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, null , null);
        int count2 = db.delete(Util.SQLITE_TABLE_TARJETA, null, null);

        return ((count > 0)&&(count2>0));
    }

    /**
     * Elimina todas las tarjetas
     * @return
     */
    public boolean deleteAllTarjeta() {
        int count = db.delete(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, KEY_FECHA_BLOQUEO_TARJETA+" IS NULL" , null);
        Log.w(TAG, Integer.toString(count));
        return count > 0;

    }

    /**
     * Elimina todas las tarjetas bloqueadas
     * @return
     */
    public boolean deleteAllTarjetaBloqueada() {
        int count = db.delete(Util.SQLITE_TABLE_TARJETA_BUSQUEDA, KEY_FECHA_BLOQUEO_TARJETA +" IS NOT NULL", null);
        Log.w(TAG, Integer.toString(count));
        return count > 0;

    }

    /**
     * Elimina todas las tarjetas de usuario
     * @return
     */
    public boolean deleteAllTarjetaUser() {
        int count = db.delete(Util.SQLITE_TABLE_TARJETA, null , null);
        Log.w(TAG, Integer.toString(count));
        return count > 0;

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
            db.execSQL(DATABASE_CREATE_TARJETA_BUSQUEDA);
            db.execSQL(DATABASE_CREATE_TARJETA);
            crearTablaAlertas(db);
            crearTablaUsuarios(db);
            crearTablaUsuariosBusqueda(db);
        }

        private void crearTablaAlertas(SQLiteDatabase db){db.execSQL(DATABASE_CREATE);}
        private void crearTablaUsuarios(SQLiteDatabase db){db.execSQL(DATABASE_CREATE2);}
        private void crearTablaUsuariosBusqueda(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_USER_BUSQUEDA);}



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_ALERTA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA_BUSQUEDA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER_BUSQUEDA);

            onCreate(db);
        }

    }




}


