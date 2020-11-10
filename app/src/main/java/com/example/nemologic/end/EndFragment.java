package com.example.nemologic.end;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
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

import com.example.nemologic.R;
import com.example.nemologic.data.CustomParser;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringGetter;

import java.sql.SQLException;
import java.util.Arrays;

public class EndFragment extends Fragment {

    private Context ctx;

    private String name;
    private int id;
    private int p_id;
    private int width;
    private int height;
    private int maxLength;
    private int timeDelay;

    private Bitmap bitmap;
    private Bitmap colorBitmap;

    Button btn_continue;

    private RecyclerView rv_board;


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
        byte[] dataSet;
        byte[] colorSet;

        //빅 레벨
        Cursor bigLevelCursor =  mDbOpenHelper.getBigLevelsCursorById(id);
        bigLevelCursor.moveToNext();

        p_id = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));
        width = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
        height = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
        colorSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));
        dataSet = bigLevelCursor.getBlob(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));

        name = StringGetter.p_name.get(p_id);

        colorBitmap = BitmapFactory.decodeByteArray(colorSet, 0, colorSet.length);
        bitmap = CustomParser.parseDataSetByteArrayToBitmap(dataSet, width, height);

        mDbOpenHelper.close();

        tv_name.setText(name);

        rv_board.setLayoutManager(new GridLayoutManager(this.getContext(), width));
        rv_board.setAdapter(new RvEndBoardAdapter(bitmap));

        maxLength = Math.max(width, height);
        timeDelay = 1000 / (width + height - 1);

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
                        int color = colorBitmap.getPixel(pos-i, i);

                        iv.setAlpha(0F);
                        iv.setBackgroundColor(color);
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