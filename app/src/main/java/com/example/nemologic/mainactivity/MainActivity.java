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

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        //mDbOpenHelper.insertLevel("dummy", "dummyC", 10, 10, new String("1, 1, 1"));
        //mDbOpenHelper.insertLevel("dummy2", "dummy2C", 10, 10, new String("1, 1, 1"));

        Cursor cursor =  mDbOpenHelper.getLevelCursor();

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.NAME));
            String category = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.CATEGORY));

            Log.d("getLevelCursor test", name + " " + category);
        }

        cursor =  mDbOpenHelper.getLevelCursorByCategory("dummyC");

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.NAME));
            String category = cursor.getString(cursor.getColumnIndex(LevelDB.CreateDB.CATEGORY));

            Log.d("getLevelCursor test", name + " " + category);
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