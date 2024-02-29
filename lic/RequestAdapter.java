package com.dam.lic;

import static com.dam.lic.ui.main.paginaPrieteni.listaPrieteni;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.lic.ui.main.paginaPrieteni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RequestAdapter extends  RecyclerView.Adapter<RequestAdapter.RequestHolder>{
    private List<ReadWriteRequestDetails> items = new ArrayList<>();
    private final Context context;
    DatabaseReference reference;
    FirebaseDatabase firebase ;
    String sender;
    String receiver;
    private final paginaPrieteni pagina;
    //public Button btn;

    public RequestAdapter(List<ReadWriteRequestDetails> items, Context context,paginaPrieteni paginaPrieteni) {
        this.items = items;
        this.context = context;
        this.pagina = paginaPrieteni;
        firebase = FirebaseDatabase.getInstance();
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
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(context,"Ma apelez",Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.request_item, parent, false);
        RequestHolder viewHolder = new RequestHolder(itemView);
        viewHolder.linkAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
       ReadWriteRequestDetails item = items.get(position);
       //Toast.makeText(this.context,"Am ajuns aici", Toast.LENGTH_SHORT).show();
         reference = firebase.getReference("RegisteredUsers/"+item.getSender());
       //  btn = holder.returnButton();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                    Picasso.get().load(data.child("img").getValue(String.class)) .transform(new CropCircleTransformation()).into(holder.ivPoza);
                    holder.tvCerere.setText(data.child("user").getValue(String.class)+ " vrea sa va imprieteniti!");

                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        int pozitie = holder.getAdapterPosition();
         sender = item.getSender();
         receiver = item.getReceiver();
        holder.btnDa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+sender+"/Prieteni").child(receiver).setValue(1);
                FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+receiver+"/Prieteni").child(sender).setValue(1);
                FirebaseDatabase.getInstance().getReference("Cereri").child(receiver+sender).removeValue();
                FirebaseDatabase.getInstance().getReference("Cereri").child(sender+receiver).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //product deleted
                                Toast.makeText(context, "Sunteti acum prieteni", Toast.LENGTH_SHORT).show();
                            }
                        });
//                items.remove(pozitie);
//                notifyItemRemoved(pozitie);
//                notifyItemRangeChanged(pozitie, items.size());
                items.clear();
                holder.adapter.notifyDataSetChanged();

                listaPrieteni.clear();
                pagina.notifyFriendsAdapter();

                // Actualizează RecyclerView-ul listaPrieteni
                //((paginaPrieteni) context).notifyFriendsAdapter();






            }
        });
        holder.btnNu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Cereri").child(sender+receiver).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //product deleted
                                Toast.makeText(context, "Cererea a fost stearsa", Toast.LENGTH_SHORT).show();
                            }
                        });

                items.clear();
                holder.adapter.notifyDataSetChanged();
//                items.remove(pozitie);
//                notifyItemRemoved(pozitie);
//                notifyItemRangeChanged(pozitie, items.size());



            }
        });


    }

    @Override
    public int getItemCount() {

        return items.size();
    }
    public void stergeCerere(String sender, String receiver, String msg)
    {


       // paginaPrieteni.listaCereriUser.clear

    }


    public class RequestHolder extends RecyclerView.ViewHolder {

        public View item;
        public ImageView ivPoza;
        public TextView tvCerere;
        public Button btnNu ,btnDa;
        public RequestAdapter adapter;
        public Boolean clickedDa=false, clickedNu=false;


        public RequestHolder(View itemView) {
            super(itemView);
            //Toast.makeText(context,"Am ajuns aici", Toast.LENGTH_SHORT).show();
            item = itemView;
            tvCerere = itemView.findViewById(R.id.tvCardViewCerere);
            ivPoza = itemView.findViewById(R.id.imageViewPozaCerere);
            btnNu = itemView.findViewById(R.id.buttonNu);
            btnDa = itemView.findViewById(R.id.buttonDa);

        }

        public void linkAdapter(RequestAdapter adapter)
        {
            this.adapter=adapter;


        }


    }

}
