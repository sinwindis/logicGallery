package com.sinwindis.logicgallery.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class BitmapMaker {

    private static final String COLOR_WHITE = "FFFFFF";
    private static final String COLOR_BLACK = "000000";

    public BitmapMaker() {

    }

    public static Bitmap getGrayScaleBitmap(byte[] blob, int height, int width) {
        int[] intColorData = new int[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color;
                if (blob[y * width + x] == 1 || blob[y * width + x] == -1) {
                    //체크된 칸은 검은색 컬러
                    color = Integer.parseInt(COLOR_BLACK, 16);
                } else {
                    //아니면 하얀색
                    color = Integer.parseInt(COLOR_WHITE, 16);
                }
                intColorData[y * width + x] = color;
            }
        }
        return Bitmap.createBitmap(intColorData, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap getGrayScaleBitmap(byte[][] blob) {

        if (blob.length == 0 || blob[0].length == 0)
            return null;

        int height = blob.length;
        int width = blob[0].length;

        int[] intColorData = new int[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color;
                if (blob[y][x] == 1) {
                    //체크된 칸은 검은색 컬러
                    color = Integer.parseInt(COLOR_BLACK, 16);
                } else {
                    //아니면 하얀색
                    color = Integer.parseInt(COLOR_WHITE, 16);
                }
                intColorData[y * width + x] = color;
            }
        }
        return Bitmap.createBitmap(intColorData, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap getColorBitmap(byte[] blob) {
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }

}
