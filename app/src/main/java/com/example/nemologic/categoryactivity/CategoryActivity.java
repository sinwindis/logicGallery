package com.example.nemologic.categoryactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.CategoryData;
import com.example.nemologic.data.DataManager;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        RecyclerView rv_category = findViewById(R.id.rv_category);

        ArrayList<CategoryData> categories = DataManager.loadCategory(this);

        String[] dummy_data = new String[categories.size()];

        for(int i = 0; i < categories.size(); i++)
        {
            dummy_data[i] = categories.get(i).getName();
        }

        rv_category.setLayoutManager(new LinearLayoutManager(this));
        rv_category.setAdapter(new RvCategoryAdapter(this, dummy_data));
    }
}