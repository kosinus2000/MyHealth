package com.example.myhealth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
        drugDoseText = findViewById(R.id.drug_dose);
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
                if (drugAmount < 0) drugAmount = 0;
                myDB.updateDrugAmount(drugId, drugAmount);
                drugAmountText.setText("Ilość leku: " + drugAmount);
                Toast.makeText(this, "Lek przyjęty", Toast.LENGTH_SHORT).show();
                if (drugAmount <= 5) {
                    showLowStockNotification(drugName);
                }
                launchHome(view);
            } else {
                Toast.makeText(this, "Brak leku", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLowStockNotification(String drugName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "low_stock_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Niski poziom leku")
                .setContentText("Ilość leku " + drugName + " jest niska. Zakup nowe opakowanie.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(2, builder.build());
    }

    public void launchHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
