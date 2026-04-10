package com.example.wibuchat100;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private List<Contacto> listaContactos;

    // Constructor: recibe la lista de contactos
    public ContactoAdapter(List<Contacto> listaContactos) {
        this.listaContactos = listaContactos;
    }

    // 1. Infla (crea) la tarjeta visual para cada item
    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(vista);
    }

    // 2. Rellena cada tarjeta con los datos del contacto correspondiente
    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = listaContactos.get(position);
        holder.nombre.setText(contacto.getNombre());
        holder.email.setText(contacto.getEmail());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ContactoIndividual.class);
            intent.putExtra("username",contacto.getNombre());
            intent.putExtra("mail",contacto.getEmail());
            intent.putExtra("uid", contacto.getUid());
            v.getContext().startActivity(intent);
        });
    }

    // 3. Cuántos items hay en total
    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    // ViewHolder: guarda las referencias a los TextView de la tarjeta
    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, email;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_contacto);
            email  = itemView.findViewById(R.id.email_contacto);
        }
    }
}