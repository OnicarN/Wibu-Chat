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

public class MensajeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_ENVIADO   = 1;
    private static final int TIPO_RECIBIDO  = 2;

    private List<Mensaje> mensajes;
    private String miUid;

    public MensajeAdapter(List<Mensaje> mensajes, String miUid) {
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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_ENVIADO) {
            View v = inflater.inflate(R.layout.item_mensaje_enviado, parent, false);
            return new MensajeViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_mensaje_recibido, parent, false);
            return new MensajeViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje m = mensajes.get(position);
        MensajeViewHolder h = (MensajeViewHolder) holder;

        h.txtTexto.setText(m.getTexto());
        h.txtHora.setText(formatHora(m.getTimestamp()));

        // Tick de leído solo en mensajes enviados
        if (h.txtLeido != null) {
            if (m.isLeido()) {
                h.txtLeido.setText("✓✓");
                h.txtLeido.setTextColor(0xFF00BCD4); // cyan
            } else {
                h.txtLeido.setText("✓");
                h.txtLeido.setTextColor(0xFFAAAAAA);
            }
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    private String formatHora(long timestamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTexto, txtHora, txtLeido;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTexto = itemView.findViewById(R.id.txtMensaje);
            txtHora  = itemView.findViewById(R.id.txtHora);
            txtLeido = itemView.findViewById(R.id.txtLeido); // puede ser null en recibidos
        }
    }
}