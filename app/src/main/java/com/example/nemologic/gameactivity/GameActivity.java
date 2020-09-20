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

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        category = Objects.requireNonNull(getIntent().getExtras()).getString("category");
        name = Objects.requireNonNull(getIntent().getExtras()).getString("name");

        Log.d("GameActivity", "category: " + category + " name: " + name);

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

        gameBoard = new GameBoard(this, lpm);

        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);

        gameBoard.makeGameBoard();

        oDialog.setMessage("기존 데이터를 불러오시겠습니까?")
                .setTitle("데이터 load")
                .setPositiveButton("예", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
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
                                //lpm.checkedSet[height][width] = 0;
                            }
                        }
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();

        gameBoard.refreshBoard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        lpm.savePlayData(this);
    }
}