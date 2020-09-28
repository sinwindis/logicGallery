package com.example.nemologic.level;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.BoardItemTouchListener;

import java.sql.SQLException;

public class LevelFragment extends Fragment {

    private Context ctx;

    public LevelFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_level, container, false);

        final RecyclerView rv_level = fragmentView.findViewById(R.id.rv_level);

        String category = "";

        if(getArguments() != null){
            category = getArguments().getString("category"); // 전달한 key 값
        }

        TextView tv_categoryName = fragmentView.findViewById(R.id.tv_category_title);

        tv_categoryName.setText(category);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Cursor levelCursor =  mDbOpenHelper.getLevelCursorByCategory(category);
        LevelData[] levelData = new LevelData[levelCursor.getCount()];
        int fullCount = 0;
        int clearCount = 0;

        while(levelCursor.moveToNext()) {

            String name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.NAME));
            int width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
            int height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));
            int progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.PROGRESS));
            String dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.DATASET));
            String saveData = "";
            if(progress == 1)
                saveData = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.SAVEDATA));

            levelData[fullCount] = new LevelData(category, name, width, height, progress, dataSet, saveData);
            fullCount++;
            if(progress == 2)
            {
                clearCount++;
            }
        }
        mDbOpenHelper.close();

        ((TextView)fragmentView.findViewById(R.id.tv_level_num)).setText(clearCount + "/" + fullCount);

        int rowItemNum = 3;

        rv_level.setLayoutManager(new GridLayoutManager(ctx, rowItemNum));
        rv_level.setAdapter(new RvLevelAdapter(ctx, levelData, rowItemNum));


        rv_level.addOnItemTouchListener(new LevelItemTouchListener("touchable") {

            @Override
            public void onDownTouchableView(int pos) {

                rv_level.getChildAt(pos).setAlpha(0.5F);
            }

            @Override
            public void onClickUp(int pos)
            {
                rv_level.getChildAt(pos).setAlpha(1.0F);
            }


        });


        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        img_back.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
                        getFragmentManager().popBackStackImmediate();
                }
                return false;
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            }
        });

        return fragmentView;
    }
}