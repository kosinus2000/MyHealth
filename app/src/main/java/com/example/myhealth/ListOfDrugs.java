package com.example.myhealth;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class ListOfDrugs extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    DataBaseSQLiteInterface myDB;
    ArrayList<String> drug_id, drug_name, drug_amount, drug_expiration_date;
    CustomAdapter customAdapter;

    private static final String TAG = "ListOfDrugs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_drugs);

        // Sprawdzanie wersji Androida i uprawnień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tworzenie kanału powiadomień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "drug_channel";
            String description = "Channel for drug notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("drug_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Dla starszych wersji Androida zakładamy, że możemy ustawiać dokładne alarmy
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

    public void setNotification(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms()) {
            Toast.makeText(this, "Uprawnienia do dokładnych alarmów nie zostały przyznane", Toast.LENGTH_SHORT).show();
            return;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view1, int year1, int month1, int dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view2, int hourOfDay, int minute1) -> {
                try {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute1);
                    calendar.set(Calendar.SECOND, 0);

                    Log.d(TAG, "Ustawianie alarmu na: " + calendar.getTime().toString());

                    Intent intent = new Intent(this, NotificationReceiver.class);
                    String drugId = "1"; // Przykład, przekaż właściwe drug_id
                    String drugName = "Przykład"; // Przykład, przekaż właściwe drug_name
                    intent.putExtra("drug_id", drugId);
                    intent.putExtra("drug_name", drugName);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d(TAG, "Alarm został ustawiony");
                    } else {
                        Log.e(TAG, "AlarmManager jest null");
                    }

                    Toast.makeText(this, "Powiadomienie ustawione na: " + dayOfMonth + "/" + (month1 + 1) + "/" + year1 + " " + hourOfDay + ":" + minute1, Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }
}
