package com.example.nemologic.data;

import android.util.Log;

import java.util.Arrays;

public class LevelData {

    private String name;
    private int[][] dataSet;


    public LevelData(String name, int width, int height, String dataSet)
    {
        this.name = name;
        parseDataSet(dataSet, width, height);
    }

    private void parseDataSet(String dataSet, int width, int height)
    {
        String[] dataTemp = dataSet.split(" ");

        this.dataSet = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.dataSet[y][x] = Integer.parseInt(dataTemp[x + y*width]);
            }
        }
    }


    public int[][] getDataSet()
    {
        return this.dataSet;
    }

    public String getName()
    {
        return this.name;
    }
}
