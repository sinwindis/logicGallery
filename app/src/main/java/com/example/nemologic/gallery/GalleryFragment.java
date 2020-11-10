package com.example.nemologic.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.data.BigPuzzleData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.listener.GalleryItemTouchListener;
import com.example.nemologic.listener.LevelItemTouchListener;

import java.sql.SQLException;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private Context ctx;

    private TextView tv_title;
    private TextView tv_level_num;
    private RecyclerView rv_level;
    private ArrayList<BigPuzzleData> bigPuzzleData;

    private RvGalleryAdapter rvGalleryAdapter;

    int fullCount;
    int clearCount;

    public GalleryFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    private void loadPuzzleData()
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor bigPuzzleCursor =  mDbOpenHelper.getBigPuzzleCursor();

        fullCount = 0;
        clearCount = 0;

        Log.d("GalleryFragment", "item count: " + bigPuzzleCursor.getCount());

        bigPuzzleData = new ArrayList<>();

        while(bigPuzzleCursor.moveToNext()) {

            int id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.ID));
            int a_id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.A_ID));
            String name = StringGetter.p_name.get(id);
            int width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.WIDTH));
            int height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.HEIGHT));
            int progress = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.PROGRESS));

            Log.d("loadPuzzleData", "progress: " + progress);
            byte[] colorSet = bigPuzzleCursor.getBlob(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.COLORSET));
            int custom = 0;

            Bitmap bitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length );

            bigPuzzleData.add(new BigPuzzleData(id, a_id, name, bitmap, width, height, progress, custom));

            fullCount++;
            if(progress == width*height)
            {
                clearCount++;
            }

        }
        mDbOpenHelper.close();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_gallery, container, false);

        rv_level = fragmentView.findViewById(R.id.rv_bigpuzzle);

        tv_title = fragmentView.findViewById(R.id.tv_title);
        tv_level_num = fragmentView.findViewById(R.id.tv_level_num);
        tv_title.setText(getResources().getString(R.string.str_gallery));

        rv_level.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        rv_level.addOnItemTouchListener(new GalleryItemTouchListener(ctx));


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

    @Override
    public void onResume()
    {
        super.onResume();

        loadPuzzleData();

        rvGalleryAdapter = new RvGalleryAdapter(ctx, bigPuzzleData);
        rv_level.setAdapter(rvGalleryAdapter);

        tv_level_num.setText(clearCount + "/" + fullCount);
    }
}