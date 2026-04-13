package com.example.wibuchat100;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {

    private List<Grupo> listaGrupos;

    public GrupoAdapter(List<Grupo> listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        Grupo grupo = listaGrupos.get(position);
        holder.txtNombre.setText(grupo.getNombre());
        holder.txtMiembros.setText(
                grupo.getParticipantes() != null
                        ? grupo.getParticipantes().size() + " miembros"
                        : "0 miembros");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatGrupalActivity.class);
            intent.putExtra("grupoId", grupo.getKey());
            intent.putExtra("grupoNombre", grupo.getNombre());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return listaGrupos.size(); }

    static class GrupoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtMiembros;

        GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre   = itemView.findViewById(R.id.txtNombreGrupo);
            txtMiembros = itemView.findViewById(R.id.txtMiembrosGrupo);
        }
    }
}