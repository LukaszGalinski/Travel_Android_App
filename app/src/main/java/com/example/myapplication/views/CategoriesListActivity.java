package com.example.myapplication.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.viewmodels.CategoryListViewModel;
import com.example.myapplication.views.adapters.FragmentsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;

public class CategoriesListActivity extends FragmentActivity {

    public static final String PLACE_NAME = "placeName";
    public static final String PLACE_CATEGORY = "placeCategory";
    public static final String PLACE_NUMBER = "1";
    private static final String CATEGORY_LABEL = "category";
    private static final String STORAGE_REFERENCE_PLACES = "places";
    private static final int WAITING_TIME = 2000;
    private static final int WAITING_TIME_INTERVAL = 1000;

    StorageReference mStorageRef;
    DatabaseReference databasePlaces;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_list_layout);
        Intent intent = getIntent();
        int categorySwitch = intent.getIntExtra(CATEGORY_LABEL, 0);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databasePlaces = FirebaseDatabase.getInstance().getReference(STORAGE_REFERENCE_PLACES);
        CategoryListViewModel categoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        categoryListViewModel.instance();
        createProgressDialog(categorySwitch);
    }

    private void createProgressDialog(int categorySwitch){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getResources().getString(R.string.loading_data_message));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.colorButton)));
        progressDialog.show();
        new CountDownTimer(WAITING_TIME, WAITING_TIME_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                createTabViewPager(categorySwitch);
            }
        }.start();
    }

    private void createTabViewPager(int categorySwitch){
        ViewPager2 placesPager = findViewById(R.id.places_view_pager);
        placesPager.setAdapter(new FragmentsAdapter(CategoriesListActivity.this));
        TabLayout categoryTab = findViewById(R.id.categories_tab_layout);

        new TabLayoutMediator(categoryTab, placesPager, (tab, position) -> {
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
        }).attach();
        moveToTab(categoryTab, categorySwitch);
    }

    private void moveToTab(TabLayout categoryTab, int categorySwitch){
        TabLayout.Tab tab = categoryTab.getTabAt(categorySwitch);
        assert tab != null;
        tab.select();
    }

    public void GoToMaps(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
    }
}
