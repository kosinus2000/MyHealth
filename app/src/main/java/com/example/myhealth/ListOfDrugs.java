package com.example.myhealth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListOfDrugs extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    DataBaseSQLiteInterface myDB;
    ArrayList<String> drug_id, drug_name, drug_amount, drug_expiration_date;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_drugs);

        recyclerView = findViewById(R.id.listOfDrugs);
        floatingActionButton = findViewById(R.id.floatingAddButton);

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(ListOfDrugs.this, AddDrugActivity.class);
            startActivity(intent);
        });

        myDB = new DataBaseSQLiteInterface(ListOfDrugs.this);
        drug_id = new ArrayList<>();
        drug_name = new ArrayList<>();
        drug_amount = new ArrayList<>();
        drug_expiration_date = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(ListOfDrugs.this, drug_id, drug_name, drug_amount, drug_expiration_date);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Ensuring layout manager is set
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                drug_id.add(cursor.getString(0));
                drug_name.add(cursor.getString(1));
                drug_amount.add(cursor.getString(2));
                drug_expiration_date.add(cursor.getString(3));
            }
        }
    }
}
