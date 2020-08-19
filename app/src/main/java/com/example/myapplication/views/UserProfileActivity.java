package com.example.myapplication.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.UserDetails;
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
import java.io.IOException;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    private static final String LOADING_TAG = "Loading status: ";

    String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    String formattedUser = Objects.requireNonNull(currentUser).replaceAll("[\\[.#$\\]]", "");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile_layout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference().child("profiles").child(formattedUser);


        Button test = findViewById(R.id.test);
        Button send = findViewById(R.id.sendUserData);

        final EditText name = findViewById(R.id.name);
        final EditText surname = findViewById(R.id.surname);
        final EditText age = findViewById(R.id.age);
        final EditText aboutMe = findViewById(R.id.aboutme);
        loadProfileImage();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] data = {"name", "surname", "age", "aboutMe"};
                String[] values = new String[4];
                for (int i = 0; i < 4; i++) {
                    if (dataSnapshot.child(data[i]).getValue() == null) {
                        values[i] = "";
                    } else {
                        values[i] = Objects.requireNonNull(dataSnapshot.child(data[i]).getValue()).toString();
                    }
                }
                name.setText(values[0]);
                surname.setText(values[1]);
                age.setText(values[2]);
                aboutMe.setText(values[3]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        test.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY));
        send.setOnClickListener(v -> {

            UserDetails user = new UserDetails(name.getText().toString(), surname.getText().toString(), age.getText().toString(), aboutMe.getText().toString());
            ref.setValue(user);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photo = findViewById(R.id.userPhoto);
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            assert data != null;
            Uri selectedImage = data.getData();

            try {
                MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Picasso.with(getApplicationContext())
                        .load(selectedImage)
                        .fit()
                        .into(photo);
                StorageReference riversRef = storageRef.child("profiles/" + formattedUser + "/");
                UploadTask uploadTask;
                assert selectedImage != null;
                uploadTask = riversRef.putFile(selectedImage);

                uploadTask.addOnFailureListener(e -> Log.d(LOADING_TAG, " Failure!"))
                        .addOnSuccessListener(taskSnapshot -> Log.d(LOADING_TAG, " Success!"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadProfileImage() {
        final ImageView photo = findViewById(R.id.userPhoto);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageRef.child("profiles/").child(formattedUser);
        photoReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getApplicationContext())
                .load(uri)
                .fit()
                .into(photo)).addOnFailureListener(e -> photo.setBackgroundResource(R.drawable.profileface));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
    }
}
