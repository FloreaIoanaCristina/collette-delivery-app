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
import android.widget.Spinner;
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

public class CourierSignupActivity extends AppCompatActivity {

    EditText etEmail,etUser,etPassword,etConfirm,etPhone,etMarca,etModel,etNr;
    Spinner etCuloare;
    Button signInButton;
    TextView logInText, memberText;
    Boolean ok;
    private static final String TAG="CourierSignupActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_signup);
        logInText = findViewById(R.id.login_sign_up_text_cr);
        etEmail=findViewById(R.id.signin_email_cr);
        etUser = findViewById(R.id.signin_username_cr);
        etPassword = findViewById(R.id.signin_password_cr);
        etConfirm = findViewById(R.id.confirm_password_cr);
        etPhone = findViewById(R.id.signin_telefon_cr);
        etMarca = findViewById(R.id.signin_marca_cr);
        etModel = findViewById(R.id.signin_model_cr);
        etNr = findViewById(R.id.signin_nr_cr);
        etCuloare = findViewById(R.id.signin_culoare_cr);
        memberText = findViewById(R.id.login_become_member_text_cr);

        signInButton = findViewById(R.id.signin_button_cr);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok=true;
                String email= etEmail.getText().toString();
                String user =etUser.getText().toString();
                String password = etPassword.getText().toString();
                String confirm = etConfirm.getText().toString();
                String phone = etPhone.getText().toString();
                String marca = etMarca.getText().toString();
                String model = etModel.getText().toString();
                String nr = etNr.getText().toString();
                String culoare= etCuloare.getSelectedItem().toString();


                if(TextUtils.isEmpty(email))
                {   ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti adresa de email!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Este obligatorie introducerea adresei de email");
                    etEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti o adresa de email valida!",Toast.LENGTH_LONG).show();
                    etEmail.setError("Este obligatorie introducerea unei adrese valide de email");
                    etEmail.requestFocus();
                }
                if(TextUtils.isEmpty(user))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti numele de utilizator!",Toast.LENGTH_LONG).show();
                    etUser.setError("Este obligatorie introducerea numelui de utilizator");
                    etUser.requestFocus();
                }
                 if(TextUtils.isEmpty(password))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti parola!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Este obligatorie introducerea unei parole");
                    etPassword.requestFocus();
                }
                else if(password.length()<8)
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti o parola de cel putin 8 caractere!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Parola trebuie sa aibe cel putin 8 caractere");
                    etPassword.requestFocus();
                }
                 if(TextUtils.isEmpty(confirm))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Reintroduceti parola!",Toast.LENGTH_LONG).show();
                    etConfirm.setError("Este obligatorie confirmarea parolei");
                    etConfirm.requestFocus();
                }
                else if(!confirm.equals(password))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Campurile \"Parola\" si \"Confirma Parola\" nu sunt identice!",Toast.LENGTH_LONG).show();
                    etPassword.setError("Parolele nu sunt identice");
                    etConfirm.setError("Parolele nu sunt identice");
                    etPassword.clearComposingText();
                    etConfirm.clearComposingText();
                    etPassword.requestFocus();
                }
                if(TextUtils.isEmpty(phone))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti numarul de telefon!",Toast.LENGTH_LONG).show();
                    etPhone.setError("Este obligatorie introducerea numarului de telefon");
                    etPhone.requestFocus();
                }
                else if(phone.length()!=10)
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti un numar de telefon valid!",Toast.LENGTH_LONG).show();
                    etPhone.setError("Numarul de telefon trebuie sa aibe 10 cifre");
                    etPhone.requestFocus();
                }
                else if(!Patterns.PHONE.matcher(phone).matches())
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti un numar de telefon valid!",Toast.LENGTH_LONG).show();
                    etPhone.setError("Este obligatorie introducerea unui numar de telefon valid");
                    etPhone.requestFocus();
                }
                 if(TextUtils.isEmpty(marca))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti marca masinii!",Toast.LENGTH_LONG).show();
                    etMarca.setError("Este obligatorie introducerea marcii masinii");
                    etMarca.requestFocus();
                }
                 if(TextUtils.isEmpty(model))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti modelul masinii!",Toast.LENGTH_LONG).show();
                    etModel.setError("Este obligatorie introducerea modelului masinii");
                    etModel.requestFocus();
                }
                 if(TextUtils.isEmpty(nr))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti numar de inmatriculare!",Toast.LENGTH_LONG).show();
                    etNr.setError("Este obligatorie introducerea numarului de inmatriculare");
                    etNr.requestFocus();
                }
                else if(nr.length()>7 || nr.length()<6)
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti un numar de inmatriculare valid!",Toast.LENGTH_LONG).show();
                    etNr.setError("Numarul de inmatriculare trebuie sa fie valid");
                    etNr.requestFocus();
                }
                if(culoare.equals("Culoare Masina"))
                {ok=false;
                    Toast.makeText(CourierSignupActivity.this,"Introduceti culoarea principala a masinii!",Toast.LENGTH_LONG).show();
                    etNr.setError("Este obligatorie introducerea culorii masinii");
                    etNr.requestFocus();
                }
               if(ok==true)
                {
                    registerCourier(email,user,password,phone,marca,model,nr,culoare);
                }
            }
        });


        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CourierSignupActivity.this, LoginActivity.class);

                startActivity(i);


                finish();

            }
        });

        memberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CourierSignupActivity.this, SignupActivity.class);

                startActivity(i);


                finish();

            }
        });
    }



    private void registerCourier(String email, String user, String password, String phone,String marca, String model,String nr, String culoare) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CourierSignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String img = "nespecificat", token = "nespecificat";
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    ReadWriteCourierDetails writeCourierDetails = new ReadWriteCourierDetails(user, phone, email, marca, model, nr, culoare, img, token);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeCourierDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(CourierSignupActivity.this, "Contul a fost creat cu succes!Verificati emailul", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(CourierSignupActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(CourierSignupActivity.this, "Contul nu s-a putut crea!Reincercati", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CourierSignupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}