package com.example.myapplication.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.myapplication.models.Rate;
import com.example.myapplication.repositories.NetworkRepository;
import java.util.ArrayList;

public class CommentsViewModel extends ViewModel {
    private ArrayList<Rate> dataSet = null;
    private NetworkRepository networkRepo = null;

    public void instance(String id){
        if (dataSet != null){
            return;
        }
        networkRepo = new NetworkRepository().instance();
        dataSet = networkRepo.getCommentList(id);
    }

    public ArrayList<Rate> getComments(){
        return dataSet;
    }
}
