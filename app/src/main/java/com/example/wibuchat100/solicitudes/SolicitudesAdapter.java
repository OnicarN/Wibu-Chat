package com.example.wibuchat100.solicitudes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;

import java.util.List;

public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.SolicitudHolder> {

    public interface OnSolicitudListener {
        void onAceptar(SolicitudItem solicitud);
        void onRechazar(SolicitudItem solicitud);
    }

    private List<SolicitudItem> lista;
    private OnSolicitudListener listener;

    public SolicitudesAdapter(List<SolicitudItem> lista, OnSolicitudListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new SolicitudHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudHolder holder, int position) {
        SolicitudItem s = lista.get(position);
        holder.txtNombre.setText(s.getEmisorNombre());
        holder.txtEmail.setText(s.getEmisorEmail());

        holder.btnAceptar.setOnClickListener(v -> listener.onAceptar(s));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(s));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class SolicitudHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtEmail;
        Button btnAceptar, btnRechazar;

        SolicitudHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre  = itemView.findViewById(R.id.txtNombreSolicitud);
            txtEmail   = itemView.findViewById(R.id.txtEmailSolicitud);
            btnAceptar = itemView.findViewById(R.id.btnAceptarSolicitud);
            btnRechazar = itemView.findViewById(R.id.btnRechazarSolicitud);
        }
    }
}