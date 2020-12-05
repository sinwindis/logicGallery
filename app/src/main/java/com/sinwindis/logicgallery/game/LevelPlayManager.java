package com.sinwindis.logicgallery.game;

import android.content.Context;
import android.content.SharedPreferences;

import com.sinwindis.logicgallery.data.DbOpenHelper;

import java.sql.SQLException;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    //Model
    private Board board;

    //Property
    public int levelId;
    public String name;
    private int height;
    private int width;
    public int progress;
    public int puzzleId;
    public boolean custom;

    public int stackIdx = 0;
    public int stackMax = 0;

    public LevelPlayManager(int levelId, int puzzleId, String name, int progress, int width, int height, byte[] dataSet, byte[] saveData, boolean custom) {
        this.levelId = levelId;
        this.puzzleId = puzzleId;
        this.name = name;

        this.board = new Board(height, width);

        this.height = height;
        this.width = width;

        //cell correct value initialization
        this.board.setData(dataSet);

        //저장 데이터가 있으면 해당 데이터를 파싱해서 저장
        this.progress = progress;
        this.custom = custom;
        if (saveData != null)
            this.board.pushValues(saveData);

    }

    public void saveToDb(Context ctx) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastPlayPref.edit();
        try {
            mDbOpenHelper.open();

            //레벨 데이터 저장
            if (progress != 2) {
                //완성된 게임이 아니므로 현재 진행 상황을 저장
                byte[] saveData = board.getParsedCells();

                if (custom) {
                    mDbOpenHelper.updateCustomBigLevel(levelId, progress, saveData);
                } else {
                    mDbOpenHelper.updateBigLevel(levelId, progress, saveData);
                }

                //가장 최근에 한 게임 데이터 갱신하기

                editor.putInt("id", levelId);
            } else {
                //게임 완료
                mDbOpenHelper.updateBigLevel(levelId, progress, null);
                //해당 레벨의 퍼즐에 해당하는 db 의 progress 값 늘려주기
                mDbOpenHelper.increaseBigPuzzleProgress(puzzleId);

                //가장 최근에 한 게임 데이터 완료 버전으로 저장

                editor.putInt("id", -1 * levelId);
            }
            editor.putBoolean("custom", custom);

            editor.apply();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();
    }

    public boolean moveToPrev() {
        if (board.moveToPrev()) {
            stackIdx = board.getStackIdx();
            return true;
        }

        return false;
    }

    public boolean moveToNext() {
        if (board.moveToNext()) {
            stackIdx = board.getStackIdx();
            stackIdx = board.getStackMax();
            return true;
        }
        return false;
    }

    public boolean isGameEnd() {
        return this.board.isBoardComplete();
    }

    public int getStackIdx() {
        return stackIdx;
    }

    public int getStackMax() {
        return stackMax;
    }

    public boolean pushValues(byte[] values) {
        return this.board.pushValues(values);
    }

    public byte[] getCurrentValues() {

        byte[] currentValues = new byte[10];

        return currentValues;
    }

    public Cell getCell(int y, int x) {
        return board.getCell(y, x);
    }

    public Cell[][] getCells() {
        return board.getCells();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
