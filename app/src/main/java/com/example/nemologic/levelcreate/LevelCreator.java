package com.example.nemologic.levelcreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.example.nemologic.data.CustomParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;


public class LevelCreator {

    private Bitmap srcBitmap;
    private Bitmap smallBitmap;
    private Bitmap scaledBitmap;

    private float averageColor;

    private byte[] colorBlob;
    private byte[] dataBlob;

    private byte[][] colorBlobs;
    private byte[][] dataBlobs;

    public LevelCreator()
    {

    }

    private void calcAverageColor(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] colors = new int[width * height];

        bitmap.getPixels(colors, 0, width, 0, 0, width, height);

        averageColor = 0;

        for (int color : colors) {
            averageColor += getColorAverageNum(color) / (float) colors.length;
        }
    }

    private float getColorAverageNum(int color)
    {
        String hexColor = String.format("#%08X", color);

        String red = hexColor.substring(3, 5);
        String green = hexColor.substring(5, 7);
        String blue = hexColor.substring(7, 9);

        int intRed = Integer.parseInt(red, 16);
        int intGreen = Integer.parseInt(green, 16);
        int intBlue = Integer.parseInt(blue, 16);

        float ret = ((float) (intRed + intBlue + intGreen)) / (float)3.0;

        return ret;
    }

    private void reduceImageSize(int height, int width)
    {
        smallBitmap = Bitmap.createScaledBitmap(srcBitmap, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        smallBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        colorBlob = stream.toByteArray();

        scaledBitmap = Bitmap.createScaledBitmap(smallBitmap, 400, 400, false);
    }

    public void loadFile(Context ctx, Uri uri)
    {
        try {
            srcBitmap = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void makeSingleDataSet(int height, int width)
    {
        reduceImageSize(height, width);

        calcAverageColor(smallBitmap);

        dataBlob = new byte[height*width];

        int[] pixels = new int[height*width];

        smallBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int i = 0; i < height*width; i++)
        {
            if(averageColor > getColorAverageNum(pixels[i]) )
            {
                dataBlob[i] = 1;
            }
            else
            {
                dataBlob[i] = 0;
            }
        }
    }

    public void makeBigLevelDataSet(int puzzleHeight, int puzzleWidth, int height, int width)
    {
        reduceImageSize(puzzleHeight*height, puzzleWidth*width);

        Bitmap bitmapFrag;

        dataBlobs = new byte[puzzleHeight*puzzleWidth][width*height];
        colorBlobs = new byte[puzzleHeight*puzzleWidth][];

        for(int y = 0; y < puzzleHeight; y++)
        {
            for(int x = 0; x < puzzleWidth; x++)
            {
                bitmapFrag = Bitmap.createBitmap(smallBitmap, x*width, y*height, width, height);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmapFrag.compress(Bitmap.CompressFormat.PNG, 100, stream);

                colorBlobs[y * puzzleWidth + x] = stream.toByteArray();

                calcAverageColor(bitmapFrag);

                int[] pixels = new int[height*width];

                bitmapFrag.getPixels(pixels, 0, width, 0, 0, width, height);

                for(int i = 0; i < height*width; i++)
                {
                    if(averageColor > getColorAverageNum(pixels[i]) )
                    {
                        dataBlobs[y * puzzleWidth + x][i] = 1;
                    }
                    else
                    {
                        dataBlobs[y * puzzleWidth + x][i] = 0;
                    }
                }
            }
        }
    }

    public void showDataBlobLog()
    {
        int width = smallBitmap.getWidth();
        int height = smallBitmap.getHeight();

        String log = "";

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                log += (int)dataBlob[y * width + x];
            }
            log += "\n";
        }

        Log.d("dataBlob", log);
    }

    public void showColorBlobLog()
    {
        int width = smallBitmap.getWidth();
        int height = smallBitmap.getHeight();

        String log = "";

        int[] pixels = new int[width*height];

        smallBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                log += String.format("%06X", pixels[y * width + x]);
            }
            log += "\n";
        }

        Log.d("dataBlob", log);
    }

    public Bitmap getSmallBitmap()
    {
        return smallBitmap;
    }

    public Bitmap getScaledBitmap()
    {
        return scaledBitmap;
    }

    public byte[] getColorBlob() {return colorBlob;}

    public byte[] getDataBlob() {return dataBlob;}

    public byte[][] getColorBlobs() {return colorBlobs;}

    public byte[][] getDataBlobs() {return dataBlobs;}
}
