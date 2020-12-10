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
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.PuzzleDto;
import com.sinwindis.logicgallery.data.SqlManager;
import com.sinwindis.logicgallery.data.StringGetter;
import com.sinwindis.logicgallery.listener.BigLevelItemTouchListener;

import java.sql.SQLException;
import java.util.ArrayList;

public class BigLevelFragment extends Fragment {

    private final Context ctx;
    private PuzzleDto puzzleDto;
    private ArrayList<LevelDto> levelDtos;
    private int puzzleId;
    private boolean isCustom;

    private RecyclerView rv_level;
    private TextView tv_title;
    private TextView tv_level_num;
    private ConstraintLayout cl_rv_cover;

    public BigLevelFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_biglevel, container, false);

        rv_level = fragmentView.findViewById(R.id.rv_biglevel);
        tv_title = fragmentView.findViewById(R.id.tv_title);
        tv_level_num = fragmentView.findViewById(R.id.tv_level_num);
        cl_rv_cover = fragmentView.findViewById(R.id.cl_rv_cover);

        if (getArguments() != null) {
            puzzleId = getArguments().getInt("p_id"); // 전달한 key 값
            isCustom = getArguments().getBoolean("custom");

            Log.d("BigLevelFragment", "p_id: " + puzzleId + " custom: " + isCustom);
        }
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();

        ImageView img_back = fragmentView.findViewById(R.id.img_back);
        ButtonAnimation.setOvalButtonAnimationBlack(img_back);
        img_back.setOnClickListener(view -> getFragmentManager().popBackStackImmediate());

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isCustom) {
            puzzleDto = mDbOpenHelper.getCustomPuzzleDto(puzzleId);
            levelDtos = mDbOpenHelper.getCustomLevelDtos(puzzleId);
        } else {
            puzzleDto = mDbOpenHelper.getPuzzleDto(puzzleId);
            levelDtos = mDbOpenHelper.getLevelDtos(puzzleId);
        }

        mDbOpenHelper.close();


        tv_title.setText(puzzleDto.getPuzzleName());

        final int finalPuzzleWidth = puzzleDto.getPuzzleWidth();
        final int finalPuzzleHeight = puzzleDto.getPuzzleHeight();
        rv_level.setLayoutManager(new GridLayoutManager(ctx, finalPuzzleWidth));

        StringBuilder strLevelCount = new StringBuilder();
        strLevelCount.append(puzzleDto.getProgress());
        strLevelCount.append("/");
        strLevelCount.append(puzzleDto.getLevelHeight() * puzzleDto.getLevelWidth());
        tv_level_num.setText(strLevelCount);

        cl_rv_cover.post(() -> {
            int parentWidth = cl_rv_cover.getMeasuredWidth() - rv_level.getPaddingEnd() - rv_level.getPaddingStart();
            int parentHeight = cl_rv_cover.getMeasuredHeight() - rv_level.getPaddingTop() - rv_level.getPaddingBottom();

            rv_level.setAdapter(new RvBigLevelAdapter(ctx, levelDtos, finalPuzzleWidth, finalPuzzleHeight, parentWidth, parentHeight));
        });

        rv_level.addOnItemTouchListener(new BigLevelItemTouchListener(ctx));
    }
}