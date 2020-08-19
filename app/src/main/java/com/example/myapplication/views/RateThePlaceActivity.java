package com.example.myapplication.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.Rate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RateThePlaceActivity extends AppCompatActivity {
    private static final String PLACE_ID = "placeId";
    private static final String ELEMENTS_COUNT_LABEL = "elementCount";
    private static final String DATA_PATTERN = "yyyy-MM-dd, HH:mm";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_the_place_layout);

        Intent intent = getIntent();
        String id = intent.getStringExtra(PLACE_ID);
        final String records = intent.getStringExtra(ELEMENTS_COUNT_LABEL);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert id != null;
        assert records != null;
        final DatabaseReference ref = database.getReference("details").child(id).child("rate").child(records);

        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final TextView ratingScale = findViewById(R.id.displayRatingMessage);
        final EditText commentMessage = findViewById(R.id.comment_edtText);
        Button sendCommentButton = findViewById(R.id.giveRating);
        TextView displayDate = findViewById(R.id.DateAndTime_txtView);
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        DateFormat df = new SimpleDateFormat(DATA_PATTERN, Locale.getDefault());
        final String currentTime = df.format(new Date());

        displayDate.setText(currentTime);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            ratingScale.setText(String.valueOf(rating));
            float val = ratingBar1.getRating();
            if (val > 0 && val <= 1)
                ratingScale.setText(getResources().getString(R.string.comhor));
            else if (val > 1 && val <= 2)
                ratingScale.setText(getResources().getString(R.string.combad));
            else if (val > 2 && val <= 3)
                ratingScale.setText(getResources().getString(R.string.comneu));
            else if (val > 3 && val <= 4)
                ratingScale.setText(getResources().getString(R.string.comgood));
            else if (val > 4 && val <= 5)
                ratingScale.setText(getResources().getString(R.string.comama));
            else ratingScale.setText("");
        });

        sendCommentButton.setOnClickListener(v -> {
            if (commentMessage.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fillfields), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.tyrev), Toast.LENGTH_SHORT).show();
                Rate rate = new Rate(commentMessage.getText().toString(), ratingBar.getRating(), currentTime, false, currentUser);
                ref.setValue(rate);
                finish();
            }
        });
    }
}
