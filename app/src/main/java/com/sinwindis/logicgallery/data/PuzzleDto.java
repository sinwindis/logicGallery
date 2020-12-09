package com.sinwindis.logicgallery.data;

import android.graphics.Bitmap;

public class PuzzleDto {

    private final int puzzleId;
    private final String artistName;
    private final String puzzleName;
    private final Bitmap bitmap;
    private final int puzzleWidth;
    private final int puzzleHeight;
    private final int levelWidth;
    private final int levelHeight;
    private final int progress;
    private final boolean isCustom;

    public PuzzleDto(int puzzleId, String artistName, String puzzleName, Bitmap bitmap, int puzzleWidth, int puzzleHeight, int levelWidth, int levelHeight, int progress, boolean isCustom) {
        this.puzzleId = puzzleId;
        this.artistName = artistName;
        this.puzzleName = puzzleName;
        this.bitmap = bitmap;
        this.puzzleWidth = puzzleWidth;
        this.puzzleHeight = puzzleHeight;
        this.levelHeight = levelHeight;
        this.levelWidth = levelWidth;
        this.isCustom = isCustom;
        this.progress = progress;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getPuzzleName() {
        return puzzleName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getPuzzleWidth() {
        return puzzleWidth;
    }

    public int getPuzzleHeight() {
        return puzzleHeight;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public int getLevelHeight() {
        return levelHeight;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
