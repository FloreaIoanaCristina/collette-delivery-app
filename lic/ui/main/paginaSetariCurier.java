package com.dam.lic.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.lic.CardAdapter;
import com.dam.lic.R;
import com.dam.lic.ReadWriteCardDetails;
import com.dam.lic.paginaCard;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class paginaSetariCurier extends Fragment implements TabLayout.OnTabSelectedListener {
    TextView termeni, ajutor;
    Switch switchNightMode,switchNotificari;
    SharedPrefs sharedPreferences;
    Button adaugaCard;
    FrameLayout fragmentCard;
    RecyclerView rvCarduri;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    CardAdapter cardAdapter;
    List<ReadWriteCardDetails> listaCarduri = new ArrayList<>();
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate =inflater.inflate(R.layout.fragment_pagina_setari_curier, container,false );
        sharedPreferences=new SharedPrefs(getContext());
        tabLayout  = getActivity().findViewById(R.id.tabs); // Obține obiectul TabLayout
        tabLayout.addOnTabSelectedListener(this);

        termeni = inflate.findViewById(R.id.textViewTermeniSiConditii);
        ajutor = inflate.findViewById(R.id.textViewAjutor);

        switchNightMode = inflate.findViewById(R.id.switchNightModeCurier);
        switchNotificari = inflate.findViewById(R.id.switchNotificariCurier);
        adaugaCard = inflate.findViewById(R.id.buttonAdaugaCardCurier);

        fragmentCard = inflate.findViewById(R.id.fragmentCardCurier);

        rvCarduri = inflate.findViewById(R.id.recyclerViewCard);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvCarduri.setLayoutManager(manager);
        cardAdapter = new CardAdapter(listaCarduri,getContext(),uid);
        rvCarduri.setAdapter(cardAdapter);
        return  inflate;

    }
    @Override
    public void onResume() {
        super.onResume();
        getCarduri();
        sharedPreferences=new SharedPrefs(getContext());

        switchNightMode.setChecked(sharedPreferences.getDarkTheme() == true);
        switchNotificari.setChecked(sharedPreferences.getNotifications()==true);



    }
    private void getCarduri() {
        DatabaseReference referenceCards = firebase.getReference("RegisteredUsers/"+uid+"/Carduri");
        referenceCards.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCarduri.clear();
                notifyCardAdapter();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ReadWriteCardDetails cardDetails = new ReadWriteCardDetails(childSnapshot.child("nr").getValue().toString(),Integer.parseInt(childSnapshot.child("expirationMonth").getValue().toString()),Integer.parseInt(childSnapshot.child("expirationYear").getValue().toString()),childSnapshot.child("name").getValue().toString(),childSnapshot.child("cvc").getValue().toString(),Integer.parseInt(childSnapshot.child("activ").getValue().toString()));
                    listaCarduri.add(cardDetails);
                    cardAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        notifyCardAdapter();

    }
    private void notifyCardAdapter() {
        CardAdapter adapter = (CardAdapter) rvCarduri.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        termeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String positiveButtonText = "AM INTELES";
                int positiveButtonColor =  Color.parseColor("#5cb6f9");
                SpannableString spannableString = new SpannableString(positiveButtonText);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(getContext())
                        .setTitle("Termeni si conditii")
                        .setMessage(R.string.termeni_conditii)
                        .setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .create()
                        .show();
            }
        });
        ajutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String positiveButtonText = "AM INTELES";
                int positiveButtonColor =  Color.parseColor("#5cb6f9");
                SpannableString spannableString = new SpannableString(positiveButtonText);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                new AlertDialog.Builder(getContext())
                        .setTitle("Ajutor")
                        .setMessage(R.string.text_ajutor)
                        .setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .create()
                        .show();
            }
        });
        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferences.setDarkTheme(true);
                    Toast.makeText(getContext(), "Mod noapte", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.setDarkTheme(false);
                    Toast.makeText(getContext(), "Mod zi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchNotificari.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferences.setNotifications(true);
                    Toast.makeText(getContext(), "Notificarile aplicatiei au fost activate", Toast.LENGTH_SHORT).show();
                } else {
                    // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.setNotifications(false);
                    Toast.makeText(getContext(), "Notificarile aplicatiei au fost dezactivate", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adaugaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginaCard pCard = new paginaCard();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragmentCardCurier, pCard);
                fragmentTransaction.commit();
                fragmentCard.setVisibility(View.VISIBLE);


            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        fragmentCard.setVisibility(View.GONE);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
