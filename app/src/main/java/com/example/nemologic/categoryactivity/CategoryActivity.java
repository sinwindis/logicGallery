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

        ArrayList<String> categories = DataManager.getCategoriesFromXml(this);

        String[] categoryStringArray = new String[categories.size()];

        for(int i = 0; i < categories.size(); i++)
        {
            categoryStringArray[i] = categories.get(i);
        }

        rv_category.setLayoutManager(new LinearLayoutManager(this));
        rv_category.setAdapter(new RvCategoryAdapter(this, categoryStringArray));
    }
}