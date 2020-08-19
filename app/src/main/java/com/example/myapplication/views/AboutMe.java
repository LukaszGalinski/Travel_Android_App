package com.example.myapplication.views;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AboutMe extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme_layout);

        Button okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> finish());
    }
}


