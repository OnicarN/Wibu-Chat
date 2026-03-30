package com.example.wibuchat100;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactoIndividual extends AppCompatActivity {

    TextView nombreUsuario,emailUsuario;
    Button botonSolicitud,botonCancelar;
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

        //Listeners de los botones

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactoIndividual.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void cargarComponentes (){
        nombreUsuario = findViewById(R.id.perfilNombreUsuario);
        emailUsuario = findViewById(R.id.perfilEmail);
        botonSolicitud = findViewById(R.id.btnEnviarSolicitud);
        botonCancelar = findViewById(R.id.btnCancelar);
    }
    //Setear bien los nombres
    public void rellenarInfoPrincipalUsuario(){
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("username");
        String mail = intent.getStringExtra("mail");
        nombreUsuario.setText(nombre);
        emailUsuario.setText(mail);
    }
}