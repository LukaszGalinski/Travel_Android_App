package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    int PROXIMITY_RADIUS = 1500;
    double latitude,longitude;
    String placeNameFromDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        placeNameFromDetails = intent.getStringExtra("place_name_to_search");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();

        ImageButton searchButton = (ImageButton) findViewById(R.id.searchBarButton);
        ImageButton restaurantSearch = (ImageButton) findViewById(R.id.gastronomia);
        ImageButton sightseeingSearch = (ImageButton) findViewById(R.id.zwiedzanie);
        ImageButton funSearch = (ImageButton) findViewById(R.id.zabawa);
        ImageButton sleepingSearch = (ImageButton) findViewById(R.id.noclegi);
        ImageButton hospitalSearch = (ImageButton) findViewById(R.id.szpitale);
        ImageButton setMyLocation = (ImageButton) findViewById(R.id.setMyLocation);

        searchButton.setOnClickListener(mapChoseBar);
        restaurantSearch.setOnClickListener(mapChoseBar);
        sightseeingSearch.setOnClickListener(mapChoseBar);
        funSearch.setOnClickListener(mapChoseBar);
        sleepingSearch.setOnClickListener(mapChoseBar);
        hospitalSearch.setOnClickListener(mapChoseBar);
        setMyLocation.setOnClickListener(mapChoseBar);
    }

    View.OnClickListener mapChoseBar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.searchBarButton:
                    EditText placeSearchField = (EditText) findViewById(R.id.searchInput);
                    String address = placeSearchField.getText().toString();

                    List<Address> addressList;
                    MarkerOptions markerOptions = new MarkerOptions();

                    if (!TextUtils.isEmpty(address)) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try{
                           addressList = geocoder.getFromLocationName(address, 6);
                          //  System.out.println(addressList);
                            if (addressList != null) {
                                mMap.clear();
                                for (int i = 0; i < addressList.size(); i++) {

                                    Address myAddress = addressList.get(i);
                                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                                    System.out.println(myAddress.getLatitude() + " :::::::" + myAddress.getLongitude());
                                    markerOptions.position(latLng);
                                    markerOptions.title(address);
                                    mMap.addMarker(markerOptions);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                }
                            }else{ Toast.makeText(getApplicationContext(), "Place not found...", Toast.LENGTH_SHORT).show(); }
                        } catch (IOException e){ e.printStackTrace();}
                    } else{ Toast.makeText(getApplicationContext(), "Please write any location name...", Toast.LENGTH_SHORT).show();}
                    break;
                case R.id.setMyLocation:
                    mMap.clear();
                    placeNameFromDetails = null;
                    GetLastLocation();
                    break;
                case R.id.gastronomia:
                    mMap.clear();
                    //get your location mark
                    GetLastLocation();
                    //get places nearby marks ~restaurant
                    String[] location = new String[] {"restaurant","bar","food"};
                    for (int j=0; j<location.length; j++){
                        String placeType = location[j];
                        String url = getUrl(latitude, longitude, placeType);
                        Object dataTransfer[] = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        getNearbyPlaces.execute(dataTransfer);
                    }
                    Toast.makeText(getApplicationContext(), "Showing nearby Gastronomy points", Toast.LENGTH_LONG).show();
                    break;
                case R.id.zabawa:
                    mMap.clear();
                    //get your location mark
                    GetLastLocation();
                    //get places nearby marks
                    location = new String[] {"amusement_park", "bowling_alley","casino","gym","movie_theater","night_club","spa"};
                    for (int j=0; j<location.length; j++){
                        String placeType = location[j];
                        String url = getUrl(latitude, longitude, placeType);
                        Object dataTransfer[] = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        getNearbyPlaces.execute(dataTransfer);
                    }
                    Toast.makeText(getApplicationContext(), "Showing nearby fun&relaks locations", Toast.LENGTH_LONG).show();
                    break;
                case R.id.noclegi:
                    mMap.clear();
                    //get your location mark
                    GetLastLocation();
                    //get places nearby marks
                    location = new String[] {"campground","lodging"};
                    for (int j=0; j<location.length; j++){
                        String placeType = location[j];
                        String url = getUrl(latitude, longitude, placeType);
                        Object dataTransfer[] = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        getNearbyPlaces.execute(dataTransfer);
                    }
                    Toast.makeText(getApplicationContext(), "Showing nearby sleeping locations", Toast.LENGTH_LONG).show();
                    break;
                case R.id.zwiedzanie:
                    mMap.clear();
                    //get your location mark
                    GetLastLocation();
                    //get places nearby marks
                    location = new String[] {"aquarium", "art_gallery","museum","park","tourist_attraction","zoo"};
                    for (int j=0; j<location.length; j++){
                        String placeType = location[j];
                        String url = getUrl(latitude, longitude, placeType);
                        Object dataTransfer[] = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        getNearbyPlaces.execute(dataTransfer);
                    }
                    Toast.makeText(getApplicationContext(), "Showing nearby tourist atractions", Toast.LENGTH_LONG).show();
                    break;
                case R.id.szpitale:
                    mMap.clear();
                    //get your location mark
                    GetLastLocation();
                    //get places nearby marks
                    location = new String[] {"hospital","pharmacy"};
                    for (int j=0; j<location.length; j++){
                        String placeType = location[j];
                        String url = getUrl(latitude, longitude, placeType);
                        Object dataTransfer[] = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                        getNearbyPlaces.execute(dataTransfer);
                    }
                    Toast.makeText(getApplicationContext(), "Showing nearby medical points", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private String getUrl(double latitude, double longitude, String nearbyPlaceType){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        System.out.println("Long: " + this.longitude);
        System.out.println("Lat: " + this.latitude);
        // if Gps is turned off
        try {
            googlePlaceUrl.append("location=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
        }catch (NullPointerException e){
                System.out.println("EXCEPTION");
            googlePlaceUrl.append("location=" + this.latitude + "," + this.longitude);
        }
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&types=" + nearbyPlaceType);
        googlePlaceUrl.append("&key=" + "AIzaSyA9Ca4EFx4jvWEnl2VyO4tD7kRW27SSNsU");

        System.out.println(googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }
    private void GetLastLocation() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                    lastLocation = location;
                    //Toast.makeText(getApplicationContext(), lastLocation.getLatitude() + " " + lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);

            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        //if call comes from Place Details

        if (placeNameFromDetails!=null){

            List<Address> addressList;
            MarkerOptions markerOptions = new MarkerOptions();

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try{
                    addressList = geocoder.getFromLocationName(placeNameFromDetails + ",Szczecin", 1);
                    if (addressList != null) {

                            Address myAddress = addressList.get(0);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            System.out.println(myAddress.getLatitude() + " :::::::" + myAddress.getLongitude());
                            longitude = myAddress.getLongitude();
                            latitude = myAddress.getLatitude();
                            System.out.println("Long: " + longitude);
                            System.out.println("Lat: " + latitude);
                            markerOptions.position(latLng);
                            markerOptions.title(placeNameFromDetails);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                    }else{ Toast.makeText(getApplicationContext(), "Place not found...", Toast.LENGTH_SHORT).show(); }
                } catch (IOException e){ e.printStackTrace();}

        }else {
            //if Gps location is working

            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), +lastLocation.getLongitude());
                //  mMap.setMyLocationEnabled(true);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                googleMap.addMarker(markerOptions);
            } else {
                //if Gps location is not working, show Szczecin
                LatLng latLng = new LatLng(53.43, 14.55); //Szczecin Centrum
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                Toast.makeText(getApplicationContext(), "Unable to get your position. Please turn on the Gps location...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    GetLastLocation();
                }
                break;
        }
    }

}
