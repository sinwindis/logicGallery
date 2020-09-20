package com.example.nemologic.levelactivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.LevelPlayManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class LevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        TextView tv_category = findViewById(R.id.tv_item_level_category);
        RecyclerView rv_level = findViewById(R.id.rv_level);

        String category = Objects.requireNonNull(getIntent().getExtras()).getString("category");



        tv_category.setText(category);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Cursor levelCursor =  mDbOpenHelper.getLevelCursorByCategory(category);
        LevelData[] levelData = new LevelData[levelCursor.getCount()];
        int count = 0;

        while(levelCursor.moveToNext()) {

            String name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.NAME));
            int width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
            int height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));
            int progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.PROGRESS));
            String dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.DATASET));
            String saveData = "";
            if(progress == 1)
                saveData = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.SAVEDATA));


            Log.d("LevelActivity", "progress: " + progress);
            Log.d("LevelActivity", "saveData: " + saveData);
            levelData[count] = new LevelData(category, name, width, height, progress, dataSet, saveData);
            count++;
        }

        mDbOpenHelper.close();

        rv_level.setLayoutManager(new LinearLayoutManager(this));
        rv_level.setAdapter(new RvLevelAdapter(this, levelData));
    }
}