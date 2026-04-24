package com.example.wibuchat100.crearcuentas;

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

import com.example.wibuchat100.MainActivity;
import com.example.wibuchat100.MyApplication;
import com.example.wibuchat100.R;
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

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

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
        btnMain = findViewById(R.id.login_button);

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

        if (username.isEmpty()) {
            editUsername.setError("Introduce tu nombre de usuario");
            editUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Introduce tu contraseña");
            editPassword.requestFocus();
            return;
        }

        // Busco el email en Realtime Database a partir del username
        //Para eso instancio un objeto tipo Query
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
                //En este caso ya se sabe que solo hay uno pero el orderbychild me devuelve un array el snapshot
                String email = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    email = child.child("email").getValue(String.class);
                    break;
                }

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Error al obtener datos del usuario",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ahora hago login con Firebase Authentication
                final String emailAutentication = email;
                mAuth.signInWithEmailAndPassword(emailAutentication, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String idUsario = mAuth.getCurrentUser().getUid();
                                com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String token = tokenTask.getResult();
                                                // 3. Lo guardamos en la carpeta del usuario en "users/UID/fcmtoken"
                                                databaseReference.child(idUsario).child("fcmToken").setValue(token);
                                            }
                                            String nombreUsuario = username;
                                            MyApplication.iniciarZegoParaUsuario(
                                                    getApplication(),
                                                    idUsario,
                                                    nombreUsuario
                                            );

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        });
                            } else {
                                editPassword.setError("Usuario o contraseña incorrectos");
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