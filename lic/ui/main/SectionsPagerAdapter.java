package com.dam.lic.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SectionsPagerAdapter extends FragmentStateAdapter {


    public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new paginaPrieteni();
            case 1:
                return new paginaAcasa();
            case 2:
                return new paginaCont();
            case 3:
                return new paginaSetari();
            default:
                return new paginaAcasa();
        }

    }


    @Override
    public int getItemCount() {
        return 4;
    }
}