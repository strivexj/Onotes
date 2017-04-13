package com.example.onotes.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cwj Apr.12.2017 1:19 PM
 */

public class NotesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";
    private static final String SQL_CREATE_ENTRIES =
            "create table Notes (" + "id integer primary key autoincrement,"
                    + "cityid text," + "cityEn text,"+ "cityZh text," +"lat text," + "lon text,"
                    + "data text," + "time text," + "title text," + "picture text,"+"textsize real,"
                    +"linespace real,"+ "content text," + "location text)";

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
