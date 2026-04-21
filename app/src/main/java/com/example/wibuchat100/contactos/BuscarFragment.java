package com.example.wibuchat100.contactos;

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
import com.example.wibuchat100.crearcuentas.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscarFragment extends Fragment {

    RecyclerView recycler;
    SearchView buscador;
    BuscarAdapter adapter;
    List<Contacto> listaUsuarios = new ArrayList<>();
    DatabaseReference db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference("users");
        recycler = view.findViewById(R.id.recyclerBuscar);
        buscador = view.findViewById(R.id.buscadorUsuarios);

        adapter = new BuscarAdapter(listaUsuarios);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String texto) {
                buscarUsuarios(texto);
                return false;
            }
            @Override public boolean onQueryTextSubmit(String q) { return false; }
        });
    }

    private void buscarUsuarios(String texto) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Usuario user = hijo.getValue(Usuario.class);
                    if (user != null && user.getUsername().toLowerCase()
                            .contains(texto.toLowerCase())) {
                        Contacto c = new Contacto();
                        c.setNombre(user.getUsername());
                        c.setEmail(user.getEmail());
                        c.setUid(user.getUid());
                        listaUsuarios.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}