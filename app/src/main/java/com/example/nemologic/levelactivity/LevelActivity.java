package com.example.nemologic.levelactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.levelPlayManager;

import java.util.Objects;

public class LevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        String categoryName = Objects.requireNonNull(getIntent().getExtras()).getString("category");
        TextView tv_category = findViewById(R.id.tv_item_level_category);
        tv_category.setText(categoryName);

        RecyclerView rv_level = findViewById(R.id.rv_level);

        levelPlayManager[] dummyGld = new levelPlayManager[1];

        int[][] dummyData = {{0, 1, 0, 1, 1, 0, 1, 0, 1, 1},
                            {0, 1, 0, 1, 1, 1, 1, 1, 0, 0},
                            {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                            {0, 1, 1, 0, 0, 0, 0, 1, 0, 1},
                            {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                            {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                            {1, 1, 0, 1, 0, 1, 1, 1, 1, 1}};

        dummyGld[0] = new levelPlayManager("dummy", dummyData);

        rv_level.setLayoutManager(new LinearLayoutManager(this));
        rv_level.setAdapter(new RvLevelAdapter(this, dummyGld));
    }
}