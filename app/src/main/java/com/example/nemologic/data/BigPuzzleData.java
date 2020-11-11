package com.example.nemologic.data;

import android.graphics.Bitmap;

public class BigPuzzleData {

    public int id;
    public int a_id;
    public String name;
    public Bitmap bitmap;
    public int width;
    public int height;
    public int l_width;
    public int l_height;
    public int progress;
    public int custom;

    public BigPuzzleData(int id, int a_id, String name, Bitmap bitmap, int width, int height, int l_width, int l_height, int progress, int custom)
    {
        this.id = id;
        this.a_id = a_id;
        this.name = name;
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.l_height = l_height;
        this.l_width = l_width;
        this.custom = custom;
        this.progress = progress;
    }
}
