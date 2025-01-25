package com.example.myhealth;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;

public class AddDrugActivity extends AppCompatActivity {

    private AddDrug lek;
    private Button button;
    private EditText nazwaLeku;
    private EditText edycjaDaty;
    private EditText dawka;
    private LocalDate obecnaData = LocalDate.now();
    private int obecnyDzien = obecnaData.getDayOfMonth();
    private int obecnyMiesiac = obecnaData.getMonthValue();
    private int obecnyRok = obecnaData.getYear();
    private String data;

    private ImageView imageView, gallery, camera;
    private Spinner spinnerCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drug);

        button = findViewById(R.id.dodaj);
        nazwaLeku = findViewById(R.id.textInputEditTextNazwa);
        edycjaDaty = findViewById(R.id.textInputEditTextData);
        dawka = findViewById(R.id.textInputEditTextDawka);
        imageView = findViewById(R.id.imageView);
        camera = findViewById(R.id.imageButton);
        gallery = findViewById(R.id.imageButtonFromFile);
        spinnerCondition = findViewById(R.id.spinner_condition);

        button.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.condition_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(adapter);

        nazwaLeku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                button.setEnabled(charSequence.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edycjaDaty.setOnClickListener(view -> OpenDateDialog());

        gallery.setOnClickListener(view -> {
            String permission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            } else {
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            }

            Dexter.withContext(getApplicationContext())
                    .withPermission(permission)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), 1);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getApplicationContext(), "Uprawnienie do odczytu mediów zostało odrzucone", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        });

        camera.setOnClickListener(view -> {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 2);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getApplicationContext(), "Uprawnienie do aparatu zostało odrzucone", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void AddNameOfDrug() {
        if (lek != null) {
            String textValue = nazwaLeku.getText().toString();
            lek.setName(textValue);
        }
    }

    public void AddAmountOfDrug() {
        EditText textIlosc = findViewById(R.id.textInputEditTextIlosc);
        String textValue = textIlosc.getText().toString();
        try {
            int amount = Integer.parseInt(textValue);
            if (lek != null) {
                lek.setAmount(amount);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Proszę wprowadzić poprawną liczbę", Toast.LENGTH_SHORT).show();
        }
    }

    public void AddDose() {
        String textValue = dawka.getText().toString();
        try {
            int dose = Integer.parseInt(textValue);
            if (lek != null) {
                lek.setDose(dose);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Proszę wprowadzić poprawną liczbę dla dawki", Toast.LENGTH_SHORT).show();
        }
    }

    public void OpenDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            String selectedDate = String.format("%02d.%02d.%04d", day, month + 1, year);
            edycjaDaty.setText(selectedDate);
            data = selectedDate;

            Toast.makeText(this, "data--->" + data, Toast.LENGTH_LONG).show();
        }, obecnyRok, obecnyMiesiac, obecnyDzien);
        datePickerDialog.show();
    }

    public void SetDate() {
        lek.setDate(data);
    }

    private void AddFoto() {
        lek.setPhoto(imageView.getDrawingCache());
    }

    public void DodajLek(View v) {
        lek = new AddDrug();
        AddNameOfDrug();
        AddAmountOfDrug();
        AddDose();
        SetDate();
        AddFoto();

        // Pobierz wybrane schorzenie ze Spinnera
        String condition = spinnerCondition.getSelectedItem().toString();
        lek.setCondition(condition);

        DataBaseSQLiteInterface dbHelper = new DataBaseSQLiteInterface(this);
        dbHelper.addDrug(lek);

        nazwaLeku.setText("");
        EditText textIlosc = findViewById(R.id.textInputEditTextIlosc);
        textIlosc.setText("");
        edycjaDaty.setText("");
        dawka.setText(""); // Resetowanie pola dawki
        button.setEnabled(false);
        ResetujZdjecie();
    }

    public void ResetujZdjecie() {
        imageView.setImageResource(0);
    }
}
