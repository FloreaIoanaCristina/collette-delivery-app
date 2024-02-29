package com.dam.lic;

import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;

public class paginaCard extends Fragment {
    public EditText etNr, etLuna,etAn,etNume,etCVC;
    public Button btnSave;
    String uid;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View inflate = inflater.inflate(R.layout.fragment_pagina_card, container, false);

       etNr = inflate.findViewById(R.id.etNrCard);
       etLuna = inflate.findViewById(R.id.etLunaCard);
       etAn = inflate.findViewById(R.id.etAnCard);
       etNume = inflate.findViewById(R.id.etNumeCard);
       etCVC = inflate.findViewById(R.id.etCvcCard);
       btnSave = inflate.findViewById(R.id.btnCardSave);
       uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nr,luna,an,nume,cvc;
                int month,year;

                nr = etNr.getText().toString();
                luna = etLuna.getText().toString();
                an = etAn.getText().toString();
                nume = etNume.getText().toString();
                cvc=etCVC.getText().toString();

                if(!luna.isEmpty() && luna.matches("\\d+")){
                    month = Integer.parseInt(luna);
                }
                else
                {
                    month =0;
                }
                if(!an.isEmpty() && an.matches("\\d+"))
                {
                    year = Integer.parseInt(an);
                }
                else {
                    year = 0;

                }

                boolean ok = true;
                if(TextUtils.isEmpty(nr))
                {   ok=false;
                    etNr.setError("Introduce-ti numarul cardului!");
                    etNr.requestFocus();
                }
                if(nr.length()!=16)
                {
                    ok=false;
                    etNr.setError("Numarul cardului trebuie sa aibe 16 cifre!");
                    etNr.requestFocus();
                }
                if(TextUtils.isEmpty(luna))
                {   ok=false;
                    etLuna.setError("Introduce-ti luna de expirare a cardului!");
                    etLuna.requestFocus();
                }
                else if(month>12 ||month<1)
                {   ok=false;
                    etLuna.setError("Introduce-ti o luna valida de expirare a cardului!");
                    etLuna.requestFocus();
                }
                if(TextUtils.isEmpty(an))
                {   ok=false;
                    etAn.setError("Introduce-ti anul de expirare a cardului!");
                    etAn.requestFocus();
                }
                else if(year>10000 || year<1000)
                {   ok=false;
                    etAn.setError("Introduce-ti un an valid de expirare a cardului!");
                    etAn.requestFocus();
                }
                else if(Year.parse(an).isBefore(Year.now())==true)
                {   ok=false;
                    etAn.setError("Cardul introdus este deja expirat!");
                    etAn.requestFocus();
                }
                else if(month>12 ||month<1)
                {   ok=false;

                }
                else if((Year.parse(an).compareTo(Year.now()))==0 && (month < LocalDate.now().getMonth().getValue())){
                    ok=false;
                    etLuna.setError("Cardul introdus este deja expirat!");
                    etLuna.requestFocus();
                }
                if(TextUtils.isEmpty(nume))
                {   ok=false;
                    etNume.setError("Introduce-ti numele posesorului cardului!");
                    etNume.requestFocus();
                }
                if(TextUtils.isEmpty(cvc))
                {   ok=false;
                    etCVC.setError("Introduce-ti codul de verificare card!");
                    etCVC.requestFocus();
                }
                if(cvc.length()!=3)
                {
                    ok=false;
                    etCVC.setError("Codul de verificare trebuie sa aibe 3 cifre!");
                    etCVC.requestFocus();
                }

                if(ok==true)
                {
                    ReadWriteCardDetails card = new ReadWriteCardDetails(nr,month,year,nume,cvc,0);
                    DatabaseReference referenceCard = firebase.getReference("RegisteredUsers/"+uid+"/Carduri/"+nr);
                    referenceCard.setValue(card);

                    Toast.makeText(getContext(),"Card adaugat",Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(paginaCard.this);
                    fragmentTransaction.commit();
                }


            }
        });
    }
}