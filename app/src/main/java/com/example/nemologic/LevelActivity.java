package com.example.nemologic;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        String categoryName = getIntent().getExtras().getString("category");
        TextView tv_category = findViewById(R.id.tv_item_level_category);
        tv_category.setText(categoryName);
    }
}