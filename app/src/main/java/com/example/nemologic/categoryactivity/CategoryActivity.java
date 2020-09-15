package com.example.nemologic.categoryactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

public class CategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        RecyclerView rv_level = findViewById(R.id.rv_category);

        String[] dummy_data = new String[10];

        dummy_data[0] = "animal";
        dummy_data[1] = "car";
        dummy_data[2] = "food";
        dummy_data[3] = "country";
        dummy_data[4] = "game";
        dummy_data[5] = "school";
        dummy_data[6] = "insect";
        dummy_data[7] = "instrument";
        dummy_data[8] = "dummy";
        dummy_data[9] = "horror";

        rv_level.setLayoutManager(new LinearLayoutManager(this));
        rv_level.setAdapter(new RvCategoryAdapter(this, dummy_data));
    }
}