package com.dam.lic.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dam.lic.LoginActivity;
import com.dam.lic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class paginaContCurier extends Fragment implements paginaEditareCurier.OnButtonClickListener{


    TextView tvNume, tvTelefon, tvMarca, tvModel, tvNrInmatriculare, tvCuloare;
    ImageView imgImagine;
    Button btnEditeaza, btnSignout, btnSterge;
    RatingBar rb;
    SharedPrefs sharedPreferences;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth;
    FrameLayout fragmentEditare;
    String uid;
    Boolean ok= false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View inflate = inflater.inflate(R.layout.fragment_pagina_cont_curier, container, false);
        sharedPreferences= new SharedPrefs(getContext());
        return inflate;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        rb = getView().findViewById(R.id.ratingBar);
        rb.setFocusable(false);

        tvNume = getView().findViewById(R.id.textViewNumCurier);
        tvTelefon = getView().findViewById(R.id.textViewTelefonInfo);
        tvMarca = getView().findViewById(R.id.textViewBrandInfo);
        tvModel = getView().findViewById(R.id.textViewModelInfo);
        tvNrInmatriculare = getView().findViewById(R.id.textViewNrInmatriculareInfo);
        tvCuloare = getView().findViewById(R.id.textViewCodCuloareInfo);
        imgImagine = getView().findViewById(R.id.imageViewProfilCurier);
        btnEditeaza = getView().findViewById(R.id.buttonEditeazaProfilCurier);
        btnSignout = getView().findViewById(R.id.buttonSignOutCurier);
        btnSterge = getView().findViewById(R.id.buttonStergeContCurier);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        fragmentEditare = getView().findViewById(R.id.fragmentEditareCurier);
        DatabaseReference reference = firebase.getReference("RegisteredUsers/" + uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("user").exists()) {
                    String nume = snapshot.child("user").getValue(String.class);
                    String telefon = snapshot.child("phone").getValue(String.class);
                    String img = snapshot.child("img").getValue(String.class);
                    String marca = snapshot.child("brand").getValue(String.class);
                    String model = snapshot.child("model").getValue(String.class);
                    String nrInmatriculare = snapshot.child("license").getValue(String.class);
                    String culoare = snapshot.child("color").getValue(String.class);
                    String ratingS,nrFeedbackS;
                    float rating=0;
                    int nrFeedback = 0;
                    if(snapshot.child("rating").exists() && snapshot.child("nrFeedback").exists() ) {
                        ratingS = snapshot.child("rating").getValue().toString();
                        nrFeedbackS = snapshot.child("nrFeedback").getValue().toString();
                        rating = Float.parseFloat(ratingS);
                        nrFeedback = Integer.parseInt(nrFeedbackS);
                    }


                  if(nrFeedback!=0) {
                      float ratingPerFeedBack = rating / nrFeedback;
                      rb.setRating(ratingPerFeedBack);
                      rb.setIsIndicator(true);
                  }else
                  {
                      rb.setRating(5);
                      rb.setIsIndicator(true);
                  }


                    tvNume.setText(nume);
                    tvTelefon.setText(telefon);
                    tvMarca.setText(marca);
                    tvModel.setText(model);
                    tvNrInmatriculare.setText(nrInmatriculare);
                    tvCuloare.setText(culoare);

                    if (img.equals("nespecificat")) {
                        imgImagine.setBackgroundResource(R.drawable.poza_profil_generica);
                    } else {
                        Picasso.get().load(img).resizeDimen(R.dimen.profile_pic, R.dimen.profile_pic).into(imgImagine);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                sharedPreferences.setRememberMeKey(false);
                firebaseAuth.signOut();


                Intent intent =new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });
        btnEditeaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginaEditareCurier fragment = new paginaEditareCurier();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragmentEditareCurier, fragment);
                fragmentTransaction.commit();
                fragmentEditare.setVisibility(View.VISIBLE);
            }
        });
        btnSterge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog =  new Dialog(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                dialog.setContentView(R.layout.delete_dialog);
                dialog.setCancelable(true);

                Button btnPozitiv =dialog.findViewById(R.id.delete_button_pozitiv),btnNegativ =dialog.findViewById(R.id.delete_button_negativ);
                btnPozitiv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firebase.getReference("RegisteredUsers/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.child("Comenzi").exists())
                                {
                                     dialog.dismiss();
                                     String positiveButtonText = "OK";
                                     int positiveButtonColor =  Color.parseColor("#5cb6f9");
                                    SpannableString spannableString = new SpannableString(positiveButtonText);
                                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                                    spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                   AlertDialog.Builder ad = new AlertDialog.Builder(getContext())
                                            .setTitle("Nu se poate sterge contul!")
                                            .setMessage("Contul dvs nu s-a putut sterge doarece inca aveti comenzi neincheiate.");

                                            ad.setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                }
                                                })
                                            .create()
                                            .show();
                                }
                                else {
                                    ok = true;
                                }
                                if(ok==true) {
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference imageRef = storage.getReference().child("UsersProfilePictures/" + uid);
                                    imageRef.delete();

                                    firebase.getReference("RegisteredUsers/" + uid).removeValue();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String id = user.getUid();

                                    sharedPreferences.setRememberMeKey(false);
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("All");
                                    firebaseAuth.signOut();

                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getContext(), "Contul a fost sters!", Toast.LENGTH_LONG).show();
                                                        Intent intent =new Intent(getContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    }
                                                }
                                            });

                                }

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
    }
    @Override
    public void onButtonClicked() {
        fragmentEditare.setVisibility(View.GONE);
    }
}


