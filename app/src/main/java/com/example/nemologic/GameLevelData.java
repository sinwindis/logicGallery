package com.example.nemologic;

import java.util.Arrays;

public class GameLevelData {

    public String name;
    public int[][] dataSet;
    public int[][] checkedSet;
    public int[][][] checkStack;
    public int height;
    public int width;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    public GameLevelData(String name, int[][] dataSet)
    {
        this.name = name;
        this.dataSet = dataSet.clone();
        this.height = dataSet.length;
        this.width = dataSet[0].length;
        this.checkedSet = new int[this.height][this.width];
        this.checkStack = new int[200][this.height][this.width];
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.checkedSet[y][x] = 0;
                this.checkStack[0][y][x] = 0;
            }
        }


    }

    public void pushCheckStack()
    {
        stackNum++;
        stackMaxNum = stackNum;

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                checkStack[stackNum][y][x] = checkedSet[y][x];
            }
        }
    }

    public void prevCheckStack()
    {
        if(stackNum == 0)
            return;
        stackNum--;
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                checkedSet[y][x] = checkStack[stackNum][y][x];
            }
        }
    }

    public void nextCheckStack()
    {
        if(stackNum == stackMaxNum)
            return;

        stackNum++;
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                checkedSet[y][x] = checkStack[stackNum][y][x];
            }
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
