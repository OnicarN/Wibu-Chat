package com.example.wibuchat100;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //Parte en la que declaro las variables
    RecyclerView recyclerView;
    DatabaseReference db;
    List<Contacto> contactos;

    SearchView searchview;

    ContactoAdapter contactoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Cargando los componentes
        cargarComponentes();
        rellenarRecycler();

        /**
         * Esta parte del código lo que hace es que mientras escribes algo
         * en el search view, inmediatamente después lo que va a suceder es
         * que se van a cargar aquellos usuarios que existan con ese "username"
         */
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        contactos.clear();

                        for (DataSnapshot hija: snapshot.getChildren()){
                            HelperClass usuario = hija.getValue(HelperClass.class);
                            Contacto contacto = new Contacto();
                            contacto.setNombre(usuario.getUsername());
                            contacto.setEmail(usuario.getEmail());

                            if (contacto.getNombre().contains(newText)){
                                contactos.add(contacto);
                            }
                        }
                        contactoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });
    }

    public void cargarComponentes(){
        recyclerView = findViewById(R.id.lista_contactos);
        contactos = new ArrayList<>();
        contactoAdapter = new ContactoAdapter(contactos);
        db = FirebaseDatabase.getInstance().getReference("users");
        searchview = findViewById(R.id.buscador);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactoAdapter);
    }

    public void rellenarRecycler(){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactos.clear();

                for (DataSnapshot hija: snapshot.getChildren()){
                    HelperClass usuario = hija.getValue(HelperClass.class);
                    Contacto contacto = new Contacto();
                    contacto.setNombre(usuario.getUsername());
                    contacto.setEmail(usuario.getEmail());
                    contactos.add(contacto);
                }
                contactoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}