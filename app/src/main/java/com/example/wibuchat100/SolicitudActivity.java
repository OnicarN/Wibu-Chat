package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SolicitudActivity extends AppCompatActivity {

    TextView txtTitulo, txtSubtitulo;
    Button btnAceptar, btnRechazar;

    String emisorUid;      // UID del que envió la solicitud
    String solicitudKey;   // Key del nodo en /solicitudes

    DatabaseReference dbSolicitudes;
    DatabaseReference dbAmigos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solicitud);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Datos que llegan desde la notificación (o desde otro sitio)
        emisorUid    = getIntent().getStringExtra("emisorUid");
        solicitudKey = getIntent().getStringExtra("solicitudKey");

        txtTitulo    = findViewById(R.id.solicitudTitulo);
        txtSubtitulo = findViewById(R.id.solicitudSubtitulo);
        btnAceptar   = findViewById(R.id.btnAceptar);
        btnRechazar  = findViewById(R.id.btnRechazar);

        dbSolicitudes = FirebaseDatabase.getInstance().getReference("solicitudes");
        dbAmigos      = FirebaseDatabase.getInstance().getReference("amigos");

        // Cargamos el nombre del emisor para mostrarlo
        if (emisorUid != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(emisorUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HelperClass user = snapshot.getValue(HelperClass.class);
                            if (user != null) {
                                txtSubtitulo.setText(user.getUsername() + " quiere ser tu amigo");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }

        btnAceptar.setOnClickListener(v -> aceptarSolicitud());
        btnRechazar.setOnClickListener(v -> rechazarSolicitud());
    }

    private void aceptarSolicitud() {
        String miUid = FirebaseAuth.getInstance().getUid();
        if (miUid == null || emisorUid == null) return;

        // Guardamos la amistad en ambas direcciones
        dbAmigos.child(miUid).child(emisorUid).setValue(true);
        dbAmigos.child(emisorUid).child(miUid).setValue(true);

        // Marcamos la solicitud como aceptada (o la borramos)
        if (solicitudKey != null) {
            dbSolicitudes.child(solicitudKey).child("estado").setValue("aceptada");
        }

        Toast.makeText(this, "¡Ahora sois amigos!", Toast.LENGTH_SHORT).show();

        // Vamos a la pantalla de amigos
        startActivity(new Intent(this, AmigosActivity.class));
        finish();
    }

    private void rechazarSolicitud() {
        if (solicitudKey != null) {
            dbSolicitudes.child(solicitudKey).child("estado").setValue("rechazada");
        }
        Toast.makeText(this, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}