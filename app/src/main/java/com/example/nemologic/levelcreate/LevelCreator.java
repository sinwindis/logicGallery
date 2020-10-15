package com.example.nemologic.levelcreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.example.nemologic.data.StringParser;

import java.io.FileNotFoundException;


public class LevelCreator {

    private Bitmap bitmap;
    private int[] resultPixels;
    private int[] resultDataSet;

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

    public void reduceImageSize(int height, int width)
    {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        resultPixels = new int[resizedBitmap.getWidth()*resizedBitmap.getHeight()];
        resizedBitmap.getPixels(resultPixels, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
    }

    public void saveLevel()
    {

    }

    public void makeDataSet(int height, int width)
    {
        float colorSumAvg = 0;

        reduceImageSize(height, width);

        resultDataSet = new int[height*width];

        for(int i = 0; i < height*width; i++)
        {
            colorSumAvg += getColorAverageNum(resultPixels[i]) / (height*width);
        }

        for(int i = 0; i < height*width; i++)
        {
            if(colorSumAvg > getColorAverageNum(resultPixels[i]))
            {
                resultDataSet[i] = 1;
            }
            else
            {
                resultDataSet[i] = 0;
            }
        }
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public int[] getResultPixels()
    {
        return resultPixels;
    }

    public int[] getResultDataSet()
    {
        return resultDataSet;
    }

}
