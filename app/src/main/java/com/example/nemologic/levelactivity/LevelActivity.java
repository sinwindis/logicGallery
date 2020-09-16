package com.example.nemologic.levelactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.CategoryData;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.levelPlayManager;

import java.util.ArrayList;
import java.util.Objects;

public class LevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        TextView tv_category = findViewById(R.id.tv_item_level_category);
        RecyclerView rv_level = findViewById(R.id.rv_level);

        int categoryPos = Objects.requireNonNull(getIntent().getExtras()).getInt("pos");

//        CategoryData categoryData = DataManager.loadCategory(this).get(categoryPos);
//        ArrayList<LevelData> levels = DataManager.loadLevel(this, categoryPos);
//
//        String categoryName = categoryData.getName();
//        tv_category.setText(categoryName);
//
//
//
//        levelPlayManager[] lpms = new levelPlayManager[categoryData.getLevelNum()];
//
//        for(int i = 0; i < lpms.length; i++)
//        {
//            LevelData levelTemp = levels.get(i);
//            lpms[i] = new levelPlayManager(levelTemp.getName(), levelTemp.getDataSet());
//        }
//
//        rv_level.setLayoutManager(new LinearLayoutManager(this));
//        rv_level.setAdapter(new RvLevelAdapter(this, lpms));
    }
}