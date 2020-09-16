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
import com.example.nemologic.data.LevelDB;

import java.sql.SQLException;

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

        Cursor cursor =  mDbOpenHelper.getLevelCursor();

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.NAME));
            String category = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.CATEGORY));

            Log.d("getLevelCursor test", name + " " + category);
        }

        cursor =  mDbOpenHelper.getLevelCursorByCategory("dummy1");

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.NAME));
            String category = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.CATEGORY));

            Log.d("getLevelCursorBC test", name + " " + category);
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