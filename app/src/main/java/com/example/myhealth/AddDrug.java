package com.example.myhealth;

import android.graphics.Bitmap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddDrug {
    private static int idCounter = 0;
    private int id;
    private String name;
    private int amount;
    private LocalDate expirationDate;
    private DateTimeFormatter formatka = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private Bitmap photo;

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    private int dose;

    public AddDrug() {
        this.id = ++idCounter;
        this.name = name;
        this.amount = amount;
        setDate(String.valueOf(expirationDate));
        this.photo = photo;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setDate(String date) {
        try {
            this.expirationDate = LocalDate.parse(date, formatka);
        } catch (DateTimeParseException e) {
            System.out.println("Błędny format daty. Proszę podać datę w formacie dd.MM.yyyy");
        }
    }

    public String getFormattedDate() {
        return expirationDate != null ? expirationDate.format(formatka) : "Brak daty";
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
