package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RateThePlaceActivity extends AppCompatActivity {

        protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_the_place_layout);

        //getting dataand setting Firebase
        Intent nowa = getIntent();
        String id = nowa.getStringExtra("elementid");
        final String records = nowa.getStringExtra("elementCount");

        //Init database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("details").child(id).child("rate").child(records);


        //get elements
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final TextView  ratingScale = (TextView) findViewById(R.id.displayRatingMessage);
        final EditText commentMessage = (EditText) findViewById(R.id.comment_edtText);
        Button sendCommentButton = (Button) findViewById(R.id.giveRating);
        //TextView displayUsername = (TextView) findViewById(R.id.userName_txtView);
        TextView displayDate = (TextView) findViewById(R.id.DateAndTime_txtView);

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

   //     final Date currentTime = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
           final String currentTime = df.format(new Date());


       // System.out.println(data);
        //displayUsername.setText(currentUser);
        displayDate.setText(String.valueOf(currentTime));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScale.setText(String.valueOf(rating));
                float val = ratingBar.getRating();
                if (val>0 && val <=1) ratingScale.setText(getResources().getString(R.string.comhor));
                else if (val>1 && val <=2) ratingScale.setText(getResources().getString(R.string.combad));
                else if (val>2 && val <=3) ratingScale.setText(getResources().getString(R.string.comneu));
                else if (val>3 && val <=4) ratingScale.setText(getResources().getString(R.string.comgood));
                else if (val>4 && val <=5) ratingScale.setText(getResources().getString(R.string.comama));
                else ratingScale.setText("");
            }
        });

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentMessage.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fillfields), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.tyrev), Toast.LENGTH_SHORT).show();
                    Rate rate = new Rate(commentMessage.getText().toString(),ratingBar.getRating(),currentTime, false, currentUser);
                    ref.setValue(rate);
                    finish();
                }
            }
        });
    }
}
