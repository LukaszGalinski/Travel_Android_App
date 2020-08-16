package com.example.myapplication.tools;

import android.widget.ImageButton;

import com.example.myapplication.R;

public class Background {

    public void setImageBg(ImageButton relax, ImageButton attraction, ImageButton hotel, ImageButton restaurants, ImageButton hospital, ImageButton university) {
        relax.setImageResource(R.drawable.relaks);
        attraction.setImageResource(R.drawable.atraction);
        hotel.setImageResource(R.drawable.hotel);
        restaurants.setImageResource(R.drawable.restaurant);
        hospital.setImageResource(R.drawable.hospital_small);
        university.setImageResource(R.drawable.university_small);
    }
}
