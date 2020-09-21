package com.example.nemologic.gameactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.R;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.optionactivity.OptionActivity;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    LevelPlayManager lpm;

    GameBoard gameBoard;

    private String category;
    private String name;
    private int width;
    private int height;
    private String dataSet;
    private String saveData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //db와 연결해 해당 게임레벨 데이터를 받아올 준비를 한다.
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //intent로 받은 category와 level의 이름을 String으로 저장한다.
        category = Objects.requireNonNull(getIntent().getExtras()).getString("category");
        name = Objects.requireNonNull(getIntent().getExtras()).getString("name");

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
        gameBoard = new GameBoard(this, lpm);

        //기존 게임을 이어서 할 지 확인한다.
        AlertDialog.Builder loadDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);

        loadDialog
            .setMessage("기존 데이터를 불러오시겠습니까?")
            .setTitle("데이터 load")
            .setPositiveButton("예", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    gameBoard.makeGameBoard();
                }
            })
            .setNeutralButton("아니오", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    for(int y = 0; y < height; y++)
                    {
                        for(int x = 0; x < width; x++)
                        {
                            lpm.checkedSet[y][x] = 0;
                            gameBoard.makeGameBoard();
                        }
                    }
                }
            })
            .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
            .show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        lpm.savePlayData(this);
    }
}