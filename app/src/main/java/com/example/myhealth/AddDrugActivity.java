package com.example.myhealth;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    private LocalDate obecnaData = LocalDate.now();
    private int obecnyDzien = obecnaData.getDayOfMonth();
    private int obecnyMiesiac = obecnaData.getMonthValue();
    private int obecnyRok = obecnaData.getYear();
    private String data;
    private ImageView imageView, gallery, camera;
    private CheckBox harmonogramCheckbox;
    private LinearLayout harmonogramForm;

    //--------------------------------------------------




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drug);

        button = findViewById(R.id.dodaj);
        nazwaLeku = findViewById(R.id.textInputEditTextNazwa);
        edycjaDaty = findViewById(R.id.textInputEditTextData);
        imageView = findViewById(R.id.imageView);
        camera = findViewById(R.id.imageButtonCamera);
        gallery = findViewById(R.id.imageButtonFile);
        harmonogramCheckbox = findViewById(R.id.harmonogram_checkbox);
        harmonogramForm = findViewById(R.id.harmonogram_form);
        button.setEnabled(false);

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

        harmonogramCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                harmonogramForm.setVisibility(View.VISIBLE);
            } else {
                harmonogramForm.setVisibility(View.GONE);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Dexter.withContext(getApplicationContext())
                            .withPermission(Manifest.permission.READ_MEDIA_IMAGES) // Zmienione na READ_MEDIA_IMAGES
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    OtwarcieGalerii();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(getApplicationContext(), "Uprawnienie do odczytu zdjęć zostało odrzucone", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                } else {
                    Dexter.withContext(getApplicationContext())
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE) // Starsze uprawnienie dla Androida < 13
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    OtwarcieGalerii();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(getApplicationContext(), "Uprawnienie do pamięci zostało odrzucone", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        ObslugaSpinera();
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


    private void OtwarcieGalerii() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), 1);
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

    public void OpenDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            String selectedDate = String.format("%02d.%02d.%04d", day, month + 1, year);
            edycjaDaty.setText(selectedDate);
            data = selectedDate;

            Toast.makeText(this, "data--->" + data, Toast.LENGTH_LONG).show();
        }, obecnyRok, obecnyMiesiac, obecnyDzien);
        datePickerDialog.show();
    }

    public void OpenTimeDialog(TextInputEditText editText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                editText.setText(selectedTime);
            }
        }, 12, 0, true);
        timePickerDialog.show();


        ObslugaSpinera();  //wywołanie spinera
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
        SetDate();
        AddFoto();
        DataBaseSQLiteInterface dbHelper = new DataBaseSQLiteInterface(this);
        dbHelper.addDrug(lek);
        nazwaLeku.setText("");
        EditText textIlosc = findViewById(R.id.textInputEditTextIlosc);
        textIlosc.setText("");
        edycjaDaty.setText("");
        button.setEnabled(false);
        ResetujZdjecie();

    }

    public void ResetujZdjecie() {
        imageView.setImageResource(0);
    }

    // NALEZY DOSTOSOWAC METODE DO POTRZEB DODAWANIA GODZINY ORAZ DAWEK Z UWZGLEDNIENIEM NOWYCH
    // ZMIENNYCH ORAZ DNI TYGODNIA
    public void ObslugaSpinera() {
        Spinner spinnerDawki = findViewById(R.id.spinnerDawki);
        LinearLayout containerPolaGodzin = findViewById(R.id.containerPolaGodzin);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.dawki_array, // Zasób z elementami spinnera
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDawki.setAdapter(adapter);

        spinnerDawki.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int liczbaDawek = position + 1;
                dodajPolaGodzin(containerPolaGodzin, liczbaDawek);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nic nie rób
            }
        });
    }

    private void dodajPolaGodzin(LinearLayout container, int liczbaDawek) {
        // Usuń wszystkie poprzednie pola
        container.removeAllViews();

        for (int i = 0; i < liczbaDawek; i++) {
            // Tworzenie nowego pola godziny
            TextInputLayout textInputLayout = new TextInputLayout(this);
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textInputLayout.setHint("Godzina dawki " + (i + 1));

            TextInputEditText textInputEditText = new TextInputEditText(this);
            textInputEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textInputEditText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);

            textInputLayout.addView(textInputEditText);
            container.addView(textInputLayout);

            textInputEditText.setOnClickListener(v -> OpenTimeDialog(textInputEditText));
        }
    }

}
