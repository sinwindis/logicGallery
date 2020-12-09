package com.sinwindis.logicgallery.end;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.BitmapMaker;
import com.sinwindis.logicgallery.data.CustomParser;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.SqlManager;
import com.sinwindis.logicgallery.data.StringGetter;

import java.sql.SQLException;

public class EndFragment extends Fragment {

    private Context ctx;

    private int maxLength;
    private int timeDelay;

    Button btn_continue;

    private RecyclerView rv_board;

    private LevelDto endLevelDto;


    public EndFragment() {
    }

    @SuppressLint("ClickableViewAccessibility")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_end, container, false);
        ctx = fragmentView.getContext();

        rv_board = fragmentView.findViewById(R.id.rv_board);
        btn_continue = fragmentView.findViewById(R.id.btn_continue);
        TextView tv_name = fragmentView.findViewById(R.id.tv_name);

        btn_continue.setOnClickListener(view -> {
            assert getFragmentManager() != null;
            getFragmentManager().popBackStack();
        });

        //db와 연결해 해당 게임레벨 데이터를 받아올 준비를 한다.

        //bundle 로 받은 category 와 level 의 이름을 String 으로 저장한다.
        if (getArguments() != null) {
            endLevelDto = (LevelDto) getArguments().getSerializable("leveldto");
        }

        tv_name.setText(endLevelDto.getName());

        //빅 레벨
        rv_board.setLayoutManager(new GridLayoutManager(this.getContext(), endLevelDto.getWidth()));
        rv_board.setAdapter(new RvEndBoardAdapter(BitmapMaker.getGrayScaleBitmap(endLevelDto.getDataSet(), endLevelDto.getHeight(), endLevelDto.getWidth())));

        maxLength = Math.max(endLevelDto.getWidth(), endLevelDto.getHeight());
        timeDelay = 1000 / (endLevelDto.getWidth() + endLevelDto.getHeight() - 1);

        Handler mainHandler = new Handler();
        Runnable animationRun = this::showGameEndAnimation;


        mainHandler.postDelayed(animationRun, 300);

        return fragmentView;
    }

    private void showTileSlowly(final int pos, final int count) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int i = 0; i <= pos; i++) {
                ImageView iv = (ImageView) rv_board.getChildAt(pos - i + (endLevelDto.getWidth() * i));
                if (pos - i < endLevelDto.getWidth() && i < endLevelDto.getHeight()) {
                    iv.setAlpha(1.0F / (10 - count));
                }
            }

            if (count == 9) {
                return;
            }
            showTileSlowly(pos, count + 1);
        }, 20);
    }

    private void delayedSetBackgroundColor(final int pos) {
        if (pos == maxLength * 2) {
            //애니메이션 종료시
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int i = 0; i <= pos; i++) {
                ImageView iv = (ImageView) rv_board.getChildAt(pos - i + (endLevelDto.getWidth() * i));
                if (pos - i < endLevelDto.getWidth() && i < endLevelDto.getHeight()) {
                    int color = BitmapMaker.getColorBitmap(endLevelDto.getColorBlob()).getPixel(pos - i, i);

                    iv.setAlpha(0F);
                    iv.setBackgroundColor(color);
                    showTileSlowly(pos, 0);
                }
            }

            delayedSetBackgroundColor(pos + 1);
        }, timeDelay);
    }

    private void showGameEndAnimation() {
        delayedSetBackgroundColor(0);
    }
}