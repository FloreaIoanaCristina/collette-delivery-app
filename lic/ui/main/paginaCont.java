package com.dam.lic.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.dam.lic.HomeActivity;
import com.dam.lic.LoginActivity;
import com.dam.lic.R;
import com.dam.lic.Stare;
import com.dam.lic.paginaCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Iterator;

public class paginaCont extends Fragment implements paginaEditare.OnButtonClickListener{
     ImageView imgProfil;
     EditText etUID;
     TextView tvNume, tvTelefon, tvJudet, tvAdresa, tvLocalitate, tvCodPostal;
     Button btnEdit, btnSterge, btnSignOut;
     FirebaseDatabase firebase = FirebaseDatabase.getInstance();
     FirebaseAuth firebaseAuth;
     SharedPrefs sharedPreferences;
     FrameLayout fragmentEditare;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pagina_cont, container,false );
        sharedPreferences= new SharedPrefs(getContext());
        fragmentEditare = inflate.findViewById(R.id.fragmentEditare);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) {


            String uid = firebaseAuth.getCurrentUser().getUid();
            imgProfil = getView().findViewById(R.id.imageViewProfil);
            etUID = getView().findViewById(R.id.editTextUID);
            tvNume = getView().findViewById(R.id.textViewNume);
            tvTelefon = getView().findViewById(R.id.textViewTelefonInfo);
            tvJudet = getView().findViewById(R.id.textViewBrandInfo);
            tvLocalitate = getView().findViewById(R.id.textViewLocalitateInfo);
            tvAdresa = getView().findViewById(R.id.textViewAdresaInfo);
            tvCodPostal = getView().findViewById(R.id.textViewCodPostalInfo);
            btnEdit = getView().findViewById(R.id.buttonEditeazaProfil);
            btnSignOut = getView().findViewById(R.id.buttonSignOut);
            btnSterge = getView().findViewById(R.id.buttonStergeCont);


            DatabaseReference reference = firebase.getReference("RegisteredUsers").child(uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String address = snapshot.child("address").getValue(String.class);
                    String county = snapshot.child("county").getValue(String.class);
                    String img = snapshot.child("img").getValue(String.class);
                    String loc = snapshot.child("loc").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String postalCode = snapshot.child("postalCode").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);

                    etUID.setText(uid);
                    tvNume.setText(user);
                    tvTelefon.setText(phone);
                    tvJudet.setText(county);
                    tvLocalitate.setText(loc);
                    tvAdresa.setText(address);
                    tvCodPostal.setText(postalCode);
                   if(img!=null) {
                        if (img.equals("nespecificat")) {
                            imgProfil.setBackgroundResource(R.drawable.poza_profil_generica);
                        } else {
                            Picasso.get().load(img).resizeDimen(R.dimen.profile_pic, R.dimen.profile_pic).into(imgProfil);
                        }
                    }
                    else {
                        Picasso.get().load(img).resizeDimen(R.dimen.profile_pic, R.dimen.profile_pic).into(imgProfil);
                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.setRememberMeKey(false);
                    firebaseAuth.signOut();


                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paginaEditare fragment = new paginaEditare();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragmentEditare, fragment);
                    fragmentTransaction.commit();
                    fragmentEditare.setVisibility(View.VISIBLE);
                }
            });
            btnSterge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                    dialog.setContentView(R.layout.delete_dialog);
                    dialog.setCancelable(true);
                    Button btnPozitiv = dialog.findViewById(R.id.delete_button_pozitiv), btnNegativ = dialog.findViewById(R.id.delete_button_negativ);
                    btnPozitiv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebase.getReference("RegisteredUsers/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Iterable<DataSnapshot> friends = snapshot.child("Prieteni").getChildren();
                                    Iterator<DataSnapshot> iterator = friends.iterator();
                                    while (iterator.hasNext()) {
                                        DataSnapshot friend = iterator.next();
                                        String friendKey = friend.getKey();

                                        // Șterge prietenul din baza de date
                                        firebase.getReference("RegisteredUsers/" + friendKey + "/Prieteni/" + uid).removeValue();
                                    }

                                    for (DataSnapshot child : snapshot.child("Comenzi").getChildren()) {


                                        firebase.getReference("Commands/" + child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String stare = snapshot.child("state").getValue().toString();
                                                String expeditor = snapshot.child("sender").getValue().toString();
                                                String destinatar = snapshot.child("recipient").getValue().toString();
                                                 String curier = snapshot.child("courier").getValue().toString();
                                                if (stare.equals("CREATA") || stare.equals("IN_ASTEPTARE_ACCEPT") || stare.equals("IN_ASTEPTARE_CURIER") || stare.equals("IN_CURS_DE_PRELUARE")) {
                                                    if(!(destinatar.equals("nespecificat")) &&!(destinatar.equals(uid)) ) {
                                                        firebase.getReference("RegisteredUsers/" + destinatar + "/Comenzi/" +child.getKey()).removeValue();

                                                    }
                                                    if(!(expeditor.equals("nespecificat")) &&!(expeditor.equals(uid)) ) {
                                                        firebase.getReference("RegisteredUsers/" + expeditor + "/Comenzi/" +child.getKey()).removeValue();

                                                    }
                                                    if(!(curier.equals("nespecificat")))
                                                    {
                                                        firebase.getReference("RegisteredUsers/" + curier + "/Comenzi/" +child.getKey()).removeValue();
                                                    }
                                                    firebase.getReference("Commands/" + child.getKey()).removeValue();


                                                }
                                                else if (expeditor.equals(uid)) {
                                                    firebase.getReference("Commands/" + child.getKey() + "/sender").setValue("nespecificat");
                                                }
                                                else if (destinatar.equals(uid)) {
                                                    firebase.getReference("Commands/" + child.getKey() + "/recipient").setValue("nespecificat");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                    firebase.getReference("Cereri").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            Iterable<DataSnapshot> cereri = snapshot.getChildren();
                                            Iterator<DataSnapshot> iterator = cereri.iterator();
                                            while (iterator.hasNext()) {
                                                DataSnapshot cerere = iterator.next();
                                                String sender = cerere.child("sender").getValue().toString();
                                                String receiver = cerere.child("receiver").getValue().toString();

                                                // Verifică dacă cererea trebuie ștearsă
                                                if (sender.equals(uid) || receiver.equals(uid)) {
                                                    cerere.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference imageRef = storage.getReference().child("UsersProfilePictures/" + uid);
                                    imageRef.delete();

                                    firebase.getReference("RegisteredUsers/" + uid).removeValue();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    //String id = user.getUid();

                                    sharedPreferences.setRememberMeKey(false);
                                    user.delete();
                                    dialog.dismiss();
                                    // onStart();
                                    Toast.makeText(getContext(), "Contul a fost sters!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    btnNegativ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.create();
                    dialog.show();

                }
            });
        }else
        {
            Toast.makeText(getContext(), "Contul a fost sters!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onButtonClicked() {
        fragmentEditare.setVisibility(View.GONE);
    }
}
