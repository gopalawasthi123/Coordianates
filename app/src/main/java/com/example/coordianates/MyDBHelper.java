package com.example.coordianates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    private static MyDBHelper helper;
    public static final String MYDATABASE = "mydb";
    public static final String TABLE_NAME = "mytable";
    public static final String COORDIANATES = "cord";
    public static final String ITEM_ID = "itemid";
    public static final String TIME_STAMP = "timestmp";


    public MyDBHelper(@Nullable Context context) {
        super(context, MYDATABASE, null, 1);
    }

    public static MyDBHelper getInstance(Context context){
        if(helper == null){
            helper = new MyDBHelper(context);
        }
        return  helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqluse = "CREATE TABLE " + TABLE_NAME + " ( " +
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TIME_STAMP + " TEXT, " +
                COORDIANATES + " TEXT )";

        db.execSQL(sqluse);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
