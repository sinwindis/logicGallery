package com.sinwindis.logicgallery.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;

public class LevelDto implements Serializable {

    private final int levelId;
    private final int puzzleId;
    private final String name;
    private final int number;
    private final int width;
    private final int height;
    private final int progress;
    private final boolean isCustom;
    private final byte[] dataSet;
    private final byte[] saveBlob;
    private final byte[] colorBlob;

    public LevelDto(int levelId, int puzzleId, String name, int number, int width, int height, int progress, byte[] dataSet, byte[] saveBlob, byte[] colorBlob, boolean isCustom) {
        this.levelId = levelId;
        this.puzzleId = puzzleId;
        this.name = name;
        this.number = number;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
        this.saveBlob = saveBlob;
        this.colorBlob = colorBlob;
        this.isCustom = isCustom;
    }

    public int getLevelId() {
        return this.levelId;
    }

    public int getPuzzleId() {
        return this.puzzleId;
    }

    public String getName() {
        return this.name;
    }

    public int getNumber() {
        return this.number;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getSaveBlob() {
        return this.saveBlob;
    }

    public SaveData getSaveData() {
        return new SaveData(saveBlob, height, width);
    }

    public byte[] getDataSet() {
        return this.dataSet;
    }

    public boolean isCustom() {
        return this.isCustom;
    }

    public byte[] getColorBlob() {
        return colorBlob;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("levelId: ");
        stringBuilder.append(levelId);
        stringBuilder.append("\npuzzleId: ");
        stringBuilder.append(puzzleId);
        stringBuilder.append("\nname: ");
        stringBuilder.append(name);
        stringBuilder.append("\nnumber: ");
        stringBuilder.append(number);
        stringBuilder.append("\nprogress: ");
        stringBuilder.append(progress);
        stringBuilder.append("\nwidth: ");
        stringBuilder.append(width);
        stringBuilder.append("\nheight: ");
        stringBuilder.append(height);
        stringBuilder.append("\nsaveBlob: ");
        stringBuilder.append(Arrays.toString(saveBlob));
        stringBuilder.append("\ndataSet: ");
        stringBuilder.append(Arrays.toString(dataSet));
        stringBuilder.append("\nisCustom: ");
        stringBuilder.append(isCustom);
        stringBuilder.append("\ncolorBlob: ");
        stringBuilder.append(Arrays.toString(colorBlob));


        return stringBuilder.toString();
    }
}
