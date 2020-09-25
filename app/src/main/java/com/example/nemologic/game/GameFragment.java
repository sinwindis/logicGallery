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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.option.OptionDialog;

import org.w3c.dom.Text;

import java.sql.SQLException;

public class GameFragment extends Fragment {

    private Context ctx;
    LevelPlayManager lpm;

    GameBoard gameBoard;

    private String category;
    private String name;
    private int width;
    private int height;
    private String dataSet;
    private String saveData;


    public GameFragment() {
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

        //bundle로 받은 category와 level의 이름을 String으로 저장한다.
        if(getArguments() != null){
            category = getArguments().getString("category");
            name = getArguments().getString("name");
        }

        //게임레벨과 카테고리의 이름을 이용해 db에서 데이터를 받아오고 이를 lpm 인스턴스에 대입한다.
        Cursor levelCursor =  mDbOpenHelper.getLevelCursorByCategoryAndName(category, name);
        levelCursor.moveToNext();

        name = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.NAME));
        width = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
        height = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));
        int progress = levelCursor.getInt(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.PROGRESS));
        dataSet = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.DATASET));
        saveData = "";
        if(progress == 1)
            saveData = levelCursor.getString(levelCursor.getColumnIndex(SqlManager.CreateLevelDB.SAVEDATA));

        lpm = new LevelPlayManager(category, name, width, height, dataSet, saveData);

        //gameBoard를 제작한다.
        gameBoard = new GameBoard(fragmentView, lpm);

        gameBoard.makeGameBoard();

        TextView tv_name = fragmentView.findViewById(R.id.tv_name);
        tv_name.setText(name);
        ImageView img_option = fragmentView.findViewById(R.id.img_option);

        img_option.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
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
                return false;
            }
        });

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        return fragmentView;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        lpm.savePlayData(ctx);

    }
}