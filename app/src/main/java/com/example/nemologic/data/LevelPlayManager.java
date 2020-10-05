package com.example.nemologic.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    public int id;
    public String category;
    public String name;
    public int[][] dataSet;
    public int[][] colorSet;
    public int[][] checkedSet;
    public int[][][] checkStack;
    public int height;
    public int width;
    public int progress;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    public LevelPlayManager(int id, String category, String name, int progress, int width, int height, String dataSet, String saveData, String colorSet)
    {
        this.id = id;
        this.category = category;
        this.name = name;
        this.dataSet = parseDataSet(dataSet, width, height);
        this.colorSet = parseDataSet(colorSet, width, height);
        this.height = height;
        this.width = width;
        this.progress = progress;
        if(saveData.isEmpty())
        {
            //저장 데이터가 없으면 새로 빈 array 할당
            this.checkedSet = new int[height][width];
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    this.checkedSet[y][x] = 0;
                }
            }
        }
        else
        {
            //저장 데이터가 있으면 해당 데이터를 파싱해서 저장
            this.checkedSet = parseDataSet(saveData, width, height);
        }

        this.checkStack = new int[10][this.height][this.width];
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.checkStack[0][y][x] = 0;
            }
        }
    }

    private int[][] parseDataSet(String dataSet, int width, int height)
    {

        String[] rawTemp = dataSet.split(" ");
        int[][] dataTemp = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                dataTemp[y][x] = Integer.parseInt(rawTemp[x + y*width], 16);
            }
        }

        return dataTemp;
    }

    private String parseDataSetToString(int[][] dataSet)
    {
        StringBuilder saveData = new StringBuilder();

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                saveData.append(dataSet[y][x]).append(" ");
            }
        }

        return saveData.toString();
    }

    public void savePlayData(Context ctx)
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        Log.d("savePlayData", "progress: " + progress);
        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastPlayPref.edit();
        try {
            mDbOpenHelper.open();

            if(progress == 1)
            {
                //저번 플레이 저장
                mDbOpenHelper.updateLevel(id, 1, parseDataSetToString(checkedSet));
                //가장 최근에 한 게임 데이터 갱신하기

                editor.putInt("id", id);
            }

            else if(progress == 2)
            {
                //게임 완료
                mDbOpenHelper.updateLevel(id, 2, "");
                //가장 최근에 한 게임 데이터 없애기

                editor.putInt("id", -1);
            }
            editor.apply();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();


    }

    private void expandStackSize()
    {
        int[][][] stackTemp = new int[checkStack.length][this.height][this.width];

        for(int i = 0; i < checkStack.length; i++)
        {
            for(int y = 0; y < this.height; y++)
            {
                if (this.width >= 0)
                    System.arraycopy(checkStack[i][y], 0, stackTemp[i][y], 0, this.width);
            }
        }

        checkStack = new int[stackTemp.length*2][this.height][this.width];

        for(int i = 0; i < stackTemp.length; i++)
        {
            for(int y = 0; y < this.height; y++)
            {
                if (this.width >= 0)
                    System.arraycopy(stackTemp[i][y], 0, checkStack[i][y], 0, this.width);
            }
        }
    }

    public void pushCheckStack()
    {
        stackNum++;
        stackMaxNum = stackNum;

        if(stackNum == checkStack.length - 1)
            expandStackSize();

        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkedSet[y], 0, checkStack[stackNum][y], 0, width);
        }
    }

    public void prevCheckStack()
    {
        if(stackNum == 0)
            return;
        stackNum--;
        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkStack[stackNum][y], 0, checkedSet[y], 0, width);
        }
    }

    public void nextCheckStack()
    {
        if(stackNum == stackMaxNum)
            return;

        stackNum++;
        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkStack[stackNum][y], 0, checkedSet[y], 0, width);
        }

    }

    public boolean isGameEnd()
    {
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(dataSet[y][x] == 1 && checkedSet[y][x] != 1)
                {
                    return false;
                }
                else if(dataSet[y][x] != 1 && checkedSet[y][x] == 1)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public int getStackNum()
    {
        return stackNum;
    }

    public int getStackMaxNum()
    {
        return stackMaxNum;
    }
}
