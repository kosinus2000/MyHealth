package com.example.myhealth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;

public class DataBaseSQLiteInterface extends SQLiteOpenHelper {
    AddDrug lek = new AddDrug();
    private Context context;
    private static final String DATABASE_NAME = "MyHealth.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "drug_list";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_EXPIRATION_DATE = "expiration_date";
    private static final String COLUMN_PHOTO = "photo";


    public DataBaseSQLiteInterface(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_EXPIRATION_DATE + " TEXT, " +
                COLUMN_PHOTO + " BLOB);";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void addDrug(AddDrug lek) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, lek.getName());
        values.put(COLUMN_AMOUNT, lek.getAmount());
        values.put(COLUMN_EXPIRATION_DATE, lek.getFormattedDate());

        
        if (lek.getPhoto() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            lek.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(COLUMN_PHOTO, byteArray);
        }
        
        long result  = db.insert(TABLE_NAME, null, values);
        if (result == -1){
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Succes", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

}
