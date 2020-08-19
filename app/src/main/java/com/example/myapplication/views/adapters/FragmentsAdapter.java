package com.example.myapplication.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.views.CategoriesFragment;

public class FragmentsAdapter extends FragmentStateAdapter {
    private static final int ITEMS_COUNT = 6;
    private String[] categories = {"relaks", "restauracje", "hotel", "atrakcje", "szpital", "uniwersytet"};
    public FragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CategoriesFragment.newInstance(categories[position]);
    }

    @Override
    public int getItemCount() {
        return ITEMS_COUNT;
    }
}
