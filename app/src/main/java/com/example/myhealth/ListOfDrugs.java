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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter; // Dodano import dla ArrayAdapter
import android.widget.DatePicker;
import android.widget.Spinner; // Dodano import dla Spinner
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    Spinner spinnerSearchCondition; // Dodano Spinner do wyszukiwania według schorzenia
    DataBaseSQLiteInterface myDB;
    ArrayList<String> drug_id, drug_name, drug_amount, drug_expiration_date;
    CustomAdapter customAdapter;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_drugs);

        // Sprawdzanie uprawnień do wysyłania powiadomień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Tworzenie kanału powiadomień
        createNotificationChannel();

        recyclerView = findViewById(R.id.listOfDrugs);
        floatingActionButton = findViewById(R.id.floatingAddButton);
        spinnerSearchCondition = findViewById(R.id.spinner_search_condition); // Inicjalizacja Spinnera

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

        // Ustawienie adaptera dla Spinnera
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.condition_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchCondition.setAdapter(adapter);
    }

    private void createNotificationChannel() {
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

    private void storeDataInArrays() {
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

    // Obsługa wyniku prośby o uprawnienia
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Uprawnienia do wysyłania powiadomień przyznane", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Uprawnienia do wysyłania powiadomień odrzucone", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    public void setNotification(View view) {
        ConstraintLayout parent = (ConstraintLayout) view.getParent();
        TextView drugIdTextView = parent.findViewById(R.id.drug_id_txt);
        TextView drugNameTextView = parent.findViewById(R.id.drug_name_txt);
        String drugId = drugIdTextView.getText().toString();
        String drugName = drugNameTextView.getText().toString();

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

                    Log.d("ListOfDrugs", "Ustawianie alarmu na: " + calendar.getTime().toString());

                    Intent intent = new Intent(this, NotificationReceiver.class);
                    intent.putExtra("drug_id", drugId);
                    intent.putExtra("drug_name", drugName);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("ListOfDrugs", "Alarm został ustawiony");
                    } else {
                        Log.e("ListOfDrugs", "AlarmManager jest null");
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

    public void searchByCondition(View view) {
        String condition = spinnerSearchCondition.getSelectedItem().toString();
        Cursor cursor = myDB.getDrugsByCondition(condition);

        drug_id.clear();
        drug_name.clear();
        drug_amount.clear();
        drug_expiration_date.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                drug_id.add(cursor.getString(0));
                drug_name.add(cursor.getString(1));
                drug_amount.add(cursor.getString(2));
                drug_expiration_date.add(cursor.getString(3));
            }
        }

        customAdapter.notifyDataSetChanged();
    }
}
