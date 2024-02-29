package com.dam.lic;

import static com.dam.lic.ServerValues.TO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.lic.ui.main.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;
import com.dam.lic.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager2 viewPager;
    SectionsPagerAdapter adapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Commands");
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String title, message;
    SharedPrefs sharedPrefs;
    boolean isRatingBarRated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefs= new SharedPrefs(HomeActivity.this);
        if(sharedPrefs.getDarkTheme() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPrefs.setDarkTheme(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPrefs.setDarkTheme(false);
        }


        tabs=findViewById(R.id.tabsH);
        viewPager=findViewById(R.id.viewPager);

        adapter = new SectionsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabs.selectTab(tabs.getTabAt(position));
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor editor = sharedPrefs.getSharedPreferences().edit();
        editor.remove("nrColete");
        editor.remove("inaltime");
        editor.remove("lungime");
        editor.remove("latime");
        editor.remove("greutate");
        editor.remove("fragil");
        editor.remove("telefon");
        editor.remove("judet");
        editor.remove("loc");
        editor.remove("adresa");
        editor.remove("card");
        editor.apply();

        notifyMe();
        finishCom();



    }

    private void finishCom() {
        Map<String,ReadWriteCommandDetails> listaComenzi= new HashMap<>();

        String uid =firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Commands");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String id = childSnapshot.getKey();

                    ReadWriteCommandDetails comanda = new ReadWriteCommandDetails(Integer.parseInt(childSnapshot.child("noPackages").getValue().toString()),
                            childSnapshot.child("height").getValue().toString(),childSnapshot.child("length").getValue().toString(),childSnapshot.child("width").getValue().toString(),childSnapshot.child("weight").getValue().toString(),
                            (Boolean) childSnapshot.child("fragile").getValue(),
                            childSnapshot.child("senderName").getValue().toString(), childSnapshot.child("senderPhone").getValue().toString(),
                            childSnapshot.child("senderCounty").getValue().toString(), childSnapshot.child("senderLoc").getValue().toString(), childSnapshot.child("senderAddress").getValue().toString(),
                            childSnapshot.child("recipientName").getValue().toString(),childSnapshot.child("recipientPhone").getValue().toString(),
                            childSnapshot.child("recipientCounty").getValue().toString(), childSnapshot.child("recipientLoc").getValue().toString(), childSnapshot.child("recipientAddress").getValue().toString(),
                            childSnapshot.child("sender").getValue().toString(), childSnapshot.child("recipient").getValue().toString(), childSnapshot.child("courier").getValue().toString(),
                            (Boolean) childSnapshot.child("cashPayment").getValue(),Stare.valueOf(childSnapshot.child("state").getValue().toString()),childSnapshot.child("date").getValue().toString(),childSnapshot.child("recipientLat").getValue().toString(),childSnapshot.child("recipientLng").getValue().toString(),childSnapshot.child("senderLat").getValue().toString(),childSnapshot.child("senderLng").getValue().toString(),Float.parseFloat(childSnapshot.child("price").getValue().toString()));
                    listaComenzi.put(id,comanda);
                    //Calendar.getInstance().getTime();
                }
                Map<String,ReadWriteCommandDetails> listaComenziEu  = new HashMap<>();
                Map<String,ReadWriteCommandDetails> listaComenziEuInAsteptare = new HashMap<>();

                listaComenziEu = listaComenzi.entrySet().stream().filter(e->(e.getValue().getSender().equals(uid))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if(listaComenziEu!=null)
                {
                    listaComenziEuInAsteptare = listaComenziEu.entrySet().stream().filter(e->e.getValue().getState().equals(Stare.LIVRATA)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }

                if(listaComenziEuInAsteptare!=null)
                {
                    for(String com: listaComenziEuInAsteptare.keySet())
                    {  //String sender = listaComenziEuInAsteptare.get(com).getSender();
                        String senderName = listaComenziEuInAsteptare.get(com).getSenderName();
                        String courier = listaComenziEuInAsteptare.get(com).getCourier();
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+courier);
                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("user").exists()) {
                                    Dialog dialog = new Dialog(HomeActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
                                    dialog.setContentView(R.layout.feedback_dialog);
                                    dialog.setCancelable(false);
                                    EditText etComment = dialog.findViewById(R.id.editTextComentariu);
                                    RatingBar rbCurier = dialog.findViewById(R.id.dialog_ratingbar);
                                    Button btnPozitiv = dialog.findViewById(R.id.dialog_button_pozitiv);
                                    rbCurier.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                        @Override
                                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                            isRatingBarRated = true;
                                        }
                                    });
                                    TextView tvTtilu = dialog.findViewById(R.id.textViewTitlu);
                                    tvTtilu.setText("Doriti sa oferiti feedback pentru " + snapshot.child("user").getValue().toString() + "?");
                                    btnPozitiv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // firebase.getReference("RegisteredUsers/"+uid+"/Comenzi/"+com).setValue("1");
                                            firebase.getReference("Commands/" + com + "/state").setValue(Stare.INCHEIATA);
                                            int nrFeedback = Integer.parseInt(snapshot.child("nrFeedback").getValue().toString());
                                            float rating = Float.parseFloat(snapshot.child("rating").getValue().toString());
                                            if (isRatingBarRated == true) {
                                                if (nrFeedback == 0) {

                                                    rating = rbCurier.getRating();


                                                } else {


                                                    rating = rating + rbCurier.getRating();

                                                }
                                                nrFeedback++;
                                                firebase.getReference("RegisteredUsers/" + courier + "/nrFeedback").setValue(nrFeedback);
                                                firebase.getReference("RegisteredUsers/" + courier + "/rating").setValue(rating);
                                                String comentariu = String.valueOf(etComment.getText());
                                                if (!(TextUtils.isEmpty(comentariu))) {
                                                    String uniqueId = firebase.getReference("RegisteredUsers/" + courier + "/Comentarii").push().getKey();
                                                    firebase.getReference("RegisteredUsers/" + courier + "/Comentarii/" + uniqueId + "/user").setValue(senderName);
                                                    firebase.getReference("RegisteredUsers/" + courier + "/Comentarii/" + uniqueId + "/comment").setValue(comentariu);
                                                }
                                                Toast.makeText(HomeActivity.this, "Multumim pentru feedback-ul dumneavoastra!", Toast.LENGTH_LONG).show();
                                                firebase.getReference("Commands/" + com + "/state").setValue(Stare.INCHEIATA);
                                                dialog.dismiss();
                                                isRatingBarRated = false;

                                            } else {
                                                etComment.setError("Nu ati oferit rating!");
                                                etComment.requestFocus();
                                            }


                                        }

                                    });
                                    Button btnNegativ = dialog.findViewById(R.id.dialog_button_negativ);
                                    btnNegativ.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            firebase.getReference("Commands/" + com + "/state").setValue(Stare.INCHEIATA);
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.create();
                                    dialog.show();
                                }
                                else
                                {
                                    firebase.getReference("Commands/" + com + "/state").setValue(Stare.INCHEIATA);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void notifyMe() {

        Map<String,ReadWriteCommandDetails> listaComenzi= new HashMap<>();

        String uid =firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Commands");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String id = childSnapshot.getKey();

                    ReadWriteCommandDetails comanda = new ReadWriteCommandDetails(Integer.parseInt(childSnapshot.child("noPackages").getValue().toString()),
                          childSnapshot.child("height").getValue().toString(), childSnapshot.child("length").getValue().toString(), childSnapshot.child("width").getValue().toString(), childSnapshot.child("weight").getValue().toString(),
                            (Boolean) childSnapshot.child("fragile").getValue(),
                            childSnapshot.child("senderName").getValue().toString(), childSnapshot.child("senderPhone").getValue().toString(),
                            childSnapshot.child("senderCounty").getValue().toString(), childSnapshot.child("senderLoc").getValue().toString(), childSnapshot.child("senderAddress").getValue().toString(),
                            childSnapshot.child("recipientName").getValue().toString(),childSnapshot.child("recipientPhone").getValue().toString(),
                            childSnapshot.child("recipientCounty").getValue().toString(), childSnapshot.child("recipientLoc").getValue().toString(), childSnapshot.child("recipientAddress").getValue().toString(),
                            childSnapshot.child("sender").getValue().toString(), childSnapshot.child("recipient").getValue().toString(), childSnapshot.child("courier").getValue().toString(),
                            (Boolean) childSnapshot.child("cashPayment").getValue(),Stare.valueOf(childSnapshot.child("state").getValue().toString()),childSnapshot.child("date").getValue().toString(),childSnapshot.child("recipientLat").getValue().toString(),childSnapshot.child("recipientLng").getValue().toString(),childSnapshot.child("senderLat").getValue().toString(),childSnapshot.child("senderLng").getValue().toString(),Float.parseFloat(childSnapshot.child("price").getValue().toString()));
                    listaComenzi.put(id,comanda);
                    //Calendar.getInstance().getTime();
                }
                Map<String,ReadWriteCommandDetails> listaComenziEu  = new HashMap<>();
                Map<String,ReadWriteCommandDetails> listaComenziEuInAsteptare = new HashMap<>();

                listaComenziEu = listaComenzi.entrySet().stream().filter(e->e.getValue().getRecipient().equals(uid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if(listaComenziEu!=null)
                {
                    listaComenziEuInAsteptare = listaComenziEu.entrySet().stream().filter(e->e.getValue().getState().equals(Stare.IN_ASTEPTARE_ACCEPT)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }

                if(listaComenziEuInAsteptare!=null)
                {
                    for(String com: listaComenziEuInAsteptare.keySet())
                    {  String sender = listaComenziEuInAsteptare.get(com).getSender();
                       String senderName = listaComenziEuInAsteptare.get(com).getSenderName();
                       String recvName = listaComenziEuInAsteptare.get(com).getRecipientName();
                        String positiveButtonText = "Accepta";
                        String negativeButtonText = "Respinge";
                        int positiveButtonColor =  Color.parseColor("#5cb6f9");
                        SpannableString spannableString = new SpannableString(positiveButtonText);
                        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                        spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableStringNegative.setSpan(foregroundColorSpan, 0, negativeButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle(senderName+" doreste sa va trimita un pachet:")
                                .setMessage(listaComenziEuInAsteptare.get(com).toString())
                                .setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        firebase.getReference("RegisteredUsers/"+uid+"/Comenzi/"+com).setValue("1");
                                         firebase.getReference("Commands/"+com+"/state").setValue(Stare.IN_ASTEPTARE_CURIER);
                                                Toast.makeText(HomeActivity.this, "Ati acceptat o comanda", Toast.LENGTH_LONG).show();

                                        String tokenPath = "RegisteredUsers/" +sender+ "/token";
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference tokenRef = database.getReference(tokenPath);

                                        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String token = dataSnapshot.getValue(String.class);
                                                if (token != null && !(token.equals("nespecificat"))) {

                                                    title=senderName + ": Cerere de trimitere colet acceptata";
                                                    message=recvName + " a acceptat cererea dvs de trimitere colet";
                                                    sendNotif(token);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Tratați eroarea în caz de eșec la citirea tokenului
                                            }
                                        });

                                        title ="Colet nou!";
                                        message= senderName+" doreste sa expedieze un colet";
                                        sendNotifAll();

                                    }

                                })
                                .setNegativeButton(spannableStringNegative, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Toast.makeText(HomeActivity.this,"Comanda a fost anulata",Toast.LENGTH_LONG).show();
                                        firebase.getReference("Commands/"+com).removeValue();
                                        firebase.getReference("RegisteredUsers/" +sender+ "/Comenzi/"+com).removeValue();
                                        String tokenPath = "RegisteredUsers/" +sender+ "/token";
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference tokenRef = database.getReference(tokenPath);

                                        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String token = dataSnapshot.getValue(String.class);
                                                if (token != null && !(token.equals("nespecificat"))) {

                                                    title=senderName + ": Cerere de trimitere colet respinsa";
                                                    message=recvName + " a respins cererea dvs de trimitere colet";
                                                    sendNotif(token);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Tratați eroarea în caz de eșec la citirea tokenului
                                            }
                                        });


                                    }


                                })
                                .setIcon(R.drawable.resource_package)
                                .create()
                                .show();
                    }
                }
                    }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotifAll() {
        ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),TO)).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(HomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(HomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendNotif(String token) {
        ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),token)).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(HomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(HomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}