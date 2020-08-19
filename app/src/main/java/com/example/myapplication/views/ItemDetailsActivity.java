package com.example.myapplication.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_CATEGORY;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_NAME;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_NUMBER;

public class ItemDetailsActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String PLACE_INFO_LABEL = "info";
    private static final String PLACE_ID = "placeId";
    private static final int  GALLERY_IMAGE_WIDTH = 400;
    private static final int GALLERY_IMAGE_HEIGHT = 500;
    private static final String PLACE_RATING = "placeRating";
    private static final String ELEMENTS_COUNT_LABEL = "elementCount";
    private static final String LOADING_TAG = "Loading Image: ";
    DatabaseReference infoAndRatingReference;
    StorageReference logoImageReference, galleryReference;
    int i;
    String result;
    int pressCounter = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_layout);

        Intent intent = getIntent();
        final String placeName = intent.getStringExtra(PLACE_NAME);
        final String placeId = intent.getStringExtra(PLACE_ID);
        final String category = intent.getStringExtra(PLACE_CATEGORY);
        final String adapterPosition = String.valueOf(Integer.parseInt(Objects.requireNonNull(intent.getStringExtra(PLACE_NUMBER))) + 1);
        final String info = intent.getStringExtra(PLACE_INFO_LABEL);

        Button rateThePlace = findViewById(R.id.rateThePlace);
        Button showComments = findViewById(R.id.showComments);
        Button markOnMapBtn = findViewById(R.id.searchAround_details);

        infoAndRatingReference = database.getReference("details").child(adapterPosition);
        assert category != null;
        logoImageReference = FirebaseStorage.getInstance().getReference().child("places").child(category).child(adapterPosition).child(adapterPosition + ".png");


        setInformation(placeName);
        buildImageGallery(category, adapterPosition);
        setInfoAndRating(info);

        rateThePlace.setOnClickListener(v -> moveToRating(placeId));
        showComments.setOnClickListener(v -> moveToComments(category, adapterPosition, placeName, placeId));
        markOnMapBtn.setOnClickListener(v -> moveToMaps(placeName));
    }

    private void setInformation(String placeName){
        TextView displayName = findViewById(R.id.placenamed);
        displayName.setText(placeName);
        final ImageView imageViewList = findViewById(R.id.logoimage);
        logoImageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.with(ItemDetailsActivity.this)
                    .load(uri)
                    .resize(GALLERY_IMAGE_WIDTH, GALLERY_IMAGE_HEIGHT)
                    .centerInside()
                    .into(imageViewList);
            Log.d(LOADING_TAG, " Success!");
        }).addOnFailureListener(exception -> Log.d(LOADING_TAG, " Failed!"));
    }

    private void buildImageGallery(String category, String adapterPosition){
        ImageView placeGallery = findViewById(R.id.placegallery);
        Button nextImage = findViewById(R.id.next);
        Button backImage = findViewById(R.id.back);
        View.OnClickListener listener = v -> {
            if (v.getId() == R.id.next) {
                pressCounter++;
            }
            if (v.getId() == R.id.back) {
                pressCounter--;
            }
            galleryReference = FirebaseStorage.getInstance().getReference().child("places/"+ category+"/"+adapterPosition+"/gallery/"+pressCounter+".png");
            galleryReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(getApplicationContext())
                    .load(uri)
                    .fit()
                    .into(placeGallery))
                    .addOnFailureListener(e -> { pressCounter = 0;
                    nextImage.performClick();
            });
        };

        nextImage.setOnClickListener(listener);
        backImage.setOnClickListener(listener);
        nextImage.performClick();
    }

    private void setInfoAndRating(String info){
        final TextView displayRating = findViewById(R.id.rateD);
        final TextView displayInfo = findViewById(R.id.infoD);
        displayInfo.setMovementMethod(new ScrollingMovementMethod());
        infoAndRatingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasElements = false;
                try {
                    double averageRate = 0;
                    for (i = 0; i < (dataSnapshot.child("rate").getChildrenCount()); i++) {
                        String rate = Objects.requireNonNull(dataSnapshot.child("rate/" + (i + 1) + "/rate").getValue()).toString();
                        averageRate += Float.parseFloat(rate);
                        hasElements = true;
                    }
                    if (!hasElements) {
                        displayRating.setText(getResources().getString(R.string.norate));
                    } else {
                        averageRate = (Math.floor((averageRate / i) * 100)) / 100;
                        result = String.valueOf(averageRate);
                        displayRating.setText(result);
                    }
                    displayInfo.setText(info);
                } catch (NullPointerException e) {
                    displayRating.setText(getResources().getString(R.string.norate));
                    displayInfo.setText(getResources().getString(R.string.noinfo));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void moveToRating(String placeId){
        Intent intent1 = new Intent(ItemDetailsActivity.this, RateThePlaceActivity.class);
        intent1.putExtra(PLACE_ID, String.valueOf(placeId));
        intent1.putExtra(ELEMENTS_COUNT_LABEL, String.valueOf(i + 1));
        startActivity(intent1);
    }

    private void moveToComments(String category, String adapterPosition, String placeName, String placeId){
        Intent intent12 = new Intent(ItemDetailsActivity.this, ShowCommentsActivity.class);
        intent12.putExtra(PLACE_CATEGORY, category);
        intent12.putExtra(PLACE_NUMBER, adapterPosition);
        intent12.putExtra(PLACE_NAME, placeName);
        intent12.putExtra(PLACE_RATING, result);
        intent12.putExtra(PLACE_ID, placeId);
        startActivity(intent12);
    }

    private void moveToMaps(String placeName){
        Intent intent13 = new Intent(getApplicationContext(), MapsActivity.class);
        intent13.putExtra(PLACE_NAME, placeName);
        startActivity(intent13);
    }
}


