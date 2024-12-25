package com.example.myhealth;

import android.content.Context;
import android.database.Cursor;
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
    private static final String COLUMN_DOSE = "dose";

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
                COLUMN_PHOTO + " BLOB," +
                COLUMN_DOSE + " INTEGER);";

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
        values.put(COLUMN_DOSE, lek.getDose());

        if (lek.getPhoto() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            lek.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(COLUMN_PHOTO, byteArray);
        }

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show();
        }


    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public int getDrugAmount(String drugId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_AMOUNT + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{drugId});
        int amount = 0;
        if (cursor.moveToFirst()) {
            amount = cursor.getInt(0);
        }
        cursor.close();
        return amount;
    }

    public int getDrugDose(String drugId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DOSE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{drugId});
        int dose = 0;
        if (cursor.moveToFirst()) {
            dose = cursor.getInt(0);
        }
        cursor.close();
        return dose;
    }

    public void updateDrugAmount(String drugId, int newAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, newAmount);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{drugId});
    }
}
