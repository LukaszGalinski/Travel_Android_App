package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.test.TestActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class RateThePlaceActivity extends AppCompatActivity {

        protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_the_place_layout);
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
