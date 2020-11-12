package com.example.nemologic.biglevel;

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

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.data.BigLevelThumbnailData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.listener.BigLevelItemTouchListener;

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

        int p_id = 0;
        String p_name = "";

        if(getArguments() != null){
            p_id = getArguments().getInt("p_id"); // 전달한 key 값
            p_name = getArguments().getString("p_name");
        }



        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor bigPuzzleCursor =  mDbOpenHelper.getBigPuzzleCursorById(p_id);

        bigPuzzleCursor.moveToNext();

        //name 다시
        String name = StringGetter.p_name.get(p_id);
        final int puzzleWidth = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.WIDTH));
        final int puzzleHeight = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.HEIGHT));

        TextView tv_title = fragmentView.findViewById(R.id.tv_title);

        tv_title.setText(name);

        Cursor bigLevelCursor =  mDbOpenHelper.getBigLevelsCursorByParentId(p_id);
        final BigLevelThumbnailData[] levelThumbnailData = new BigLevelThumbnailData[bigLevelCursor.getCount()];
        int fullCount = 0;
        int clearCount = 0;

        while(bigLevelCursor.moveToNext()) {

            int id = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.ID));
            int width = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
            int height = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
            int progress = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
            byte[] dataSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
            byte[] colorSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));
            byte[] saveData = new byte[0];
            if(progress == 1 || progress == 3)
                saveData = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));

            levelThumbnailData[fullCount] = new BigLevelThumbnailData(id, p_name, width, height, progress, dataSet, saveData, colorSet);
            fullCount++;
            if(progress == 2 || progress == 3)
            {
                clearCount++;
            }
        }
        mDbOpenHelper.close();

        ((TextView)fragmentView.findViewById(R.id.tv_level_num)).setText(clearCount + "/" + fullCount);

        //rv_level.addItemDecoration(new BigLevelBorder(ctx, R.drawable.border_gameboard_normal, puzzleWidth));

        final ConstraintLayout cl_rv_cover = fragmentView.findViewById(R.id.cl_rv_cover);

        rv_level.setLayoutManager(new GridLayoutManager(ctx, puzzleWidth));

        cl_rv_cover.post(new Runnable() {
            @Override
            public void run() {
                int parentSize = cl_rv_cover.getMeasuredWidth();
                Log.d("BigLevelFragment", "parentSize: " + parentSize);

                rv_level.setAdapter(new RvBigLevelAdapter(ctx, levelThumbnailData, puzzleWidth, puzzleHeight, parentSize));
            }
        });

        rv_level.addOnItemTouchListener(new BigLevelItemTouchListener(ctx));


        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        ButtonAnimation.setOvalButtonAnimationNormal(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragmentView;
    }
}