package com.dam.lic.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.lic.CommandActivity;
import com.dam.lic.CourierHomeActivity;
import com.dam.lic.FriendsAdapter;
import com.dam.lic.HomeActivity;
import com.dam.lic.LoginActivity;
import com.dam.lic.PackageActivity;
import com.dam.lic.PackageAdapter;
import com.dam.lic.R;
import com.dam.lic.ReadWriteRequestDetails;
import com.dam.lic.RequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class paginaAcasa extends Fragment {
    Button adaugaComanda;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    RecyclerView recyclerViewPachete;
    public PackageAdapter packageAdapter;
    public static List<String> listaPachete = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pagina_acasa, container,false );

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewPachete = inflate.findViewById(R.id.recyclerViewPachete);
        recyclerViewPachete.setLayoutManager(manager);
        packageAdapter = new PackageAdapter(listaPachete,getContext());
        recyclerViewPachete.setAdapter(packageAdapter);

        adaugaComanda= inflate.findViewById(R.id.buttonAdaugaComandaNoua);
        adaugaComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PackageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        packageAdapter.setOnItemClickListener(new PackageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent i = new Intent(getContext(), CommandActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("id", packageAdapter.getItem(position));
                startActivity(i);
            }
        });
        return inflate;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
        getPachete();
    }

    private void getPachete() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/Comenzi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPachete.clear();
                notifyPackageAdapter();
                for (DataSnapshot data : snapshot.getChildren()) {
                    listaPachete.add(data.getKey());
                    packageAdapter.notifyDataSetChanged();
                }
                //notifyFriendsAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        notifyPackageAdapter();

    }
    private void notifyPackageAdapter() {
        PackageAdapter adapter = (PackageAdapter) recyclerViewPachete.getAdapter();
        adapter.notifyDataSetChanged();


    }


}
