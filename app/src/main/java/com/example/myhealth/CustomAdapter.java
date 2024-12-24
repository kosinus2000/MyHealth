package com.example.myhealth;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> drug_id, drug_name, drug_amount, drug_expiration_date;

    CustomAdapter(Context context, ArrayList<String> drug_id, ArrayList<String> drug_name,
                  ArrayList<String> drug_amount, ArrayList<String> drug_expiration_date) {
        this.context = context;
        this.drug_id = drug_id;
        this.drug_name = drug_name;
        this.drug_amount = drug_amount;
        this.drug_expiration_date = drug_expiration_date;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.drug_id_txt.setText(drug_id.get(position));
        holder.drug_name_txt.setText(drug_name.get(position));
        holder.drug_expiration_date_txt.setText(drug_expiration_date.get(position));
        holder.drug_amount_txt.setText(drug_amount.get(position));
    }

    @Override
    public int getItemCount() {
        return drug_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView drug_id_txt, drug_name_txt, drug_expiration_date_txt, drug_amount_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            drug_id_txt = itemView.findViewById(R.id.drug_id_txt);
            drug_name_txt = itemView.findViewById(R.id.drug_name_txt);
            drug_expiration_date_txt = itemView.findViewById(R.id.drug_expiration_date_txt);
            drug_amount_txt = itemView.findViewById(R.id.drug_amount_txt);
        }
    }
}
