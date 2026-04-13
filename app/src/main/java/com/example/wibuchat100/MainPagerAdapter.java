package com.example.wibuchat100;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AmigosFragment();
            case 1: return new BuscarFragment();
            case 2: return new GruposFragment();  // ← ¿tienes esta línea?
            case 3: return new PerfilFragment();
            default: return new AmigosFragment();
        }
    }

    @Override
    public int getItemCount() { return 4; }
}