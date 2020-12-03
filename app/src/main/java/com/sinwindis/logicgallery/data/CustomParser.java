package com.sinwindis.logicgallery.data;

import android.graphics.Bitmap;
import android.util.Log;

public class CustomParser {

    public CustomParser()
    {

    }

    public static byte[] parseDataSetStringToByteArray(String dataSet)
    {
        int splitNum = dataSet.length();

        String[] strSplit = new String[splitNum];

        for(int i = 0; i < splitNum; i++)
        {
            strSplit[i] = dataSet.substring(i, i+1);
        }

        byte[] bytes = new byte[strSplit.length];

        for(int i = 0; i < strSplit.length; i++)
        {
            if(strSplit[i].equals("0"))
                bytes[i] = 0;
            else
                bytes[i] = 1;
        }

        return bytes;
    }

    public static Bitmap parseDataSetByteArrayToBitmap(byte[] dataSet, int width, int height)
    {
        int[] colors = new int[dataSet.length];

        final int WHITE = Integer.parseInt("ffffff", 16);
        final int BLACK = Integer.parseInt("000000", 16);

        for(int i = 0; i < dataSet.length; i++)
        {
            if(dataSet[i] == 1 || dataSet[i] == -1)
            {
                Log.d("parseDataSet", "dataSet[i]: " + dataSet[i]);
                colors[i] = BLACK;
            }
            else
            {
                colors[i] = WHITE;
            }
        }

        Bitmap image = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);

        return image;
    }
}
