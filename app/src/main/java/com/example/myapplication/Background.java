package com.example.myapplication;

import android.widget.ImageButton;



public class Background implements ChangingBarImage{

    @Override
    public void setImageBg(ImageButton relaks, ImageButton atraction, ImageButton hotel, ImageButton restaurants, ImageButton hospital, ImageButton university) {
        relaks.setImageResource(R.drawable.relaks);
        atraction.setImageResource(R.drawable.atraction);
        hotel.setImageResource(R.drawable.hotel);
        restaurants.setImageResource(R.drawable.restaurant);
        hospital.setImageResource(R.drawable.hospital_small);
        university.setImageResource(R.drawable.university_small);
    }
}
