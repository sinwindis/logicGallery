package com.example.nemologic.data;

import android.util.Log;

import java.util.Arrays;

public class LevelData {

    private String name;
    private int progress;
    private int[][] dataSet;
    private int[][] saveData;


    public LevelData(String name, int width, int height, int progress, String dataSet, String saveData)
    {
        this.name = name;
        this.dataSet = parseDataSet(dataSet, width, height);
        this.progress = progress;
        if(progress == 1)
        {
            //예전에 플레이하던 레벨의 경우에만 세이브데이터를 파싱해서 저장한다.
            this.saveData = parseDataSet(saveData, width, height);
        }

    }

    private int[][] parseDataSet(String dataSet, int width, int height)
    {
        String[] rawTemp = dataSet.split(" ");
        int[][] dataTemp = new int[height][width];

        this.dataSet = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                dataTemp[y][x] = Integer.parseInt(rawTemp[x + y*width]);
            }
        }

        return dataTemp;
    }


    public int[][] getDataSet()
    {
        return this.dataSet;
    }

    public String getName()
    {
        return this.name;
    }

    public int getProgress() {return this.progress;}

    public int[][] getSaveData() { return this.saveData;}
}
