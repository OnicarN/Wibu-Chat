package com.example.wibuchat100.secciones;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wibuchat100.perfiles.PerfilFragment;
import com.example.wibuchat100.amigos.AmigosPestania;
import com.example.wibuchat100.chats.GruposFragment;
import com.example.wibuchat100.contactos.BuscarFragment;
import com.example.wibuchat100.botfriend.BotFriendFragment;
import com.example.wibuchat100.solicitudes.SolicitudesPestania;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AmigosPestania();
            case 1: return new BuscarFragment();
            case 2: return new GruposFragment();
            case 3: return new SolicitudesPestania(); // ← nuevo
            case 4: return new BotFriendFragment();
            case 5: return new PerfilFragment();
            default: return new AmigosPestania();
        }
    }

    @Override
    public int getItemCount() { return 4; }
}