package com.sinwindis.logicgallery.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.SQLException;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    public int levelId;
    public String name;
    public byte[] dataSet;
    public byte[] checkedSet;
    public byte[][] checkStack;
    public int height;
    public int width;
    public int progress;
    public int puzzleId;
    public boolean[] hint;
    public boolean custom;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    public LevelPlayManager(int levelId, int puzzleId, String name, int progress, int width, int height, byte[] dataSet, byte[] saveData, boolean custom)
    {
        this.levelId = levelId;
        this.puzzleId = puzzleId;
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

    @Override
    public String toString() {
        return "LevelPlayManager{" +
                "id=" + levelId +
                ", name='" + name + '\'' +
                ", dataSet=" + Arrays.toString(dataSet) +
                ", checkedSet=" + Arrays.toString(checkedSet) +
                ", checkStack=" + Arrays.toString(checkStack) +
                ", height=" + height +
                ", width=" + width +
                ", progress=" + progress +
                ", p_id=" + puzzleId +
                ", hint=" + Arrays.toString(hint) +
                ", custom=" + custom +
                ", stackNum=" + stackNum +
                ", stackMaxNum=" + stackMaxNum +
                '}';
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
                    mDbOpenHelper.updateCustomBigLevel(levelId, progress, checkedSet);
                }
                else
                {
                    mDbOpenHelper.updateBigLevel(levelId, progress, checkedSet);
                }

                //가장 최근에 한 게임 데이터 갱신하기

                editor.putInt("id", levelId);
                editor.putBoolean("custom", custom);
            }

            else
            {
                //게임 완료
                mDbOpenHelper.updateBigLevel(levelId, progress, null);
                //해당 레벨의 퍼즐에 해당하는 db 의 progress 값 늘려주기
                mDbOpenHelper.increaseBigPuzzleProgress(puzzleId);

                //가장 최근에 한 게임 데이터 완료 버전으로 저장

                editor.putInt("id", -1* levelId);
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
