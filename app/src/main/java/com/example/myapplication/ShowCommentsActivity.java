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

        final ImageButton relaks = (ImageButton) findViewById(R.id.relaks);
        final ImageButton restaurant = (ImageButton) findViewById(R.id.restauracje);
        final ImageButton hotel = (ImageButton) findViewById(R.id.hotele);
        final ImageButton atraction = (ImageButton) findViewById(R.id.atrakcje);
        final ImageButton hospital = (ImageButton) findViewById(R.id.szpitale);
        final ImageButton universe = (ImageButton) findViewById(R.id.universytety);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), TestActivity.class);
                ChangingBarImage changeImageBar = new Background();
                changeImageBar.setImageBg(relaks,atraction,hotel,restaurant,hospital,universe);
                switch (v.getId()){
                    case R.id.relaks:
                        intent.putExtra("category", "relaks");

                        break;
                    case R.id.restauracje:
                        intent.putExtra("category", "restauracje");
                        break;
                    case R.id.hotele:
                        intent.putExtra("category", "hotel");
                        hotel.setImageResource(R.drawable.hotel_shone);
                        break;
                    case R.id.atrakcje:
                        intent.putExtra("category", "atrakcje");
                        atraction.setImageResource(R.drawable.atraction_shine);
                        break;
                    case R.id.szpitale:
                        intent.putExtra("category", "szpital");
                        hospital.setImageResource(R.drawable.hosp_shine);
                        break;
                    case R.id.universytety:
                        intent.putExtra("category", "uniwersytet");
                        universe.setImageResource(R.drawable.univ_shine);
                        break;
                }
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        };
        relaks.setOnClickListener(listener);
        restaurant.setOnClickListener(listener);
        hotel.setOnClickListener(listener);
        atraction.setOnClickListener(listener);
        hospital.setOnClickListener(listener);
        universe.setOnClickListener(listener);
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
