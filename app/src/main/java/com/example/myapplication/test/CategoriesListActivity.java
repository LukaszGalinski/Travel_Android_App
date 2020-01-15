package com.example.myapplication.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Background;
import com.example.myapplication.ChangingBarImage;
import com.example.myapplication.ItemDetailsActivity;
import com.example.myapplication.MainMenuActivity;
import com.example.myapplication.MapsActivity;
import com.example.myapplication.Place;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CategoriesListActivity extends AppCompatActivity {

    public static final String PLACE_NAME = "placename";
    public static final String PLACE_ID = "placeid";
    public static final String PLACE_CATEGORY = "placecategory";
    public static final String PLACE_NUMBER = "1";

    StorageReference mStorageRef;
    DatabaseReference databasePlaces;
    ListView listViewPlaces;

    List<Place> placeList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaks_layout);

        Intent intent = getIntent();
        String categorySwitch = intent.getStringExtra("category");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        databasePlaces = FirebaseDatabase.getInstance().getReference("places");

        listViewPlaces = (ListView) findViewById(R.id.list_places);

        placeList = new ArrayList<>();

        final ImageButton relaks = (ImageButton) findViewById(R.id.relaks);
        final ImageButton restaurant = (ImageButton) findViewById(R.id.restauracje);
        final ImageButton hotel = (ImageButton) findViewById(R.id.hotele);
        final ImageButton atraction = (ImageButton) findViewById(R.id.atrakcje);
        final ImageButton hospital = (ImageButton) findViewById(R.id.szpitale);
        final ImageButton universe = (ImageButton) findViewById(R.id.universytety);

        switch (categorySwitch) {
            case "relaks":
                relaks.setImageResource(R.drawable.relaks_shine);
                break;
            case "restauracje":
                restaurant.setImageResource(R.drawable.restaurant_shine);
                break;
            case "hotel":
                hotel.setImageResource(R.drawable.hotel_shone);
                break;
            case "atrakcje":
                atraction.setImageResource(R.drawable.atraction_shine);
                break;
            case "szpital":
                hospital.setImageResource(R.drawable.hosp_shine);
                break;
            case "uniwersytet":
                universe.setImageResource(R.drawable.univ_shine);
                break;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
                ChangingBarImage changeImageBar = new Background();
                changeImageBar.setImageBg(relaks, atraction, hotel, restaurant, hospital, universe);
                switch (v.getId()) {
                    case R.id.relaks:
                        intent.putExtra("category", "relaks");
                        break;
                    case R.id.restauracje:
                        intent.putExtra("category", "restauracje");
                        break;
                    case R.id.hotele:
                        intent.putExtra("category", "hotel");
                        break;
                    case R.id.atrakcje:
                        intent.putExtra("category", "atrakcje");
                        break;
                    case R.id.szpitale:
                        intent.putExtra("category", "szpital");
                        break;
                    case R.id.universytety:
                        intent.putExtra("category", "uniwersytet");
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

        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place place = placeList.get(position);
                String sendPosition = Integer.toString(position + 1);
                Intent intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
                intent.putExtra(PLACE_CATEGORY, place.getCategory());
                intent.putExtra("placeid", place.getPlaceid());
                intent.putExtra(PLACE_NAME, place.getName());
                intent.putExtra(PLACE_NUMBER, sendPosition);

                SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                String lang = preferences.getString("Language", "");
                if (lang.equals("en")) intent.putExtra("info", place.getInfoen());
                else intent.putExtra("info", place.getInfo());
                startActivity(intent);
            }
        });
    }

    public void GoToMaps(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        final String categorySwitch = intent.getStringExtra("category");
        databasePlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeList.clear();
                for (DataSnapshot placesSnapshot : dataSnapshot.getChildren()) {
                    Place place = placesSnapshot.getValue(Place.class);
                    if (place.getCategory().equals(categorySwitch)) {
                        placeList.add(place);
                    }
                }
                TestList adapter = new TestList(CategoriesListActivity.this, placeList, categorySwitch);
                listViewPlaces.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
    }
}
