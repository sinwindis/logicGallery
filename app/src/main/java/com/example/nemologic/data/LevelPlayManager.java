package com.example.nemologic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.SQLException;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    public int id;
    public String name;
    public byte[] dataSet;
    public byte[] checkedSet;
    public byte[][] checkStack;
    public int height;
    public int width;
    public int progress;
    public int p_id;
    public boolean[] hint;
    public boolean custom;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    public LevelPlayManager(int id, int p_id, String name, int progress, int width, int height, byte[] dataSet, byte[] saveData, boolean custom)
    {
        this.id = id;
        this.p_id = p_id;
        this.name = name;
        this.dataSet = dataSet;
        this.height = height;
        this.width = width;
        this.progress = progress;
        this.hint = new boolean[dataSet.length];
        this.custom = custom;
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
            for(int i = 0; i < checkedSet.length; i++)
            {
                if(checkedSet[i] < 0)
                {
                    hint[i] = true;
                    checkedSet[i] *= -1;
                }
                else
                {
                    hint[i] = false;
                }
            }
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

            //빅퍼즐 저장하기
            if(progress != 2)
            {
                //이번 플레이 저장
                for(int i = 0; i < checkedSet.length; i++)
                {
                    if(hint[i])
                    {
                        checkedSet[i] *= -1;
                    }
                }
                if(custom)
                {
                    mDbOpenHelper.updateCustomBigLevel(id, progress, checkedSet);
                }
                else
                {
                    mDbOpenHelper.updateBigLevel(id, progress, checkedSet);
                }

                //가장 최근에 한 게임 데이터 갱신하기

                editor.putInt("id", id);
                editor.putBoolean("custom", custom);
            }

            else
            {
                //게임 완료
                mDbOpenHelper.updateBigLevel(id, progress, null);
                //해당 레벨의 퍼즐에 해당하는 db 의 progress 값 늘려주기
                mDbOpenHelper.increaseBigPuzzleProgress(p_id);

                //가장 최근에 한 게임 데이터 완료 버전으로 저장

                editor.putInt("id", -1*id);
                editor.putBoolean("custom", custom);
            }

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

        for(int i = 0; i < width*height; i++)
        {
            if(!hint[i])
            {
                checkedSet[i] = checkStack[stackNum][i];
            }
        }
    }

    public void nextCheckStack()
    {
        if(stackNum == stackMaxNum)
            return;

        stackNum++;

        for(int i = 0; i < width*height; i++)
        {
            if(!hint[i])
            {
                checkedSet[i] = checkStack[stackNum][i];
            }
        }
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
