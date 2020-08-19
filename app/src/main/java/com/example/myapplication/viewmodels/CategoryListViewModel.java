package com.example.myapplication.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.myapplication.models.Place;
import com.example.myapplication.repositories.NetworkRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryListViewModel extends ViewModel {
    private ArrayList<Place> dataSet = null;
    private NetworkRepository networkRepo = null;

    public void instance(){
        if (dataSet != null){
            return;
        }
        networkRepo = new NetworkRepository().instance();
        dataSet = networkRepo.getPlacesList();
    }

    public ArrayList<Place> getPlaces(){
        return dataSet;
    }

    public List<Place> getCategorizedDataSet(String category){
        ArrayList<Place> filteredData = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                if (dataSet.get(i).getCategory().equals(category)) {
                    filteredData.add(dataSet.get(i));
                }
            }

        return filteredData;
    }
}
