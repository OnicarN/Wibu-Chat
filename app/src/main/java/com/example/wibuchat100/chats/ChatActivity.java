package com.example.wibuchat100.chats;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.MyApplication;
import com.example.wibuchat100.cifrador.CifradoHelper;
import com.example.wibuchat100.R;
import com.example.wibuchat100.crearcuentas.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerMensajes;
    EditText inputMensaje;
    ImageButton btnEnviar;
    TextView txtNombreChat;

    String miUid, miNombre, otroUid, otroNombre, chatId;
    DatabaseReference dbChat;
    List<Mensaje> listaMensajes = new ArrayList<>();
    MensajeAdapter mensajeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, 200);
        }

        cargarComponentes();
        escucharMensajes();
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    public void cargarComponentes() {
        miUid = FirebaseAuth.getInstance().getUid();
        otroUid = getIntent().getStringExtra("idusuario");
        otroNombre = getIntent().getStringExtra("username");

        chatId = miUid.compareTo(otroUid) < 0
                ? miUid + "_" + otroUid
                : otroUid + "_" + miUid;

        dbChat = FirebaseDatabase.getInstance()
                .getReference("chats").child(chatId).child("messages");

        txtNombreChat = findViewById(R.id.txtNombreChat);
        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        inputMensaje = findViewById(R.id.inputMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        txtNombreChat.setText(otroNombre);

        mensajeAdapter = new MensajeAdapter(listaMensajes, miUid);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(mensajeAdapter);

        FirebaseDatabase.getInstance().getReference("users").child(miUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario user = snapshot.getValue(Usuario.class);
                        if (user != null) miNombre = user.getUsername();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        ImageButton btnLlamadaVoz   = findViewById(R.id.btnLlamadaVoz);
        ImageButton btnVideollamada = findViewById(R.id.btnVideollamada);

        btnLlamadaVoz.setOnClickListener(v -> iniciarLlamada(false));
        btnVideollamada.setOnClickListener(v -> iniciarLlamada(true));
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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

    private void iniciarLlamada(boolean esVideo) {
        String nombreParaZego = miNombre != null ? miNombre : miUid;

        ZegoUIKitPrebuiltCallConfig config = esVideo
                ? ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
                : ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();

        ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment
                .newInstance(
                        MyApplication.APP_ID,
                        MyApplication.APP_SIGN,
                        miUid,
                        nombreParaZego,
                        chatId,
                        config
                );

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, fragment)
                .commitNow();
    }
}