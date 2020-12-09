package com.sinwindis.logicgallery.game;

import android.content.Context;
import android.content.SharedPreferences;

import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.SaveData;

import java.sql.SQLException;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    //Model
    private Board board;
    private LevelDto levelDto;

    public LevelPlayManager() {
    }

    public void setLevelDto(LevelDto levelDto) {
        this.levelDto = levelDto;
    }

    public void saveToDb(Context ctx, int progress) {


    }

    public boolean moveToPrev() {
        return board.moveToPrev();
    }

    public boolean moveToNext() {
        return board.moveToNext();
    }

    public boolean isGameEnd() {
        return this.board.isBoardComplete();
    }

    public int getStackIdx() {
        return board.getStackIdx();
    }

    public int getStackMax() {
        return board.getStackMax();
    }

    public Cell getCell(int y, int x) {
        return board.getCell(y, x);
    }

    public int getHeight() {
        return board.getHeight();
    }

    public int getWidth() {
        return board.getWidth();
    }
}
