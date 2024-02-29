package com.dam.lic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FriendsAdapter extends  RecyclerView.Adapter<FriendsAdapter.FriendsHolder>{
    private List<String> items =new ArrayList<>();
    private final Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase ;


    public  FriendsAdapter(List<String> items, Context context) {
       // this.items = new ArrayList<>();
        this.items = items;
        this.context = context;
        firebase = FirebaseDatabase.getInstance();
    }

    @Override
    public String toString() {
        return "FriendsAdapter{" +
                "items=" + items +
                ", context=" + context +
                '}';
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(context,"Ma apelez",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.prieteni_item, parent, false);
        FriendsAdapter.FriendsHolder viewHolder = new FriendsAdapter.FriendsHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.FriendsHolder holder, int position) {
        String item = items.get(position);
        //FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
         reference = firebase.getReference("RegisteredUsers/"+item);
       // Toast.makeText(this.context,"Am ajuns aici", Toast.LENGTH_SHORT).show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tvNume.setText(snapshot.child("user").getValue(String.class));
                holder.tvAdresa.setText(snapshot.child("address").getValue(String.class)+", "+ snapshot.child("loc").getValue(String.class)+", "+snapshot.child("county").getValue(String.class));
                Picasso.get().load(snapshot.child("img").getValue(String.class)).transform(new CropCircleTransformation()).into(holder.ivPoza);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btnSterge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item;
                FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Prieteni").child(id).removeValue();
                FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+id).child("Prieteni").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class FriendsHolder extends RecyclerView.ViewHolder {
        public View item;
        public ImageView ivPoza;
        public TextView tvNume, tvAdresa;
        public Button btnSterge;
        public FriendsAdapter adapter;

        public FriendsHolder(View itemView) {
            super(itemView);
            //Toast.makeText(context,"Am ajuns aici", Toast.LENGTH_SHORT).show();
            item = itemView;
            tvNume = itemView.findViewById(R.id.textViewNumePr);
            tvAdresa= itemView.findViewById(R.id.textViewAdresaPr);
            ivPoza = itemView.findViewById(R.id.imageViewPrieten);
            btnSterge = itemView.findViewById(R.id.buttonStergePrieten);


        }

        public void linkAdapter(FriendsAdapter adapter)
        {
            this.adapter=adapter;

        }


    }

}