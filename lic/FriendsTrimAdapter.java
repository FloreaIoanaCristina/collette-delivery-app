package com.dam.lic;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FriendsTrimAdapter extends  RecyclerView.Adapter<FriendsTrimAdapter.FriendsTrimHolder> {
    private List<String> items = new ArrayList<>();
    private final Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FriendsTrimAdapter(List<String> items, Context context) {
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
    public FriendsTrimAdapter.FriendsTrimHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.prieteni_trim_item, parent, false);
        FriendsTrimAdapter.FriendsTrimHolder viewHolder = new FriendsTrimAdapter.FriendsTrimHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsTrimHolder holder, int position) {
        String item = items.get(position);
        reference = firebase.getReference("RegisteredUsers/" + item);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tvNume.setText(snapshot.child("user").getValue(String.class));
                holder.tvAdresa.setText(snapshot.child("address").getValue(String.class) + ", " + snapshot.child("loc").getValue(String.class) + ", " + snapshot.child("county").getValue(String.class));
                Picasso.get().load(snapshot.child("img").getValue(String.class)).transform(new CropCircleTransformation()).into(holder.ivPoza);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(context, PackageActivity.class);
              //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              i.putExtra("uid", item);
              context.startActivity(i);
                if (context instanceof FriendsTrimActivity) {
                    FriendsTrimActivity activity = (FriendsTrimActivity) context;
                    activity.finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class FriendsTrimHolder extends RecyclerView.ViewHolder {
        //public View item;
        public ImageView ivPoza;
        public TextView tvNume, tvAdresa;
        public FriendsTrimAdapter adapter;


        public FriendsTrimHolder(View itemView) {
            super(itemView);
            //item = itemView;
            tvNume = itemView.findViewById(R.id.textViewNumePrTrim);
            tvAdresa = itemView.findViewById(R.id.textViewAdresaPrTrim);
            ivPoza = itemView.findViewById(R.id.imageViewPrietenTrim);

        }

        public void linkAdapter(FriendsTrimAdapter adapter) {
            this.adapter = adapter;

        }

    }
}