package com.example.myhealth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DrugDetailsActivity extends AppCompatActivity {

    TextView drugNameText, drugAmountText, drugDoseText;
    Button btnTakeDrug;
    DataBaseSQLiteInterface myDB;
    String drugId;
    int drugAmount;
    int drugDose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_details);

        drugNameText = findViewById(R.id.drug_name);
        drugAmountText = findViewById(R.id.drug_amount);
        drugDoseText = findViewById(R.id.drug_dose); // Dodaj ten element w swoim layoucie
        btnTakeDrug = findViewById(R.id.btn_take_drug);
        myDB = new DataBaseSQLiteInterface(this);


        drugId = getIntent().getStringExtra("drug_id");
        String drugName = getIntent().getStringExtra("drug_name");


        drugAmount = myDB.getDrugAmount(drugId);
        drugDose = myDB.getDrugDose(drugId);

        drugNameText.setText(drugName);
        drugAmountText.setText("Ilość leku: " + drugAmount);
        drugDoseText.setText("Dawka leku: " + drugDose);

        btnTakeDrug.setOnClickListener(view -> {
            if (drugAmount > 0) {
                drugAmount -= drugDose;
                if (drugAmount < 0) drugAmount = 0; // Upewnij się, że ilość nie jest mniejsza niż 0
                myDB.updateDrugAmount(drugId, drugAmount);
                drugAmountText.setText("Ilość leku: " + drugAmount);
                Toast.makeText(this, "Lek przyjęty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brak leku", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
