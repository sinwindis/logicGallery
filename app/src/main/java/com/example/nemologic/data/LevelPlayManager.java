package com.example.nemologic.data;

import android.util.Log;

public class LevelPlayManager {

    public String name;
    public int[][] dataSet;
    public int[][] checkedSet;
    public int[][][] checkStack;
    public int height;
    public int width;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    private int[][] parseDataSet(String dataSet, int width, int height)
    {
        String[] rawTemp = dataSet.split(" ");
        int[][] dataTemp = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                dataTemp[y][x] = Integer.parseInt(rawTemp[x + y*width]);
            }
        }

        return dataTemp;
    }

    public LevelPlayManager(String name, int width, int height, String dataSet, String saveData)
    {
        this.name = name;
        this.dataSet = parseDataSet(dataSet, width, height);
        this.height = height;
        this.width = width;
        if(saveData.isEmpty())
        {
            //저장 데이터가 없으면 새로 빈 array 할당
            this.checkedSet = new int[height][width];
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
                this.checkedSet[y][x] = 0;
                this.checkStack[0][y][x] = 0;
            }
        }


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

    public int getStackNum()
    {
        return stackNum;
    }

    public int getStackMaxNum()
    {
        return stackMaxNum;
    }
}
