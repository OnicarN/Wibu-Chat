package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText username, email, password;
    Button btnSignUp;
    TextView txtLogin;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarComponentesSignup();
    }

    public void cargarComponentesSignup() {
        username = findViewById(R.id.SignUsername);
        email = findViewById(R.id.SignEmail);
        password = findViewById(R.id.SignPassword);
        btnSignUp = findViewById(R.id.signup_button);
        txtLogin = findViewById(R.id.signRedirectTet);

        // Inicializo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void registrarUsuario() {
        String name = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String passwd = password.getText().toString().trim();

        if (name.isEmpty()) {
            username.setError("Introduce un nombre de usuario");
            return;
        }
        if (mail.isEmpty()) {
            email.setError("Introduce un email");
            return;
        }
        if (passwd.isEmpty()) {
            password.setError("Introduce una contraseña");
            return;
        }


        validarUsuarioExistente(name, new ValidacionesUsername() {
            @Override
            public void onUsernameAvailable() {
                // 1) Crear usuario en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(mail, passwd)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();

                                // 2) Guardar el displayName en Auth (opcional)
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                mAuth.getCurrentUser().updateProfile(profile);

                                // 3) Guardar el perfil público en Realtime Database
                                HelperClass helperClass = new HelperClass(uid, name, mail);
                                databaseReference.child(uid).setValue(helperClass);

                                Toast.makeText(SignUpActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }

            @Override
            public void onUsernameExists() {
                Toast.makeText(SignUpActivity.this, "El nombre de usuario ya ha sido usado en otra cuenta " ,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Si ya existe un usuario con ese nombre
     * no vamos a dejar crear otro usuario con el
     * mismo nombre entonces
     *
     * @return
     */
    public void validarUsuarioExistente(String nombreUsuario, ValidacionesUsername validacion) {
        Query query = databaseReference.orderByChild("username").equalTo(nombreUsuario);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    validacion.onUsernameAvailable();
                }else{
                    validacion.onUsernameExists();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}