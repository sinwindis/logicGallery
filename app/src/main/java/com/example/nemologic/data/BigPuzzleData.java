package com.example.nemologic.data;

import android.graphics.Bitmap;

public class BigPuzzleData {

    public int id;
    public String a_name;
    public String p_name;
    public Bitmap bitmap;
    public int width;
    public int height;
    public int l_width;
    public int l_height;
    public int progress;
    public boolean custom;

    public BigPuzzleData(int id, String a_name, String p_name, Bitmap bitmap, int width, int height, int l_width, int l_height, int progress, boolean custom)
    {
        this.id = id;
        this.a_name = a_name;
        this.p_name = p_name;
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.l_height = l_height;
        this.l_width = l_width;
        this.custom = custom;
        this.progress = progress;
    }
}
