package com.example.wibuchat100;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MensajeGrupalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_ENVIADO  = 1;
    private static final int TIPO_RECIBIDO = 2;

    private List<Mensaje> mensajes;
    private String miUid;

    public MensajeGrupalAdapter(List<Mensaje> mensajes, String miUid) {
        this.mensajes = mensajes;
        this.miUid    = miUid;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).getEmisorUid().equals(miUid)
                ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_ENVIADO) {
            return new MsgHolder(inf.inflate(R.layout.item_mensaje_enviado, parent, false));
        } else {
            return new MsgHolder(inf.inflate(R.layout.item_mensaje_recibido_grupal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje m  = mensajes.get(position);
        MsgHolder h = (MsgHolder) holder;

        h.txtTexto.setText(m.getTexto());
        h.txtHora.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(m.getTimestamp())));

        // Nombre del emisor (solo en recibidos)
        if (h.txtNombreEmisor != null) {
            h.txtNombreEmisor.setText(m.getEmisorNombre() != null ? m.getEmisorNombre() : "");
        }
        // Tick leído (solo en enviados)
        if (h.txtLeido != null) {
            h.txtLeido.setText("✓");
            h.txtLeido.setTextColor(0xFFAAAAAA);
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class MsgHolder extends RecyclerView.ViewHolder {
        TextView txtTexto, txtHora, txtLeido, txtNombreEmisor;

        MsgHolder(@NonNull View itemView) {
            super(itemView);
            txtTexto       = itemView.findViewById(R.id.txtMensaje);
            txtHora        = itemView.findViewById(R.id.txtHora);
            txtLeido       = itemView.findViewById(R.id.txtLeido);         // null en recibidos
            txtNombreEmisor = itemView.findViewById(R.id.txtNombreEmisor); // null en enviados
        }
    }
}