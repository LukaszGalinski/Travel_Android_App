package com.example.myapplication.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.myapplication.R;
import com.example.myapplication.views.account.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Locale;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String NOT_ALLOWED_SIGNS = "[\\[.#$\\]]";
    private static final String PREFERENCES_LANGUAGE_LABEL = "Language";
    private static final String CATEGORY_LABEL = "category";
    private static final String TRANSPORT_INFO_URL = "https://www.zditm.szczecin.pl/pl/pasazer/rozklady-jazdy,wedlug-linii";
    private static final String EVENTS_INFO_URL = "https://infoludek.pl/wydarzenia/";
    private ActionBarDrawerToggle toggle;
    String lngChose, formattedUser;
    View.OnClickListener listener;

    protected void onCreate(Bundle savedInstanceState) {
        loadAppLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        formattedUser = Objects.requireNonNull(currentUser).replaceAll(NOT_ALLOWED_SIGNS,"");
        loadProfileImage();
        buildDrawerMenu();
        buildSquareNavigation();
        final ImageButton relax = findViewById(R.id.relax);
        final ImageButton restaurant = findViewById(R.id.restaurants);
        final ImageButton hotel = findViewById(R.id.hotel);
        final ImageButton attraction = findViewById(R.id.attractions);
        final ImageButton hospital = findViewById(R.id.hospitals);
        final ImageButton universe = findViewById(R.id.universities);

        relax.setOnClickListener(listener);
        restaurant.setOnClickListener(listener);
        hotel.setOnClickListener(listener);
        attraction.setOnClickListener(listener);
        hospital.setOnClickListener(listener);
        universe.setOnClickListener(listener);
    }

    private void buildDrawerMenu(){
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameDrawerBar = headerView.findViewById(R.id.drawerbarUserName);
        ImageButton userImg = headerView.findViewById(R.id.drawerbarImage);
        String userName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        usernameDrawerBar.setText(userName);
        userImg.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerBar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int selectedItem = menuItem.getItemId();
        Intent intent;
        switch(selectedItem){
            case R.id.aboutme:
                intent = new Intent(getApplicationContext(), AboutMe.class);
                startActivity(intent);
                break;
            case R.id.contact:
                intent = new Intent(getApplicationContext(), Contact.class);
                startActivity(intent);
                break;
            case R.id.events:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(EVENTS_INFO_URL));
                startActivity(browserIntent);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.url_open_info), Toast.LENGTH_SHORT).show();
                break;
            case R.id.changeLanguage:
                changeAppLanguage();
                break;
            case R.id.moving:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TRANSPORT_INFO_URL)));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.url_open_info) , Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return false;
    }

    private void buildSquareNavigation(){
        listener = v -> {
            Intent intent;
            intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
            switch (v.getId()){
                case R.id.relax:
                    intent.putExtra(CATEGORY_LABEL,0);
                    break;
                case R.id.restaurants:
                    intent.putExtra(CATEGORY_LABEL, 1);
                    break;
                case R.id.hotel:
                    intent.putExtra(CATEGORY_LABEL, 2);
                    break;
                case R.id.attractions:
                    intent.putExtra(CATEGORY_LABEL, 3);
                    break;
                case R.id.hospitals:
                    intent.putExtra(CATEGORY_LABEL, 4);
                    break;
                case R.id.universities:
                    intent.putExtra(CATEGORY_LABEL, 5);
                    break;
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
        };
    }

    private void changeAppLanguage() {
        final String[] lang = {"Polski", "English"};
        final AlertDialog.Builder theBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        theBuilder.setTitle(getResources().getString(R.string.choselng));
        theBuilder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
            if (lngChose == null){
                dialog.dismiss();
            }else{
                setAppDefaultLanguage(lngChose);
                recreate();
            }
        });

        theBuilder.setSingleChoiceItems(lang, -1, (dialog, which) -> {
            switch (which){
                case 0:
                    lngChose = "pl";
                    break;
                case 1:
                    lngChose = "en";
                    break;
            }
        });
        AlertDialog theDialog = theBuilder.create();
        theDialog.show();
    }

    private void setAppDefaultLanguage(String lng){
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration cfg = new Configuration();
        cfg.locale = locale;
        getBaseContext().getResources().updateConfiguration(cfg, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_LANGUAGE_LABEL, MODE_PRIVATE).edit();
        editor.putString(PREFERENCES_LANGUAGE_LABEL, lng);
        editor.apply();
    }

    private void loadAppLanguage(){
        SharedPreferences preferences= getSharedPreferences(PREFERENCES_LANGUAGE_LABEL, Activity.MODE_PRIVATE);
        String lang = preferences.getString(PREFERENCES_LANGUAGE_LABEL, "");
        setAppDefaultLanguage(lang);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToMaps(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    private void loadProfileImage() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        final ImageButton photo = headerView.findViewById(R.id.drawerbarImage);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageRef.child("profiles/").child(formattedUser);
        photoReference.getDownloadUrl().addOnFailureListener(e -> photo.setBackgroundResource(R.drawable.profileface))
                .addOnSuccessListener(uri -> Picasso.with(getApplicationContext())
                        .load(uri)
                        .fit()
                        .into(photo));
    }

    @Override
    public void onBackPressed() {}
}