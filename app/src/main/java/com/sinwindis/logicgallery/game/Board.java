package com.sinwindis.logicgallery.game;

import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sinwindis.logicgallery.data.SaveData;

public class Board {


    private final Cell[][] cells;
    private final BoardStack boardStack;

    private final int height;
    private final int width;

    public Board(int height, int width) {
        this.height = height;
        this.width = width;

        this.cells = new Cell[height][width];
        this.boardStack = new BoardStack(height, width);
    }

    public Cell getCell(int y, int x) {

        if (y < this.height && x < this.width) {
            return cells[y][x];
        } else {
            return null;

        }

    }

    public Cell getCell(Point point) {

        int y = point.y;
        int x = point.x;

        if (y < this.height && x < this.width) {
            return cells[y][x];
        } else {
            return null;

        }
    }

    public boolean setCorrectValues(byte[] values) {

        //correctValue 를 초기화해주는 작업

        if (values.length != this.height * this.width) {
            return false;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(values[x + y * this.width]);
            }
        }
        return true;
    }

    public boolean loadSaveData(SaveData saveData) {

        if (saveData.getHeight() != this.height || saveData.getWidth() != this.width) {
            return false;
        }

        byte[][] saveValues = saveData.getValues();
        boolean[][] saveFixes = saveData.getFixes();
        boolean[][] saveHints = saveData.getHints();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x].setCurrentValue(saveValues[y][x]);
                cells[y][x].setFixed(saveFixes[y][x]);
                cells[y][x].setHinted(saveHints[y][x]);
            }
        }

        boardStack.push();

        return true;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void push() {
        boardStack.push();
    }

    public boolean moveToNext() {
        if (boardStack.moveToNext()) {
            byte[][] stack = boardStack.getCurrentStack();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cells[y][x].setCurrentValue(stack[y][x]);
                }
            }
            return true;
        }
        return false;

    }

    public boolean moveToPrev() {
        if (boardStack.moveToPrev()) {
            byte[][] stack = boardStack.getCurrentStack();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cells[y][x].setCurrentValue(stack[y][x]);
                }
            }
            return true;
        }
        return false;
    }

    public byte[] getParsedCells() {
        byte[] parsedCells = new byte[this.height * this.width];

        for (int i = 0; i < parsedCells.length; i++) {
            int y = i / this.width;
            int x = i % this.width;
            byte cellValue = cells[y][x].getCurrentValue();
            if (cells[y][x].isHinted())
                cellValue *= -1;
            parsedCells[i] = cellValue;
        }

        return parsedCells;
    }

    public boolean isBoardComplete() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                //셀에 하나라도 오답이 있으면 false 리턴
                if (!cells[y][x].isCorrect()) {
                    return false;
                }
            }
        }

        //오답이 하나도 없으면 true 리턴;
        return true;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getExploredStackNum() {
        return boardStack.exploredStackNum;
    }

    public int getCurrentStackNum() {
        return boardStack.currentStackNum;
    }


    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    class BoardStack {
        private final int MAX_STACK_NUM_DEFAULT = 10;


        private int currentStackNum = -1;
        private int exploredStackNum = -1;
        private int maxStackNum = MAX_STACK_NUM_DEFAULT;

        private byte[][][] stack;

        public BoardStack(int height, int width) {
            stack = new byte[MAX_STACK_NUM_DEFAULT][height][width];
        }

        private void expandStack() {
            byte[][][] stackTemp = new byte[maxStackNum][height][width];

            for (int i = 0; i < maxStackNum; i++) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        stackTemp[i][y][x] = stack[i][y][x];
                    }
                }
            }
            maxStackNum *= 2;
            stack = new byte[maxStackNum][height][width];

            for (int i = 0; i < stackTemp.length; i++) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        stack[i][y][x] = stackTemp[i][y][x];
                    }
                }
            }
        }

        public void push() {

            currentStackNum++;
            exploredStackNum = currentStackNum;

            if (currentStackNum == maxStackNum) {
                expandStack();
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    stack[currentStackNum][y][x] = cells[y][x].getCurrentValue();
                }
            }
        }

        public boolean moveToPrev() {
            if (currentStackNum == 0) {
                return false;
            }
            currentStackNum--;
            return true;
        }

        public boolean moveToNext() {
            if (currentStackNum == exploredStackNum) {
                return false;
            }
            currentStackNum++;
            return true;
        }

        public byte[][] getCurrentStack() {
            return stack[currentStackNum];
        }
    }
}
