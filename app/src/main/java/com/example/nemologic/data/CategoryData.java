package com.example.nemologic.data;

import java.util.ArrayList;

public class CategoryData {

    private String name;
    private int levelNum;

    public CategoryData(String name, int numOfLevels)
    {
        this.name = name;
        this.levelNum = numOfLevels;
    }

    public String getName()
    {
        return this.name;
    }

    public int getLevelNum() { return this.levelNum; }
}
