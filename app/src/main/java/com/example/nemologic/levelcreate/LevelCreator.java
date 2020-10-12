package com.example.nemologic.levelcreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.example.nemologic.data.StringParser;

import java.io.FileNotFoundException;


public class LevelCreator {

    private Bitmap bitmap;
    private int[] pixels;
    private int[][] resultPixels;
    private int[][] resultDataSet;

    public LevelCreator()
    {

    }

    public void loadFile(Context ctx, Uri uri)
    {
        try {
            bitmap = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    private float getColorAverageNum(int color)
    {
        float avgColor = 0;

        String temp = Integer.toHexString(color);
        while(temp.length() < 6)
        {
            temp = "0" + temp;
        }

        if(temp.length() == 6)
        {
            temp = "ff" + temp;
        }
        else if(temp.length() == 7)
        {
            temp = "0" + temp;
        }
        String[] rgbTemp = StringParser.getRGBAFromString(temp);

        avgColor += ((float)Integer.parseInt(rgbTemp[0], 16)/(float)3);
        avgColor += ((float)Integer.parseInt(rgbTemp[1], 16)/(float)3);
        avgColor += ((float)Integer.parseInt(rgbTemp[2], 16)/(float)3);

        return avgColor;
    }

    public void reduceImageSize(int width, int height)
    {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        pixels = new int[resizedBitmap.getWidth()*resizedBitmap.getHeight()];
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
        resultPixels = new int[height][width];


        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                resultPixels[y][x] = pixels[x + y*width];
            }
        }

    }

    public void saveLevel()
    {

    }

    public void makeDataSet()
    {
        float colorSumAvg = 0;
        int height = resultPixels.length;
        int width = resultPixels[0].length;

        resultDataSet = new int[height][width];

        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                colorSumAvg += getColorAverageNum(resultPixels[i][j]) / (height*width);
            }
        }

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(colorSumAvg > getColorAverageNum(resultPixels[y][x]))
                {
                    resultDataSet[y][x] = 1;
                }
                else
                {
                    resultDataSet[y][x] = 0;
                }
            }
        }
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public int[][] getResultPixels()
    {
        return resultPixels;
    }

    public int[][] getResultDataSet()
    {
        return resultDataSet;
    }

}
