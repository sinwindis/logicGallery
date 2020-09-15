package com.example.nemologic.levelactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

import java.util.Objects;

public class LevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        String categoryName = Objects.requireNonNull(getIntent().getExtras()).getString("category");
        TextView tv_category = findViewById(R.id.tv_item_level_category);
        tv_category.setText(categoryName);
    }
}