package com.example.nemologic.data;

import android.util.Log;

import java.util.Arrays;

public class StringParser {

    public StringParser()
    {

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
}
