package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    TextView redirectSign;
    Button btnMain;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarComponentesLogin();
    }

    public void cargarComponentesLogin() {
        editUsername = findViewById(R.id.LoginUsername);
        editPassword = findViewById(R.id.LoginPassword);
        redirectSign = findViewById(R.id.loginRedirectTet);
        btnMain      = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnMain.setOnClickListener(v -> realizarLogin());

        redirectSign.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );
    }

    public void realizarLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty()) { editUsername.setError("Introduce tu nombre de usuario"); return; }
        if (password.isEmpty()) { editPassword.setError("Introduce tu contraseña"); return; }

        // Busco el email en Realtime Database a partir del username
        Query query = databaseReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    editUsername.setError("Usuario no encontrado");
                    editUsername.requestFocus();
                    return;
                }

                // Obtengo el email del primer resultado
                String email = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    email = child.child("email").getValue(String.class);
                    break;
                }

                if (email == null) {
                    Toast.makeText(LoginActivity.this, "Error al obtener datos del usuario",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ahora hago login con Firebase Authentication
                final String emailFinal = email;
                mAuth.signInWithEmailAndPassword(emailFinal, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                editPassword.setError("Contraseña incorrecta");
                                editPassword.requestFocus();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}