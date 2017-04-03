package com.example.onotes.datebase;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by cwj Apr.02.2017 9:14 PM
 */

public class CityDbHelper extends SQLiteOpenHelper {
    /**
     * id : CN101010100
     * cityEn : beijing
     * cityZh : 北京
     * provinceEn : beijing
     * provinceZh : 北京
     * leaderEn : beijing
     * leaderZh : 北京
     * lat : 39.904989
     * lon : 116.405285
     */

    private static final String SQL_CREATE_ENTRIES =
                    "create table City (" + "id integer primary key autoincrement,"
                    + "cityid text," + "cityEn text," +
                    "cityZh text,"+ "provinceEn text,"+
                            "provinceZh text,"+"leaderEn text,"
            +"leaderZh text,"+"lat real,"+"lon real)";
    private Context mContext;
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + "City";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "City.db";

    public CityDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CityDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Toast.makeText(mContext, "Created succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
