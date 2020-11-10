package com.example.nemologic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.SQLException;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManagerOld {

    public int id;
    public String category;
    public String name;
    public byte[] dataSet;
    public byte[] checkedSet;
    public byte[][] checkStack;
    public int height;
    public int width;
    public int progress;
    public int type;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    public LevelPlayManagerOld(int id, String category, String name, int progress, int width, int height, byte[] dataSet, byte[] saveData, int type)
    {
        this.id = id;
        this.category = category;
        this.name = name;
        this.dataSet = dataSet;
        this.height = height;
        this.width = width;
        this.progress = progress;
        this.type = type;
        if(saveData.length == 0)
        {
            //저장 데이터가 없으면 새로 빈 array 할당
            this.checkedSet = new byte[width*height];
            for(int i = 0; i < height*width; i++)
            {
                this.checkedSet[i] = 0;
            }
        }
        else
        {
            //저장 데이터가 있으면 해당 데이터를 파싱해서 저장
            this.checkedSet = saveData;
        }

        this.checkStack = new byte[10][checkedSet.length];
        for(int i = 0; i < checkedSet.length; i++)
        {
            this.checkStack[0][i] = 0;
        }
    }

    public void showDataLog()
    {
        Log.d("LPM", "id: " + id);
        Log.d("LPM", "category: " + category);
        Log.d("LPM", "name: " + name);
        Log.d("LPM", "dataSet: " + Arrays.toString(dataSet));
        Log.d("LPM", "height: " + height);
        Log.d("LPM", "width: " + width);
        Log.d("LPM", "progress: " + progress);
        Log.d("LPM", "checkedSet: " + Arrays.toString(checkedSet));
    }

    public void savePlayData(Context ctx)
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastPlayPref.edit();
        try {
            mDbOpenHelper.open();

            if(type == 0)
            {
                //일반 레벨 저장
                if(progress == 1)
                {
                    //저번 플레이 저장
                    mDbOpenHelper.updateLevel(id, 1, checkedSet);
                    //가장 최근에 한 게임 데이터 갱신하기

                    editor.putInt("id", id);
                }

                else if(progress == 2)
                {
                    //게임 완료
                    mDbOpenHelper.updateLevel(id, 2, null);
                    //가장 최근에 한 게임 데이터 없애기

                    editor.putInt("id", -1);
                }
            }
            else if(type == 1)
            {
                //빅퍼즐 저장하기
                if(progress == 1)
                {
                    //저번 플레이 저장
                    mDbOpenHelper.updateBigLevel(id, 1, checkedSet);

                    editor.putInt("id", id);
                }

                else if(progress == 2)
                {
                    //게임 완료
                    mDbOpenHelper.updateBigLevel(id, 2, null);
                    //가장 최근에 한 게임 데이터 없애기

                    editor.putInt("id", -1);
                }
            }

            editor.putInt("type", type);
            editor.apply();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();


    }

    private void expandStackSize()
    {
        byte[][] stackTemp = new byte[checkStack.length][this.height*this.width];

        for(int i = 0; i < checkStack.length; i++)
        {
            System.arraycopy(checkStack[i], 0, stackTemp[i], 0, checkStack[i].length);
        }

        checkStack = new byte[stackTemp.length*2][checkedSet.length];

        for(int i = 0; i < stackTemp.length; i++)
        {
            System.arraycopy(stackTemp[i], 0, checkStack[i], 0, checkStack[i].length);
        }
    }

    public void pushCheckStack()
    {
        if(Arrays.equals(checkStack[stackNum], checkedSet))
            return;

        stackNum++;
        stackMaxNum = stackNum;

        if(stackNum == checkStack.length - 1)
            expandStackSize();

        System.arraycopy(checkedSet, 0, checkStack[stackNum], 0, checkedSet.length);
    }

    public void prevCheckStack()
    {
        if(stackNum == 0)
            return;
        stackNum--;

        System.arraycopy(checkStack[stackNum], 0, checkedSet, 0, checkedSet.length);
    }

    public void nextCheckStack()
    {
        if(stackNum == stackMaxNum)
            return;

        stackNum++;

        System.arraycopy(checkStack[stackNum], 0, checkedSet, 0, checkedSet.length);
    }

    public boolean isGameEnd()
    {

        for(int i = 0; i < height*width; i++)
        {
            if(dataSet[i] == 1 && checkedSet[i] != 1)
            {
                return false;
            }
            else if(dataSet[i] != 1 && checkedSet[i] == 1)
            {
                return false;
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
