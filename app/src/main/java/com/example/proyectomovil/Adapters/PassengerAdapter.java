package com.example.proyectomovil.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.Passenger;
import com.example.proyectomovil.R;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {
    private List<Passenger> pasajeros;
    private OnScanQRClickListener scanQRListener;

    public interface OnScanQRClickListener {
        void onScanQR(Passenger passenger);
    }

    public PassengerAdapter(List<Passenger> pasajeros, OnScanQRClickListener listener) {
        this.pasajeros = pasajeros;
        this.scanQRListener = listener;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        Passenger pasajero = pasajeros.get(position);
        holder.tvPassangerName.setText(pasajero.getName());
        
        String status = pasajero.getTicketStatus();
        
        holder.ivConfirmIcon.setVisibility(View.GONE);
        holder.ivCancelIcon.setVisibility(View.GONE);
        
        if ("confirmed".equals(status)) {
            holder.ivConfirmIcon.setVisibility(View.VISIBLE);
            holder.mapIconContainer.setCardBackgroundColor(0xFF4CAF50);
        } else if ("cancelled".equals(status)) {
            holder.ivCancelIcon.setVisibility(View.VISIBLE);
            holder.mapIconContainer.setCardBackgroundColor(0xFFE53935);
        } else {
            holder.mapIconContainer.setCardBackgroundColor(0xFF9CADBA);
        }
        
        holder.mapIconContainer.setOnClickListener(v -> {
            if ("pending".equals(status) && scanQRListener != null) {
                scanQRListener.onScanQR(pasajero);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pasajeros.size();
    }

    public void updateTicketStatus(String passengerId, String newStatus) {
        for (int i = 0; i < pasajeros.size(); i++) {
            if (pasajeros.get(i).getId().equals(passengerId)) {
                pasajeros.get(i).setTicketStatus(newStatus);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {
        TextView tvPassangerName;
        CardView mapIconContainer;
        ImageView ivConfirmIcon;
        ImageView ivCancelIcon;

        PassengerViewHolder(View itemView) {
            super(itemView);
            tvPassangerName = itemView.findViewById(R.id.tvPassangerName);
            mapIconContainer = itemView.findViewById(R.id.mapIconContainer);
            ivConfirmIcon = itemView.findViewById(R.id.ivConfirmIcon);
            ivCancelIcon = itemView.findViewById(R.id.ivCancelIcon);
        }
    }
}
