package com.example.nemologic.bigpuzzle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.data.BigPuzzleData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.listener.LevelItemTouchListener;

import java.sql.SQLException;

public class BigPuzzleFragment extends Fragment {

    private Context ctx;

    public BigPuzzleFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_big, container, false);

        final RecyclerView rv_level = fragmentView.findViewById(R.id.rv_bigpuzzle);

        TextView tv_title = fragmentView.findViewById(R.id.tv_category_title);
        tv_title.setText(getResources().getString(R.string.str_big_puzzle));

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor bigPuzzleCursor =  mDbOpenHelper.getBigPuzzleCursor();
        int fullCount = 0;
        int clearCount = 0;

        BigPuzzleData[] bigPuzzleData = new BigPuzzleData[bigPuzzleCursor.getCount()];

        while(bigPuzzleCursor.moveToNext()) {

            int id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.ID));
            String name = bigPuzzleCursor.getString(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.NAME));
            int width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.WIDTH));
            int height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.HEIGHT));
            int progress = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.PROGRESS));
            byte[] colorSet = bigPuzzleCursor.getBlob(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.COLORSET));


            Bitmap bitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length );

            Log.d("BigPuzzleFrag", name);

            bigPuzzleData[fullCount] = new BigPuzzleData(id, name, bitmap, width, height, progress);

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
        rv_level.setAdapter(new RvBigPuzzleAdapter(ctx, bigPuzzleData, rowItemNum));

        rv_level.addOnItemTouchListener(new LevelItemTouchListener(ctx));


        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        ButtonAnimation.setButtonAnimationNormal(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragmentView;
    }
}