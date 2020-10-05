package com.example.nemologic.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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

    private int id;

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
        }

        //게임레벨과 카테고리의 이름을 이용해 db에서 데이터를 받아오고 이를 lpm 인스턴스에 대입한다.
        Cursor levelCursor =  mDbOpenHelper.getLevelCursorById(id);
        levelCursor.moveToNext();

        String category = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.CATEGORY));
        String name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.NAME));
        int width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.WIDTH));
        int height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.HEIGHT));
        int progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.LevelDBSql.PROGRESS));
        String dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.DATASET));
        String colorSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.COLORSET));
        String saveData = "";
        if(progress == 1)
            saveData = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.LevelDBSql.SAVEDATA));

        if(progress != 2)
            progress = 1;

        lpm = new LevelPlayManager(id, category, name, progress, width, height, dataSet, saveData, colorSet);

        mDbOpenHelper.close();

        //gameBoard를 제작한다.
        gameBoard = new GameBoard(mainActivityContext, fragmentView, lpm);

        gameBoard.makeGameBoard();

        TextView tv_name = fragmentView.findViewById(R.id.tv_name);
        tv_name.setText(name);

        ImageView img_option = fragmentView.findViewById(R.id.img_option);

        img_option.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_dark));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_bright));
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
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_dark));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_bright));
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