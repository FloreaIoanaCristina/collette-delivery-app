package com.dam.lic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<ReadWriteCommentDetails> items =new ArrayList<>();
    private final Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase ;


    public  CommentAdapter(List<ReadWriteCommentDetails> items, Context context) {
        // this.items = new ArrayList<>();
        this.items = items;
        this.context = context;
        firebase = FirebaseDatabase.getInstance();
    }

    @Override
    public String toString() {
        return "CommentAdapter{" +
                "items=" + items +
                ", context=" + context +
                '}';
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(context,"Ma apelez",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.comment_item, parent, false);
        CommentAdapter.CommentHolder viewHolder = new CommentAdapter.CommentHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        ReadWriteCommentDetails item = items.get(position);
        holder.tvNume.setText(item.getUser());
        holder.tvComment.setText(item.getComment());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder {
        public View item;
        public TextView tvNume, tvComment;
        public CommentAdapter adapter;

        public CommentHolder(View itemView) {
            super(itemView);

            item = itemView;
            tvNume = itemView.findViewById(R.id.textViewNumeComentariu);
            tvComment = itemView.findViewById(R.id.textViewComentariu);



        }

        public void linkAdapter(CommentAdapter adapter)
        {
            this.adapter=adapter;

        }


    }
}
