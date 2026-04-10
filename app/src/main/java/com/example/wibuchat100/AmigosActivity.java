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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AmigosActivity extends AppCompatActivity {

    RecyclerView recyclerAmigos;
    SearchView buscadorAmigos;
    ContactoAdapter amigoAdapter;
    List<Contacto> listaAmigos = new ArrayList<>();
    List<Contacto> listaAmigosCompleta = new ArrayList<>(); // para filtrar

    DatabaseReference dbAmigos;
    DatabaseReference dbUsers;
    String miUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_amigos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        miUid = FirebaseAuth.getInstance().getUid();
        dbAmigos = FirebaseDatabase.getInstance().getReference("amigos");
        dbUsers  = FirebaseDatabase.getInstance().getReference("users");

        recyclerAmigos  = findViewById(R.id.recyclerAmigos);
        buscadorAmigos  = findViewById(R.id.buscadorAmigos);

        amigoAdapter = new ContactoAdapter(listaAmigos);
        recyclerAmigos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAmigos.setAdapter(amigoAdapter);

        cargarAmigos();

        buscadorAmigos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String texto) {
                listaAmigos.clear();
                for (Contacto c : listaAmigosCompleta) {
                    if (c.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                        listaAmigos.add(c);
                    }
                }
                amigoAdapter.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
        });
    }

    private void cargarAmigos() {
        if (miUid == null) return;

        dbAmigos.child(miUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAmigos.clear();
                listaAmigosCompleta.clear();

                for (DataSnapshot hijo : snapshot.getChildren()) {
                    String amigoUid = hijo.getKey();
                    // Por cada amigo, leemos sus datos de /users
                    dbUsers.child(amigoUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            HelperClass user = userSnap.getValue(HelperClass.class);
                            if (user != null) {
                                Contacto c = new Contacto();
                                c.setNombre(user.getUsername());
                                c.setEmail(user.getEmail());
                                c.setUid(user.getUid());
                                listaAmigos.add(c);
                                listaAmigosCompleta.add(c);
                                amigoAdapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}