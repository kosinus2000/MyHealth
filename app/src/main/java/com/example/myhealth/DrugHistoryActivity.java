package com.example.myhealth;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DrugHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DrugHistoryAdapter drugHistoryAdapter;
    private DataBaseSQLiteInterface myDB;
    private ArrayList<String> drug_id, drug_name, drug_amount, drug_date, drug_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_history);

        recyclerView = findViewById(R.id.recyclerViewDrugHistory);
        myDB = new DataBaseSQLiteInterface(this);
        drug_id = new ArrayList<>();
        drug_name = new ArrayList<>();
        drug_amount = new ArrayList<>();
        drug_date = new ArrayList<>();
        drug_condition = new ArrayList<>();

        storeDataInArrays();

        drugHistoryAdapter = new DrugHistoryAdapter(this, drug_id, drug_name, drug_amount, drug_date, drug_condition);
        recyclerView.setAdapter(drugHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                drug_id.add(cursor.getString(0));
                drug_name.add(cursor.getString(1));
                drug_amount.add(cursor.getString(2));
                drug_date.add(cursor.getString(3));
                drug_condition.add(cursor.getString(4));
            }
        }
    }
}
