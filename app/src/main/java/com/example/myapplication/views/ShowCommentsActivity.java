package com.example.myapplication.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_CATEGORY;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_NAME;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_NUMBER;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.models.Rate;
import com.example.myapplication.viewmodels.CommentsViewModel;
import com.example.myapplication.views.adapters.CommentsList;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ShowCommentsActivity extends AppCompatActivity {
    private static final int IMG_WIDTH = 500;
    private static final int IMG_HEIGHT = 500;
    private static final String PLACE_ID = "placeId";
    private static final String PLACE_RATING = "placeRating";
    private static final String LOADING_TAG = "Loading Image: ";

    ListView listViewComments;
    List<Rate> commentsList;
    StorageReference photoReference;
    CommentsViewModel commentsViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_comments_layout);

        Intent intent = getIntent();
        String placeName = intent.getStringExtra(PLACE_NAME);
        String id = intent.getStringExtra(PLACE_ID);
        String category = intent.getStringExtra(PLACE_CATEGORY);
        String averageRate = intent.getStringExtra(PLACE_RATING);
        String pos = intent.getStringExtra(PLACE_NUMBER);

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        commentsViewModel.instance(id);

        assert id != null;
        assert category != null;
        assert pos != null;

        photoReference = FirebaseStorage.getInstance().getReference().child("places").child(category).child(pos).child(pos + ".png");
        listViewComments = findViewById(R.id.listViewComments);
        commentsList = new ArrayList<>();
        setTheFields(placeName, averageRate);
        getComments();
    }

    private void setTheFields(String placeName, String averageRate){
        final ImageView imageBackground = findViewById(R.id.imageComments);
        TextView placeNameView = findViewById(R.id.placeNameComments);
        RatingBar ratingBar = findViewById(R.id.rateBarComments);
        placeNameView.setText(placeName);
        try {
            assert averageRate != null;
            ratingBar.setRating(Float.parseFloat(averageRate));
        } catch (NullPointerException | NumberFormatException ignored) {}

        photoReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(ShowCommentsActivity.this)
                .load(uri)
                .resize(IMG_WIDTH, IMG_HEIGHT)
                .into(imageBackground)).addOnFailureListener(exception -> Log.d(LOADING_TAG, " Failed!"));
    }

    private void getComments(){
        CommentsList adapter = new CommentsList(ShowCommentsActivity.this, commentsViewModel.getComments());
        listViewComments.setAdapter(adapter);
    }
}
