package com.dam.lic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PackageAdapter extends  RecyclerView.Adapter<PackageAdapter.PackageHolder>{
    private List<String> items = new ArrayList<>();
    private final Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase ;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public String getItem(int position)
    {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        } else {
            return null; // Sau un alt comportament dorit pentru poziții invalide
        }
    }

    public PackageAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
        firebase = FirebaseDatabase.getInstance();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "RequestAdapter{" +
                "items=" + items +
                ", context=" + context +
                '}';
    }

    @NonNull
    @Override
    public PackageAdapter.PackageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.package_item, parent, false);
        PackageAdapter.PackageHolder viewHolder = new PackageAdapter.PackageHolder(itemView);
        viewHolder.linkAdapter(this);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapter.PackageHolder holder, int position) {
        String item = items.get(position);
        reference = firebase.getReference("Commands/"+item);
        // Toast.makeText(this.context,"Am ajuns aici", Toast.LENGTH_SHORT).show();
        holder.cod.setText(item);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    holder.adresaDest.setText(snapshot.child("recipientCounty").getValue(String.class) + ", " + snapshot.child("recipientLoc").getValue(String.class) + ", " + snapshot.child("recipientAddress").getValue(String.class));
                    holder.adresaExp.setText(snapshot.child("senderCounty").getValue(String.class) + ", " + snapshot.child("senderLoc").getValue(String.class) + ", " + snapshot.child("senderAddress").getValue(String.class));
                    holder.numeDest.setText(snapshot.child("recipientName").getValue(String.class));
                    holder.numeExp.setText(snapshot.child("senderName").getValue(String.class));
                    holder.stare.setText(snapshot.child("state").getValue(String.class));
                }
                //Picasso.get().load(snapshot.child("img").getValue(String.class)) .transform(new CropCircleTransformation()).into(holder.ivPoza);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {

        return items.size();
    }


    public class PackageHolder extends RecyclerView.ViewHolder {

        public View item;
        public TextView cod,stare,adresaDest,adresaExp,numeDest,numeExp;
        public PackageAdapter adapter;


        public PackageHolder(View itemView) {
            super(itemView);
            item = itemView;
            cod = itemView.findViewById(R.id.codComanda);
            numeDest = itemView.findViewById(R.id.numeDestinatar);
            numeExp = itemView.findViewById(R.id.numeExpeditor);
            adresaDest = itemView.findViewById(R.id.adresaDestinatar);
            adresaExp = itemView.findViewById(R.id.adresaExpediere);
            stare = itemView.findViewById(R.id.stareComanda);

        }

        public void linkAdapter(PackageAdapter adapter)
        {
            this.adapter=adapter;


        }


    }
}
