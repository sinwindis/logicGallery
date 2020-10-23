package com.example.nemologic.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.option.OptionDialog;

import java.sql.SQLException;

public class GameFragment extends Fragment {

    private Context ctx;
    private Context mainActivityContext;
    LevelPlayManager lpm;

    GameBoard gameBoard;
    
    private TextView tv_title;

    private int id;
    private int type;

    public GameFragment(Context ctx) {
        mainActivityContext = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_game, container, false);
        ctx = fragmentView.getContext();

        //db와 연결해 해당 게임레벨 데이터를 받아올 준비를 한다.
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //bundle 로 받은 id 를 저장한다.
        if(getArguments() != null){
            id = getArguments().getInt("id");
            type = getArguments().getInt("type");
        }

        //게임 타입에 따라 빅레벨 DB 에서 가져올지, 일반 레벨 DB 에서 가져올지 판단한다.

        String category;
        String name;
        int width;
        int height;
        int progress;
        int custom;
        byte[] dataSet;
        byte[] saveData;
        
        Cursor levelCursor;
        if(type == 1)
        {
            levelCursor = mDbOpenHelper.getBigLevelsCursorById(id);
            levelCursor.moveToNext();

            category = "Big Puzzle";

            int p_id = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));

            Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

            bigPuzzleCursor.moveToNext();

            name = bigPuzzleCursor.getString(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.NAME));
            width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
            height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
            progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
            custom = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.CUSTOM));
            dataSet = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
            saveData = new byte[0];
            if(progress == 1)
                saveData = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));

            if(progress != 2)
                progress = 1;
        }
        else
        {
            levelCursor = mDbOpenHelper.getLevelCursorById(id);
            levelCursor.moveToNext();

            category = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.CATEGORY));
            name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.NAME));
            width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.WIDTH));
            height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.HEIGHT));
            progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.PROGRESS));
            custom = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.CUSTOM));
            dataSet = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.LevelDBSql.DATASET));
            saveData = new byte[0];
            if(progress == 1)
                saveData = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.LevelDBSql.SAVEDATA));

            if(progress != 2)
                progress = 1;
        }

        lpm = new LevelPlayManager(id, category, name, progress, width, height, dataSet, saveData, type);

        mDbOpenHelper.close();

        tv_title = fragmentView.findViewById(R.id.tv_title);
        if(progress == 2 || custom == 1)
        {
            tv_title.setText(name);
        }
        else
        {
            tv_title.setText("???");
        }

        //gameBoard 를 제작한다.
        gameBoard = new GameBoard(mainActivityContext, fragmentView, lpm);

        gameBoard.makeGameBoard();

        ImageView img_option = fragmentView.findViewById(R.id.img_option);

        img_option.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
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

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                OptionDialog optionDialog = new OptionDialog();

                optionDialog.makeOptionDialog(getActivity());
                optionDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        gameBoard.loadOption();
                    }
                });
                optionDialog.dialog.show();
            }
        });

        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        img_back.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
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

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        lpm.savePlayData(ctx);
    }
}