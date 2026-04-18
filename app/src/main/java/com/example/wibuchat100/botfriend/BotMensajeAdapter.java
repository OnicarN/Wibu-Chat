package com.example.wibuchat100.botfriend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;

import java.util.List;

public class BotMensajeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_ENVIADO  = 1;
    private static final int TIPO_RECIBIDO = 2;

    private List<MensajeBot> mensajes;

    public BotMensajeAdapter(List<MensajeBot> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsHumano() ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = viewType == TIPO_ENVIADO
                ? inf.inflate(R.layout.item_mensaje_enviado, parent, false)
                : inf.inflate(R.layout.item_mensaje_recibido, parent, false);
        return new BotHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BotHolder h = (BotHolder) holder;
        h.txtTexto.setText(mensajes.get(position).getMensaje());
        if (h.txtHora != null) h.txtHora.setText("");
        if (h.txtLeido != null) h.txtLeido.setText("");
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class BotHolder extends RecyclerView.ViewHolder {
        TextView txtTexto, txtHora, txtLeido;

        BotHolder(@NonNull View itemView) {
            super(itemView);
            txtTexto = itemView.findViewById(R.id.txtMensaje);
            txtHora  = itemView.findViewById(R.id.txtHora);
            txtLeido = itemView.findViewById(R.id.txtLeido);
        }
    }
}