package com.dam.lic;

import static com.dam.lic.ServerValues.TO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.dam.lic.ui.main.SharedPrefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageActivity extends AppCompatActivity {
    SharedPrefs sharedPrefs;
    private static final String TAG = "PackageActivity";
    FirebaseAuth auth;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    Button btnTrimiteComanda, btnTrimitePrieten;
    EditText etNrColete, etInaltime, etLungime, etLatime,etGreutate,etTelExp,etTelDest,
            etNumeExpeditor, etOrasExpeditor, etAdresaExpeditor,
            etNumeDestinatar, etOrasDestinatar, etAdresaDestinatar;
    Spinner spnJudetExpeditor, spJudetDestinatar;
    CheckBox chkFragil, chkTermeni;
    RadioButton rbCash, rbCard;
    String destinatar ="nespecificat";
    String title, message;
    String cardActiv="nespecificat";
    List<Address> expAddressList;
    List<Address> desAddressList;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // onRestoreInstanceState(savedInstanceState);
        sharedPrefs = new SharedPrefs(PackageActivity.this);
        setContentView(R.layout.activity_package);
            btnTrimiteComanda = findViewById(R.id.buttonTrimiteComanda);
            btnTrimitePrieten = findViewById(R.id.buttonTrimitePrieten);

            etNrColete = findViewById(R.id.editTextNrColete);
            etInaltime = findViewById(R.id.editTextInaltimeTotala);
            etLungime = findViewById(R.id.editTextLungimeTotala);
            etLatime = findViewById(R.id.editTextLatimeTotala);
            etGreutate = findViewById(R.id.editTextGreutateTotala);
            etNumeExpeditor = findViewById(R.id.editTextNumeExpeditor);
            etTelExp = findViewById(R.id.nrTelefonExpeditor);
            etTelDest = findViewById(R.id.nrTelefonDestinatar);
            etOrasExpeditor = findViewById(R.id.editTextOrasExpeditor);
            etAdresaExpeditor = findViewById(R.id.editTextAdresaExpeditor);
            etNumeDestinatar = findViewById(R.id.editTextNumeDestinatar);
            etOrasDestinatar = findViewById(R.id.editTextOrasDestinatar);
            etAdresaDestinatar = findViewById(R.id.editTextAdresaDestinatar);

            spnJudetExpeditor = findViewById(R.id.spinnerJudetExpeditor);
            spJudetDestinatar = findViewById(R.id.spinnerJudetDestinatar);

            chkFragil = findViewById(R.id.checkBoxFragil);
            chkTermeni = findViewById(R.id.checkBoxTermeni);

            rbCash = findViewById(R.id.radioButtonCash);
            rbCard = findViewById(R.id.radioButtonCard);

            //fl = findViewById(R.id.fragmentContainer);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


            auth = FirebaseAuth.getInstance();
            String uid = auth.getCurrentUser().getUid();


            DatabaseReference reference = firebase.getReference("RegisteredUsers/" + uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String address = snapshot.child("address").getValue(String.class);
                    String county = snapshot.child("county").getValue(String.class);
                    String phone =snapshot.child("phone").getValue(String.class);
                    String loc = snapshot.child("loc").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    if(snapshot.child("cardActiv").exists()) {
                        cardActiv = snapshot.child("cardActiv").getValue(String.class);
                    }
                    etNumeExpeditor.setText(user);
                    etNumeExpeditor.setEnabled(false);
                    etTelExp.setText(phone);
                    ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) spnJudetExpeditor.getAdapter();
                    spnJudetExpeditor.setSelection(array_spinner.getPosition(county));
                    etOrasExpeditor.setText(loc);
                    etAdresaExpeditor.setText(address);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            btnTrimiteComanda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String nrColete, inaltime, lungime, latime,greutate, numeExp,telefExp, orasExp, adresaExp, numeDes, orasDes, adresaDes,telefDes;

                    boolean fragil = chkFragil.isChecked();
                    boolean plataCash = rbCash.isChecked();
                    String  judetExp = spnJudetExpeditor.getSelectedItem().toString();
                    String judetDes = spJudetDestinatar.getSelectedItem().toString();
                    nrColete = etNrColete.getText().toString();
                    inaltime = etInaltime.getText().toString();
                    lungime = etLungime.getText().toString();
                    latime = etLatime.getText().toString();
                    greutate = etGreutate.getText().toString();
                    numeExp = etNumeExpeditor.getText().toString();
                    telefExp = etTelExp.getText().toString();
                    orasExp = etOrasExpeditor.getText().toString();
                    adresaExp = etAdresaExpeditor.getText().toString();
                    numeDes = etNumeDestinatar.getText().toString();
                    telefDes = etTelDest.getText().toString();
                    orasDes = etOrasDestinatar.getText().toString();
                    adresaDes = etAdresaDestinatar.getText().toString();
                    Geocoder geocoder = new Geocoder(PackageActivity.this, Locale.getDefault());
                    try {
                         expAddressList = geocoder.getFromLocationName(judetExp+", "+orasExp+", "+adresaExp, 1);
                         desAddressList = geocoder.getFromLocationName(judetDes+", "+orasDes+", "+adresaDes, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    boolean ok =true;
                    if (TextUtils.isEmpty(nrColete)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti nr de colete!", Toast.LENGTH_LONG).show();
                        etNrColete.setError("Este obligatorie introducerea numarului de colete");
                        etNrColete.requestFocus();
                    }
                    if (TextUtils.isEmpty(inaltime)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti inaltimea!", Toast.LENGTH_LONG).show();
                        etInaltime.setError("Este obligatorie introducerea inaltimii");
                        etInaltime.requestFocus();
                    }
                    if (TextUtils.isEmpty(lungime)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti lungimea!", Toast.LENGTH_LONG).show();
                        etLungime.setError("Este obligatorie introducerea lungimii");
                        etLungime.requestFocus();
                    }
                    if (TextUtils.isEmpty(latime)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti latimea!", Toast.LENGTH_LONG).show();
                        etLatime.setError("Este obligatorie introducerea latimii");
                        etLatime.requestFocus();
                    }
                    if (TextUtils.isEmpty(greutate)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti greutatea!", Toast.LENGTH_LONG).show();
                        etGreutate.setError("Este obligatorie introducerea greutatii");
                        etGreutate.requestFocus();
                    }
                    if (TextUtils.isEmpty(numeExp)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti numele expeditorului!", Toast.LENGTH_LONG).show();
                        etNumeExpeditor.setError("Este obligatorie introducerea numelui expeditorului");
                        etNumeExpeditor.requestFocus();
                    }
                    if (TextUtils.isEmpty(telefExp)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti numarul de telefon al expeditorului!", Toast.LENGTH_LONG).show();
                        etTelExp.setError("Este obligatorie introducerea numarului de telefon al expeditorului");
                        etTelExp.requestFocus();
                    }
                    else if (telefExp.length()!=10) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti un numar de telefon valid al expeditorului!", Toast.LENGTH_LONG).show();
                        etTelExp.setError("Este obligatorie introducerea unui numar de telefon valid al expeditorului");
                        etTelExp.requestFocus();
                    }
                    if (TextUtils.isEmpty(orasExp)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti orasul expeditorului!", Toast.LENGTH_LONG).show();
                        etOrasExpeditor.setError("Este obligatorie introducerea orasul expeditorului");
                        etOrasExpeditor.requestFocus();
                    }
                    else if(spnJudetExpeditor.getSelectedItem().toString().equals("MUNICIPIUL BUCUREŞTI") && !(orasExp.matches("(?i)Sector [1-6]"))){
                        ok=false;
                        etOrasExpeditor.setError("Pentru Municipiul Bucuresti introduceti un sector de forma 'Sector x' cu x intre 1 si 6.");
                        etOrasExpeditor.requestFocus();
                    }
                    else if(!(spnJudetExpeditor.getSelectedItem().toString().equals("MUNICIPIUL BUCUREŞTI")) && !(orasExp.matches("[A-Za-z -]+")))
                    { ok=false;
                        etOrasExpeditor.setError("Este obligatorie introducerea unui oras valid al expeditorului");
                        etOrasExpeditor.requestFocus();
                    }
                    if (TextUtils.isEmpty(adresaExp)) {
                        ok = false;
                        Toast.makeText(PackageActivity.this, "Introduceti adresa expeditorului!", Toast.LENGTH_LONG).show();
                        etAdresaExpeditor.setError("Este obligatorie introducerea adresei expeditorului");
                        etAdresaExpeditor.requestFocus();
                    } else if(expAddressList.isEmpty()) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti o adresa a expeditorului valida!", Toast.LENGTH_LONG).show();
                        etAdresaExpeditor.setError("Este obligatorie introducerea unei adrese valide a expeditorului");
                        etAdresaExpeditor.requestFocus();
                    }
                    if (TextUtils.isEmpty(numeDes)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti numele destinatarului!", Toast.LENGTH_LONG).show();
                        etNumeDestinatar.setError("Este obligatorie introducerea numelui destinatarului");
                        etNumeDestinatar.requestFocus();
                    }
                    if (TextUtils.isEmpty(telefDes)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti numarul de telefon al destinatarului!", Toast.LENGTH_LONG).show();
                        etTelDest.setError("Este obligatorie introducerea numarului de telefon al destinatarului");
                        etTelDest.requestFocus();
                    } else if (telefDes.length()!=10) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti un numarul de telefon valid al destinatarului!", Toast.LENGTH_LONG).show();
                        etTelDest.setError("Este obligatorie introducerea unui numar de telefon valid al destinatarului");
                        etTelDest.requestFocus();
                    }
                    if (TextUtils.isEmpty(orasDes)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti orasul destinatarului!", Toast.LENGTH_LONG).show();
                        etOrasDestinatar.setError("Este obligatorie introducerea orasului destinatarului");
                        etOrasDestinatar.requestFocus();
                    }
                    else if(spJudetDestinatar.getSelectedItem().toString().equals("MUNICIPIUL BUCUREŞTI") && !(orasDes.matches("(?i)Sector [1-6]"))){
                        ok=false;
                        etOrasDestinatar.setError("Pentru Municipiul Bucuresti introduceti un sector de forma: 'Sector x' cu x intre 1 si 6.");
                        etOrasDestinatar.requestFocus();
                    }
                    else if(!(spJudetDestinatar.getSelectedItem().toString().equals("MUNICIPIUL BUCUREŞTI")) && !(orasDes.matches("[A-Za-z -]+")))
                    { ok=false;
                        etOrasDestinatar.setError("Este obligatorie introducerea unui oras valid al destinatarului");
                        etOrasDestinatar.requestFocus();
                    }
                    if (TextUtils.isEmpty(adresaDes)) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti adresa destinatarului!", Toast.LENGTH_LONG).show();
                        etAdresaDestinatar.setError("Este obligatorie introducerea adresa destinatarului");
                        etAdresaDestinatar.requestFocus();
                    } else if(desAddressList.isEmpty()) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Introduceti o adresa a destinatarului valida!", Toast.LENGTH_LONG).show();
                        etAdresaDestinatar.setError("Este obligatorie introducerea unei adrese valide a destinatarului");
                        etAdresaDestinatar.requestFocus();
                    }
                    if(rbCard.isChecked() && cardActiv.equals("nespecificat"))
                    {  ok=false;
                        Toast.makeText(PackageActivity.this, "Nu aveti nici un card de plati selectat din setari!", Toast.LENGTH_LONG).show();
                        rbCard.setError("Daca doriti sa efectuati plata cu cardul, este necesar sa aveti un card selectat pentru plati in setari.");
                        rbCard.requestFocus();

                    }
                    if (!chkTermeni.isChecked()) {
                        ok=false;
                        Toast.makeText(PackageActivity.this, "Acceptati termenii si conditiile!", Toast.LENGTH_LONG).show();
                        chkTermeni.setError("Este obligatorie aceptarea termenilor si conditiilor");
                        chkTermeni.requestFocus();
                    }
                    if(ok==true)
                    {
                       int nrColeteToInt = Integer.parseInt(nrColete);
                       float greutateToFloat = Float.parseFloat(greutate);
                       Address locExp= getLatLngFromAddress(judetExp+", "+orasExp+", "+adresaExp);
                       Address locDes = getLatLngFromAddress(judetDes+", "+orasDes+", "+adresaDes);
                       Double lngExp,latExp,lngDes,latDes;

                       lngExp = locExp.getLongitude();
                       latExp = locExp.getLatitude();
                       lngDes = locDes.getLongitude();
                       latDes = locDes.getLatitude();

                       float distance = Math.round(calculateDistance(locExp, locDes));
                       float price = (5+  Math.round(((distance*10*7.5)/100)) + +Math.round(greutateToFloat-1));

                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                        ReadWriteCommandDetails command = new ReadWriteCommandDetails(nrColeteToInt,inaltime,lungime,latime,greutate,fragil,numeExp,telefExp,judetExp,orasExp,adresaExp,numeDes,telefDes,judetDes,orasDes,adresaDes,uid,destinatar,"nespecificat",plataCash,Stare.CREATA, sdf.format(Calendar.getInstance().getTime()),latDes.toString(),lngDes.toString(),latExp.toString(),lngExp.toString(),price);
                        if(command.getRecipient().equals("nespecificat"))
                        {
                            command.setState(Stare.IN_ASTEPTARE_CURIER);

                            title ="Colet nou!";
                            message= numeExp+" doreste sa expedieze un colet";
                            sendNotifAll();
                        }
                        else
                        {
                            command.setState(Stare.IN_ASTEPTARE_ACCEPT);
                        }

                        DatabaseReference referenceCommand = firebase.getReference("Commands");
                        String uniqueId = referenceCommand.push().getKey();
                        referenceCommand.child(uniqueId).setValue(command).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    DatabaseReference referenceExpeditor = firebase.getReference("RegisteredUsers/" + uid + "/Comenzi");
                                    referenceExpeditor.child(uniqueId).setValue(1);

                                    String tokenPath = "RegisteredUsers/" + destinatar + "/token";
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference tokenRef = database.getReference(tokenPath);

                                    tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String token = dataSnapshot.getValue(String.class);
                                            if (token != null && !(token.equals("nespecificat"))) {

                                                title =numeDes+":Cerere noua de primire colet";
                                                message= numeExp+" doreste sa va trimita un colet";
                                                sendNotif(token);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Tratați eroarea în caz de eșec la citirea tokenului
                                        }
                                    });


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

                                    String positiveButtonText = "OK";
                                    int positiveButtonColor =  Color.parseColor("#5cb6f9");
                                    SpannableString spannableString = new SpannableString(positiveButtonText);
                                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                                    spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    new AlertDialog.Builder(PackageActivity.this)
                                            .setTitle("Aveti de platit:"+Math.round(price)+" lei")
                                            .setMessage("Preturile se calculeaza astfel:\nPret de baza:5 lei\nPret per kg(pt fiecare kg peste 1):1 leu\nGreutatea comenzii:"+greutateToFloat+" => +"+Math.round(greutateToFloat-1)+" lei\nPret  per kilometru:0.75 lei\nDistanta dintre locatii este "+distance+" km =>"+(float)((distance*10*7.5)/100)+"\n__________________\nTotal: "+price+" lei")
                                            .setPositiveButton(spannableString,new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                      if(rbCard.isChecked())
                                                                      {
                                                                          Toast.makeText(PackageActivity.this,"Plata se va realiza de pe cardul cu nr "+cardActiv,Toast.LENGTH_SHORT).show();
                                                                      }
                                                }
                                            })
                                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    Intent i = new Intent(PackageActivity.this, HomeActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            })
                                            .show();






                                }
                                else
                                {
                                    Toast.makeText(PackageActivity.this,"Comanda nu s-a putut inregistra! Va rugam reincercati!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });




                    }
                    else
                    {
                        Toast.makeText(PackageActivity.this,"Comanda nu s-a putut inregistra! Va rugam reincercati!",Toast.LENGTH_LONG).show();
                    }

                }

                private void sendNotifAll() {
                    ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),TO)).enqueue(new Callback<PushNotification>() {
                        @Override
                        public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                            if(response.isSuccessful())
                            {
                                Toast.makeText(PackageActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PackageActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PushNotification> call, Throwable t) {
                            Toast.makeText(PackageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            btnTrimitePrieten.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openNewFriendsTrimActivity();

                }
            });


        }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefs.getSharedPreferences().contains("nrColete")) {
            int nrColete = sharedPrefs.getSharedPreferences().getInt("nrColete", 0);
            if (nrColete != 0) {
                etNrColete.setText(String.valueOf(nrColete));
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("inaltime")) {
            String inaltime = sharedPrefs.getSharedPreferences().getString("inaltime", null);
            if (inaltime != null) {
                etInaltime.setText(String.valueOf(inaltime));
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("lungime")) {
            String lungime = sharedPrefs.getSharedPreferences().getString("lungime", null);
            if (lungime != null) {
                etLungime.setText(lungime);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("latime")) {
            String latime = sharedPrefs.getSharedPreferences().getString("latime", null);
            if (latime !=null) {
                etLatime.setText(latime);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("greutate")) {
            String greutate = sharedPrefs.getSharedPreferences().getString("greutate", null);
            if (greutate != null) {
                etGreutate.setText(greutate);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("fragil")) {
            boolean isFragil = sharedPrefs.getSharedPreferences().getBoolean("fragil", false);
            if (isFragil) {
                chkFragil.setChecked(true);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("telefon")) {
            String telefon = sharedPrefs.getSharedPreferences().getString("telefon", null);
            if (telefon != null) {
                etTelExp.setText(telefon);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("judet")) {
            String judet = sharedPrefs.getSharedPreferences().getString("judet", null);
            if (judet != null) {
                ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) spnJudetExpeditor.getAdapter();
                spnJudetExpeditor.setSelection(array_spinner.getPosition(judet));
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("loc")) {
            String loc = sharedPrefs.getSharedPreferences().getString("loc", null);
            if (loc != null) {
                etOrasExpeditor.setText(loc);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("adresa")) {
            String adresa = sharedPrefs.getSharedPreferences().getString("adresa", null);
            if (adresa != null) {
                etAdresaExpeditor.setText(adresa);
            }
        }

        if (sharedPrefs.getSharedPreferences().contains("card")) {
            boolean hasCard = sharedPrefs.getSharedPreferences().getBoolean("card", false);
            if (hasCard) {
                rbCard.setChecked(true);
            }
        }
        if (getIntent().hasExtra("uid")) {
            etNumeDestinatar.setEnabled(false);
            destinatar = getIntent().getStringExtra("uid");
            DatabaseReference reference2 = firebase.getReference("RegisteredUsers/" + destinatar);
            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String address = snapshot.child("address").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String county = snapshot.child("county").getValue(String.class);
                    String loc = snapshot.child("loc").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);

                    etNumeDestinatar.setText(user);
                    ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) spJudetDestinatar.getAdapter();
                    spJudetDestinatar.setSelection(array_spinner.getPosition(county));
                    etOrasDestinatar.setText(loc);
                    etTelDest.setText(phone);
                    etAdresaDestinatar.setText(address);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }


    private float calculateDistance(Address locExp, Address locDes) {
        float[] results = new float[1];
        Location.distanceBetween(locExp.getLatitude(), locExp.getLongitude(), locDes.getLatitude(), locDes.getLongitude(), results);
        return results[0] / 1000;
    }


    private Address getLatLngFromAddress(String s) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(s, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return location;
            } else {
                Toast.makeText(this, "Adresa nu a fost găsită.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la obținerea locației din adresă.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void sendNotif(String token) {
        ApiUtils.getClients().sendNotification(new PushNotification(new NotificationData(title,message),token)).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(PackageActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(PackageActivity.this,"Notification send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(PackageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private static float roundToTwoDecimals(float number) {
//        DecimalFormat decimalFormat = new DecimalFormat("#.##");
//        String roundedString = decimalFormat.format(number);
//        return Float.parseFloat(roundedString);
//    }
    private void openNewFriendsTrimActivity() {
        Intent i = new Intent(PackageActivity.this, FriendsTrimActivity.class);
        startActivity(i);
        finish();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPrefs.getSharedPreferences().edit();

        if (!TextUtils.isEmpty(etNrColete.getText().toString())) {
            editor.putInt("nrColete", Integer.parseInt(etNrColete.getText().toString()));
        }
        if (!TextUtils.isEmpty(etInaltime.getText().toString())) {
            editor.putString("inaltime", etInaltime.getText().toString());
        }
        if (!TextUtils.isEmpty(etLungime.getText().toString())) {
            editor.putString("lungime", etLungime.getText().toString());
        }
        if (!TextUtils.isEmpty(etLatime.getText().toString())) {
            editor.putString("latime", etLatime.getText().toString());
        }
        if (!TextUtils.isEmpty(etGreutate.getText().toString())) {
            editor.putString("greutate", etGreutate.getText().toString());
        }
        if (chkFragil.isChecked()) {
            editor.putBoolean("fragil", true);
        }

        if (!TextUtils.isEmpty(etTelExp.getText().toString())) {
            editor.putString("telefon", etTelExp.getText().toString());
        }

        editor.putString("judet", spnJudetExpeditor.getSelectedItem().toString());

        if (!TextUtils.isEmpty(etOrasExpeditor.getText().toString())) {
            editor.putString("loc", etOrasExpeditor.getText().toString());
        }

        if (!TextUtils.isEmpty(etAdresaExpeditor.getText().toString())) {
            editor.putString("adresa", etAdresaExpeditor.getText().toString());
        }

        if (rbCard.isChecked()) {
            editor.putBoolean("card", true);
        }

        editor.apply();
    }




}
