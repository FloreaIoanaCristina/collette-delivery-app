package com.dam.lic;

import android.content.Context;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

public class CardAdapter extends  RecyclerView.Adapter<CardAdapter.CardHolder> {
    private List<ReadWriteCardDetails> items = new ArrayList<>();
    private Context context;
    FirebaseDatabase firebase;
    private int selectedItemPosition = -1;
    private String uid;
    String cardActiv;
    boolean touch=false;


    public CardAdapter(List<ReadWriteCardDetails> items, Context context, String uid) {
        this.items = items;
        this.context = context;
        firebase = FirebaseDatabase.getInstance();
        this.uid=uid;
    }

    @NonNull
    @Override
    public CardAdapter.CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(context,"Ma apelez",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.card_item, parent, false);
        CardAdapter.CardHolder viewHolder = new CardAdapter.CardHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardHolder holder, int position) {
        int p = position;
        ReadWriteCardDetails item = items.get(p);
        if(item.getActiv()==0)
        {
            holder.cb.setChecked(false);
        }
        else{
            holder.cb.setChecked(true);
        }
//        firebase.getReference("RegisteredUsers/"+uid+"/cardActiv").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.getValue()!=null) {
//                    cardActiv = snapshot.getValue().toString();
//                    if(item.getNr().equals(cardActiv))
//                    {
//                        holder.cb.setChecked(true);
//                        for(ReadWriteCardDetails card : items)
//                        {
//                            if(!(card.getNr().equals(item.getNr())))
//                            {
//
//                            }
//                        }
//                    }
//                    else{
//                        holder.cb.setChecked(false);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

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

        holder.eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    holder.tvCVC.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                return false;
            }
        });

        holder.cb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                  touch = true;
                return false;
            }
        });
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(touch==true)
                {

                if (holder.cb.isChecked()) {
                    for (ReadWriteCardDetails card : items) {
                        if(!(card.getNr().equals(item.getNr())))
                        {
                            card.setActiv(0);
                            firebase.getReference("RegisteredUsers/" + uid + "/Carduri/" + card.getNr()).child("activ").setValue(0);
                        }
                    }
                        item.setActiv(1);
                        firebase.getReference("RegisteredUsers/" + uid + "/Carduri/" + item.getNr()).child("activ").setValue(1);
                        firebase.getReference("RegisteredUsers/" + uid + "/cardActiv").setValue(item.getNr());
                    } else {
                        item.setActiv(0);
                        firebase.getReference("RegisteredUsers/" + uid + "/Carduri/" + item.getNr()).child("activ").setValue(0);
                        firebase.getReference("RegisteredUsers/" + uid + "/cardActiv").setValue("nespecificat");
                    }
                    touch=false;
                }


            }

        });

//        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//                    selectedItemPosition = p; // Actualizează poziția elementului selectat
//                    notifyDataBase(item); // Notifică adapterul despre schimbarea selecției pentru a actualiza vizualizarea
//                } else {
//                    selectedItemPosition = -1; // Deselectează elementul
//                    notifyDataBase(null);// Notifică adapterul despre schimbarea selecției pentru a actualiza vizualizarea
//                }
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CardHolder extends RecyclerView.ViewHolder {
        public View item;
        public TextView tvNr, tvLuna,tvAn,tvNume,tvCVC;
        public CardAdapter adapter;
        public CheckBox cb;
        public ImageButton eye;

        public CardHolder(View itemView) {
            super(itemView);

            item = itemView;
            tvNr = itemView.findViewById(R.id.textViewNrCard);
            tvLuna = itemView.findViewById(R.id.textViewLunaCard);
            tvAn = itemView.findViewById(R.id.textViewAnCard);
            tvNume = itemView.findViewById(R.id.textViewNumeCard);
            tvCVC = itemView.findViewById(R.id.textViewCvcCard);
            cb = itemView.findViewById(R.id.checkBoxCard);
            eye = itemView.findViewById(R.id.eye);




        }

        public void linkAdapter(CardAdapter adapter) {
            this.adapter = adapter;

        }


    }
}
