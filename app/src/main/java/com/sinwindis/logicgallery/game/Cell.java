package com.sinwindis.logicgallery.game;

public class Cell {

    public static final byte BLANK = 0;
    public static final byte CHECK = 1;
    public static final byte X = 2;

    private final byte correctValue;
    private byte currentValue;

    private boolean isHinted;
    private boolean isFixed;

    public Cell(byte correctValue) {
        this.correctValue = correctValue;

        isHinted = false;
        isFixed = false;
    }

    public boolean useHint() {
        if (isHinted) {
            //만약 이미 힌트를 사용한 칸이면 false 를 반환한다.
            return false;
        }
        //현재 값을 정답으로 수정해 주고 힌트 사용, 값 고정을 체크해 준다.

        currentValue = correctValue;
        isHinted = true;
        isFixed = true;

        return true;
    }

    public boolean isCorrect() {

        switch (this.correctValue) {
            case BLANK:
                if (this.currentValue == BLANK || this.currentValue == X)
                    return true;
                break;
            case CHECK:
                if (this.currentValue == CHECK)
                    return true;
                break;
        }

        return false;
    }

    public void setHinted(boolean isHintUsed) {
        this.isHinted = isHintUsed;
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

    public boolean isHinted() {
        return this.isHinted;
    }

    public boolean isFixed() {
        return this.isFixed;
    }
}


