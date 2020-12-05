package com.sinwindis.logicgallery.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.sql.SQLException;

public class LevelData {

    private int levelId;
    private int puzzleId;
    private int number;
    private int width;
    private int height;
    private int progress;
    private boolean isCustom;
    private byte[] dataSet;
    private byte[] saveData;
    private byte[] colorSet;


    public LevelData(int levelId, int puzzleId, int number, int width, int height, int progress, byte[] dataSet, byte[] saveData, byte[] colorSet, boolean isCustom) {
        this.levelId = levelId;
        this.puzzleId = puzzleId;
        this.number = number;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
        this.saveData = saveData;
        this.colorSet = colorSet;
        this.isCustom = isCustom;
    }

    public int saveData(Context ctx) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int insertId = (int) mDbOpenHelper.insertBigLevel(puzzleId, number, width, height, dataSet, colorSet);

        Log.d("BigLevelData", "insert res: " + insertId);

        mDbOpenHelper.close();

        return insertId;
    }

    public int getLevelId() {
        return this.levelId;
    }

    public int getPuzzleId() {
        return this.puzzleId;
    }

    public int getNumber() {
        return this.number;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getSaveData() {
        return this.saveData;
    }

    public Bitmap getSaveBitmap() {
        int[] intColorData = new int[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color;
                if (saveData[y * width + x] == 1 || saveData[y * width + x] == -1) {
                    //체크된 칸은 검은색 컬러
                    color = Integer.parseInt("000000", 16);
                } else {
                    //아니면 하얀색
                    color = Integer.parseInt("ffffff", 16);
                }
                intColorData[y * width + x] = color;
            }
        }
        return Bitmap.createBitmap(intColorData, width, height, Bitmap.Config.RGB_565);
    }

    public Bitmap getColorBitmap() {
        return BitmapFactory.decodeByteArray(colorSet, 0, colorSet.length);
    }

    public byte[] getDataSet() {
        return this.dataSet;
    }

    public boolean getCustom() {
        return this.isCustom;
    }
}
