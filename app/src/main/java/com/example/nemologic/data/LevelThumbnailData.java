package com.example.nemologic.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Arrays;

public class LevelThumbnailData {

    private int id;
    private String name;
    private String category;
    private int width;
    private int height;
    private int progress;
    private byte[] dataSet;
    private byte[] saveData;
    private Bitmap colorBitmap;
    private Bitmap saveBitmap;
    private int type;
    private int custom;


    public LevelThumbnailData(int id, String category, String name, int width, int height, int progress, byte[] dataSet, byte[] saveData, byte[] colorSet, int type, int custom)
    {
        this.id = id;
        this.category = category;
        this.name = name;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
        this.type = type;
        this.custom = custom;
        if(progress == 1)
        {
            //예전에 플레이하던 레벨의 경우에만 세이브데이터를 파싱해서 저장한다.
            this.saveData = saveData;
            int[] intColorData = new int[height*width];
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    int color;
                    if(saveData[y*width + x] == 1)
                    {
                        //체크된 칸은 검은색 컬러
                        color = Integer.parseInt("000000", 16);
                    }
                    else
                    {
                        //아니면 하얀색
                        color = Integer.parseInt("ffffff", 16);
                    }
                    intColorData[y*width + x] = color;
                }
            }

            saveBitmap = Bitmap.createBitmap(intColorData, width, height, Bitmap.Config.RGB_565);
        }

        colorBitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length);
    }

    public void showDataLog()
    {
        Log.d("LevelThumbnailData", "id: " + id);
        Log.d("LevelThumbnailData", "name: " + name);
        Log.d("LevelThumbnailData", "category: " + category);
        Log.d("LevelThumbnailData", "width: " + width);
        Log.d("LevelThumbnailData", "height: " + height);
        Log.d("LevelThumbnailData", "progress: " + progress);
        Log.d("LevelThumbnailData", "dataSet: " + Arrays.toString(dataSet));
        Log.d("LevelThumbnailData", "saveData: " + Arrays.toString(saveData));
        Log.d("LevelThumbnailData", "colorBitmap: " + colorBitmap);
        Log.d("LevelThumbnailData", "saveBitmap: " + saveBitmap);
    }

    public int getId() {return this.id;}
    public String getCategory() {return this.category;}

    public String getName()
    {
        return this.name;
    }

    public int getProgress() {return this.progress;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
    public int getType() { return this.type; }
    public int getCustom() {return this.custom;}
    public byte[] getSaveData() {return this.saveData;}
    public Bitmap getSaveBitmap() {
        if(progress == 1)
            return saveBitmap;
        else
            return null;
    }
    public Bitmap getColorBitmap() { return colorBitmap; }
    public byte[] getDataSet() {return this.dataSet;}
}
