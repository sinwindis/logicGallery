package com.sinwindis.logicgallery.data;

import com.sinwindis.logicgallery.game.Board;
import com.sinwindis.logicgallery.game.Cell;

public class SaveData {

    private final int width;
    private final int height;
    private final byte[][] values;
    private final boolean[][] isFixed;
    private final boolean[][] isHintUsed;

    private static final byte MASK_VALUE = 0b00000011;
    private static final byte MASK_FIX = 0b01000000;
    private static final byte MASK_HINT = (byte) 0b10000000;


    public SaveData(byte[] saveBlob, int height, int width) {
        this.height = height;
        this.width = width;

        values = new byte[height][width];
        isFixed = new boolean[height][width];
        isHintUsed = new boolean[height][width];

        if (saveBlob != null && saveBlob.length == height * width) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    values[y][x] = (byte) (saveBlob[y * width + x] & MASK_VALUE);
                    isFixed[y][x] = (saveBlob[y * width + x] & MASK_FIX) == MASK_FIX;
                    isHintUsed[y][x] = (saveBlob[y * width + x] & MASK_HINT) == MASK_HINT;
                }
            }
        }

    }

    public static byte[] of(Board board) {

        int height = board.getHeight();
        int width = board.getWidth();

        byte[] saveBlob = new byte[height * width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Cell cell = board.getCell(y, x);

                byte value = cell.getCurrentValue();
                if (cell.isHinted()) {
                    value = (byte) (value | MASK_HINT);
                }
                if (cell.isFixed()) {
                    value = (byte) (value | MASK_FIX);
                }
                saveBlob[y * width + x] = value;
            }
        }

        return saveBlob;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[][] getValues() {
        return this.values;
    }

    public boolean[][] getFixes() {
        return this.isFixed;
    }

    public boolean[][] getHints() {
        return this.isHintUsed;
    }
}
