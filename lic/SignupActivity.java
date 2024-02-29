package com.dam.lic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText etEmail,etUser,etPassword,etConfirm,etPhone;
    Button signInButton;
    TextView logInText, courierText;
    Boolean ok;
    private static final String TAG="SignupActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        logInText = findViewById(R.id.login_sign_up_text);
        etEmail=findViewById(R.id.signin_email);
        etUser = findViewById(R.id.signin_username);
        etPassword = findViewById(R.id.signin_password);
        etConfirm = findViewById(R.id.confirm_password);
        etPhone = findViewById(R.id.signin_telefon);
        courierText = findViewById(R.id.login_become_courier_text);

        signInButton = findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok=true;
                String email= etEmail.getText().toString();
                String user =etUser.getText().toString();
                String password = etPassword.getText().toString();
                String confirm = etConfirm.getText().toString();
                String phone = etPhone.getText().toString();


                if(TextUtils.isEmpty(email))
                {   ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti adresa de email!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Este obligatorie introducerea adresei de email");
                    etEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti o adresa de email valida!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Este obligatorie introducerea unei adrese valide de email");
                    etEmail.requestFocus();
                }
                if(TextUtils.isEmpty(user))
                {ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti numele de utilizator!",Toast.LENGTH_LONG).show();
                    etUser.setError("Este obligatorie introducerea numelui de utilizator");
                    etUser.requestFocus();
                }
                if(TextUtils.isEmpty(password))
                {ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti parola!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Este obligatorie introducerea unei parole");
                    etPassword.requestFocus();
                }
                else if(password.length()<8)
                {ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti o parola de cel putin 8 caractere!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Parola trebuie sa aibe cel putin 8 caractere");
                    etPassword.requestFocus();
                }
                 if(TextUtils.isEmpty(confirm))
                {ok=false;
                    Toast.makeText(SignupActivity.this,"Reintroduceti parola!",Toast.LENGTH_LONG).show();
                    etConfirm.setError("Este obligatorie confirmarea parolei");
                    etConfirm.requestFocus();
                }
                else if(!confirm.equals(password))
                { ok=false;
                    Toast.makeText(SignupActivity.this,"Campurile \"Parola\" si \"Confirma Parola\" nu sunt identice!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Parolele nu sunt identice");
                    etConfirm.setError("Parolele nu sunt identice");
                    etPassword.clearComposingText();
                    etConfirm.clearComposingText();
                    etPassword.requestFocus();
                }
                 if(TextUtils.isEmpty(phone))
                { ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti numarul de telefon!",Toast.LENGTH_LONG).show();
                    etPhone.setError("Este obligatorie introducerea numarului de telefon");
                    etPhone.requestFocus();
                }
                else if(phone.length()!=10)
                { ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti un numar de telefon valid!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Numarul de telefon trebuie sa aibe 10 cifre");
                    etEmail.requestFocus();
                }
                else if(!Patterns.PHONE.matcher(phone).matches())
                { ok=false;
                    Toast.makeText(SignupActivity.this,"Introduceti un numar de telefon valid!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Este obligatorie introducerea unui numar de telefon valid");
                    etEmail.requestFocus();
                }
                if(ok==true)
                {
                    registerUser(email,user,password,phone);
                }
            }
        });


        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SignupActivity.this, LoginActivity.class);

                startActivity(i);


                finish();

            }
        });

        courierText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SignupActivity.this, CourierSignupActivity.class);

                startActivity(i);


                finish();

            }
        });
    }



    private void registerUser(String email, String user, String password, String phone) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {   String judet = "nespecificat", localitate = "nespecificat", adresa = "nespecificat", codPostal = "nespecificat", img="nespecificat", token ="nespecificat" , cardActiv="nespecificat";
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(user,phone,email,judet,localitate,adresa,codPostal,img,token);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful())
                            {
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(SignupActivity.this,"Contul a fost creat cu succes!Verificati emailul",Toast.LENGTH_LONG).show();

                                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                               Toast.makeText(SignupActivity.this,"Contul nu s-a putut crea!Reincercati",Toast.LENGTH_LONG).show();
                           }

                        }
                    });
                    //Mail de verificare


                }
                else
                {
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException firebaseAuthInvalidCredentialsException){
                        etEmail.setError("Emailul nu e valid sau a fost deja utilizat pentru un alt cont!");
                        etEmail.requestFocus();
                    }catch(FirebaseAuthUserCollisionException firebaseAuthUserCollisionException)
                    {
                        etEmail.setError("Emailul a fost deja utilizat pentru un alt cont!");
                        etEmail.requestFocus();
                    }catch(Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}