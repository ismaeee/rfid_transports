package es.uca.tfg_ismaelsantos.dbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.utils.Util;

/**
 * Created by Ismael Santos Cabaña on 18/11/15.
 * Adapter para obtener datos de usuario de la base de datos interna
 */
public class UserDBAdapter {

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


    private static final String TAG = "UserDbAdapter";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;



    private final Context context;

    private static final String DATABASE_CREATE_ALERTA =
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



    private static final String DATABASE_CREATE_USER =
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
     * Consctuctor
     * @param context
     */
    public UserDBAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open, para "abrir" la base de datos y poder realizar las demás operaciones
     * @return
     * @throws SQLException
     */
    public UserDBAdapter open() throws SQLException {
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
     * createUser, almacena en bd Usuario mediante objeto Usuario
     * @param usuario
     * @return
     */
    public long createUser(Usuario usuario) {

        int id = usuario.getId();
        String user = usuario.getUser();
        int rol = usuario.getRol();
        String dni = usuario.getDni();
        String nombre = usuario.getNombre();
        String apellidos  = usuario.getApellidos();
        String email = usuario.getEmail();
        String telefono = usuario.getTelefono();

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_ID_USER, id);
        initialValues.put(KEY_USER, user);
        initialValues.put(KEY_ROL_USER, rol);
        initialValues.put(KEY_DNI_USER, dni);
        initialValues.put(KEY_NOMBRE_USER, nombre);
        initialValues.put(KEY_APELLIDOS_USER,apellidos);
        initialValues.put(KEY_EMAIL_USER, email);
        initialValues.put(KEY_TELEFONO_USER, telefono);

        return db.insertWithOnConflict(Util.SQLITE_TABLE_USER, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /**
     * createUserBusqueda, almacena en bd usuarioBusqueda mediante objeto Usuario
     * @param usuario
     * @return
     */
    public long createUserBusqueda(Usuario usuario) {

        int id = usuario.getId();
        String user = usuario.getUser();
        String dni = usuario.getDni();
        String nombre = usuario.getNombre();
        String apellidos  = usuario.getApellidos();

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_ID_USER, id);
        initialValues.put(KEY_USER, user);
        initialValues.put(KEY_DNI_USER, dni);
        initialValues.put(KEY_NOMBRE_USER, nombre);
        initialValues.put(KEY_APELLIDOS_USER, apellidos);

        return db.insertWithOnConflict(Util.SQLITE_TABLE_USER_BUSQUEDA, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    /**
     * Selecciona Usuario por id
     * @param id
     * @return
     * @throws SQLException
     */
    public Cursor fetchUserById(int id) throws SQLException {
        Log.w(TAG, "User id: " + id);
        Cursor mCursor = null;

        mCursor = db.query(true, Util.SQLITE_TABLE_USER, new String[]{KEY_ID_USER,KEY_USER,KEY_ROL_USER,KEY_DNI_USER,KEY_NOMBRE_USER,KEY_APELLIDOS_USER,KEY_EMAIL_USER,KEY_TELEFONO_USER},
                KEY_ROWID + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Obtiene Usuario por id
     * @param id
     * @return
     * @throws SQLException
     */
    public Usuario devolverUsuario(int id) throws SQLException {
        Log.w(TAG, "Alert id: " + id);
        Cursor mCursor = null;

        mCursor = db.query(true, Util.SQLITE_TABLE_USER, new String[]{KEY_ID_USER,KEY_USER,KEY_ROL_USER,KEY_DNI_USER,KEY_NOMBRE_USER,KEY_APELLIDOS_USER,KEY_EMAIL_USER,KEY_TELEFONO_USER},
                KEY_ROWID + " = " + id, null,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return new Usuario(mCursor.getInt(mCursor.getColumnIndex(KEY_ID_USER)),mCursor.getInt(mCursor.getColumnIndex(KEY_ROL_USER)), mCursor.getString(mCursor.getColumnIndex(KEY_USER)),mCursor.getString(mCursor.getColumnIndex(KEY_DNI_USER)),
                mCursor.getString(mCursor.getColumnIndex(KEY_NOMBRE_USER)),mCursor.getString(mCursor.getColumnIndex(KEY_APELLIDOS_USER)),mCursor.getString(mCursor.getColumnIndex(KEY_EMAIL_USER)),mCursor.getString(mCursor.getColumnIndex(KEY_TELEFONO_USER)));
    }

    /**
     * Selecciona todos los usuarios
     * @return
     */
    public Cursor fetchAllUser() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_USER, new String[] {KEY_ID_USER,KEY_USER,KEY_ROL_USER,KEY_DNI_USER,KEY_NOMBRE_USER,KEY_APELLIDOS_USER,KEY_EMAIL_USER,KEY_TELEFONO_USER},
                null, null, null, null, KEY_FECHA+" DESC",null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Selecciona todos los usuarios de búsqueda
     * @return
     */
    public Cursor fetchAllUserBusqueda() {

        Cursor mCursor = db.query(Util.SQLITE_TABLE_USER_BUSQUEDA, new String[] {KEY_ID_USER,KEY_USER,KEY_DNI_USER,KEY_NOMBRE_USER,KEY_APELLIDOS_USER},
                null, null, null, null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * ELimina usuario por id
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean deleteUser(int id) throws SQLException {
        Log.w(TAG, "Delete User id: " + id);

        return db.delete(Util.SQLITE_TABLE_USER, KEY_ROWID + "=" + id, null) > 0;

    }


    /**
     * Elimina usuarioBusqueda por id
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean deleteUserBusqueda(int id) throws SQLException {
        Log.w(TAG, "Delete User id: " + id);

        return db.delete(Util.SQLITE_TABLE_USER_BUSQUEDA, KEY_ROWID + "=" + id, null) > 0;

    }

    /**
     * Elimina todos los usuarios
     * @return
     */
    public boolean deleteAllUser() {
        int count = db.delete(Util.SQLITE_TABLE_USER, null , null);
        int count2 = db.delete(Util.SQLITE_TABLE_USER_BUSQUEDA, null , null);
        return ((count > 0)&&(count2>0));

    }

    /**
     * Elimina todos los usuarioBusqueda
     * @return
     */
    public boolean deleteAllUserBusqueda() {
        int count = db.delete(Util.SQLITE_TABLE_USER_BUSQUEDA, null , null);
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
            db.execSQL(DATABASE_CREATE_USER);
            db.execSQL(DATABASE_CREATE_USER_BUSQUEDA);
            crearTablaAlertas(db);
            crearTablaTarjetasBusqueda(db);
            crearTablaTarjetas(db);

        }

        private void crearTablaAlertas(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_ALERTA);}
        private void crearTablaTarjetasBusqueda(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_TARJETA_BUSQUEDA);}
        private void crearTablaTarjetas(SQLiteDatabase db){db.execSQL(DATABASE_CREATE_TARJETA);}


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_USER_BUSQUEDA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA_BUSQUEDA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_ALERTA);
            db.execSQL("DROP TABLE IF EXISTS " + Util.SQLITE_TABLE_TARJETA);


            onCreate(db);
        }

    }




}


