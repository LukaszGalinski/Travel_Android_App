package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.account.LoginActivity;
import com.example.myapplication.drawermenu.Contact;
import com.example.myapplication.drawermenu.aboutme;
import com.example.myapplication.test.TestActivity;
import com.facebook.FacebookActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameDrawerbar = (TextView) headerView.findViewById(R.id.drawerbarUserName);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        usernameDrawerbar.setText(userName);

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
                Toast.makeText(getApplicationContext(), "Trying to open browser...", Toast.LENGTH_SHORT).show();
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
    //do nothing when back button is pressed
    @Override
    public void onBackPressed() {}

    //go to maps activity
    public void search(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }
}