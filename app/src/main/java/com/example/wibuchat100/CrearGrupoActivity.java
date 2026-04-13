package com.example.wibuchat100;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearGrupoActivity extends AppCompatActivity {

    EditText inputNombreGrupo;
    LinearLayout contenedorAmigos;
    Button btnCrear;

    DatabaseReference dbAmigos, dbUsers, dbGrupos;
    String miUid;

    // Mapa uid -> checkbox para saber quién está seleccionado
    Map<String, CheckBox> checkBoxMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_grupo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        miUid           = FirebaseAuth.getInstance().getUid();
        dbAmigos        = FirebaseDatabase.getInstance().getReference("amigos");
        dbUsers         = FirebaseDatabase.getInstance().getReference("users");
        dbGrupos        = FirebaseDatabase.getInstance().getReference("grupos");

        inputNombreGrupo  = findViewById(R.id.inputNombreGrupo);
        contenedorAmigos  = findViewById(R.id.contenedorAmigos);
        btnCrear          = findViewById(R.id.btnCrearGrupoFinal);

        cargarAmigosConCheckbox();

        btnCrear.setOnClickListener(v -> crearGrupo());
    }

    private void cargarAmigosConCheckbox() {
        dbAmigos.child(miUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    String amigoUid = hijo.getKey();
                    dbUsers.child(amigoUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            HelperClass user = userSnap.getValue(HelperClass.class);
                            if (user != null) {
                                // Creamos un CheckBox dinámicamente por cada amigo
                                CheckBox cb = new CheckBox(CrearGrupoActivity.this);
                                cb.setText(user.getUsername());
                                cb.setTextColor(0xFFFFFFFF);
                                cb.setTextSize(16f);
                                cb.setPadding(8, 12, 8, 12);
                                contenedorAmigos.addView(cb);
                                checkBoxMap.put(amigoUid, cb);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void crearGrupo() {
        String nombre = inputNombreGrupo.getText().toString().trim();
        if (nombre.isEmpty()) {
            inputNombreGrupo.setError("Ponle un nombre al grupo");
            return;
        }

        List<String> participantes = new ArrayList<>();
        participantes.add(miUid); // Me añado yo

        for (Map.Entry<String, CheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isChecked()) {
                participantes.add(entry.getKey());
            }
        }

        if (participantes.size() < 2) {
            Toast.makeText(this, "Selecciona al menos un amigo", Toast.LENGTH_SHORT).show();
            return;
        }

        Grupo grupo = new Grupo();
        grupo.setNombre(nombre);
        grupo.setCreadoPor(miUid);
        grupo.setParticipantes(participantes);

        dbGrupos.push().setValue(grupo).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "¡Grupo creado!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}