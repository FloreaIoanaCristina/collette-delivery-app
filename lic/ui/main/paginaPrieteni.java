package com.dam.lic.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dam.lic.FriendsAdapter;
import com.dam.lic.HomeActivity;
import com.dam.lic.R;
import com.dam.lic.ReadWriteRequestDetails;
import com.dam.lic.RequestAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class paginaPrieteni extends Fragment{
    Context context = getContext();
    TextView tvCereri,refresh;
    EditText etCauta;
    Button btnCauta, btnAdauga, btnNU, btnDa, btnSterge;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    RecyclerView recyclerViewPrieteni, recyclerViewCereri;
   public RequestAdapter requestAdapter;
   public FriendsAdapter friendsAdapter;
   public static List<ReadWriteRequestDetails> listaCereriUser = new ArrayList<>();
   public static List<String> listaPrieteni = new ArrayList<>();
    List<String> listaCautari = new ArrayList<>();
    DatabaseReference databaseReference0 = FirebaseDatabase.getInstance().getReference("Cereri");
    //Button btn;
    String cauta;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pagina_prieteni, container, false);
        tvCereri = inflate.findViewById(R.id.textViewCereri);
        refresh =inflate.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
        //tvCereri.setVisibility(View.VISIBLE);
        etCauta = inflate.findViewById(R.id.editTextFriendName);
        btnAdauga = inflate.findViewById(R.id.buttonAddFriend);
        btnCauta = inflate.findViewById(R.id.buttonSearch);

        LinearLayoutManager manager1 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewPrieteni = inflate.findViewById(R.id.recyclerViewPrieteni);
        recyclerViewPrieteni.setLayoutManager(manager1);
        friendsAdapter = new FriendsAdapter(listaPrieteni,getContext());
        recyclerViewPrieteni.setAdapter(friendsAdapter);

        recyclerViewCereri = inflate.findViewById(R.id.recyclerViewCereri);
        requestAdapter = new RequestAdapter(listaCereriUser,getContext(),this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewCereri.setLayoutManager(manager);
        recyclerViewCereri.setAdapter(requestAdapter);

        return inflate;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }
    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {


        super.onResume();
        getCereri();
        getPrieteni();

        if(listaCereriUser.size()==0)
        {
            tvCereri.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStart() {

        super.onStart();


        btnAdauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Dialog dialog =  new Dialog(getContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                dialog.setContentView(R.layout.friends_dialog);
                dialog.setCancelable(true);
                EditText etUID = dialog.findViewById(R.id.editTextUIDFren);
                Button btnTrim = dialog.findViewById(R.id.dialog_button_pozitivFren);
                btnTrim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String textUID = etUID.getText().toString();
                        if (textUID.isEmpty()) {
                            etUID.setError("Introduceti UID-ul!");
                        } else if (!textUID.matches("[a-zA-Z0-9]+"))
                        { etUID.setError("Introduceti un UID valid!");
                        }
                        else{

                            DatabaseReference reference = firebase.getReference("RegisteredUsers/");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child(textUID).exists() && snapshot.child(textUID).child("curier").getValue().toString() == "false") {
                                        if (!(textUID.equals(firebaseAuth.getCurrentUser().getUid())) && !(snapshot.child(firebaseAuth.getCurrentUser().getUid()).child("Prieteni").child(textUID).exists())) {


                                            DatabaseReference referenceCereri = firebase.getReference("Cereri");
                                            ReadWriteRequestDetails requestDetails = new ReadWriteRequestDetails(firebaseAuth.getCurrentUser().getUid(), textUID);
                                            referenceCereri.child(firebaseAuth.getCurrentUser().getUid() + textUID).setValue(requestDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Toast.makeText(requireContext(), "Cererea de prietenie a fost trimisa utilizatorului " + snapshot.child(textUID).child("user").getValue().toString(), Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(requireContext(), "Sunteti deja prieten cu aceasta persoana", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                    } else {
                                        Toast.makeText(requireContext(), "Utilizatorul nu a fost gasit.Solicitati retrimiterea codului unic personal (UID)", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                        }
                    }
                });

                dialog.create();
                dialog.show();

            }

        });
        btnCauta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 cauta = etCauta.getText().toString();
                recyclerViewPrieteni.setAdapter(new FriendsAdapter(listaCautari,getContext() ));
                if(TextUtils.isEmpty(cauta)) {
                    cauta = "";
                }
                 FirebaseDatabase.getInstance().getReference("RegisteredUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaCautari.clear();
                        notifyFriendsAdapter();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (listaPrieteni.contains(data.getKey()) && data.child("user").getValue().toString().contains(cauta)) {
                                   listaCautari.add(data.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
              //  }
               // if(listaCautari!=null) {

                    notifyFriendsAdapter();
               // }
            }
        });

        etCauta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etCauta.getText()))
                { recyclerViewPrieteni.setAdapter(friendsAdapter);
                    notifyFriendsAdapter();

                }
            }
        });

    }
    public void getCereri(){

        String uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference0.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCereriUser.clear();
                notifyRequestAdapter();
                for (DataSnapshot data : snapshot.getChildren()) {

                    ReadWriteRequestDetails cerere = new ReadWriteRequestDetails(data.child("sender").getValue().toString(), data.child("receiver").getValue().toString());
                    if (cerere.getReceiver().equals(uid)) {
                        listaCereriUser.add(cerere);
                    }


                }
                if (listaCereriUser.size() > 0) {
                    tvCereri.setVisibility(View.VISIBLE);
                    recyclerViewCereri.setVisibility(View.VISIBLE);
                } else {
                    tvCereri.setVisibility(View.GONE);
                    recyclerViewCereri.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    public void getPrieteni()
    {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/Prieteni");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPrieteni.clear();
                notifyFriendsAdapter();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String snapshotPath = data.getRef().toString();
                    if (snapshotPath.contains("RegisteredUsers/" + uid + "/Prieteni")) {
                        listaPrieteni.add(data.getKey());
                        friendsAdapter.notifyDataSetChanged();
                    }
                }
                //notifyFriendsAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        notifyFriendsAdapter();

    }
    private void notifyRequestAdapter() {
        RequestAdapter adapter = (RequestAdapter) recyclerViewCereri.getAdapter();
        adapter.notifyDataSetChanged();
        notifyFriendsAdapter();

    }
    public void notifyFriendsAdapter() {
        FriendsAdapter adapter = (FriendsAdapter) recyclerViewPrieteni.getAdapter();
        adapter.notifyDataSetChanged();

    }

}
