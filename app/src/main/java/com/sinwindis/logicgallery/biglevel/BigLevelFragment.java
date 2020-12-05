package com.sinwindis.logicgallery.biglevel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.animation.ButtonAnimation;
import com.sinwindis.logicgallery.data.LevelData;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.SqlManager;
import com.sinwindis.logicgallery.data.StringGetter;
import com.sinwindis.logicgallery.listener.BigLevelItemTouchListener;

import java.sql.SQLException;

public class BigLevelFragment extends Fragment {

    private Context ctx;

    public BigLevelFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_biglevel, container, false);

        final RecyclerView rv_level = fragmentView.findViewById(R.id.rv_biglevel);

        int puzzleWidth;
        int puzzleHeight;
        int fullCount = 0;
        int clearCount = 0;

        LevelData[] levelThumbnailData;

        int p_id = 0;
        boolean custom = false;

        if (getArguments() != null) {
            p_id = getArguments().getInt("p_id"); // 전달한 key 값
            custom = getArguments().getBoolean("custom");

            Log.d("BigLevelFragment", "p_id: " + p_id + " custom: " + custom);
        }

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (custom) {
            Cursor customBigPuzzleCursor = mDbOpenHelper.getCustomBigPuzzleCursorById(p_id);
            customBigPuzzleCursor.moveToNext();

            String name = customBigPuzzleCursor.getString(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_NAME));
            puzzleWidth = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_WIDTH));
            puzzleHeight = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_HEIGHT));

            TextView tv_title = fragmentView.findViewById(R.id.tv_title);

            tv_title.setText(name);

            Cursor customBigLevelCursor = mDbOpenHelper.getCustomBigLevelsCursorByParentId(p_id);
            levelThumbnailData = new LevelData[customBigLevelCursor.getCount()];

            while (customBigLevelCursor.moveToNext()) {

                int id = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.ID));
                int number = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.NUMBER));
                int width = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.WIDTH));
                int height = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.HEIGHT));
                int progress = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.PROGRESS));
                byte[] dataSet = customBigLevelCursor.getBlob(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.DATASET));
                byte[] colorSet = customBigLevelCursor.getBlob(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.COLORSET));
                byte[] saveData = new byte[0];
                if (progress == 1 || progress == 3)
                    saveData = customBigLevelCursor.getBlob(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.SAVEDATA));

                levelThumbnailData[fullCount] = new LevelData(id, p_id, number, width, height, progress, dataSet, saveData, colorSet, true);
                fullCount++;
                if (progress == 2 || progress == 3) {
                    clearCount++;
                }
            }
        } else {
            Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

            bigPuzzleCursor.moveToNext();

            puzzleWidth = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_WIDTH));
            puzzleHeight = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_HEIGHT));

            TextView tv_title = fragmentView.findViewById(R.id.tv_title);

            String name = StringGetter.p_name.get(p_id);
            tv_title.setText(name);

            Cursor bigLevelCursor = mDbOpenHelper.getBigLevelsCursorByParentId(p_id);
            levelThumbnailData = new LevelData[bigLevelCursor.getCount()];

            while (bigLevelCursor.moveToNext()) {

                int id = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.ID));
                int number = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.NUMBER));
                int width = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
                int height = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
                int progress = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
                byte[] dataSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
                byte[] colorSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));
                byte[] saveData = new byte[0];
                if (progress == 1 || progress == 3)
                    saveData = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));

                levelThumbnailData[fullCount] = new LevelData(id, p_id, number, width, height, progress, dataSet, saveData, colorSet, false);
                fullCount++;
                if (progress == 2 || progress == 3) {
                    clearCount++;
                }
            }
        }


        mDbOpenHelper.close();

        ((TextView) fragmentView.findViewById(R.id.tv_level_num)).setText(clearCount + "/" + fullCount);

        //rv_level.addItemDecoration(new BigLevelBorder(ctx, R.drawable.border_gameboard_normal, puzzleWidth));

        final ConstraintLayout cl_rv_cover = fragmentView.findViewById(R.id.cl_rv_cover);

        rv_level.setLayoutManager(new GridLayoutManager(ctx, puzzleWidth));

        final int finalPuzzleWidth = puzzleWidth;
        final int finalPuzzleHeight = puzzleHeight;
        final LevelData[] finalLevelThumbnailData = levelThumbnailData;
        cl_rv_cover.post(new Runnable() {
            @Override
            public void run() {
                int parentWidth = cl_rv_cover.getMeasuredWidth() - rv_level.getPaddingEnd() - rv_level.getPaddingStart();
                int parentHeight = cl_rv_cover.getMeasuredHeight() - rv_level.getPaddingTop() - rv_level.getPaddingBottom();

                rv_level.setAdapter(new RvBigLevelAdapter(ctx, finalLevelThumbnailData, finalPuzzleWidth, finalPuzzleHeight, parentWidth, parentHeight));
            }
        });

        rv_level.addOnItemTouchListener(new BigLevelItemTouchListener(ctx));


        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        ButtonAnimation.setOvalButtonAnimationBlack(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragmentView;
    }
}