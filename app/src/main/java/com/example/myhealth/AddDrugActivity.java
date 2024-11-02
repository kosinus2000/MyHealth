package com.example.myhealth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drug);

        button = findViewById(R.id.dodaj);
        nazwaLeku = findViewById(R.id.textInputEditTextNazwa);
        edycjaDaty = findViewById(R.id.textInputEditTextData);

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

    public void SetDate() {
        lek.setDate(data);
    }

    public void AddFoto(View v){
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void DodajLek(View v) {
        lek = new AddDrug();
        AddNameOfDrug();
        AddAmountOfDrug();
        SetDate();
        Toast.makeText(this, "Dodano lek: " + lek.getName() + ", Ilość: " + lek.getAmount() + ", Data ważności: " + lek.getFormattedDate(), Toast.LENGTH_LONG).show();

        nazwaLeku.setText("");
        EditText textIlosc = findViewById(R.id.textInputEditTextIlosc);
        textIlosc.setText("");
        edycjaDaty.setText("");
        button.setEnabled(false);
    }
}