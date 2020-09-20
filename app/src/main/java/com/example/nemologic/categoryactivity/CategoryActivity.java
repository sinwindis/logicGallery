package com.example.nemologic.categoryactivity;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.CategoryData;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.SqlManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        RecyclerView rv_category = findViewById(R.id.rv_category);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Cursor categoryCursor =  mDbOpenHelper.getCategoryCursor();
        ArrayList<String> categoryArray = new ArrayList<>();

        while(categoryCursor.moveToNext()) {

            String name = categoryCursor.getString(categoryCursor.getColumnIndex(SqlManager.CreateCategoryDB.NAME));

            categoryArray.add(name);
        }

        mDbOpenHelper.close();
        String[] categoryStringArray = new String[categoryCursor.getCount()];

        for(int i = 0; i < categoryArray.size(); i++)
        {
            categoryStringArray[i] = categoryArray.get(i);
        }

        rv_category.setLayoutManager(new LinearLayoutManager(this));
        rv_category.setAdapter(new RvCategoryAdapter(this, categoryStringArray));
    }
}