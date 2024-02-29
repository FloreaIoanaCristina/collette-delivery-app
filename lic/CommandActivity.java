package com.dam.lic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.FragmentContainer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class CommandActivity extends AppCompatActivity implements OnMapReadyCallback{

   // FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    // sdf.format(Calendar.getInstance().getTime())
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    Button btnMultiTask, btnSchimbaCurier;
    ImageButton dropDown;
    CardView cardView;
    TextView etNrComanda,etData,etNrColete, etInaltime, etLungime, etLatime,etGreutate,etTelExp,etTelDest,
            etNumeExpeditor, etOrasExpeditor, etAdresaExpeditor,etJudetExpeditor, etJudetDestinatar,
            etNumeDestinatar, etOrasDestinatar, etAdresaDestinatar, etPlata,etPret,etNumeCurier,etMasinaCurier,anuleaza;
    ImageView imgCurier, imgExpeditor,imgDestinatar;
    CheckBox chkFragil;
    RatingBar rbCurier;
    RecyclerView rvComentarii;
    String destinatar, expeditor,curier;
    String title,message;
    String mesaj="" , mesaj1="";
    Stare stare;
    SupportMapFragment supportMapFragment;
    String token_curier=null,token_expeditor=null,token_destinatar=null;
    TextView locCurier;
    FragmentContainerView fcv;
    LatLng location2,location3;

    CommentAdapter commentAdapter;
    List<ReadWriteCommentDetails> listaComentarii = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
                locCurier=findViewById(R.id.textViewLocCurier);
                fcv = findViewById(R.id.mapView);
               etNrComanda=findViewById(R.id.textViewNrCom);
                etData = findViewById(R.id.textViewDataCom);
                etNrColete= findViewById(R.id.textViewNrColeteCom);
                etInaltime= findViewById(R.id.textViewInaltimeCom);
                etLungime= findViewById(R.id.textViewLungimeCom);
                etLatime= findViewById(R.id.textViewLatimeCom);
                etGreutate = findViewById(R.id.textViewGreutateCom);
                etTelExp= findViewById(R.id.textViewTelefonExpCom);
                etTelDest= findViewById(R.id.textViewTelefonDesCom);
                etNumeExpeditor= findViewById(R.id.textViewNumeExpCom);
                etOrasExpeditor= findViewById(R.id.textViewOrasExpCom);
                etAdresaExpeditor= findViewById(R.id.textViewAdresaExpCom);
                etJudetExpeditor= findViewById(R.id.textViewJudetExpCom);
                etJudetDestinatar= findViewById(R.id.textViewJudetDesCom);
                etNumeDestinatar= findViewById(R.id.textViewNumeDesCom);
                etOrasDestinatar= findViewById(R.id.textViewOrasDesCom);
                etAdresaDestinatar= findViewById(R.id.textViewAdresaDesCom);
                etPlata= findViewById(R.id.textViewTipPlataCom);
                etPret = findViewById(R.id.textViewPretCom);
                etNumeCurier= findViewById(R.id.textViewNumeCurierCom);
                etMasinaCurier= findViewById(R.id.textViewMasinaCurierCom);
                imgCurier= findViewById(R.id.imageViewCurier);
                imgExpeditor= findViewById(R.id.imageViewExp);
                imgDestinatar= findViewById(R.id.imageViewDes);
                chkFragil= findViewById(R.id.checkBoxFragilCom);
                rbCurier= findViewById(R.id.ratingBarCurierCom);
                dropDown = findViewById(R.id.commentDrop);

                supportMapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                supportMapFragment.getMapAsync(this);

                rvComentarii= findViewById(R.id.rvComentarii);
                LinearLayoutManager manager1 = new LinearLayoutManager(CommandActivity.this,LinearLayoutManager.VERTICAL,false);
                rvComentarii.setLayoutManager(manager1);
                commentAdapter = new CommentAdapter(listaComentarii,CommandActivity.this);
                rvComentarii.setAdapter(commentAdapter);

                anuleaza = findViewById(R.id.textViewAnuleaza);


                btnMultiTask = findViewById(R.id.buttonPreiaLivreazaIncheieComanda);
                btnSchimbaCurier = findViewById(R.id.buttonRefuzaCurier);

                cardView = findViewById(R.id.cardViewCurier);




        if (getIntent().hasExtra("id")) {

            String comanda = getIntent().getStringExtra("id");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("curier").getValue().toString().equals("true")) {
                        cardView.setVisibility(View.GONE);
                        anuleaza.setVisibility(View.GONE);


                    } else {
                        btnMultiTask.setVisibility(View.GONE);
                    }
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Commands/" + comanda);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child("courier").getValue().equals("nespecificat")) {
                                    cardView.setVisibility(View.GONE);
                                    fcv.setVisibility(View.GONE);
                                    locCurier.setVisibility(View.GONE);
                                }
                                if (snapshot.child("state").getValue().equals(Stare.IN_CURS_DE_PRELUARE.toString())) {
                                    btnMultiTask.setText("PREIA COMANDA");
                                    anuleaza.setVisibility(View.VISIBLE);
                                    btnSchimbaCurier.setVisibility(View.VISIBLE);
                                    fcv.setVisibility(View.VISIBLE);
                                    locCurier.setVisibility(View.VISIBLE);

                                }
                                if (snapshot.child("state").getValue().equals(Stare.PRELUATA.toString())) {
                                    btnMultiTask.setText("INCEPE LIVRAREA");
                                    anuleaza.setVisibility(View.GONE);
                                    btnSchimbaCurier.setVisibility(View.GONE);
                                    fcv.setVisibility(View.VISIBLE);
                                    locCurier.setVisibility(View.VISIBLE);
                                }
                                if (snapshot.child("state").getValue().equals(Stare.IN_CURS_DE_LIVRARE.toString())) {
                                    btnMultiTask.setText("INCHEIE LIVRAREA");
                                    anuleaza.setVisibility(View.GONE);
                                    fcv.setVisibility(View.VISIBLE);
                                    btnSchimbaCurier.setVisibility(View.GONE);
                                    locCurier.setVisibility(View.VISIBLE);
                                }


                                etNrComanda.setText(comanda);
                                etData.setText(snapshot.child("date").getValue().toString());
                                etNrColete.setText(snapshot.child("noPackages").getValue().toString());
                                etInaltime.setText(snapshot.child("height").getValue().toString());
                                etLungime.setText(snapshot.child("length").getValue().toString());
                                etLatime.setText(snapshot.child("width").getValue().toString());
                                etGreutate.setText(snapshot.child("weight").getValue().toString());
                                if (snapshot.child("fragile").getValue().equals(true)) {
                                    chkFragil.setChecked(true);
                                }
                                etNumeExpeditor.setText(snapshot.child("senderName").getValue().toString());
                                etTelExp.setText(snapshot.child("senderPhone").getValue().toString());
                                etJudetExpeditor.setText(snapshot.child("senderCounty").getValue().toString());
                                etOrasExpeditor.setText(snapshot.child("senderLoc").getValue().toString());
                                etAdresaExpeditor.setText(snapshot.child("senderAddress").getValue().toString());
                                etNumeDestinatar.setText(snapshot.child("recipientName").getValue().toString());
                                etTelDest.setText(snapshot.child("recipientPhone").getValue().toString());
                                etJudetDestinatar.setText(snapshot.child("recipientCounty").getValue().toString());
                                etOrasDestinatar.setText(snapshot.child("recipientLoc").getValue().toString());
                                etAdresaDestinatar.setText(snapshot.child("recipientAddress").getValue().toString());

                                if (snapshot.child("cashPayment").getValue().equals(true)) {
                                    etPlata.setText("cash");
                                } else {
                                    etPlata.setText("card");
                                }
                                etPret.setText(snapshot.child("price").getValue().toString());
                                curier = snapshot.child("courier").getValue().toString();
                                destinatar = snapshot.child("recipient").getValue().toString();
                                expeditor = snapshot.child("sender").getValue().toString();


                                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + expeditor);
                                databaseReference2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String img = snapshot.child("img").getValue(String.class);
                                            if (img.equals("nespecificat")) {
                                                Picasso.get().load(R.drawable.poza_profil_generica).transform(new CropCircleTransformation()).into(imgExpeditor);
                                            } else {
                                                Picasso.get().load(img).transform(new CropCircleTransformation()).into(imgExpeditor);
                                            }
                                            if (!(snapshot.child("token").getValue().toString().equals("nespecificat")) && (!(snapshot.child("token").exists()))) {
                                                token_expeditor = snapshot.child("token").getValue().toString();
                                            }
                                        } else {
                                            imgExpeditor.setBackgroundResource(R.drawable.resource_package);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + destinatar);
                                databaseReference3.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String img = snapshot.child("img").getValue(String.class);
                                            if (img.equals("nespecificat")) {
                                                Picasso.get().load(R.drawable.poza_profil_generica).transform(new CropCircleTransformation()).into(imgDestinatar);
                                            } else {
                                                Picasso.get().load(img).transform(new CropCircleTransformation()).into(imgDestinatar);
                                            }
                                            if (!(snapshot.child("token").getValue().toString().equals("nespecificat")) && (!(snapshot.child("token").exists()))) {
                                                token_destinatar = snapshot.child("token").getValue().toString();
                                            }
                                        } else
                                            imgDestinatar.setBackgroundResource(R.drawable.resource_package);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + curier);
                                databaseReference4.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String img = snapshot.child("img").getValue(String.class);

                                            Picasso.get().load(img).transform(new CropCircleTransformation()).into(imgCurier);
                                            etNumeCurier.setText(snapshot.child("user").getValue(String.class));
                                            String ratingS = snapshot.child("rating").getValue().toString();
                                            String nrFeedbackS = snapshot.child("nrFeedback").getValue().toString();

                                            float rating = Float.parseFloat(ratingS);
                                            int nrFeedback = Integer.parseInt(nrFeedbackS);
                                            if (nrFeedback != 0) {
                                                double ratingPerFeedBack = rating / nrFeedback;
                                                rbCurier.setRating((float) ratingPerFeedBack);
                                                rbCurier.setIsIndicator(true);
                                            } else {
                                                rbCurier.setRating(5);
                                                rbCurier.setIsIndicator(true);
                                            }

                                            if (!(snapshot.child("token").getValue().toString().equals("nespecificat")) && (!(snapshot.child("token").exists()))) {
                                                token_curier = snapshot.child("token").getValue().toString();
                                            }
                                            etMasinaCurier.setText(snapshot.child("license").getValue(String.class) + ", " + snapshot.child("brand").getValue(String.class) + " " + snapshot.child("model").getValue(String.class) + ", Culoare: " + snapshot.child("color").getValue(String.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        }


                            @Override
                            public void onCancelled (@NonNull DatabaseError error){

                            }

                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnMultiTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (btnMultiTask.getText().equals("PREIA COMANDA")) {
                        mesaj = " a preluat comanda dvs";
                        mesaj1 = " a preluat comanda dvs";
                        stare = Stare.PRELUATA;

                    } else if (btnMultiTask.getText().equals("INCEPE LIVRAREA")) {
                        mesaj = " a inceput livrarea comenzii dvs";
                        mesaj1 = "  a inceput livrarea comenzii dvs";
                        stare = Stare.IN_CURS_DE_LIVRARE;
                    } else if (btnMultiTask.getText().equals("INCHEIE LIVRAREA")) {
                        if(etPlata.getText().equals("card"))
                        {
                            Toast.makeText(CommandActivity.this,"Ati primit pe cardul de incasari suma de "+etPret.getText()+" lei",Toast.LENGTH_SHORT).show();
                            mesaj1 = " a livrat comanda dvs. S-au preluat "+etPret.getText()+" lei de pe cardul dvs";
                        }
                        else
                        {
                            mesaj1 = " a livrat comanda dvs";
                        }
                        mesaj = " a livrat comanda dvs";
                        stare = Stare.LIVRATA;
                        FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + expeditor + "/Comenzi").child(comanda).removeValue();
                        FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + destinatar + "/Comenzi").child(comanda).removeValue();
                        FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + curier + "/Comenzi").child(comanda).removeValue();

                    }
                    if (stare != null) {
                        FirebaseDatabase.getInstance().getReference("Commands/" + comanda + "/state").setValue(stare);
                        FirebaseDatabase.getInstance().getReference("Commands/" + comanda + "/end").setValue(sdf.format(Calendar.getInstance().getTime()));
                        String tokenPathSender = "RegisteredUsers/" + expeditor + "/token";
                        String tokenPathReceiver = "RegisteredUsers/" + destinatar + "/token";
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference tokenRefSender = database.getReference(tokenPathSender);
                        DatabaseReference tokenRefReceiver = database.getReference(tokenPathReceiver);

                        tokenRefSender.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String token = dataSnapshot.getValue(String.class);
                                if (token != null && !(token.equals("nespecificat"))) {

                                    title = etNumeExpeditor.getText() + ": Update comanda";
                                    message = etNumeCurier.getText() + mesaj1;
                                    sendNotif(token);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        tokenRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String token = dataSnapshot.getValue(String.class);
                                if (token != null && !(token.equals("nespecificat"))) {

                                    title = etNumeDestinatar.getText() + ": Update comanda";
                                    message = etNumeCurier.getText() + mesaj;
                                    sendNotif(token);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    if (btnMultiTask.getText().equals("INCHEIE LIVRAREA"))

                    {
                        Intent i = new Intent(CommandActivity.this,CourierHomeActivity.class);
                        startActivity(i);
                        finish();

                    }

                }


            });
            Drawable leftDrawableDown = getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24);
            Drawable leftDrawableUp = getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24);
            dropDown.setBackground(leftDrawableDown);
            dropDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dropDown.getBackground().equals(leftDrawableDown)) {
                          rvComentarii.setVisibility(View.GONE);
                           dropDown.setBackground(leftDrawableUp);

                    } else {

                        rvComentarii.setVisibility(View.VISIBLE);
                        dropDown.setBackground(leftDrawableDown);
                    }
                }
            });

            anuleaza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                     DatabaseReference dRCom = FirebaseDatabase.getInstance().getReference("Commands").child(comanda);
                     DatabaseReference dRefExp =   FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + expeditor + "/Comenzi").child(comanda);
                     DatabaseReference dRefDes =   FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + destinatar + "/Comenzi").child(comanda);
                     DatabaseReference dRefCou =    FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + curier + "/Comenzi").child(comanda);
                     DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + uid);
                     databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("curier").getValue().toString().equals("true")) {

                                title = etNumeExpeditor.getText() + ": Un curier a anulat o  comanda. Se cauta un nou curier...";
                                message = etNumeCurier.getText() + "a anulat comanda dvs cu id-ul "+comanda;
                                if(token_expeditor!=null) {
                                    sendNotif(token_expeditor);
                                }
                                message = etNumeCurier.getText() + "a anulat comanda dvs cu id-ul "+comanda;
                                if(token_destinatar!=null) {
                                    sendNotif(token_destinatar);
                                }

                                dRCom.child("state").setValue(Stare.IN_ASTEPTARE_CURIER);
                                dRCom.child("courier").setValue("nespecificat");

                                Toast.makeText(CommandActivity.this,"Comanda a fost anulata",Toast.LENGTH_SHORT).show();
                                dRefCou.removeValue();
                                Intent i = new Intent(CommandActivity.this,CourierHomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {   Boolean des=false;

                                if(destinatar.equals(uid)) {
                                    title = etNumeExpeditor.getText() + ":Un destinatar a anulat o comanda!";
                                    message = etNumeDestinatar.getText() + "a anulat comanda dvs cu id-ul " + comanda;
                                    des=true;
                                if(token_expeditor!=null) {
                                    sendNotif(token_expeditor);
                                }
                                }

                                if(expeditor.equals(uid)) {
                                    title = etNumeDestinatar.getText() + ":Un expeditor a anulat o comanda!";
                                    message = etNumeExpeditor.getText() + "a anulat comanda dvs cu id-ul "+comanda;
                                    des=false;
                                    if(token_destinatar!=null) {
                                        sendNotif(token_destinatar);
                                    }
                                }

                                title = etNumeCurier.getText() + ": Un client a anulat o comanda!";
                                if(des=true) {
                                    message = etNumeDestinatar.getText() + "a anulat comanda dvs cu id-ul " + comanda;
                                }
                                else{
                                    message = etNumeExpeditor.getText() + "a anulat comanda dvs cu id-ul " + comanda;
                                }
                                if(token_curier!=null) {
                                    sendNotif(token_curier);
                                }
                                dRefExp.removeValue();
                                dRefDes.removeValue();
                                dRefCou.removeValue();
                                dRCom.removeValue();
                                Toast.makeText(CommandActivity.this,"Comanda a fost anulata",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(CommandActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            });
            btnSchimbaCurier.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DatabaseReference dRefCom = FirebaseDatabase.getInstance().getReference("Commands/" + comanda);
                    DatabaseReference dRefExp =   FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + expeditor + "/Comenzi/" + comanda);
                    DatabaseReference dRefDes =   FirebaseDatabase.getInstance().getReference("Commands/" + destinatar + "/Comenzi/" + comanda);
                    DatabaseReference dRefCou =    FirebaseDatabase.getInstance().getReference("Commands/" + curier + "/Comenzi/" + comanda);
                    Boolean des=false;

                    if(destinatar.equals(uid)) {
                        title = etNumeExpeditor.getText() + ":Un destinatar a refuzat un curier!Se cauta curier...";
                        message = etNumeDestinatar.getText() + "a refuzat curierul asociat comenzii dvs cu id-ul " + comanda;
                        des=true;
                        if(token_expeditor!=null) {
                            sendNotif(token_expeditor);
                        }
                    }

                    if(expeditor.equals(uid)) {
                        title = etNumeDestinatar.getText() + ":Un expeditor a refuzat un curier!Se cauta curier...";
                        message = etNumeExpeditor.getText() + "a refuzat curierul asociat comenzii dvs cu id-ul " + comanda;
                        des=false;
                        if(token_destinatar!=null) {
                            sendNotif(token_destinatar);
                        }
                    }

                    title = etNumeCurier.getText() + ": Un client a anulat o comanda!";
                    if(des=true) {
                        message = etNumeDestinatar.getText() + "a anulat comanda dvs cu id-ul " + comanda;
                    }
                    else{
                        message = etNumeExpeditor.getText() + "a anulat comanda dvs cu id-ul " + comanda;
                    }
                    if(token_curier!=null) {
                        sendNotif(token_curier);
                    }

                        dRefCom.child("state").setValue(Stare.IN_ASTEPTARE_CURIER);
                        dRefCom.child("courier").setValue("nespecificat");

                        Toast.makeText(CommandActivity.this, "Curierul a fost refuzat.Se cauta curier...", Toast.LENGTH_SHORT).show();
                        dRefCou.removeValue();
                        Intent i = new Intent(CommandActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getComentarii();

    }

    private void notifyCommentAdapter() {
        CommentAdapter adapter = (CommentAdapter) rvComentarii.getAdapter();
        adapter.notifyDataSetChanged();
    }
    public void getComentarii()
    {   //listaPrieteni = new ArrayList<>();
        listaComentarii.clear();
        String comanda = getIntent().getStringExtra("id");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Commands/"+comanda);
        databaseReference1.addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                         String curierul = snapshot.child("courier").getValue(String.class);
                                                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + curierul + "/Comentarii");
                                                         databaseReference.addValueEventListener(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                 for (DataSnapshot data : snapshot.getChildren()) {
                                                                     String nume = data.child("user").getValue(String.class);
                                                                     String comment = data.child("comment").getValue(String.class);
                                                                     listaComentarii.add(new ReadWriteCommentDetails(nume,comment));

                                                                 }
                                                                 notifyCommentAdapter();
                                                             }

                                                             @Override
                                                             public void onCancelled(@NonNull DatabaseError error) {

                                                             }

                                                         });
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {

                                                     }
                                                 });



    }

    private void sendNotif(String token) {
        ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),token)).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(CommandActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CommandActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(CommandActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        Drawable drawable = getResources().getDrawable(R.drawable.pin);
        Drawable drawableExp = getResources().getDrawable(R.drawable.exp);
        Drawable drawableDest = getResources().getDrawable(R.drawable.dest);

        BitmapDescriptor bitmapDescriptor = getMarkerIconFromDrawable(drawable);
        BitmapDescriptor bitmapDescriptorExp = getMarkerIconFromDrawable(drawableExp);
        BitmapDescriptor bitmapDescriptorDes = getMarkerIconFromDrawable(drawableDest);

        String comanda = getIntent().getStringExtra("id");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Commands/"+comanda);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotMare) {

                if(!snapshotMare.child("courier").getValue().toString().equals("nespecificat"))
                {
                    String curierid = snapshotMare.child("courier").getValue().toString();
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers/" + curierid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            googleMap.clear();
                            LatLng location2 = new LatLng(Double.parseDouble(snapshotMare.child("recipientLat").getValue().toString()),
                                    Double.parseDouble(snapshotMare.child("recipientLng").getValue().toString()));
                            googleMap.addMarker(new MarkerOptions().position(location2).title("Punct Destinatie").icon(bitmapDescriptorDes));
                            // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location2, 15));

                            LatLng location3 = new LatLng(Double.parseDouble(snapshotMare.child("senderLat").getValue().toString()),
                                    Double.parseDouble(snapshotMare.child("senderLng").getValue().toString()));
                            googleMap.addMarker(new MarkerOptions().position(location3).title("Punct Expediere").icon(bitmapDescriptorExp));
                            // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location2, 15));

                            LatLng location1;
                            if(snapshot.child("courierLat").exists() && snapshot.child("courierLng").exists()) {
                                location1 = new LatLng(Double.parseDouble(snapshot.child("courierLat").getValue().toString()),
                                        Double.parseDouble(snapshot.child("courierLng").getValue().toString()));
                                googleMap.addMarker(new MarkerOptions().position(location1).title("Locație curier").icon(bitmapDescriptor));
                               // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));


                            }else
                            {
                                location1 = new LatLng(40.712776, -74.005974);
                                googleMap.addMarker(new MarkerOptions().position(location1).title("Locație curier").icon(bitmapDescriptor));
                               // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));
                            }

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(location2);
                            builder.include(location3);
                            builder.include(location1);

                            // Obține limita hărții bazate pe pini
                            LatLngBounds bounds = builder.build();


                            int padding = calculatePadding(); // Spatiu adaugat in jurul piniilor vizibile
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);


                            googleMap.animateCamera(cu);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            LatLng location1 = new LatLng(40.712776, -74.005974);
                            googleMap.addMarker(new MarkerOptions().position(location1).title("Locație curier").icon(bitmapDescriptor));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));

                        }
                    });
                }
                else
                {
                    LatLng location1 = new LatLng(40.712776, -74.005974);
                    googleMap.addMarker(new MarkerOptions().position(location1).title("Locație curier").icon(bitmapDescriptor));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    LatLng location1 = new LatLng(40.712776, -74.005974);
                    googleMap.addMarker(new MarkerOptions().position(location1).title("Locație curier").icon(bitmapDescriptor));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));

                }
            });


    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {

            // Verificați dacă drawable-ul este nul
            if (drawable == null) {
                return null;
            }

            // Creați un Bitmap din Drawable
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            // Returnați un BitmapDescriptor din Bitmap
            return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
    private int calculatePadding() {
        // Obține dimensiunile ecranului
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Calculează dimensiunea minimă dintre lățime și înălțime
        int minDimension = Math.min(screenWidth, screenHeight);

        // Calculează padding-ul ca o fracțiune a dimensiunii minime
        int paddingFraction = 8; // Poți ajusta această valoare pentru a obține rezultatul dorit
        int padding = minDimension / paddingFraction;

        return padding;
    }

}