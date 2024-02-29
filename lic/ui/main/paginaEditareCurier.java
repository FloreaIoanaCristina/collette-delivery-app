package com.dam.lic.ui.main;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.lic.CardEditorAdapter;
import com.dam.lic.R;
import com.dam.lic.ReadWriteCardDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class paginaEditareCurier extends Fragment {

    ImageView pozaProfil;
    Button butonRenunta,butonSalveaza;
    TextView schimbaPoza;
    EditText etNrTelefon, etMarca, etModel, etNrInmatriculare;
    Spinner spnCuloare;
    RecyclerView rvCarduri;
    CardEditorAdapter cardEditorAdapter;
    List<ReadWriteCardDetails> listaCarduri = new ArrayList<>();
    int rotationAngle=0;



    String uid ;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    StorageReference storageReference ;
    String storagePath="UsersProfilePictures/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    String[] cameraPermissions;
    String[] storagePermissions;

    Uri image_uri=null;

    public interface OnButtonClickListener {
        void onButtonClicked();
    }

    private paginaEditareCurier.OnButtonClickListener buttonClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            buttonClickListener = (paginaEditareCurier.OnButtonClickListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement OnButtonClickListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pagina_editare_curier, container, false);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pozaProfil = inflate.findViewById(R.id.imageViewProfilCurierEditare);

        schimbaPoza = inflate.findViewById(R.id.textViewSchimbaPozaProfilCurierEditare);

        etNrTelefon = inflate.findViewById(R.id.editTextTelefonInfoCurierEditare);
        etMarca = inflate.findViewById(R.id.editTextMarcaMasinaCurierEditare);
        etModel = inflate.findViewById(R.id.editTextModelMasinaCurierEditare);
        etNrInmatriculare = inflate.findViewById(R.id.editTextNrInmatriculareMasinaCurierEditare);

        spnCuloare = inflate.findViewById(R.id.spinnerCuloareMasinaCurierEditare);

        butonRenunta = inflate.findViewById(R.id.buttonAnuleazaModificariCurier);
        butonSalveaza = inflate.findViewById(R.id.buttonSalveazaModificariCurier);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvCarduri = inflate.findViewById(R.id.rvCarduriEditareCurier);
        rvCarduri.setLayoutManager(manager);
        cardEditorAdapter = new CardEditorAdapter(listaCarduri,getContext(),uid);
        rvCarduri.setAdapter(cardEditorAdapter);

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storageReference= FirebaseStorage.getInstance().getReference();

        schimbaPoza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
        butonRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(paginaEditareCurier.this);
                    fragmentTransaction.commit();
                    buttonClickListener.onButtonClicked();
                }

            }
        });
        butonSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean ok=true;
                if(TextUtils.isEmpty(etNrTelefon.getText().toString()))
                {   ok=false;
                    etNrTelefon.setError("Nu ati adaugat numarul de telefon!");
                    etNrTelefon.requestFocus();

                }
                else if(etNrTelefon.getText().toString().length()!=10)
                {   ok=false;
                    etNrTelefon.setError("Numarul de telefon trebuie sa aibe 10 cifre!");
                    etNrTelefon.requestFocus();

                }
                else if(!etNrTelefon.getText().toString().matches("[0-9]+"))
                {   ok=false;
                    etNrTelefon.setError("Numarul de telefon trebuie sa contina doar cifre!");
                    etNrTelefon.requestFocus();

                }
                if(TextUtils.isEmpty(etMarca.getText().toString()))
                {   ok=false;
                    etMarca.setError("Nu ati adaugat marca masinii!");
                    etMarca.requestFocus();

                }
                if(TextUtils.isEmpty(etModel.getText().toString()))
                {   ok=false;
                    etModel.setError("Nu ati adaugat modelul masinii!");
                    etModel.requestFocus();

                }
                if(TextUtils.isEmpty(etNrInmatriculare.getText().toString()))
                {   ok=false;
                    etNrInmatriculare.setError("Nu ati adaugat numarul de inmatriculare al masinii!");
                    etNrInmatriculare.requestFocus();

                }
                else if(etNrInmatriculare.getText().toString().length()>7 || etNrInmatriculare.getText().toString().length()<6)

                {   ok=false;
                    etNrInmatriculare.setError("Numarul de inmatriculare trebuie sa aibe intre 6 si 7 cifre!");
                    etNrInmatriculare.requestFocus();

                }
                else if(!etNrInmatriculare.getText().toString().matches("^[A-Z0-9]+$"))
                {   ok=false;
                    etNrInmatriculare.setError("Numarul de inmatriculare trebuie sa fie valid!");
                    etNrInmatriculare.requestFocus();

                }
                if(spnCuloare.getSelectedItem().toString().equals("Culoare Masina"))
                {
                    ok=false;
                    Toast.makeText(getContext(),"Selectati o culoare pentru masina!",Toast.LENGTH_SHORT).show();
                    spnCuloare.requestFocus();
                }
                if(ok==true) {
                    firebase.getReference("RegisteredUsers/" + uid).child("phone").setValue(etNrTelefon.getText().toString());
                    firebase.getReference("RegisteredUsers/" + uid).child("color").setValue(spnCuloare.getSelectedItem().toString());
                    firebase.getReference("RegisteredUsers/" + uid).child("brand").setValue(etMarca.getText().toString());
                    firebase.getReference("RegisteredUsers/" + uid).child("model").setValue(etModel.getText().toString());
                    firebase.getReference("RegisteredUsers/" + uid).child("license").setValue(etNrInmatriculare.getText().toString());
                    uploadProfilePhoto(image_uri);
                    if (buttonClickListener != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(paginaEditareCurier.this);
                        fragmentTransaction.commit();
                        buttonClickListener.onButtonClicked();
                    }

                }

            }
        });
        pozaProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image_uri!=null)
                {
                    rotateImage(image_uri);
                }
            }
        });





        return inflate;
    }
    private void rotateImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            rotationAngle += 90;
            rotationAngle = rotationAngle % 360; // Asigură că unghiul de rotație rămâne între 0 și 359

            // Rotirea imaginii cu unghiul calculat
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // Salvarea imaginii rotite într-un fișier temporar
            String timestamp = String.valueOf(System.currentTimeMillis());
            String tempFilePath = getContext().getExternalCacheDir() + "/"+timestamp+"_temp_image.jpg";
            FileOutputStream outputStream = new FileOutputStream(tempFilePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Încărcarea imaginii rotite în Firebase Storage
            Uri rotatedImageUri = Uri.fromFile(new File(tempFilePath));
            changeProfilePhoto(rotatedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri cropImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int dimension = Math.min(width, height);
            int startX = (width - dimension) / 2;
            int startY = (height - dimension) / 2;

            // Cropparea imaginii
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, startX, startY, dimension, dimension);

            // Salvarea imaginii cropate într-un fișier temporar
            String timestamp = String.valueOf(System.currentTimeMillis());
            String tempFilePath = getContext().getExternalCacheDir() + "/"+timestamp+"_temp_image.jpg";
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
    private void showImageDialog() {
        String[] options ={"Camera","Galerie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase.getReference("RegisteredUsers/"+uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String img = snapshot.child("img").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String marca = snapshot.child("brand").getValue(String.class);
                    String model = snapshot.child("model").getValue(String.class);
                    String nrInmatriculare = snapshot.child("license").getValue(String.class);
                    String culoare = snapshot.child("color").getValue(String.class);

                    etNrTelefon.setText(phone);
                    ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) spnCuloare.getAdapter();
                    spnCuloare.setSelection(array_spinner.getPosition(culoare));
                    etMarca.setText(marca);
                    etModel.setText(model);
                    etNrInmatriculare.setText(nrInmatriculare);

                    if (img.equals("nespecificat")) {
                        pozaProfil.setBackgroundResource(R.drawable.poza_profil_generica);
                    } else {
                        Picasso.get().load(img).resizeDimen(R.dimen.profile_pic, R.dimen.profile_pic).into(pozaProfil);
                    }
                }
            }

                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            rotationAngle=0;
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                image_uri=data.getData();
                changeProfilePhoto(image_uri);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                changeProfilePhoto(image_uri);

            }

        }
    }

    private void changeProfilePhoto(Uri imageUri) {
        image_uri= cropImage(imageUri);
        Picasso.get().load(image_uri).resizeDimen(R.dimen.profile_pic,R.dimen.profile_pic).into(pozaProfil);
    }

    private void uploadProfilePhoto(Uri imageUri) {
        if(image_uri!=null) {
            String filePathAndName = storagePath+uid;
            Toast.makeText(getContext(),filePathAndName, Toast.LENGTH_LONG).show();
            StorageReference storageReference2nd = storageReference.child(filePathAndName);
            storageReference2nd.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()) {

                        FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(uid).child("img").setValue(downloadUri.toString());
                        //Picasso.get().load(downloadUri.toString()).resizeDimen(R.dimen.profile_pic,R.dimen.profile_pic).into(pozaProfil);

                    } else {
                        Toast.makeText(getContext(), "A aparut o eroare", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        getCarduri();
    }

    private void getCarduri() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers/"+uid+"/Carduri");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCarduri.clear();
                notifyPackageAdapter();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ReadWriteCardDetails cardDetails = new ReadWriteCardDetails(data.child("nr").getValue().toString(),Integer.parseInt(data.child("expirationMonth").getValue().toString()),Integer.parseInt(data.child("expirationYear").getValue().toString()),data.child("name").getValue().toString(),data.child("cvc").getValue().toString(),Integer.parseInt(data.child("activ").getValue().toString()));
                    listaCarduri.add(cardDetails);

                }
                cardEditorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        notifyPackageAdapter();

    }
    private void notifyPackageAdapter() {
        CardEditorAdapter adapter = (CardEditorAdapter) rvCarduri.getAdapter();
        adapter.notifyDataSetChanged();


    }

}