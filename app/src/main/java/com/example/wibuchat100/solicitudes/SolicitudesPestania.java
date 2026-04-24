package com.example.wibuchat100.solicitudes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;
import com.example.wibuchat100.crearcuentas.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesPestania extends Fragment {

    RecyclerView recycler;
    TextView txtVacio;
    SolicitudesAdapter adapter;
    List<SolicitudItem> listaSolicitudes = new ArrayList<>();

    DatabaseReference dbSolicitudes, dbAmigos, dbUsers;
    String miUid;

    //ArrayList para que no salgan repetidas las solicitudes


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        miUid        = FirebaseAuth.getInstance().getUid();
        dbSolicitudes = FirebaseDatabase.getInstance().getReference("solicitudes");
        dbAmigos     = FirebaseDatabase.getInstance().getReference("amigos");
        dbUsers      = FirebaseDatabase.getInstance().getReference("users");

        recycler  = view.findViewById(R.id.recyclerSolicitudes);
        txtVacio  = view.findViewById(R.id.txtSinSolicitudes);

        adapter = new SolicitudesAdapter(listaSolicitudes, new SolicitudesAdapter.OnSolicitudListener() {
            @Override
            public void onAceptar(SolicitudItem solicitud) {
                aceptar(solicitud);
            }

            @Override
            public void onRechazar(SolicitudItem solicitud) {
                rechazar(solicitud);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        dbSolicitudes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaSolicitudes.clear();

                for (DataSnapshot hijo : snapshot.getChildren()) {
                    String para  = hijo.child("para").getValue(String.class);
                    String estado = hijo.child("estado").getValue(String.class);
                    String de    = hijo.child("de").getValue(String.class);

                    if (miUid.equals(para) && "pendiente".equals(estado)) {
                        String key = hijo.getKey();

                        dbUsers.child(de).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnap) {
                                Usuario user = userSnap.getValue(Usuario.class);
                                if (user != null) {
                                    // Comprobar si el UID ya existe en la lista
                                    if (!yaExiste(de)) {
                                        SolicitudItem item = new SolicitudItem();
                                        item.setKey(key);
                                        item.setEmisorUid(de);
                                        item.setEmisorNombre(user.getUsername());
                                        item.setEmisorEmail(user.getEmail());
                                        listaSolicitudes.add(item);
                                        adapter.notifyDataSetChanged();
                                    }
                                    actualizarVacio();
                                }
                            }
                            @Override public void onCancelled(@NonNull DatabaseError e) {}
                        });
                    }
                }
                actualizarVacio();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void aceptar(SolicitudItem solicitud) {
        // Guardar amistad en ambas direcciones
        dbAmigos.child(miUid).child(solicitud.getEmisorUid()).setValue(true);
        dbAmigos.child(solicitud.getEmisorUid()).child(miUid).setValue(true);

        // Marcar como aceptada
        dbSolicitudes.child(solicitud.getKey()).child("estado").setValue("aceptada");

        Toast.makeText(getContext(), "¡Ahora sois amigos!", Toast.LENGTH_SHORT).show();
    }

    private void rechazar(SolicitudItem solicitud) {
        dbSolicitudes.child(solicitud.getKey()).child("estado").setValue("rechazada");
        Toast.makeText(getContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
    }

    private void actualizarVacio() {
        if (listaSolicitudes.isEmpty()) {
            txtVacio.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            txtVacio.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    private boolean yaExiste(String uid) {
        for (SolicitudItem item : listaSolicitudes) {
            if (item.getEmisorUid().equals(uid)) {
                return true;
            }
        }
        return false;
    }
}