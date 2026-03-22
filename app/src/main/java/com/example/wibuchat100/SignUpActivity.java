package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    //Zona en la que voy a ir declarando mis variables
    EditText username,email,password;
    Button btnSignUp;



    //Declaro las variables para la parte del firebase
    FirebaseDatabase firebaseDatabase;
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

        //Llamo a la función para cargar componentes
        cargarComoponentesSignup();
    }

    //Función para cargar los componentes
    public void cargarComoponentesSignup(){
        HelperClass helperClass;
        username = findViewById(R.id.SignUsername);
        email = findViewById(R.id.SignEmail);
        password = findViewById(R.id.SignPassword);
        btnSignUp = findViewById(R.id.signup_button);

        //Inicializo la parte de la base de datos
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperClass helperClass;
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("users");

                String name = username.getText().toString();
                String mail = email.getText().toString();
                String passwd = password.getText().toString();

                if (!name.isEmpty() && !mail.isEmpty() && !passwd.isEmpty()){
                    helperClass = new HelperClass(name,mail,passwd);
                    databaseReference.child(name).setValue(helperClass);
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                }


            }
        });
    }
}