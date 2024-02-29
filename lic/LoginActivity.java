package com.dam.lic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.lic.ui.main.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail;
    EditText loginPassword;
    Button loginButton;
    TextView signInText;
    FirebaseAuth firebaseAuth;
    CheckBox rememberMe;
    SharedPrefs sharedPreferences;
    Boolean ok;
   ImageButton imageButton;


    String judet,loc,adresa,codPostal;
    Boolean curier;
    private static final String TAG="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences= new SharedPrefs(LoginActivity.this);
        if(sharedPreferences.getDarkTheme() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreferences.setDarkTheme(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.setDarkTheme(false);
        }

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signInText = findViewById(R.id.login_sign_up_text);

        imageButton = findViewById(R.id.password_eye_toggle);
        rememberMe = findViewById(R.id.checkBoxRememberMe);
        firebaseAuth = FirebaseAuth.getInstance();

        remember();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok=true;
                String textEmail = loginEmail.getText().toString();
                String textPassword = loginPassword.getText().toString();
                if (TextUtils.isEmpty(textEmail)) {
                    ok=false;
                    Toast.makeText(LoginActivity.this, "Introdu emailul!", Toast.LENGTH_SHORT).show();
                    loginEmail.setError("Este necesara introducerea emailului!");
                    loginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    ok=false;
                    Toast.makeText(LoginActivity.this, "Introdu un email valid!", Toast.LENGTH_SHORT).show();
                    loginEmail.setError("Este necesara introducerea unui email valid!");
                    loginEmail.requestFocus();
                }
                if (TextUtils.isEmpty(textPassword)) {
                    ok=false;
                    Toast.makeText(LoginActivity.this, "Introdu parola!", Toast.LENGTH_SHORT).show();
                    loginPassword.setError("Este necesara introducerea parolei!");
                    loginPassword.requestFocus();

                }
                if(ok==true) {
                    if (rememberMe.isChecked()) {
                        sharedPreferences.setRememberMeKey(true);
                    }
                    loginUser(textEmail, textPassword);
                }

            }
        });

        Drawable leftDrawableLock = getResources().getDrawable(R.drawable.ic_baseline_lock_24);
        Drawable leftDrawableUnlock = getResources().getDrawable(R.drawable.ic_baseline_lock_open_24);
        imageButton.setBackground(leftDrawableLock);
      imageButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              int cursorPosition = loginPassword.getSelectionStart();
              if(imageButton.getBackground().equals(leftDrawableLock)) {

                  loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                  imageButton.setBackground(leftDrawableUnlock);
              } else {

                  loginPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                  imageButton.setBackground(leftDrawableLock);
              }
              loginPassword.setSelection(cursorPosition);
          }
      });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                            Intent i = new Intent(LoginActivity.this, SignupActivity.class);

                            startActivity(i);

                            // close this activity

                            finish();

            }
        });
    }

    private void remember() {

        boolean rememberMeChecked = sharedPreferences.getRememberMeKey();
        if (rememberMeChecked && firebaseAuth.getCurrentUser() != null) {
            rememberMe.setChecked(true);
            conectare();
        }
    }

    private void loginUser(String textEmail,String textPassword)
    {
        firebaseAuth.signInWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    conectare();
                  }

                else{
                    Toast.makeText(LoginActivity.this, "Conectarea a esuat!Va rugam reincercati.", Toast.LENGTH_SHORT).show();
                   try{
                       throw task.getException();
                   }catch (FirebaseAuthInvalidUserException e) {
                       loginEmail.setError("Utilizatorul nu a fost gasit!");
                       loginEmail.requestFocus();
                       e.printStackTrace();
                   } catch (FirebaseAuthInvalidCredentialsException e) {
                       loginEmail.setError("Date de conectare invalide!");
                       loginEmail.requestFocus();
                       e.printStackTrace();
                   } catch (Exception e) {
                       Log.e(TAG,e.getMessage());
                       Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                   }

                }
            }
        });

    }

    private void conectare() {
        Toast.makeText(LoginActivity.this, "Conectat cu succes!", Toast.LENGTH_SHORT).show();
        String uid= firebaseAuth.getCurrentUser().getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/token");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful())
                {
                    Log.w(TAG,"Fetching FCM not succesful",task.getException());
                    return;
                }
                String token = task.getResult();
                 dr.setValue(token);

            }
        });
        DatabaseReference databaseReference0 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/curier");
        databaseReference0.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curier = snapshot.getValue(Boolean.class);
                // Toast.makeText(LoginActivity.this,curier.toString(),Toast.LENGTH_SHORT).show();

                if(curier==false)
                {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/county");
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/address");
                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/loc");
                    DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/postalCode");
                    // DatabaseReference attributeRef = databaseReference.child(uid);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            judet = dataSnapshot.getValue(String.class);
                            //Toast.makeText(LoginActivity.this,judet,Toast.LENGTH_SHORT).show();
                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    adresa = dataSnapshot.getValue(String.class);
                                    //Toast.makeText(LoginActivity.this,judet,Toast.LENGTH_SHORT).show();
                                    databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            loc = dataSnapshot.getValue(String.class);
                                            //Toast.makeText(LoginActivity.this,judet,Toast.LENGTH_SHORT).show();
                                            databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    codPostal = dataSnapshot.getValue(String.class);
                                                    //Toast.makeText(LoginActivity.this,judet,Toast.LENGTH_SHORT).show();

                                                    if(judet.equals("nespecificat") || adresa.equals("nespecificat") || loc.equals("nespecificat") ||codPostal.equals("nespecificat")) {

                                                        Intent i = new Intent(LoginActivity.this, OnboardingSupportActivity.class);
                                                        startActivity(i);
//                            // close this activity
                                                        finish();
                                                    }else{
                                                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                                        startActivity(i);
                                                        finish();

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    // handle error
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // handle error
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // handle error
                                }
                            });



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // handle error
                        }
                    });



                }
                else
                {  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/img");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String imag = dataSnapshot.getValue(String.class);

                            if(imag.equals("nespecificat"))
                            {
                                Intent i = new Intent(LoginActivity.this, OnboardingCourierActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Intent i = new Intent(LoginActivity.this, CourierHomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // handle error
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}

