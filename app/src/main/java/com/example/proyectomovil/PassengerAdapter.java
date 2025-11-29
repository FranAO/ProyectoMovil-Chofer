package com.example.proyectomovil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.Student;
import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {
    private List<Student> pasajeros;

    public PassengerAdapter(List<Student> pasajeros) {
        this.pasajeros = pasajeros;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        Student pasajero = pasajeros.get(position);
        holder.tvPassangerName.setText(pasajero.getFirstName() + " " + pasajero.getLastName());
    }

    @Override
    public int getItemCount() {
        return pasajeros.size();
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {
        TextView tvPassangerName;

        PassengerViewHolder(View itemView) {
            super(itemView);
            tvPassangerName = itemView.findViewById(R.id.tvPassangerName);
        }
    }
}
