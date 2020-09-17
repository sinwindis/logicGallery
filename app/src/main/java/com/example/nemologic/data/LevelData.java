package com.example.nemologic.data;

import android.util.Log;

import java.util.Arrays;

public class LevelData {

    private String name;
    private int width;
    private int height;
    private int progress;
    private String dataSet;
    private String saveData = "";


    public LevelData(String name, int width, int height, int progress, String dataSet, String saveData)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
        if(progress == 1)
        {
            //예전에 플레이하던 레벨의 경우에만 세이브데이터를 파싱해서 저장한다.
            this.saveData = saveData;
        }

    }


    public String getDataSet()
    {
        return this.dataSet;
    }

    public String getName()
    {
        return this.name;
    }

    public int getProgress() {return this.progress;}

    public String getSaveData() { return this.saveData;}

    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
}
