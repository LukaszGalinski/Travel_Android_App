package com.example.myapplication.repositories;

import androidx.annotation.NonNull;
import com.example.myapplication.models.Place;
import com.example.myapplication.models.Rate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NetworkRepository {
    private static final String STORAGE_REFERENCE_PLACES = "places";
    private NetworkRepository instance = null;
    private ArrayList<Place> placesData = new ArrayList<Place>();

    public NetworkRepository instance(){
        if (instance == null){
            synchronized (this){
                if (instance == null){
                    instance = new NetworkRepository();
                }
            }
        }
        return instance;
    }

    public ArrayList<Place> getPlacesList(){
        setPlacesList();
        return placesData;
    }

    public void setPlacesList(){
        DatabaseReference databasePlaces = FirebaseDatabase.getInstance().getReference(STORAGE_REFERENCE_PLACES);
        databasePlaces.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placesData.clear();
                for (DataSnapshot placesSnapshot : dataSnapshot.getChildren()) {
                    Place place = placesSnapshot.getValue(Place.class);
                    placesData.add(place);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public ArrayList<Rate> getCommentList(String id) {
        ArrayList<Rate> commentList = new ArrayList<>();
        DatabaseReference databaseComments = FirebaseDatabase.getInstance().getReference("details").child(id).child("rate");
        databaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot placesSnapshot : dataSnapshot.getChildren()) {
                    Rate rate = placesSnapshot.getValue(Rate.class);
                    assert rate != null;
                    if (rate.isAccept()) {
                        commentList.add(rate);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return commentList;
    }
}
