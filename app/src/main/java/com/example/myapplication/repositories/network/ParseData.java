package com.example.myapplication.repositories.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseData {
    private static final String NAME_LABEL = "name";
    private static final String VICINITY_LABEL = "vicinity";
    private static final String GEOMETRY_LABEL = "geometry";
    private static final String LOCATION_LABEL = "location";
    private static final String LATITUDE_LABEL = "lat";
    private static final String LONGITUDE_LABEL = "lng";
    private static final String REFERENCE_LABEL = "reference";
    private static final String JSON_TABLE_NAME = "results";
    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude;
        String longitude;
        String reference;

        try {
            if (!googlePlaceJson.isNull(NAME_LABEL)) {
                placeName = googlePlaceJson.getString(NAME_LABEL);
            }
            if (!googlePlaceJson.isNull(VICINITY_LABEL)) {
                vicinity = googlePlaceJson.getString(VICINITY_LABEL);
            }
            latitude = googlePlaceJson.getJSONObject(GEOMETRY_LABEL).getJSONObject(LOCATION_LABEL).getString(LATITUDE_LABEL);
            longitude = googlePlaceJson.getJSONObject(GEOMETRY_LABEL).getJSONObject(LOCATION_LABEL).getString(LONGITUDE_LABEL);

            reference = googlePlaceJson.getString(REFERENCE_LABEL);

            googlePlacesMap.put("place_name", placeName);
            googlePlacesMap.put(VICINITY_LABEL, vicinity);
            googlePlacesMap.put(LATITUDE_LABEL, latitude);
            googlePlacesMap.put(LONGITUDE_LABEL, longitude);
            googlePlacesMap.put(REFERENCE_LABEL, reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlacesMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        int number = jsonArray.length();
        System.out.println(number);
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap;

        for (int i=0; i<number; i++){
            try {
                placeMap = getPlace((JSONObject)jsonArray.get(i));
                placesList.add(placeMap);
                System.out.println(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    public List<HashMap<String, String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray(JSON_TABLE_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonArray != null;
        return getPlaces(jsonArray);
    }

}

