package com.example.nemologic.data;

import java.util.ArrayList;

public class CategoryData {

    private String name;
    private int levelNum;
    private int clearNum;

    public CategoryData(String name, int numOfLevels, int numOfClears)
    {
        this.name = name;
        this.levelNum = numOfLevels;
        this.clearNum = numOfClears;
    }

    public String getName()
    {
        return this.name;
    }

    public int getLevelNum() { return this.levelNum; }
}
