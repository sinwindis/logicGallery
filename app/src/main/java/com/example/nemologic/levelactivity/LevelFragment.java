package com.example.nemologic.levelactivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.SqlManager;

import java.sql.SQLException;
import java.util.Objects;

public class LevelFragment extends Fragment {

    private Context ctx;

    public LevelFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_level, container, false);

        TextView tv_category = fragmentView.findViewById(R.id.tv_item_level_category);
        RecyclerView rv_level = fragmentView.findViewById(R.id.rv_level);

        String category = "";

        if(getArguments() != null){
            category = getArguments().getString("category"); // 전달한 key 값
        }

        //tv_category.setText(category);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
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

        rv_level.setLayoutManager(new LinearLayoutManager(ctx));
        rv_level.setAdapter(new RvLevelAdapter(ctx, levelData));

        return fragmentView;
    }
}