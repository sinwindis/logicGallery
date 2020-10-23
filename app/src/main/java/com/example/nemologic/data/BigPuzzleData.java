package com.example.nemologic.data;

import android.graphics.Bitmap;

public class BigPuzzleData {

    public int id;
    public String name;
    public Bitmap bitmap;
    public int width;
    public int height;
    public int progress;

    public BigPuzzleData(int id, String name, Bitmap bitmap, int width, int height, int progress)
    {
        this.id = id;
        this.name = name;
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
    }
}
