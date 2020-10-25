package com.example.nemologic.tutorial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

public class BoardTutorialFragment extends Fragment {

    private Context ctx;

    private int maxLength;
    private int timeDelay;

    Button btn_continue;

    private GridLayoutManager glm;
    private RecyclerView rv_board;
    private RecyclerView rv_column;
    private RecyclerView rv_row;

    private ImageView ll_accent;


    public BoardTutorialFragment() {
    }

    @SuppressLint("ClickableViewAccessibility")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_tutorial_board, container, false);
        ctx = fragmentView.getContext();

        rv_board = fragmentView.findViewById(R.id.rv_board);
        rv_column = fragmentView.findViewById(R.id.rv_column);
        rv_row = fragmentView.findViewById(R.id.rv_row);
        btn_continue = fragmentView.findViewById(R.id.btn_continue);

        ll_accent = fragmentView.findViewById(R.id.iv_accent);

        rv_board.addItemDecoration(new GameBoardBorder(ctx, R.drawable.border_gameboard_normal, 3));
        glm = new GridLayoutManager(this.getContext(), 3);
        rv_board.setLayoutManager(glm);
        rv_board.setAdapter(new RvTutorialBoardAdapter(3, 3));

        rv_column.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_row.setLayoutManager(new LinearLayoutManager(this.getContext()));

        Handler mainHandler = new Handler();
        Runnable animationRun = new Runnable() {
            @Override
            public void run() {

                showGameEndAnimation();
            }
        };


        mainHandler.postDelayed(animationRun, 300);

        return fragmentView;
    }

    private void updateBoardView(byte[][] dataSet)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x< 3; x++)
            {
                ImageView view = (ImageView) glm.findViewByPosition(y*3 + x);

                switch (dataSet[y][x])
                {
                    case 0:
                        view.setImageDrawable(null);
                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    case 1:
                        view.setImageDrawable(null);
                        view.setBackgroundColor(Color.parseColor("#000000"));
                        break;
                    case 2:
                        view.setImageResource(R.drawable.background_x);
                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                }
            }
        }

    }


    private void showTutorial0()
    {
        //scene 0
        //빈 칸

        String[] rowIdx = new String[3];
        String[] columnIdx = new String[3];

        rowIdx[0] = "2";
        rowIdx[1] = "1";
        rowIdx[2] = "2";

        columnIdx[0] = "0";
        columnIdx[1] = "1\n1";
        columnIdx[2] = "3";

        byte[][] scene = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

        updateBoardView(scene);
        rv_column.setAdapter(new RvColumnAdapter(columnIdx));
        rv_row.setAdapter(new RvRowAdapter(rowIdx));
    }
    private void showTutorial1()
    {
        SpannableStringBuilder strTemp = new SpannableStringBuilder();

        //scene 1 ~ 3
        //빈 칸

        String[] rowIdx = new String[3];
        String[] columnIdx = new String[3];

        rowIdx[0] = "2";
        rowIdx[1] = "1";
        rowIdx[2] = "2";

        columnIdx[0] = "0";
        columnIdx[1] = "1\n1";

        strTemp = new SpannableStringBuilder("3");
        strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        columnIdx[2] = "";

        byte[][] scene = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

        updateBoardView(scene);
        rv_column.setAdapter(new RvColumnAdapter(columnIdx));
        rv_row.setAdapter(new RvRowAdapter(rowIdx));

//        rv_column.setAdapter(new RvColumnAdapter());
//        rv_row.setAdapter(new RvRowAdapter());
    }

    private void showTileSlowly(final int pos, final int count)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for(int i = 0; i <= pos; i++)
                {
                    ImageView iv = (ImageView) rv_board.getChildAt(i);
                    {
                        iv.setAlpha(1.0F/(10-count));
                    }
                }

                if(count == 9)
                {
                    return;
                }
                showTileSlowly(pos, count+1);
            }
        }, 20);
    }

    private void showGameEndAnimation()
    {

    }
}