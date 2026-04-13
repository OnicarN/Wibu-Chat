package com.example.wibuchat100;

import android.os.Bundle;
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

public class ChatGrupalActivity extends AppCompatActivity {

    RecyclerView recyclerMensajes;
    EditText inputMensaje;
    ImageButton btnEnviar;
    TextView txtNombreGrupo;

    String miUid, miNombre, grupoId, grupoNombre;
    DatabaseReference dbGrupo;
    List<Mensaje> listaMensajes = new ArrayList<>();
    MensajeGrupalAdapter mensajeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_grupal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        miUid      = FirebaseAuth.getInstance().getUid();
        grupoId    = getIntent().getStringExtra("grupoId");
        grupoNombre = getIntent().getStringExtra("grupoNombre");

        dbGrupo = FirebaseDatabase.getInstance()
                .getReference("grupos").child(grupoId).child("messages");

        txtNombreGrupo   = findViewById(R.id.txtNombreGrupo);
        recyclerMensajes = findViewById(R.id.recyclerMensajesGrupal);
        inputMensaje     = findViewById(R.id.inputMensajeGrupal);
        btnEnviar        = findViewById(R.id.btnEnviarGrupal);

        txtNombreGrupo.setText(grupoNombre);

        mensajeAdapter = new MensajeGrupalAdapter(listaMensajes, miUid);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(mensajeAdapter);

        // Cargamos nuestro nombre para los mensajes
        FirebaseDatabase.getInstance().getReference("users").child(miUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HelperClass user = snapshot.getValue(HelperClass.class);
                        if (user != null) miNombre = user.getUsername();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        escucharMensajes();
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void escucharMensajes() {
        dbGrupo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Mensaje m = hijo.getValue(Mensaje.class);
                    if (m != null) listaMensajes.add(m);
                }
                mensajeAdapter.notifyDataSetChanged();
                if (!listaMensajes.isEmpty())
                    recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void enviarMensaje() {
        String texto = inputMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        Mensaje m = new Mensaje();
        m.setTexto(texto);
        m.setEmisorUid(miUid);
        m.setEmisorNombre(miNombre != null ? miNombre : "Yo");
        m.setTimestamp(System.currentTimeMillis());

        dbGrupo.push().setValue(m);
        inputMensaje.setText("");
    }
}