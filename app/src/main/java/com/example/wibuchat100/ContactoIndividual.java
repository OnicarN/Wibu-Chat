package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ContactoIndividual extends AppCompatActivity {

    TextView nombreUsuario, emailUsuario;
    Button botonSolicitud, botonCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.contacto_individual);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarComponentes();
        rellenarInfoPrincipalUsuario();

        botonCancelar.setOnClickListener(v -> {
            startActivity(new Intent(ContactoIndividual.this, MainActivity.class));
            finish();
        });

        botonSolicitud.setOnClickListener(v -> {
            String emisorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String receptorUid = getIntent().getStringExtra("uid");

            if (receptorUid != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("solicitudes");

                HashMap<String, Object> data = new HashMap<>();
                data.put("de", emisorUid);
                data.put("para", receptorUid);
                data.put("estado", "pendiente");
                data.put("timestamp", System.currentTimeMillis());

                // La Cloud Function detecta este nuevo nodo y envía la notificación sola
                ref.push().setValue(data).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "¡Solicitud enviada!", Toast.LENGTH_SHORT).show();
                    botonSolicitud.setEnabled(false);
                    botonSolicitud.setText("Enviada");

// ← Ya NO llamamos a enviarNotificacionPush() ni sendFCM()
                    // La Cloud Function lo hace automáticamente y correctamente

                });
            }
        });
    }

    public void cargarComponentes() {
        nombreUsuario = findViewById(R.id.perfilNombreUsuario);
        emailUsuario = findViewById(R.id.perfilEmail);
        botonSolicitud = findViewById(R.id.btnEnviarSolicitud);
        botonCancelar = findViewById(R.id.btnCancelar);
    }

    public void rellenarInfoPrincipalUsuario() {
        Intent intent = getIntent();
        nombreUsuario.setText(intent.getStringExtra("username"));
        emailUsuario.setText(intent.getStringExtra("mail"));
    }
    // ← Los métodos enviarNotificacionPush() y sendFCM() se eliminan por completo
}
