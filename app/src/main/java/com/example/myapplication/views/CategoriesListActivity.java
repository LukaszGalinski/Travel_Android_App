package com.example.myapplication.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.models.Place;
import com.example.myapplication.tools.Background;
import com.example.myapplication.views.adapters.FragmentsAdapter;
import com.example.myapplication.views.adapters.TestList;
import com.example.myapplication.views.adapters.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class CategoriesListActivity extends FragmentActivity {

    public static final String PLACE_NAME = "placeName";
    public static final String PLACE_ID = "placeId";
    public static final String PLACE_CATEGORY = "placeCategory";
    public static final String PLACE_NUMBER = "1";
    private static final String CATEGORY_LABEL = "category";
    private static final String STORAGE_REFERENCE_PLACES = "places";

    StorageReference mStorageRef;
    DatabaseReference databasePlaces;
    ListView listViewPlaces;
    List<Place> placeList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_list_layout);
        Intent intent = getIntent();
        String categorySwitch = intent.getStringExtra(CATEGORY_LABEL);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databasePlaces = FirebaseDatabase.getInstance().getReference(STORAGE_REFERENCE_PLACES);

       // listViewPlaces = findViewById(R.id.list_places);

        TabLayout categoryTab = findViewById(R.id.categories_tab_layout);
        ViewPager2 placesPager = findViewById(R.id.places_view_pager);
        FragmentsAdapter fragmentsAdapter = new FragmentsAdapter(this);
        placesPager.setAdapter(fragmentsAdapter);
        new TabLayoutMediator(categoryTab, placesPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.relaks, null));
                        break;
                    case 1:
                        tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.restaurant, null));
                        break;
                    case 2:
                        tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.hotel, null));
                        break;
                    case 3:
                        tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atraction, null));
                        break;
                    case 4:
                        tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.hospital, null));
                        break;
                    case 5: tab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.university, null));
                        break;
                }
            }
        }).attach();

        /*listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place place = placeList.get(position);
                String sendPosition = Integer.toString(position + 1);
                Intent intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
                intent.putExtra(PLACE_CATEGORY, place.getCategory());
                intent.putExtra(PLACE_ID, place.getPlaceid());
                intent.putExtra(PLACE_NAME, place.getName());
                intent.putExtra(PLACE_NUMBER, sendPosition);

                String language = getLanguageSP(getApplicationContext());
                if (language.equals("en")) intent.putExtra("info", place.getInfoen());
                else intent.putExtra("info", place.getInfo());
                startActivity(intent);
            }
        });

         */
    }

    private static String getLanguageSP(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        return preferences.getString("Language", "");
    }

    public void GoToMaps(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        final String categorySwitch = intent.getStringExtra(CATEGORY_LABEL);
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
