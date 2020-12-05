package com.sinwindis.logicgallery.game;

public class Board {


    private Cell[][] cells;

    private int height;
    private int width;

    private int stackMax = 0;
    private int stackIdx = 0;

    public Board(int height, int width)
    {
        this.height = height;
        this.width = width;

        this.cells = new Cell[height][width];
    }

    public Cell getCell(int y, int x)
    {
        if(y < this.height && x < this.width)
            return cells[y][x];
        else
            return null;
    }

    public Cell[][] getCells()
    {
        return cells;
    }

    public boolean setData(byte[] values) {

        //correctValue 를 초기화해주는 작업

        if(values.length != this.height*this.width) {
            return false;
        }

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++)
            {
                cells[y][x] = new Cell(values[x + y * this.width]);
            }
        }
        return true;
    }

    public boolean pushValues(byte[][] values)
    {
        if(values.length != height || values[0].length != width) {
            return false;
        }

        boolean isValueChanged = false;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++)
            {
                //만약 셀의 값이 하나라도 바뀌면 isValueChanged 를 true 로 바꿔준다.
                if(cells[y][x].push(values[y][x]))
                    isValueChanged = true;
            }
        }
        return isValueChanged;
    }

    public boolean pushValues(byte[] values)
    {
        if(values.length != this.height*this.width) {
            return false;
        }

        boolean isValueChanged = false;

        for(int i = 0; i < values.length; i++) {
            int y = i/this.width;
            int x = i%this.width;

            //만약 셀의 값이 하나라도 바뀌면 isValueChanged 를 true 로 바꿔준다.
            if(cells[y][x].push(values[i]))
                isValueChanged = true;
        }

        return isValueChanged;
    }

    public boolean moveToNext() {

        if(width == 0 || height == 0)
            return false;

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++) {
                cells[y][x].moveToNext();
            }
        }

        stackIdx = cells[0][0].getStackIdx();
        stackMax = cells[0][0].getStackMax();

        return true;
    }

    public boolean moveToPrev() {
        if(width == 0 || height == 0)
            return false;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                cells[y][x].moveToPrev();
            }
        }

        stackIdx = cells[0][0].getStackIdx();
        stackMax = cells[0][0].getStackMax();

        return true;
    }

    public byte[] getParsedCells()
    {
        byte[] parsedCells = new byte[this.height*this.width];

        for(int i = 0; i < parsedCells.length; i++)
        {
            int y = i/this.width;
            int x = i%this.width;
            byte cellValue = cells[y][x].getCurrentValue();
            if(cells[y][x].isHintUsed())
                cellValue *= -1;
            parsedCells[i] = cellValue;
        }

        return parsedCells;
    }

    public int getStackMax() { return this.stackMax; }
    public int getStackIdx() { return this.stackIdx; }
    public boolean isBoardComplete() {
        for(int y = 0; y < this.height; y++)
        {
            for(int x = 0; x < this.width; x++)
            {
                //셀에 하나라도 오답이 있으면 false 리턴
                if(!cells[y][x].isCorrect()) {
                    return false;
                }
            }
        }

        //오답이 하나도 없으면 true 리턴;
        return true;
    }

}
