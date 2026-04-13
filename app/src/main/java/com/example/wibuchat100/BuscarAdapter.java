package com.example.wibuchat100;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BuscarAdapter extends RecyclerView.Adapter<BuscarAdapter.BuscarViewHolder> {

    private List<Contacto> listaContactos;

    public BuscarAdapter(List<Contacto> listaContactos) {
        this.listaContactos = listaContactos;
    }

    @NonNull
    @Override
    public BuscarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new BuscarViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull BuscarViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);
        holder.nombre.setText(contacto.getNombre());
        holder.email.setText(contacto.getEmail());

        // ← Abre ContactoIndividual para poder enviar solicitud
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContactoIndividual.class);
            intent.putExtra("username", contacto.getNombre());
            intent.putExtra("mail", contacto.getEmail());
            intent.putExtra("uid", contacto.getUid());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return listaContactos.size(); }

    static class BuscarViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, email;

        BuscarViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_contacto);
            email  = itemView.findViewById(R.id.email_contacto);
        }
    }
}