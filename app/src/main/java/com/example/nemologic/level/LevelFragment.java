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
import com.example.nemologic.data.LevelThumbnailData;
import com.example.nemologic.data.SqlManager;

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
        LevelThumbnailData[] levelThumbnailData = new LevelThumbnailData[levelCursor.getCount()];
        int fullCount = 0;
        int clearCount = 0;

        while(levelCursor.moveToNext()) {

            int id = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.ID));
            String name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.NAME));
            int width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.WIDTH));
            int height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.HEIGHT));
            int progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.PROGRESS));
            String dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.DATASET));
            String colorSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.COLORSET));
            String saveData = "";
            if(progress == 1)
                saveData = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.SAVEDATA));

            levelThumbnailData[fullCount] = new LevelThumbnailData(id, category, name, width, height, progress, dataSet, saveData, colorSet);
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
        rv_level.setAdapter(new RvLevelAdapter(ctx, levelThumbnailData, rowItemNum));


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

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(null);
                        break;
                }

                return false;
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragmentView;
    }
}