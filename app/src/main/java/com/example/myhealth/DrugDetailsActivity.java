package com.example.myhealth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DrugDetailsActivity extends AppCompatActivity {

    TextView drugNameText, drugAmountText;
    Button btnTakeDrug;
    SharedPreferences sharedPreferences;
    String drugId;
    int drugAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_details);

        drugNameText = findViewById(R.id.drug_name);
        drugAmountText = findViewById(R.id.drug_amount);
        btnTakeDrug = findViewById(R.id.btn_take_drug);
        sharedPreferences = getSharedPreferences("DrugPrefs", MODE_PRIVATE);

        // Pobierz drugId i drugName z Intent
        drugId = getIntent().getStringExtra("drug_id");
        String drugName = getIntent().getStringExtra("drug_name");

        // Pobierz ilość leku z SharedPreferences
        drugAmount = sharedPreferences.getInt(drugId + "_amount", 0);

        drugNameText.setText(drugName);
        drugAmountText.setText("Ilość leku: " + drugAmount);

        btnTakeDrug.setOnClickListener(view -> {
            if (drugAmount > 0) {
                drugAmount--;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(drugId + "_amount", drugAmount);
                editor.apply();
                drugAmountText.setText("Ilość leku: " + drugAmount);
                Toast.makeText(this, "Lek przyjęty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brak leku", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
