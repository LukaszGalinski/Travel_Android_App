package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;


    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String formattedUser = currentUser.replaceAll("[\\[.#$\\]]","");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_layout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference().child("profiles").child(formattedUser);


        Button test  = (Button) findViewById(R.id.test);
        Button send = (Button) findViewById(R.id.sendUserData);

        final EditText name = (EditText) findViewById(R.id.name);
        final EditText surname = (EditText) findViewById(R.id.surname);
        final EditText age = (EditText) findViewById(R.id.age);
        final EditText aboutme = (EditText) findViewById(R.id.aboutme);
        loadProfileImage();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] data = {"name","surname","age","aboutMe"};
                String[] values = new String[4];
                for (int i=0; i<4; i++){
                    if (dataSnapshot.child(data[i]).getValue() == null) {values[i] = "";}
                    else{values[i]=dataSnapshot.child(data[i]).getValue().toString();}
                    }
                name.setText(values[0]);
                surname.setText(values[1]);
                age.setText(values[2]);
                aboutme.setText(values[3]);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),GET_FROM_GALLERY);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                UserDetails user = new UserDetails(name.getText().toString(),surname.getText().toString(),age.getText().toString(),aboutme.getText().toString());
                ref.setValue(user);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photo = (ImageView) findViewById(R.id.userPhoto);
        if (requestCode==GET_FROM_GALLERY && resultCode== Activity.RESULT_OK){
            StorageReference storageRef= FirebaseStorage.getInstance().getReference();
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Picasso.with(getApplicationContext())
                        .load(selectedImage)
                        .fit()
                        .into(photo);
                Log.d("Loading Image: "," Success!");
                StorageReference riversRef = storageRef.child("profiles/"+formattedUser+"/");
                UploadTask uploadTask;
                uploadTask = riversRef.putFile(selectedImage);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Image to Storage: "," Failure!");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Image to Storage: "," Success!");
                    }
                });

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void loadProfileImage(){
        final ImageView photo = (ImageView) findViewById(R.id.userPhoto);
        StorageReference storageRef= FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageRef.child("profiles/").child(formattedUser);
        photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .fit()
                        .into(photo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                photo.setBackgroundResource(R.drawable.profileface);
            }
        });
    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
    }
}
