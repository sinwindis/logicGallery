package com.sinwindis.logicgallery.game;

public class SaveData {

    private int width;
    private int height;
    private byte[][] values;
    private boolean[][] isFixed;
    private boolean[][] isHintUsed;

    private static final byte MASK_VALUE = 0b00000011;
    private static final byte MASK_FIX = 0b01000000;
    private static final byte MASK_HINT = (byte) 0b10000000;


    public SaveData(byte[] saveData, int height, int width) {
        this.height = height;
        this.width = width;

        values = new byte[height][width];
        isFixed = new boolean[height][width];
        isHintUsed = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                values[y][x] = (byte) (saveData[y * width + x] & MASK_VALUE);
                isFixed[y][x] = (saveData[y * width + x] & MASK_FIX) == MASK_FIX;
                isHintUsed[y][x] = (saveData[y * width + x] & MASK_HINT) == MASK_HINT;
            }
        }
    }

    public static byte[] getBlob(Board board) {

        int height = board.getHeight();
        int width = board.getWidth();

        byte[] saveBlob = new byte[height*width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++) {

                Cell cell = board.getCell(y, x);

                byte value = cell.getCurrentValue();
                if(cell.isHinted()){
                    value = (byte) (value | MASK_HINT);
                }
                if(cell.isFixed()) {
                    value = (byte) (value | MASK_FIX);
                }
                saveBlob[y*width + x] = value;
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
