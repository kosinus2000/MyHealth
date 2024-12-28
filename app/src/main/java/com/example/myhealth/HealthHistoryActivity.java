package com.example.myhealth;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HealthHistoryActivity extends AppCompatActivity {

    private EditText editTextBloodPressure, editTextBloodSugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        editTextBloodPressure = findViewById(R.id.editTextBloodPressure);
        editTextBloodSugar = findViewById(R.id.editTextBloodSugar);
    }

    public void generateReport(View view) {
        String bloodPressure = editTextBloodPressure.getText().toString();
        String bloodSugar = editTextBloodSugar.getText().toString();

        if (bloodPressure.isEmpty() || bloodSugar.isEmpty()) {
            Toast.makeText(this, "Wprowadź wszystkie wyniki badań", Toast.LENGTH_SHORT).show();
        } else {
            String report = "Raport stanu zdrowia:\n" +
                    "Ciśnienie krwi: " + bloodPressure + "\n" +
                    "Poziom cukru: " + bloodSugar;

            // Wyświetlanie raportu (tu można dodać bardziej zaawansowane funkcje, np. zapis do bazy danych)
            Toast.makeText(this, report, Toast.LENGTH_LONG).show();
        }
    }
}
