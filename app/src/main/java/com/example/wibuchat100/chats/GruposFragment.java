package com.example.wibuchat100.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GruposFragment extends Fragment {

    RecyclerView recyclerGrupos;
    Button btnCrearGrupo;
    GrupoAdapter grupoAdapter;
    List<Grupo> listaGrupos = new ArrayList<>();
    DatabaseReference dbGrupos;
    String miUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grupos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        miUid      = FirebaseAuth.getInstance().getUid();
        dbGrupos   = FirebaseDatabase.getInstance().getReference("grupos");

        recyclerGrupos = view.findViewById(R.id.recyclerGrupos);
        btnCrearGrupo  = view.findViewById(R.id.btnCrearGrupo);

        grupoAdapter = new GrupoAdapter(listaGrupos);
        recyclerGrupos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerGrupos.setAdapter(grupoAdapter);

        cargarGrupos();

        btnCrearGrupo.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CrearGrupoActivity.class)));
    }

    private void cargarGrupos() {
        dbGrupos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaGrupos.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Grupo g = hijo.getValue(Grupo.class);
                    if (g != null && g.getParticipantes() != null
                            && g.getParticipantes().contains(miUid)) {
                        g.setKey(hijo.getKey());
                        listaGrupos.add(g);
                    }
                }
                grupoAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}