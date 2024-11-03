package com.example.myhealth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseSQLiteInterface extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "MyHealth.db";
    public static final int DATABASE_VERSION = 1;

    public DataBaseSQLiteInterface(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
