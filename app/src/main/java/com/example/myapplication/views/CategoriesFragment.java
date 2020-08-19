package com.example.myapplication.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Place;
import com.example.myapplication.viewmodels.CategoryListViewModel;
import com.example.myapplication.views.adapters.MyRecyclerViewAdapter;
import java.util.List;

public class CategoriesFragment extends Fragment {
    public static final String PLACE_NAME = "placeName";
    public static final String PLACE_ID = "placeId";
    public static final String PLACE_CATEGORY = "placeCategory";
    public static final String PLACE_NUMBER = "1";
    private static final String PREFERENCES_LANGUAGE_LABEL = "Language";
    private static final String PLACE_INFO_LABEL = "info";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories_fragment_layout, container, false);
    }

    public static CategoriesFragment newInstance(String category) {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        args.putString(PLACE_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        String category = getArguments().getString(PLACE_CATEGORY);
        RecyclerView recyclerViewPlaces = view.findViewById(R.id.list_places);
        CategoryListViewModel gameDetailsViewModel = new ViewModelProvider(requireActivity()).get(CategoryListViewModel.class);
        List<Place> data = gameDetailsViewModel.getCategorizedDataSet(category);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPlaces.setLayoutManager(layoutManager);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getActivity(), data, category);
        setAdapterItemPressed(adapter);
        recyclerViewPlaces.setAdapter(adapter);
    }

    private void setAdapterItemPressed(MyRecyclerViewAdapter adapter){
        adapter.setClickListener((view1, position, place) -> {
            String sendPosition = Integer.toString(position);
            Intent intent = new Intent(getContext(), ItemDetailsActivity.class);
            intent.putExtra(PLACE_CATEGORY, place.getCategory());
            intent.putExtra(PLACE_ID, place.getPlaceid());
            intent.putExtra(PLACE_NAME, place.getName());
            intent.putExtra(PLACE_NUMBER, sendPosition);
            String language = getLanguageSP(requireContext());

            if (language.equals("en")) intent.putExtra(PLACE_INFO_LABEL, place.getInfoen());
            else intent.putExtra(PLACE_INFO_LABEL, place.getInfo());
            startActivity(intent);
        });
    }

    private static String getLanguageSP(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_LANGUAGE_LABEL, Activity.MODE_PRIVATE);
        return preferences.getString(PREFERENCES_LANGUAGE_LABEL, "");
    }
}