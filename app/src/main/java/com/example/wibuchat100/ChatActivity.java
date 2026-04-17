package com.example.wibuchat100;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerMensajes;
    EditText inputMensaje;
    ImageButton btnEnviar;
    TextView txtNombreChat;

    String miUid, otroUid, otroNombre, chatId;
    DatabaseReference dbChat;
    List<Mensaje> listaMensajes = new ArrayList<>();
    MensajeAdapter mensajeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        miUid      = FirebaseAuth.getInstance().getUid();
        otroUid    = getIntent().getStringExtra("uid");
        otroNombre = getIntent().getStringExtra("username");

        // ChatId único: ordenamos los UIDs para que siempre sea el mismo
        chatId = miUid.compareTo(otroUid) < 0
                ? miUid + "_" + otroUid
                : otroUid + "_" + miUid;

        dbChat = FirebaseDatabase.getInstance()
                .getReference("chats").child(chatId).child("messages");

        txtNombreChat    = findViewById(R.id.txtNombreChat);
        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        inputMensaje     = findViewById(R.id.inputMensaje);
        btnEnviar        = findViewById(R.id.btnEnviar);

        txtNombreChat.setText(otroNombre);

        mensajeAdapter = new MensajeAdapter(listaMensajes, miUid);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(mensajeAdapter);

        escucharMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void escucharMensajes() {
        dbChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Mensaje m = hijo.getValue(Mensaje.class);
                    if (m != null) {
                        m.setKey(hijo.getKey());

                        // ← DESCIFRAR antes de mostrar en pantalla
                        m.setTexto(CifradoHelper.descifrar(m.getTexto(), chatId));

                        listaMensajes.add(m);

                        if (!m.getEmisorUid().equals(miUid) && !m.isLeido()) {
                            dbChat.child(hijo.getKey()).child("leido").setValue(true);
                        }
                    }
                }
                mensajeAdapter.notifyDataSetChanged();
                if (!listaMensajes.isEmpty()) {
                    recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void enviarMensaje() {
        String texto = inputMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;
        String textoCifrado = CifradoHelper.cifrar(texto, chatId);
        Mensaje m = new Mensaje();
        m.setTexto(textoCifrado);
        m.setEmisorUid(miUid);
        m.setTimestamp(System.currentTimeMillis());
        m.setLeido(false);

        dbChat.push().setValue(m);
        inputMensaje.setText("");
    }
}