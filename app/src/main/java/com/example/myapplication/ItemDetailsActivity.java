package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


import static com.example.myapplication.test.TestActivity.PLACE_CATEGORY;
import static com.example.myapplication.test.TestActivity.PLACE_NAME;
import static com.example.myapplication.test.TestActivity.PLACE_NUMBER;


public class ItemDetailsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final String RECORD_NUMBER = "1";
    public static final String ID_NUMBER = "1";
    int i;
    double averageRate;
    String result;
    int pressCounter=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_layout);
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

        //get data from previous activity
        Intent intent = getIntent();
        final String placeName = intent.getStringExtra(PLACE_NAME);
        final String id = intent.getStringExtra("placeid");
        final String category = intent.getStringExtra(PLACE_CATEGORY);
        final String pos = intent.getStringExtra(PLACE_NUMBER);
        final String info = intent.getStringExtra("info");
        TextView displayName = findViewById(R.id.placenamed);
        displayName.setText(placeName);

        //info and rating data
        DatabaseReference infoAndRatingReference = database.getReference("details").child(id);

        final TextView displayInfo = (TextView) findViewById(R.id.infoD);
        final TextView displayRating = (TextView) findViewById(R.id.rateD);
        final ImageView imageViewList = (ImageView) findViewById(R.id.logoimage);

        Button rateThePlace = (Button) findViewById(R.id.rateThePlace);
        Button showComments = (Button) findViewById(R.id.showComments);
        Button markOnMapBtn = (Button) findViewById(R.id.searchAround_details);


        displayInfo.setMovementMethod(new ScrollingMovementMethod());
        //images logo reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("places").child(category).child(pos).child(pos + ".png");

        //gallery reference
        final ImageView placeGallery  = (ImageView) findViewById(R.id.placegallery);
        final Button next = (Button) findViewById(R.id.next);
        final Button back = (Button) findViewById(R.id.back);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()==R.id.next){pressCounter++;}
                if (v.getId()==R.id.back){pressCounter--;}

                StorageReference galleryref = FirebaseStorage.getInstance().getReference();
                StorageReference galleryReference = galleryref.child("places").child(category).child(pos).child("gallery").child(pressCounter+".png");

                galleryReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext())
                                .load(uri)
                                .fit()
                                .into(placeGallery);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pressCounter=0;
                        next.performClick();

                    }
                });
            }
        };
        next.setOnClickListener(listener);
        back.setOnClickListener(listener);
        next.performClick();
        //get and monitor rate with info
        infoAndRatingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasElements=false;
                try {
                    averageRate = 0;
                    for (i = 0; i < (dataSnapshot.child("rate").getChildrenCount()); i++) {
                        String rate = dataSnapshot.child("rate/" + (i+1) + "/rate").getValue().toString();
                        averageRate += Float.parseFloat(rate);
                        hasElements=true;
                    }

                    //DecimalFormat decimalFormat = new DecimalFormat("##.00");
                    //Check if there are any ratings
                    if (!hasElements){
                        displayRating.setText(getResources().getString(R.string.norate));
                    }else
                    {
                        averageRate =  (Math.floor((averageRate/i)*100))/100;
                        result  = String.valueOf(averageRate);
                        displayRating.setText(result);
                    }

                    displayInfo.setText(info);
                }catch (NullPointerException e){
                    displayRating.setText(getResources().getString(R.string.norate));
                    displayInfo.setText(getResources().getString(R.string.noinfo));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //loading image
        photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ItemDetailsActivity.this)
                        .load(uri)
                        .resize(400,500)
                        .centerInside()
                        .into(imageViewList);
                Log.d("Loading Image: "," Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading Image: "," Failed!");
            }
        });

        //rate the place
        rateThePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, RateThePlaceActivity.class);
                intent.putExtra("elementid", String.valueOf(id));
                intent.putExtra("elementCount", String.valueOf(i+1));
                startActivity(intent);
            }
        });

        //show comments in the list

        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ShowCommentsActivity.class);
                intent.putExtra("placecategory", category);
                intent.putExtra("placenumber", pos);
                intent.putExtra("placenamee", placeName);
                intent.putExtra("averagerate", result);
                intent.putExtra("placeidd", id);
                startActivity(intent);
            }
        });

        markOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("place_name_to_search", placeName);
                startActivity(intent);
            }
        });
    }
}

