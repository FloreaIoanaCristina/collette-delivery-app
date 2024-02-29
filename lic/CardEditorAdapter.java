package com.dam.lic;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardEditorAdapter extends  RecyclerView.Adapter<CardEditorAdapter.CardEditorHolder> {
    private List<ReadWriteCardDetails> items = new ArrayList<>();
    private Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase;
    private int selectedItemPosition = -1;
    private String uid;
   // String cardActiv;


    public CardEditorAdapter(List<ReadWriteCardDetails> items, Context context, String uid) {
        this.items = items;
        this.context = context;
        firebase = FirebaseDatabase.getInstance();
        this.uid=uid;
    }

    @NonNull
    @Override
    public CardEditorAdapter.CardEditorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(context,"Ma apelez",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.card_editor_item, parent, false);
        CardEditorAdapter.CardEditorHolder viewHolder = new CardEditorAdapter.CardEditorHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardEditorAdapter.CardEditorHolder holder, int position) {
        int p = position;
        ReadWriteCardDetails item = items.get(p);
        holder.tvNr.setText(item.getNr());
        int luna = item.getExpirationMonth();
        if(luna<10)
        {
            holder.tvLuna.setText("0"+String.valueOf(luna)+" ");
        }
        else
        {
            holder.tvLuna.setText(String.valueOf(luna)+" ");
        }
        int an= item.getExpirationYear();
        an = an%100;
        holder.tvAn.setText("/ "+ String.valueOf(an));
        holder.tvNume.setText(item.getName());
        holder.tvCVC.setText(item.getCvc());
        holder.eye.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                holder.tvCVC.setInputType(InputType.TYPE_CLASS_TEXT);

                return true;
            }
        });
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nr = item.getNr();
                if(item.getActiv()==1)
                {
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/cardActiv").setValue("nespecificat");

                }
                FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/Carduri/" + nr).removeValue();
                items.clear();
                holder.adapter.notifyDataSetChanged();
            }
        });
    }

    private void notifyDataBase(ReadWriteCardDetails item) {

        if(item!=null) {
            firebase.getReference("RegisteredUsers/" + uid + "/cardActiv").setValue(item.getNr());
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CardEditorHolder extends RecyclerView.ViewHolder {
        public View item;
        public TextView tvNr, tvLuna,tvAn,tvNume,tvCVC;
        public CardEditorAdapter adapter;
        public Button btn;
        public ImageButton eye;

        public CardEditorHolder(View itemView) {
            super(itemView);

            item = itemView;
            tvNr = itemView.findViewById(R.id.textViewNrCard);
            tvLuna = itemView.findViewById(R.id.textViewLunaCard);
            tvAn = itemView.findViewById(R.id.textViewAnCard);
            tvNume = itemView.findViewById(R.id.textViewNumeCard);
            tvCVC = itemView.findViewById(R.id.textViewCvcCard);
            btn = itemView.findViewById(R.id.buttonStergeCard);
            eye = itemView.findViewById(R.id.eye);




        }

        public void linkAdapter(CardEditorAdapter adapter) {
            this.adapter = adapter;

        }


    }
}
