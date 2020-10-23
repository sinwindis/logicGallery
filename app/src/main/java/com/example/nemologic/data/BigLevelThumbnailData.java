package com.example.nemologic.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BigLevelThumbnailData {

    private int id;
    private String name;
    private int width;
    private int height;
    private int progress;
    private byte[] dataSet;
    private byte[] saveData;
    private Bitmap colorBitmap;
    private Bitmap saveBitmap;


    public BigLevelThumbnailData(int id, String name, int width, int height, int progress, byte[] dataSet, byte[] saveData, byte[] colorSet)
    {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
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

        colorBitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length );
    }

    public int getId() {return this.id;}

    public String getName()
    {
        return this.name;
    }

    public int getProgress() {return this.progress;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
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
