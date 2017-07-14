package com.saran.tictoesampleapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by saran on 13/7/17.
 */

public class ResultDataBase extends SQLiteOpenHelper {

    private static String DB_NAME = "TicToe.db";
    private static final String KEY_ID = "id";
    private static final String KEY_RESULT = "result";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE__TABLE_NAME = "myresulttable";



    public ResultDataBase(Context context) {
        super(context,DB_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_RESULT_TABLE = "CREATE TABLE " + DATABASE__TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RESULT + " TEXT"
                + ");";
        db.execSQL(CREATE_RESULT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String player) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RESULT,player);
        sqLiteDatabase.insert(DATABASE__TABLE_NAME,null,contentValues);
        sqLiteDatabase.close();
    }
}
