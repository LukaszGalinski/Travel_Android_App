package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.account.LoginActivity;
import com.example.myapplication.drawermenu.Contact;
import com.example.myapplication.drawermenu.aboutme;
import com.example.myapplication.test.TestActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Locale;


public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    String lngChose;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String formattedUser = currentUser.replaceAll("[\\[.#$\\]]","");

    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        loadProfileImage();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameDrawerbar = (TextView) headerView.findViewById(R.id.drawerbarUserName);
        ImageButton userImg = (ImageButton) headerView.findViewById(R.id.drawerbarImage);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        usernameDrawerbar.setText(userName);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                finish();
                startActivity(intent);
            }
        });
        //toogle bar configuration
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerBar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        //connecting activity with layout
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

                switch (v.getId()){
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
    }

    //Drawer bar select
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int selectedItem = menuItem.getItemId();
        Intent intent;
        switch(selectedItem){
            case R.id.aboutme:
                intent = new Intent(getApplicationContext(), aboutme.class);
                startActivity(intent);
                break;
            case R.id.contact:
                intent = new Intent(getApplicationContext(), Contact.class);
                startActivity(intent);
                break;
            case R.id.events:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://infoludek.pl/wydarzenia/"));
                startActivity(browserIntent);
                Toast.makeText(getApplicationContext(), "Trying to open the browser...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.changeLanguage:
                showChangeLanguageIntent();
                break;
            case R.id.moving:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zditm.szczecin.pl/pl/pasazer/rozklady-jazdy,wedlug-linii")));
                Toast.makeText(getApplicationContext(), "Trying to open the browser...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                //Logout user
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    private void showChangeLanguageIntent() {

        final String[] lang = {"Polski", "English"};
        final AlertDialog.Builder theBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        theBuilder.setTitle(getResources().getString(R.string.choselng));
        theBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (lngChose == null){
                    dialog.dismiss();
                }else{
                    setLocale(lngChose);
                    recreate();
                }
            }
        });
        theBuilder.setSingleChoiceItems(lang, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        lngChose = "pl";
                        break;
                    case 1:
                        lngChose = "en";
                        break;

                }
            }
        });

        AlertDialog theDialog = theBuilder.create();
        theDialog.show();
    }
    public void setLocale(String lng){
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration cfg = new Configuration();
        cfg.locale = locale;
        getBaseContext().getResources().updateConfiguration(cfg, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", lng);
        editor.apply();
    }
    public void loadLocale(){
        SharedPreferences preferences= getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = preferences.getString("Language", "");
        setLocale(lang);
    }

    //do nothing when back button is pressed
    @Override
    public void onBackPressed() {}

    //go to maps activity
    public void search(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }
    public void loadProfileImage() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        final ImageButton photo = (ImageButton) headerView.findViewById(R.id.drawerbarImage);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageRef.child("profiles/").child(formattedUser);
            photoReference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    photo.setBackgroundResource(R.drawable.profileface);
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext())
                            .load(uri)
                            .fit()
                            .into(photo);
                }
            });
    }
}