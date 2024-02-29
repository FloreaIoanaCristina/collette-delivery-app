package com.dam.lic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsTrimActivity extends AppCompatActivity {

    Button btnCauta;
    EditText etCauta;
    String cauta;
    RecyclerView recyclerViewPrieteniTrim;
    FriendsTrimAdapter friendsAdapter;
    public static List<String> listaPrieteni2 = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid =auth.getCurrentUser().getUid();
    //private FriendsTrimAdapter.OnItemClickListener itemSelectListener;
    List<String> listaCautari = new ArrayList<>();

        @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pagina_trimite_prieten);
            recyclerViewPrieteniTrim = findViewById(R.id.recyclerViewPrieteniTrim);
            LinearLayoutManager managerlay = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerViewPrieteniTrim.setLayoutManager(managerlay);
            friendsAdapter = new FriendsTrimAdapter(listaPrieteni2, this);
            recyclerViewPrieteniTrim.setAdapter(friendsAdapter);

            btnCauta = findViewById(R.id.buttonSearchTrim);
            etCauta = findViewById(R.id.editTextFriendNameTrim);
            btnCauta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cauta = etCauta.getText().toString();
                    recyclerViewPrieteniTrim.setAdapter(new FriendsTrimAdapter(listaCautari, FriendsTrimActivity.this));
                    if (TextUtils.isEmpty(cauta)) {
                        cauta = "";
                    }
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listaCautari.clear();
                            notifyFriendsAdapter();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (listaPrieteni2.contains(data.getKey()) && data.child("user").getValue().toString().contains(cauta)) {
                                    listaCautari.add(data.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    // }


                    notifyFriendsAdapter();
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
                { recyclerViewPrieteniTrim.setAdapter(friendsAdapter);
                    notifyFriendsAdapter();

                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getPrieteni();



    }

    public void getPrieteni()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/Prieteni");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPrieteni2.clear();
                notifyFriendsAdapter();
                for (DataSnapshot data : snapshot.getChildren()) {
                    listaPrieteni2.add(data.getKey());
                    friendsAdapter.notifyDataSetChanged();
                }

                if(listaPrieteni2.size()==0)
                {
                    closeActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        notifyFriendsAdapter();


    }
    public void notifyFriendsAdapter() {
        FriendsTrimAdapter adapter = (FriendsTrimAdapter)  recyclerViewPrieteniTrim.getAdapter();
        adapter.notifyDataSetChanged();

    }
    public void closeActivity() {

        Toast.makeText(this,"Nu aveti nici un prieten adaugat!",Toast.LENGTH_LONG).show();
        Intent i = new Intent(FriendsTrimActivity.this, PackageActivity.class);
       // i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();


    }

}
