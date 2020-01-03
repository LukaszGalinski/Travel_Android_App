package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.categories_list.CommentsList;
import com.example.myapplication.test.TestActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowCommentsActivity extends AppCompatActivity {
    DatabaseReference databaseComments;
    ListView listViewComments;

    List<Rate> commentsList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_comments_layout);
    }

        protected void onStart() {
            super.onStart();

            Intent intent = getIntent();
             String placeName = intent.getStringExtra("placenamee");
            String id = intent.getStringExtra("placeidd");
            String category = intent.getStringExtra("placecategory");
            String averageRate = intent.getStringExtra("averagerate");
            String pos = intent.getStringExtra("placenumber");
            System.out.println(category);
            System.out.println(pos);
            System.out.println(placeName);
            System.out.println(averageRate);
            System.out.println(id);

            databaseComments = FirebaseDatabase.getInstance().getReference("details").child(id).child("rate");

            //images reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference photoReference = storageReference.child("places").child(category).child(pos).child(pos + ".png");

            final ImageView imageBackground = (ImageView) findViewById(R.id.imageComments);
            TextView placeNameView = (TextView) findViewById(R.id.placeNameComments);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.rateBarComments);
           listViewComments = (ListView) findViewById(R.id.listViewComments);

            commentsList = new ArrayList<>();

            placeNameView.setText(placeName);
          try {
              ratingBar.setRating(Float.valueOf(averageRate));
              }catch (NullPointerException | NumberFormatException e){};





        //loading image
       photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ShowCommentsActivity.this)
                        .load(uri)
                        .resize(500,500)
                        .into(imageBackground);
                Log.d("Loading ImageBG: "," Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading ImageBG: "," Failed!");
            }
        });


        databaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();

                for (DataSnapshot placesSnapshot : dataSnapshot.getChildren()) {
                    Rate rate = placesSnapshot.getValue(Rate.class);

                    //akceptacja
                   if (rate.isAccept()) {
                       commentsList.add(rate);
                    }
                }
                CommentsList adapter = new CommentsList(ShowCommentsActivity.this, commentsList);
               listViewComments.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
