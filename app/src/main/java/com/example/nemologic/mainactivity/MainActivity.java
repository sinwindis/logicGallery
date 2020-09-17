package com.example.nemologic.mainactivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.categoryactivity.CategoryActivity;
import com.example.nemologic.R;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_category = findViewById(R.id.btn_category);

        final Intent intent = new Intent(this, CategoryActivity.class);

        DataManager.loadLevel(this);
        DataManager.loadCategory(this);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        Cursor cursor;

        ArrayList<String> categories = DataManager.getCategoriesFromXml(this);

        for(int i = 0; i < categories.size(); i++)
        {
            String category = categories.get(i);
            cursor =  mDbOpenHelper.getLevelCursorByCategory(category);

            while(cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(SqlManager.CreateLevelDB.NAME));
            }
        }


        mDbOpenHelper.close();

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);



            }
        });

    }
}