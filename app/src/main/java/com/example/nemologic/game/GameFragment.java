package com.example.nemologic.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.option.OptionDialog;
import com.example.nemologic.tutorial.TutorialDialog;

import java.sql.SQLException;

public class GameFragment extends Fragment {

    private Context ctx;
    private Context mainActivityContext;
    LevelPlayManager lpm;

    View fragmentView;

    GameBoard gameBoard;
    
    private TextView tv_title;

    private int id;

    public GameFragment(Context ctx) {
        mainActivityContext = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_game, container, false);
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
        }

        int width;
        int height;
        int progress;
        byte[] dataSet;
        byte[] saveData;
        
        Cursor levelCursor;

        levelCursor = mDbOpenHelper.getBigLevelsCursorById(id);
        levelCursor.moveToNext();

        int p_id = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));
        String name = StringGetter.p_name.get(p_id);

        Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

        bigPuzzleCursor.moveToNext();

        width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
        height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
        progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
        dataSet = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
        saveData = new byte[0];

        switch (progress)
        {
            case 0:
                progress = 1;
                break;
            case 1:
            case 3:
                saveData = levelCursor.getBlob(levelCursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));
                break;
            case 2:
                progress = 3;
                break;
        }

        lpm = new LevelPlayManager(id, p_id, name, progress, width, height, dataSet, saveData);

        mDbOpenHelper.close();

        tv_title = fragmentView.findViewById(R.id.tv_title);
        tv_title.setText(name);

        //gameBoard 를 제작한다.
        gameBoard = new GameBoard(mainActivityContext, fragmentView, lpm);

        gameBoard.makeGameBoard();

        setButtonListeners();

        return fragmentView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonListeners()
    {

        //버튼 이벤트
        //토글 버튼
        final ImageView img_toggle = fragmentView.findViewById(R.id.img_toggle);

        ButtonAnimation.setButtonAnimationShadow(img_toggle);

        img_toggle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int touchMode = gameBoard.func_toggle();

                if(touchMode == 1)
                {
                    ((ImageView)view).setImageResource(R.drawable.background_btn_x);
                }
                else
                {
                    ((ImageView)view).setImageResource(R.drawable.background_btn_o);
                }
                
            }
        });

        //다음 스택 버튼
        ImageView img_next = fragmentView.findViewById(R.id.img_nextstack);
        ButtonAnimation.setButtonAnimationShadow(img_next);

        img_next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                gameBoard.func_nextStack();
            }
        });

        ImageView img_prev = fragmentView.findViewById(R.id.img_prevstack);
        ButtonAnimation.setButtonAnimationShadow(img_prev);

        img_prev.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameBoard.func_prevStack();
            }
        });

        //힌트 버튼
        ImageView img_hint = fragmentView.findViewById(R.id.img_hint);
        ButtonAnimation.setButtonAnimationShadow(img_hint);

        img_hint.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameBoard.func_hint();
                img_toggle.setImageResource(R.drawable.ic_hint);
            }
        });

        //튜토리얼 버튼
        ImageView img_tutorial = fragmentView.findViewById(R.id.img_tutorial);

        ButtonAnimation.setButtonAnimationNormal(img_tutorial);
        img_tutorial.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                TutorialDialog tutorialDialog = new TutorialDialog();

                tutorialDialog.makeDialog(getActivity());
                tutorialDialog.dialog.show();
                tutorialDialog.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            }
        });

        //옵션 버튼
        ImageView img_option = fragmentView.findViewById(R.id.img_option);
        ButtonAnimation.setButtonAnimationNormal(img_option);

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                OptionDialog optionDialog = new OptionDialog();

                optionDialog.makeDialog(getActivity());
                optionDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        gameBoard.loadPref();
                    }
                });
                optionDialog.dialog.show();
                optionDialog.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });

        //뒤로가기 버튼
        ImageView img_back = fragmentView.findViewById(R.id.img_back);
        ButtonAnimation.setButtonAnimationNormal(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();

            }
        });
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

        gameBoard.saveGame();
    }
}