package com.example.wibuchat100.amigos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;
import com.example.wibuchat100.contactos.Contacto;
import com.example.wibuchat100.contactos.ContactoAdapter;
import com.example.wibuchat100.crearcuentas.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AmigosPestania extends Fragment {

    RecyclerView recycler;
    SearchView buscador;
    ContactoAdapter adapter;
    List<Contacto> listaAmigos = new ArrayList<>();
    List<Contacto> listaCompleta = new ArrayList<>();

    DatabaseReference dbAmigos, dbUsers;
    String miUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_amigos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        miUid    = FirebaseAuth.getInstance().getUid();
        dbAmigos = FirebaseDatabase.getInstance().getReference("amigos");
        dbUsers  = FirebaseDatabase.getInstance().getReference("users");

        recycler  = view.findViewById(R.id.recyclerAmigos);
        buscador  = view.findViewById(R.id.buscadorAmigos);

        adapter = new ContactoAdapter(listaAmigos);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        cargarAmigos();

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String texto) {
                listaAmigos.clear();
                for (Contacto c : listaCompleta) {
                    if (c.getNombre().toLowerCase().contains(texto.toLowerCase()))
                        listaAmigos.add(c);
                }
                adapter.notifyDataSetChanged();
                return false;
            }
            @Override public boolean onQueryTextSubmit(String q) { return false; }
        });
    }

    private void cargarAmigos() {
        if (miUid == null) return;
        dbAmigos.child(miUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAmigos.clear();
                listaCompleta.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    String amigoUid = hijo.getKey();
                    dbUsers.child(amigoUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            Usuario user = userSnap.getValue(Usuario.class);
                            if (user != null) {
                                Contacto c = new Contacto();
                                c.setNombre(user.getUsername());
                                c.setEmail(user.getEmail());
                                c.setUid(user.getUid());
                                listaAmigos.add(c);
                                listaCompleta.add(c);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}