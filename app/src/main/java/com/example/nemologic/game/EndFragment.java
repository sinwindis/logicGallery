package com.example.nemologic.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringParser;

import java.sql.SQLException;

public class EndFragment extends Fragment {

    private Context ctx;
    LevelPlayManager lpm;

    private String category;
    private String name;
    private int id;
    private int width;
    private int height;
    private int maxLength;
    private int timeDelay;
    private String colorStr;
    private String dataSet;
    private int[][] colorSet;

    private int count;

    private Handler mainHandler;
    private Runnable animationRun;

    Button btn_continue;

    private RecyclerView rv_board;
    private ConstraintLayout cl_frame;


    public EndFragment() {
    }

    @SuppressLint("ClickableViewAccessibility")

    @Override
    public void onResume() {
        super.onResume();

        mainHandler = new Handler();
        animationRun = new Runnable() {
            @Override
            public void run() {

                count = 0;
                showGameEndAnimation();
            }
        };


        mainHandler.postDelayed(animationRun, 300);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_end, container, false);
        ctx = fragmentView.getContext();

        rv_board = fragmentView.findViewById(R.id.rv_board);
        cl_frame = fragmentView.findViewById(R.id.cl_frame);
        btn_continue = fragmentView.findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        //db와 연결해 해당 게임레벨 데이터를 받아올 준비를 한다.
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //bundle 로 받은 category 와 level 의 이름을 String 으로 저장한다.
        if(getArguments() != null){
            id = getArguments().getInt("id");
        }

        //게임레벨과 카테고리의 이름을 이용해 db 에서 데이터를 받아오고 이를 lpm 인스턴스에 대입한다.
        Cursor levelCursor =  mDbOpenHelper.getLevelCursorById(id);
        levelCursor.moveToNext();

        name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.NAME));
        category = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.CATEGORY));
        width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.WIDTH));
        height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.HEIGHT));
        colorStr = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.COLORSET));
        dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.DATASET));

        mDbOpenHelper.close();

        //받아온 컬러 스트링을 컬러셋에 파싱한다.
        colorSet = StringParser.getParsedColorSet(colorStr, width, height);

        rv_board.setLayoutManager(new GridLayoutManager(this.getContext(), width));
        rv_board.setAdapter(new RvEndBoardAdapter(StringParser.getParsedSaveData(dataSet, width, height)));

        maxLength = Math.max(width, height);
        timeDelay = 1000 / (width + height - 1);

        return fragmentView;
    }

    private void showTileSlowly(final int pos, final int count)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for(int i = 0; i <= pos; i++)
                {
                    ImageView iv = (ImageView) rv_board.getChildAt(pos - i + (width * i));
                    if(pos - i < width && i < height)
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

    private void delayedSetBackgroundColor(final int pos)
    {
        if(pos == maxLength*2)
        {
            //애니메이션 종료시
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for(int i = 0; i <= pos; i++)
                {
                    ImageView iv = (ImageView) rv_board.getChildAt(pos - i + (width * i));
                    if(pos - i < width && i < height)
                    {
                        String colorStr = Integer.toHexString(colorSet[i][pos - i]);

                        iv.setAlpha(0F);
                        iv.setBackgroundColor(Color.parseColor("#" + colorStr));
                        iv.setImageResource(R.drawable.background_transparent);
                        showTileSlowly(pos, 0);
                    }


                }

                delayedSetBackgroundColor(pos+1);
            }
        }, timeDelay);
    }

    private void showGameEndAnimation()
    {
        delayedSetBackgroundColor(0);
    }
}