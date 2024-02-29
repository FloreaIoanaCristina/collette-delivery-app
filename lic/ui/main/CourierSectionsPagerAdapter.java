package com.dam.lic.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CourierSectionsPagerAdapter extends FragmentStateAdapter {


    public CourierSectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new paginaStatistici();
            case 1:
                return new paginaAcasaCurier();
            case 2:
                return new paginaContCurier();
            case 3:
                return new paginaSetariCurier();
            default:
                return new paginaAcasaCurier();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}