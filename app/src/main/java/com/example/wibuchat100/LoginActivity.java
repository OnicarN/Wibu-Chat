package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername,editPassword;
    TextView redirectSign;

    Button btnMain;
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

        //Llamo a la función para cargar los componentes
        cargarComponentesLogin();
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });
    }

    public void cargarComponentesLogin(){
        editUsername = findViewById(R.id.LoginUsername);
        editPassword = findViewById(R.id.LoginPassword);
        redirectSign = findViewById(R.id.loginRedirectTet);
        btnMain = findViewById(R.id.login_button);
    }

    public boolean validarCredenciales(String username,String password){
        boolean res;
        int contadorBuenos = 0;

        if (username.isEmpty()){
            editUsername.setError("Falta por introducir el nombre de usuario para poder hacer login");
        }else{
            contadorBuenos++;
        }

        if (password.isEmpty()){
            editPassword.setError("Falta por introducir la contraseña para poder hacer login");
        }else{
            contadorBuenos++;
        }

        if (contadorBuenos == 2){
            res = true;
        }else{
            res = false;
        }
        return res;
    }

    public void realizarLogin(){
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);;


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = databaseReference.orderByChild("username").equalTo(username);

        if (validarCredenciales(username, password)) {

        }
        //Esto se hace para que firebase vaya buscando la info
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (validarCredenciales(username,password)){
                    int casosBuenos = 0;
                    //Snapshot es como la foto con los datos
                    if (snapshot.exists()) {
                        editUsername.setError(null);
                        String passwd = snapshot.child(username).child("password").getValue(String.class);
                        casosBuenos++;
                        if (passwd.equals(password)) {
                            editPassword.setError(null);
                            casosBuenos++;
                        } else {
                            editPassword.setError("Contraseña incorrecta, vuelve a intentarlo");
                            editPassword.requestFocus();

                        }

                        if (casosBuenos == 2){
                            startActivity(intent);
                        }
                    } else {
                        editUsername.setError("Usuario no existe");
                        editUsername.requestFocus();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        redirectSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}