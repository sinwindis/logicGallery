package com.sinwindis.logicgallery.game;

public class Cell {

    //0: 공백 1: 체크 2: X
    private byte correctValue;
    private byte currentValue;

    private boolean isHintUsed;
    private boolean isFixed;

    private CellStack stack;

    public Cell(byte correctValue) {
        this.correctValue = correctValue;

        isHintUsed = false;
        isFixed = false;

        stack = new CellStack();
    }

    public boolean push(byte value) {


        if (isFixed) {
            //해당 셀이 잠겨있으면 스택 한 칸 추가만 하고 끝냄
            stack.push(currentValue);
            return false;
        } else {
            //현재 값을 입력 value 로 바꿔주고 스택도 한 칸 추가함.
            currentValue = value;
            stack.push(value);
            return true;
        }
    }

    public boolean moveToPrev() {

        byte value = stack.moveToPrev();
        if (isFixed) {
            return false;
        }

        currentValue = value;

        return true;
    }

    public boolean moveToNext() {
        byte value = stack.moveToNext();
        if (isFixed) {
            return false;
        }

        currentValue = value;

        return true;
    }

    public boolean useHint() {
        if (isHintUsed) {
            //만약 이미 힌트를 사용한 칸이면 false 를 반환한다.
            return false;
        }
        //현재 값을 정답으로 수정해 주고 힌트 사용, 값 고정을 체크해 준다.

        currentValue = correctValue;
        isHintUsed = true;
        isFixed = true;

        return true;
    }

    public boolean isCorrect() {

        switch (this.correctValue) {
            case 0:
                if (this.currentValue == 0 || this.currentValue == 2)
                    return true;
                break;
            case 1:
                if (this.currentValue == 1)
                    return true;
                break;
        }

        return false;
    }

    public void setHintUsed(boolean isHintUsed) {
        this.isHintUsed = isHintUsed;
    }

    public void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public boolean setCurrentValue(byte value) {
        if (isFixed) {
            return false;
        }
        this.currentValue = value;
        return true;
    }

    public byte getCurrentValue() {
        return this.currentValue;
    }

    public byte getCorrectValue() {
        return this.correctValue;
    }

    public boolean isHintUsed() {
        return this.isHintUsed;
    }

    public boolean isFixed() {
        return this.isFixed;
    }


    public int getStackMax() {
        return stack.getStackMax();
    }

    public int getStackIdx() {
        return stack.getStackIdx();
    }
}

class CellStack {

    byte[] values;

    private int stackSize = 10;
    private int stackMax = 0;
    private int stackIdx = 0;

    public CellStack() {
        values = new byte[stackSize];
        values[0] = 0;
    }

    public void push(byte value) {
        stackIdx++;
        stackMax = stackIdx;

        if (stackIdx == stackSize) {
            expandStackSize();
        }

        values[stackIdx] = value;
    }

    public byte moveToPrev() {
        if (stackIdx == 0)
            return 0;

        stackIdx--;
        return values[stackIdx];
    }

    public byte moveToNext() {
        if (stackIdx != stackMax) {
            //마지막 칸이 아니라면 다음으로 한 칸 이동
            stackIdx++;
        }

        return values[stackIdx];
    }

    private void expandStackSize() {
        byte[] tempValues = values.clone();

        stackSize *= 2;
        values = new byte[stackSize];

        System.arraycopy(tempValues, 0, values, 0, tempValues.length);
    }

    public int getStackMax() {
        return this.stackMax;
    }

    public int getStackIdx() {
        return this.stackIdx;
    }
}
