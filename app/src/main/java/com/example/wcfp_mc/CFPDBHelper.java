package com.example.wcfp_mc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CFPDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Class";
    private static final int DATABASE_VERSION = 2;
    public CFPDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id integer  primary key autoincrement,event text,name text, url text,time text, deadline text) ");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS history");
        onCreate(db);
    }
}
