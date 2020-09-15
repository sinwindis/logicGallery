package com.example.nemologic.data;

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
