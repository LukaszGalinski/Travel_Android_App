package com.example.myapplication.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.myapplication.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static com.example.myapplication.views.CategoriesListActivity.PLACE_NAME;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastLocation;
    View.OnClickListener placesAroundSearching;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int PROXIMITY_RADIUS = 1500;
    private static final double DEFAULT_PLACE_X = 53.43;
    private static final double DEFAULT_PLACE_Y = 14.55;
    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final int DEFAULT_ZOOM = 8;
    double latitude, longitude;
    String placeNameFromDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        placeNameFromDetails = intent.getStringExtra(PLACE_NAME);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        createMapFragment();
        buildPlacesAroundSearching();
        ImageButton restaurantSearch = findViewById(R.id.gastronomia);
        ImageButton sightseeingSearch = findViewById(R.id.zwiedzanie);
        ImageButton funSearch = findViewById(R.id.zabawa);
        ImageButton sleepingSearch = findViewById(R.id.noclegi);
        ImageButton hospitalSearch = findViewById(R.id.hospitals);
        ImageButton setMyLocation = findViewById(R.id.setMyLocation);

        restaurantSearch.setOnClickListener(placesAroundSearching);
        sightseeingSearch.setOnClickListener(placesAroundSearching);
        funSearch.setOnClickListener(placesAroundSearching);
        sleepingSearch.setOnClickListener(placesAroundSearching);
        hospitalSearch.setOnClickListener(placesAroundSearching);
        setMyLocation.setOnClickListener(placesAroundSearching);
    }

    private void buildPlacesAroundSearching(){
        placesAroundSearching = v -> {
            mMap.clear();
            GetLastLocation();
            switch (v.getId()) {
                case R.id.setMyLocation:
                    placeNameFromDetails = null;
                    break;
                case R.id.gastronomia:
                    String[] location = new String[]{"restaurant", "bar", "food"};
                    getPlacesMarkers(location);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.showgastro), Toast.LENGTH_LONG).show();
                    break;
                case R.id.zabawa:
                    location = new String[]{"amusement_park", "bowling_alley", "casino", "gym", "movie_theater", "night_club", "spa"};
                    getPlacesMarkers(location);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.showfun), Toast.LENGTH_LONG).show();
                    break;
                case R.id.noclegi:
                    location = new String[]{"campground", "lodging"};
                    getPlacesMarkers(location);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.showsleep), Toast.LENGTH_LONG).show();
                    break;
                case R.id.zwiedzanie:
                    location = new String[]{"aquarium", "art_gallery", "museum", "park", "tourist_attraction", "zoo"};
                    getPlacesMarkers(location);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.showatra), Toast.LENGTH_LONG).show();
                    break;
                case R.id.hospitals:
                    location = new String[]{"hospital", "pharmacy"};
                    getPlacesMarkers(location);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.showmed), Toast.LENGTH_LONG).show();
                    break;
            }
        };
    }

    private void createMapFragment(){
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setHint(getResources().getString(R.string.search));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                mMap.clear();
                LatLng latLng = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                assert latLng != null;
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });
    }

    private void getPlacesMarkers(String[] location) {
        for (String placeType : location) {
            String url = getUrl(placeType);
            Object[] dataTransfer = new Object[2];
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
            getNearbyPlaces.execute(dataTransfer);
        }
    }

    private String getUrl(String nearbyPlaceType) {
        StringBuilder googlePlaceUrl = new StringBuilder(GOOGLE_MAPS_API_URL);
        try {
            googlePlaceUrl.append("location=").append(lastLocation.getLatitude()).append(",").append(lastLocation.getLongitude());
        } catch (NullPointerException e) {
            googlePlaceUrl.append("location=").append(this.latitude).append(",").append(this.longitude);
        }
        googlePlaceUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlaceUrl.append("&types=").append(nearbyPlaceType);
        googlePlaceUrl.append("&key=").append(getResources().getString(R.string.google_maps_key));
        return googlePlaceUrl.toString();
    }

    private void GetLastLocation() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            lastLocation = location;

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(MapsActivity.this);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (placeNameFromDetails != null) {
            List<Address> addressList;
            MarkerOptions markerOptions = new MarkerOptions();

            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(placeNameFromDetails + ",Szczecin", 1);
                if (addressList != null) {
                    Address myAddress = addressList.get(0);
                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    longitude = myAddress.getLongitude();
                    latitude = myAddress.getLatitude();
                    markerOptions.position(latLng);
                    markerOptions.title(placeNameFromDetails);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                } else {
                    Toast.makeText(getApplicationContext(), "Place not found...", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), +lastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                googleMap.addMarker(markerOptions);
            } else {
                LatLng latLng = new LatLng(DEFAULT_PLACE_X, DEFAULT_PLACE_Y);
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                mMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nogps), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetLastLocation();
            }
        }
    }
}
