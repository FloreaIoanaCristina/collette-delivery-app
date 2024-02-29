package com.dam.lic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.dam.lic.ui.main.CourierSectionsPagerAdapter;
import com.dam.lic.ui.main.SectionsPagerAdapter;
import com.dam.lic.ui.main.SharedPrefs;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourierHomeActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager2 viewPager;
    CourierSectionsPagerAdapter adapter;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String title, message;
    String numeCurier;
    String uid =firebaseAuth.getCurrentUser().getUid();
    LocationService locationService;
    SharedPrefs sharedPrefs;
    String lat, lng;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 10 && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationService.startLocationUpdates();
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_home);
         locationService = new LocationService(CourierHomeActivity.this,uid);
         locationService.startLocationUpdates();
          msg();
        sharedPrefs= new SharedPrefs(CourierHomeActivity.this);
        if(sharedPrefs.getDarkTheme() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPrefs.setDarkTheme(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPrefs.setDarkTheme(false);
        }

        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);

        adapter = new CourierSectionsPagerAdapter(this);
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
    public void onStart()
    {
        super.onStart();



    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyMe();
    }

    private void msg() {
        FirebaseMessaging.getInstance().subscribeToTopic("All");
    }
    private void notifyMe() {
        FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lat = snapshot.child("courierLat").getValue().toString();
                lng= snapshot.child("courierLng").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Map<String,ReadWriteCommandDetails> listaComenzi= new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Commands");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if(childSnapshot.child("courier").getValue().equals("nespecificat") && childSnapshot.child("state").getValue().equals(Stare.IN_ASTEPTARE_CURIER.toString())) {
                        String id = childSnapshot.getKey();
                        ReadWriteCommandDetails comanda = new ReadWriteCommandDetails(Integer.parseInt(childSnapshot.child("noPackages").getValue().toString()),
                                childSnapshot.child("height").getValue().toString(), childSnapshot.child("length").getValue().toString(), childSnapshot.child("width").getValue().toString(),childSnapshot.child("weight").getValue().toString(),
                                (Boolean) childSnapshot.child("fragile").getValue(),
                                childSnapshot.child("senderName").getValue().toString(), childSnapshot.child("senderPhone").getValue().toString(),
                                childSnapshot.child("senderCounty").getValue().toString(), childSnapshot.child("senderLoc").getValue().toString(), childSnapshot.child("senderAddress").getValue().toString(),
                                childSnapshot.child("recipientName").getValue().toString(),childSnapshot.child("recipientPhone").getValue().toString(),
                                childSnapshot.child("recipientCounty").getValue().toString(), childSnapshot.child("recipientLoc").getValue().toString(), childSnapshot.child("recipientAddress").getValue().toString(),
                                childSnapshot.child("sender").getValue().toString(), childSnapshot.child("recipient").getValue().toString(), childSnapshot.child("courier").getValue().toString(),
                                (Boolean) childSnapshot.child("cashPayment").getValue(),Stare.valueOf(childSnapshot.child("state").getValue().toString()),childSnapshot.child("date").getValue().toString(),childSnapshot.child("recipientLat").getValue().toString(),childSnapshot.child("recipientLng").getValue().toString(),childSnapshot.child("senderLat").getValue().toString(),childSnapshot.child("senderLng").getValue().toString(),Float.parseFloat(childSnapshot.child("price").getValue().toString()));
                        listaComenzi.put(id, comanda);
                    }

                }
                List<Map.Entry<String, ReadWriteCommandDetails>> sortedEntries = listaComenzi.entrySet().stream().filter(entry -> calculateDistance(Double.parseDouble(entry.getValue().getSenderLat()),Double.parseDouble(entry.getValue().getSenderLng()),Double.parseDouble(lat),Double.parseDouble(lng)) <50)
                        .sorted(Comparator.comparingDouble(entry -> calculateDistance(Double.parseDouble(entry.getValue().getRecipientLat()),Double.parseDouble(entry.getValue().getRecipientLng()),Double.parseDouble(lat),Double.parseDouble(lng)))).collect(Collectors.toList());
                Collections.reverse(sortedEntries);
                // Creați un nou map sortat
                Map<String, ReadWriteCommandDetails> sortedMap = new LinkedHashMap<>();
                for (Map.Entry<String, ReadWriteCommandDetails> entry : sortedEntries) {
                    sortedMap.put(entry.getKey(), entry.getValue());
                }

                if(sortedMap!=null)
                {
                    for(String com: sortedMap.keySet())
                    {
                        String sender = sortedMap.get(com).getSender();
                        String reciever = sortedMap.get(com).getRecipient();
                        String senderName = sortedMap.get(com).getSenderName();
                        String recvName = sortedMap.get(com).getRecipientName();

                        String positiveButtonText = "Accepta";
                        String negativeButtonText = "Respinge";
                        int positiveButtonColor =  Color.parseColor("#5cb6f9");
                        SpannableString spannableString = new SpannableString(positiveButtonText);
                        SpannableString spannableStringNegative = new SpannableString(negativeButtonText);
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                        spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableStringNegative.setSpan(foregroundColorSpan, 0, negativeButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        new AlertDialog.Builder(CourierHomeActivity.this)
                                .setTitle("Comanda noua:")
                                .setMessage(sortedMap.get(com).toStringCourier())
                                .setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                           firebase.getReference("RegisteredUsers/"+uid+"/cardActiv").addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                         if(snapshot.exists() && (snapshot.getValue().toString().equals("nespecificat")) && !(sortedMap.get(com).isCashPayment()))
                                                         {
                                                             Toast.makeText(CourierHomeActivity.this,"Nu se poate accepta comanda deoarece nu aveti selectat un card de incasari in setari!\n Pentru a accepta comenzi cu plata card este necesara introducerea unui card  pe care sa se vireze platile!",Toast.LENGTH_LONG).show();
                                                         }
                                                         else
                                                         {

                                                             firebase.getReference("RegisteredUsers/"+uid+"/Comenzi/"+com).setValue("1");
                                                             firebase.getReference("Commands/"+com+"/state").setValue(Stare.IN_CURS_DE_PRELUARE);
                                                             firebase.getReference("Commands/"+com+"/courier").setValue(uid);
                                                             Toast.makeText(CourierHomeActivity.this, "Ati acceptat o comanda", Toast.LENGTH_LONG).show();

                                                             String tokenPathSender = "RegisteredUsers/" +sender+ "/token";
                                                             String tokenPathReceiver = "RegisteredUsers/" +reciever+ "/token";
                                                             FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                             DatabaseReference tokenRefSender = database.getReference(tokenPathSender);
                                                             DatabaseReference tokenRefReceiver = database.getReference(tokenPathReceiver);
                                                             FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid).addValueEventListener(new ValueEventListener() {
                                                                 @Override
                                                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                     if (snapshot.child("user").exists()) {
                                                                         numeCurier = snapshot.child("user").getValue().toString();

                                                                     }
                                                                 }

                                                                 @Override
                                                                 public void onCancelled(@NonNull DatabaseError error) {

                                                                 }
                                                             });

                                                             tokenRefSender.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                 @Override
                                                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                                                     String token = dataSnapshot.getValue(String.class);
                                                                     if (token != null && !(token.equals("nespecificat"))) {

                                                                         title= senderName + ": Cerere de trimitere colet acceptata";
                                                                         message= numeCurier+ " a acceptat cererea dvs de livrare colet";
                                                                         sendNotif(token);

                                                                     }
                                                                 }

                                                                 @Override
                                                                 public void onCancelled(DatabaseError databaseError) {
                                                                     // Tratați eroarea în caz de eșec la citirea tokenului
                                                                 }
                                                             });

                                                             tokenRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                 @Override
                                                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                                                     String token = dataSnapshot.getValue(String.class);
                                                                     if (token != null && !(token.equals("nespecificat"))) {

                                                                         title= recvName+ ": Cerere de trimitere colet acceptata";
                                                                         message= numeCurier+ " a acceptat cererea dvs de livrare colet";
                                                                         sendNotif(token);

                                                                     }
                                                                 }

                                                                 @Override
                                                                 public void onCancelled(DatabaseError databaseError) {

                                                                 }
                                                             });
                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {

                                                     }
                                                 }
                                           );

                                    }

                                })
                                .setNegativeButton(spannableStringNegative, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(CourierHomeActivity.this, "Ati respins o comanda", Toast.LENGTH_LONG).show();



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

    private void sendNotif(String token) {
        ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),token)).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(CourierHomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CourierHomeActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(CourierHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        public  double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            final double EARTH_RADIUS = 6371; // Raza Pământului în kilometri

            // Convertiți latitudinea și longitudinea în radiani
            double lat1Rad = Math.toRadians(lat1);
            double lon1Rad = Math.toRadians(lon1);
            double lat2Rad = Math.toRadians(lat2);
            double lon2Rad = Math.toRadians(lon2);

            // Calculează diferența dintre latitudine și longitudine
            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;

            // Aplicați formula haversine
            double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                    Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            // Calculează distanța
            double distance = EARTH_RADIUS * c;

            return distance;
        }


}