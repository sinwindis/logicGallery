package com.example.nemologic.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class CustomParser {

    public CustomParser()
    {

    }

    public static int[][] parseColorSetByteArrayToIntArray(byte[] colorSet)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(colorSet, 0, colorSet.length);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] colorArray = new int[width*height];

        bitmap.getPixels(colorArray, 0, width, 0, 0, width, height);

        Log.d("CustomParser", "colorArray: " + Arrays.toString(colorArray));

        int[][] colorIntArray = new int[bitmap.getHeight()][bitmap.getWidth()];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                colorIntArray[y][x] = colorArray[y*width + x];
            }
        }

        Log.d("CustomParser", "colorIntArray: " + Arrays.deepToString(colorIntArray));

        return colorIntArray;
    }

    public static byte[] parseDataSetStringToByteArray(String dataSet)
    {
        int splitNum = dataSet.length();

        String[] strSplit = new String[splitNum];

        for(int i = 0; i < splitNum; i++)
        {
            strSplit[i] = dataSet.substring(i, i+1);
            //Log.d("PARSER", "strSplit[" + i + "]: " + strSplit[i]);
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

    public static byte[] parseSaveDataArrayToByteArray(int[][] saveData)
    {
        int height = saveData.length;
        int width = saveData[0].length;

        byte[] bytes = new byte[width*height];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                bytes[y*width + x] = (byte) saveData[y][x];
            }
        }

        return bytes;
    }

    public static Bitmap parseColorSetStringToBitmap(String dataSet, int width, int height)
    {
        int size = 6;
        int splitNum = dataSet.length()/size;

        String[] strSplit = new String[splitNum];

        for(int i = 0; i < splitNum; i++)
        {
            strSplit[i] = dataSet.substring(i*size, (i+1)*size);
        }

        int[] colors = new int[strSplit.length];

        for(int i = 0; i < strSplit.length; i++)
        {
            colors[i] = Integer.parseInt(strSplit[i], 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);

        return bitmap;
    }

    public static Bitmap parseDataSetStringToBitmap(String dataSet, int width, int height)
    {
        int splitNum = dataSet.length();

        String[] strSplit = new String[splitNum];

        for(int i = 0; i < splitNum; i++)
        {
            strSplit[i] = dataSet.substring(i, i+1);
        }

        int[] colors = new int[strSplit.length];

        final int WHITE = Integer.parseInt("ffffff", 16);
        final int BLACK = Integer.parseInt("000000", 16);

        for(int i = 0; i < strSplit.length; i++)
        {
            if(strSplit[i] == "1")
            {
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

    public static Bitmap parseDataSetByteArrayToBitmap(byte[] dataSet, int width, int height)
    {
        int[] colors = new int[dataSet.length];

        final int WHITE = Integer.parseInt("ffffff", 16);
        final int BLACK = Integer.parseInt("000000", 16);

        for(int i = 0; i < dataSet.length; i++)
        {
            if(dataSet[i] == 1)
            {
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

    public static byte[] parseColorSetStringToByteArray(String dataSet, int width, int height)
    {
        Bitmap bitmap = parseColorSetStringToBitmap(dataSet, width, height);

        byte[] bytes;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        bytes = stream.toByteArray();

        return bytes;
    }

    public static String[] getRGBAFromString(String strData)
    {
        String[] ret = new String[4];

        switch (strData.length())
        {
            case 6:
                ret[0] = strData.substring(0, 2);
                ret[1] = strData.substring(2, 4);
                ret[2] = strData.substring(4, 6);
                ret[3] = "FF";
                break;
            case 7:
                ret[0] = strData.substring(1, 3);
                ret[1] = strData.substring(3, 5);
                ret[2] = strData.substring(5, 7);
                ret[3] = "FF";
                break;
            case 8:
                ret[3] = strData.substring(0, 2);
                ret[0] = strData.substring(2, 4);
                ret[1] = strData.substring(4, 6);
                ret[2] = strData.substring(6, 8);
                break;
            case 9:
                ret[3] = strData.substring(1, 3);
                ret[0] = strData.substring(3, 5);
                ret[1] = strData.substring(5, 7);
                ret[2] = strData.substring(7, 9);
                break;
        }
        return ret;
    }

    public static int[][] getParsedColorSet(String strData, int width, int height)
    {
        int[][] dataTemp = new int[height][width];

        String[] rawTemp = strData.split(" ");

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                dataTemp[y][x] = Integer.parseInt(rawTemp[x + y*width], 16);
            }
        }

        return dataTemp;
    }

    public static int[][] getParsedSaveData(String strData, int width, int height)
    {
        int[][] colorTemp = new int[height][width];

        if(strData.equals(""))
            return colorTemp;

        String[] rawTemp = strData.split(" ");

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                switch (rawTemp[x + y*width])
                {
                    case "0":
                    case "2":
                        //공백이거나 x 표인 경우 colorSet white 를 출력한다.
                        colorTemp[y][x] = Integer.parseInt("ffffff", 16);
                        break;
                    case "1":
                        //체크된 칸의 경우 colorSet black 을 출력한다.
                        colorTemp[y][x] = Integer.parseInt("000000", 16);

                }
            }
        }

        return colorTemp;
    }



    public static String parseDataSetToString(int[][] dataSet, int height, int width)
    {
        StringBuilder saveData = new StringBuilder();

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                saveData.append(dataSet[y][x]).append(" ");
            }
        }

        return saveData.toString();
    }

    public static String parseDataArrayToString(int[] dataSet)
    {
        StringBuilder saveData = new StringBuilder();

        for (int value : dataSet) {
            saveData.append(value).append(" ");
        }

        return saveData.toString();
    }

    public static String parseColorSetToString(int[][] dataSet, int height, int width)
    {
        StringBuilder saveData = new StringBuilder();

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                String colorStr = Integer.toHexString(dataSet[y][x]);
                while(colorStr.length() < 6)
                {
                    colorStr = "0" + colorStr;
                }
                if(colorStr.length() > 6)
                {
                    colorStr = colorStr.substring(colorStr.length() - 6);
                }

                saveData.append(colorStr).append(" ");
            }
        }

        return saveData.toString();
    }

    public static String parseColorArrayToString(int[] dataSet)
    {
        StringBuilder saveData = new StringBuilder();

        for (int value : dataSet) {
            String colorStr = Integer.toHexString(value);
            while (colorStr.length() < 6) {
                colorStr = "0" + colorStr;
            }
            if (colorStr.length() > 6) {
                colorStr = colorStr.substring(colorStr.length() - 6);
            }

            saveData.append(colorStr).append(" ");
        }

        return saveData.toString();
    }
}
