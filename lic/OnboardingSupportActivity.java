package com.dam.lic;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.dam.lic.ui.main.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class OnboardingSupportActivity extends AppCompatActivity {
    EditText etLocalitate, etAdresa, etCodPostal;
    Spinner spJudete;
    ImageButton imgBtnProfil, roteste;
    ImageView imgVHand;
    Button btnConfirma;
    AnimationDrawable animationDrawable;
    TextView textReminder;
    private Target loadtarget;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int  rotationAngle = 0;
    StorageReference storageReference ;
    String storagePath="UsersProfilePictures/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    String[] cameraPermissions;
    String[] storagePermissions;

    Uri image_uri;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+user.getUid()+"/img");



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_support);

        etLocalitate= findViewById(R.id.editTextLocalitate);
        etAdresa = findViewById(R.id.editTextAdresa);
        etCodPostal=findViewById(R.id.editTextCodPostal);
        spJudete= findViewById(R.id.spinnerJudete);
        imgBtnProfil= findViewById(R.id.imageButton);
        imgVHand=findViewById(R.id.hand_image_view);
        btnConfirma= findViewById(R.id.buttonConfirma);
        textReminder=findViewById(R.id.textViewReminder);
        roteste = findViewById(R.id.imageButtonRotesc);
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        if(sharedPrefs.getDarkTheme() == true)
        {
            roteste.setBackground(getResources().getDrawable(R.drawable.ic_baseline_rotate_90_degrees_cwhite_24));
        }
        else
        {
            roteste.setBackground(getResources().getDrawable(R.drawable.ic_baseline_rotate_90_degrees_cw_24));
        }
        animationDrawable=new AnimationDrawable();

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storageReference= FirebaseStorage.getInstance().getReference();

        loadButton();

        imgBtnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showImageDialog();
               // loadButton();
            }

        });


        animationDrawable=(AnimationDrawable) imgVHand.getBackground();


        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textLocalitate=etLocalitate.getText().toString();
                String textAdresa=etAdresa.getText().toString();
                String textCodPostal=etCodPostal.getText().toString();
                String textJudet=spJudete.getSelectedItem().toString();
                AlertDialog alert = null;

                String positiveButtonText = "OK";
                int positiveButtonColor =  Color.parseColor("#5cb6f9");
                SpannableString spannableString = new SpannableString(positiveButtonText);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(positiveButtonColor);
                spannableString.setSpan(foregroundColorSpan, 0, positiveButtonText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (TextUtils.isEmpty(textLocalitate) ) {
                    alert = new AlertDialog.Builder(OnboardingSupportActivity.this).create();
                    alert.setTitle("AVERTIZARE");

                    alert.setMessage("Nu ati adaugat o localitate");

                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, spannableString,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                }
                else if(TextUtils.isEmpty(textAdresa)){
                    alert = new AlertDialog.Builder(OnboardingSupportActivity.this).create();
                    alert.setTitle("AVERTIZARE");

                    alert.setMessage("Nu ati adaugat o adresa");

                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, spannableString,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                }
                else if(TextUtils.isEmpty(textCodPostal) || textCodPostal.length()!=6)
                {
                    alert = new AlertDialog.Builder(OnboardingSupportActivity.this).create();
                    alert.setTitle("AVERTIZARE");

                    alert.setMessage("Nu ati adaugat un cod postal valid");

                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, spannableString,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user.getUid()).child("address").setValue(textAdresa);
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user.getUid()).child("county").setValue(textJudet);
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user.getUid()).child("loc").setValue(textLocalitate);
                    FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user.getUid()).child("postalCode").setValue(textCodPostal);

                    Intent i = new Intent(OnboardingSupportActivity.this, HomeActivity.class);
                    startActivity(i);
                }



            }
        });
        roteste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image_uri!=null)
                {
                    rotateImage(image_uri);
                }
            }
        });

        textReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardingSupportActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        animationDrawable.start();
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 1)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted)
                    {
                        pickFromCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "Va rugam permiteti aplicatiei accesul la camera si spatiu de stocare!", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(this,"Eroare",Toast.LENGTH_SHORT).show();
                }
            }
            case STORAGE_REQUEST_CODE:
            { if (grantResults.length > 0)
            {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if(writeStorageAccepted)
                {
                    pickFromGallery();
                }
                else
                {
                    Toast.makeText(this, "Va rugam permiteti aplicatiei accesul la camera!", Toast.LENGTH_SHORT).show();
                }
            }else
            {
                Toast.makeText(this,"Eroare",Toast.LENGTH_SHORT).show();
            }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            rotationAngle=0;
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                image_uri=data.getData();
                 uploadProfilePhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {


                uploadProfilePhoto(image_uri);
            }

        }
    }
    private void rotateImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            rotationAngle += 90;
            rotationAngle = rotationAngle % 360; // Asigură că unghiul de rotație rămâne între 0 și 359

            // Rotirea imaginii cu unghiul calculat
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // Salvarea imaginii rotite într-un fișier temporar
            String tempFilePath = getExternalCacheDir() + "/temp_image.jpg";
            FileOutputStream outputStream = new FileOutputStream(tempFilePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Încărcarea imaginii rotite în Firebase Storage
            Uri rotatedImageUri = Uri.fromFile(new File(tempFilePath));
            uploadProfilePhoto(rotatedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri cropImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int dimension = Math.min(width, height);
            int startX = (width - dimension) / 2;
            int startY = (height - dimension) / 2;

            // Cropparea imaginii
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, startX, startY, dimension, dimension);

            // Salvarea imaginii cropate într-un fișier temporar
            String tempFilePath = getExternalCacheDir() + "/temp_image.jpg";
            FileOutputStream outputStream = new FileOutputStream(tempFilePath);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Încărcarea imaginii cropate în Firebase Storage
            Uri croppedImageUri = Uri.fromFile(new File(tempFilePath));
            return croppedImageUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadProfilePhoto(Uri image_uri) {
        image_uri= cropImage(image_uri);
        if(image_uri!=null) {
            String filePathAndName = storagePath + user.getUid();
            Toast.makeText(this, filePathAndName, Toast.LENGTH_LONG).show();
            StorageReference storageReference2nd = storageReference.child(filePathAndName);
            storageReference2nd.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()) {

                        FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(user.getUid()).child("img").setValue(downloadUri.toString());
                        loadButton();
                    } else {
                        Toast.makeText(OnboardingSupportActivity.this, "A aparut o eroare", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OnboardingSupportActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }
    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void showImageDialog(){

        String[] options ={"Camera","Galerie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alegeti imaginea din " );

        builder.setItems(options, (dialog, which) -> {
            if(which == 0)
            {
                if(!checkCameraPermission()){
                    requestCameraPermission();
                }
                else{
                    pickFromCamera();
                }


            }else if(which ==1)
            {
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }
                else
                {
                    pickFromGallery();
                }
            }
        });

      builder.show();

    }
    public void handleLoadedBitmap(Bitmap b) {

        BitmapDrawable bdrawable = new BitmapDrawable(b);
        imgBtnProfil.setBackground(bdrawable);



    }

    private void loadButton()
    {
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot dataSnapshot)
                    {
                        String link = dataSnapshot.getValue(
                                String.class);
                        if (loadtarget == null)
                            loadtarget = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    handleLoadedBitmap(bitmap);

                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable arg0) {

                                }
                            };

                        Picasso.get().load(link).resizeDimen(R.dimen.on_board_button_pic,R.dimen.on_board_button_pic).into(loadtarget);
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError databaseError)
                    {
                        Toast
                                .makeText(OnboardingSupportActivity.this,
                                        "Error Loading Image",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }



}